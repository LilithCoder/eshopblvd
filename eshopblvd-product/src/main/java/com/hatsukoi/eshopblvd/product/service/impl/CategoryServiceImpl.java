package com.hatsukoi.eshopblvd.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hatsukoi.eshopblvd.product.dao.CategoryMapper;
import com.hatsukoi.eshopblvd.product.entity.Category;
import com.hatsukoi.eshopblvd.product.entity.CategoryExample;
import com.hatsukoi.eshopblvd.product.service.CategoryBrandRelationService;
import com.hatsukoi.eshopblvd.product.service.CategoryService;
import com.hatsukoi.eshopblvd.product.vo.CatalogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/03/13 Sun 1:14 PM
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 查出所有的分类以及其子分类，并且以父子树形结构组装起来
     * @return
     */
    @Override
    public List<Category> getCategoryTree() {
        // TODO: 查询时只查showStatus为1的分类
        // 查出所有分类
        List<Category> allCategories = this.categoryMapper.selectByExample(null);
        // 从所有一级分类开始查找并组装其子分类
        List<Category> level1Categories = allCategories.stream()
                .filter(category -> {
                    return category.getCatLevel() == 1;
                }).map(category -> {
                    category.setChildren(getChildren(category, allCategories));
                    return category;
                }).sorted((cat1, cat2) -> {
                    // 子分类排序
                    Integer sort1 = cat1.getSort() != null ? cat1.getSort() : 0;
                    Integer sort2 = cat2.getSort() != null ? cat2.getSort() : 0;
                    return sort1.compareTo(sort2);
                }).collect(Collectors.toList());
        return level1Categories;
    }

    /**
     * 根据分类id查询
     * @param catId
     * @return
     */
    @Override
    public Category getCategoryById(Long catId) {
        return categoryMapper.selectByPrimaryKey(catId);
    }

    @Override
    public int removeCategoriesByIds(List<Long> catIds) {
        // TODO: 先检查当前删除的分类是否已经没有子分类或者是否被其他地方引用，没有才可以删
        // 根据catIds批量删除分类
        CategoryExample example = new CategoryExample();
        example.createCriteria().andCatIdIn(catIds);
        return categoryMapper.deleteByExample(example);
    }

    /**
     * 插入指定分类
     * @param category
     */
    @Override
    public int insertCategory(Category category) {
        return categoryMapper.insert(category);
    }

    /**
     * 根据catId去更新指定分类内容
     * @param category
     */
    @CacheEvict(key = "'catalogJSON'")
    @Override
    @Transactional
    public void updateCategory(Category category) {
        // updateByPrimaryKeySelective不同，当某一实体类的属性为null时，mybatis会使用动态sql过滤掉，不更新该字段
        categoryMapper.updateByPrimaryKeySelective(category);
        // 级联修改
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * 批量更新分类
     * @param categories
     */
    @CacheEvict(key = "'catalogJSON'")
    @Transactional
    @Override
    public void batchUpdateCategories(List<Category> categories) {
        categoryMapper.batchUpdateSelective(categories);
    }

    /**
     * 递归查询分类路径
     * @param catelogId
     * @return [2, 34, 225]
     */
    @Override
    public Long[] getCatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        findPath(catelogId, path);
        Collections.reverse(path);
        return path.toArray(new Long[path.size()]);
    }

    /**
     * 从获取首页数据
     * @return
     */
    @Override
    public Map<String, Object> getHomepageInitData() {
        Map<String, Object> result = new HashMap<>();
        Map<Long, CatalogVO> catalog = getHomepageCatalogFromRedis();
        result.put("catalog", catalog);
        return result;
    }

    /**
     * 从redis分布式缓存中查询菜单信息
     * @return
     */
    private Map<Long, CatalogVO> getHomepageCatalogFromRedis() {
        long beginTime = System.currentTimeMillis();
        // 先从缓存中找有没有数据，没有的话再查数据库
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            log.info("获取三级菜单未命中redis缓存");
            Map<Long, CatalogVO> catalogFromDB = getHomepageCatalogFromDBWithLock();
            String toJSONString = JSON.toJSONString(catalogFromDB);
            redisTemplate.opsForValue().set("catalogJSON", toJSONString, 1, TimeUnit.DAYS);
            long time = System.currentTimeMillis() - beginTime;
            log.info("获取三级菜单耗时：{}ms", time);
            return catalogFromDB;
        }
        log.info("获取三级菜单命中redis缓存!");
        Map<Long, CatalogVO> result = JSON.parseObject(catalogJSON, new TypeReference<Map<Long, CatalogVO>>() {
        });
        long time = System.currentTimeMillis() - beginTime;
        log.info("获取三级菜单耗时：{}ms", time);
        return result;

    }

    /**
     * 加个分布式锁从数据库查数据
     * @return
     */
    private Map<Long, CatalogVO> getHomepageCatalogFromDBWithLock() {
        // 锁的标识
        String uuid = UUID.randomUUID().toString();
        // 原子操作获取锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
        if (lock) {
            log.info("获取分布式锁成功，线程id：" + Thread.currentThread().getId());
            Map<Long, CatalogVO> homepageCatalog = null;
            try {
                String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
                if (!StringUtils.isEmpty(catalogJSON)) {
                    log.info("缓存命中! 数据返回");
                    homepageCatalog = JSON.parseObject(catalogJSON, new TypeReference<Map<Long, CatalogVO>>() {});
                } else {
                    log.info("开始查询数据库...");
                    homepageCatalog = getHomepageCatalogFromDB();
                    // 查到的数据再放入缓存，将对象转为json放在缓存中
                    // 如果数据库查询结果为null，也给缓存存入"null"来避免「缓存穿透」
                    String toJSONString = JSON.toJSONString(homepageCatalog);
                    redisTemplate.opsForValue().set("catalogJSON", toJSONString, 1, TimeUnit.DAYS);
                }
            } catch (Exception e) {
                log.error(e.toString());
            } finally {
                // 释放锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
                log.info("分布式锁释放成功，线程id：" + Thread.currentThread().getId());
            }
            return homepageCatalog;
        } else {
            // 获取不到分布式锁就休眠200ms后自旋重试获取锁
            log.info("获取分布式锁失败，重试中...");
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                log.error(e.toString());
            }
            return getHomepageCatalogFromDBWithLock();
        }
    }


    /**
     * 从数据库获取首页的分类菜单
     * @return
     */
    private Map<Long, CatalogVO> getHomepageCatalogFromDB() {
        Map<Long, CatalogVO> collect = null;

        // 【优化】仅查询一次数据库，获取全部的分类数据「pms_category」
        List<Category> categories = categoryMapper.selectByExample(null);

        // 获取一级分类
        List<Category> category1List = getChildren(categories, 0L, 0);

        // 一级分类id为key，CatalogVO为value
        if (category1List != null && category1List.size() > 0) {
            collect = category1List.stream().collect(Collectors.toMap(category -> {
                return category.getCatId();
            }, category -> {
                CatalogVO catalogVO = new CatalogVO();
                // 每个一级分类找到其所有二级分类
                List<Category> category2List = getChildren(categories, category.getCatId(), category.getCatLevel());
                List<CatalogVO.Catalog2VO> catalog2VOList = null;
                if (category2List != null && category2List.size() > 0) {
                    catalog2VOList = category2List.stream().map(category2 -> {
                        CatalogVO.Catalog2VO catalog2VO = new CatalogVO.Catalog2VO();
                        // 每个二级分类找到其所有三级分类
                        List<Category> category3List = getChildren(categories, category2.getCatId(), category2.getCatLevel());
                        List<CatalogVO.Catalog3VO> catalog3VOList = null;
                        if (category3List != null && category3List.size() > 0) {
                            catalog3VOList = category3List.stream().map(category3 -> {
                                CatalogVO.Catalog3VO catalog3VO = new CatalogVO.Catalog3VO();
                                catalog3VO.setCatalog3Id(category3.getCatId());
                                catalog3VO.setCatalog3Name(category3.getName());
                                return catalog3VO;
                            }).collect(Collectors.toList());
                        }
                        catalog2VO.setCatalog2Id(category2.getCatId());
                        catalog2VO.setCatalog2Name(category2.getName());
                        catalog2VO.setCatalog3list(catalog3VOList);
                        return catalog2VO;
                    }).collect(Collectors.toList());
                }
                catalogVO.setCatalog1Id(category.getCatId());
                catalogVO.setCatalog1Name(category.getName());
                catalogVO.setCatalog2list(catalog2VOList);
                return catalogVO;
            }));
        }
        return collect;
    }

    /**
     * 根据当前分类的id和level来获取该分类的子分类们
     * @param parentId
     * @param parentLevel
     * @return
     */
    private List<Category> getChildren(List<Category> categories, Long parentId, Integer parentLevel) {
        return categories.stream().filter(category -> {
            return category.getParentCid() == parentId && category.getCatLevel() == parentLevel + 1;
        }).collect(Collectors.toList());
    }

    /**
     * 递归辅助函数
     * 查找父分类，记录在path里
     * @param catelogId
     * @param path
     */
    private void findPath(Long catelogId, List<Long> path) {
        if (catelogId == 0) return;
        path.add(catelogId);
        Category category = categoryMapper.selectByPrimaryKey(catelogId);
        findPath(category.getParentCid(), path);
    }

    /**
     * 递归查询并组装子分类辅助函数
     * @param root 父分类
     * @param allCategories 所有分类
     * @return
     */
    private List<Category> getChildren(Category root, List<Category> allCategories) {
        // 在所有分类中查找到所有root分类的子分类，继续遍历递归找其子分类直到叶层级分类，并组装树结构
        // 边界条件：如果是叶层级分类，那么就在所有分类中查找不到其子分类了，所有不用处理
        // 最后返回属于一级分类下的子分类树结构，让一级去组装完成最后的树结构
        List<Category> children = allCategories.stream()
                .filter(category -> {
                    return category.getParentCid() == root.getCatId();
                }).map(subCategory -> {
                    // 子分类查找并组装属于自己的子分类
                    subCategory.setChildren(getChildren(subCategory, allCategories));
                    return subCategory;
                }).sorted((cat1, cat2) -> {
                    // 子分类排序
                    Integer sort1 = cat1.getSort() != null ? cat1.getSort() : 0;
                    Integer sort2 = cat2.getSort() != null ? cat2.getSort() : 0;
                    return sort1.compareTo(sort2);
                }).collect(Collectors.toList());
        // 返回的是子分类们
        return children;
    }
}
