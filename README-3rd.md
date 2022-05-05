# 购物车业务

vue打包完的静态资源都放在了/mydata/nginx/html/static/cart目录下，nginx根据请求的域名来路由

购物车有两个页面，列表页和添加商品成功页

## 游客购物车**/**离线购物车**/**临时购物车

用户可以在未登录状态下将商品添加到购物车

放入 **redis**(采用) 浏览器即使关闭，下次进入，临时购物车数据都在

购物车是一个读多写多的场景，因此放入数据库并不合适，但购物车又是需要持久化，因此这里我们选用redis存储购物车数据。

## 用户购物车**/**在线购物车

放入 **redis**(采用) 登录以后，会将临时购物车的数据全部合并过来，并清空临时购物车

购物车是一个读多写多的场景，因此放入数据库并不合适，但购物车又是需要持久化，因此这里我们选用redis存储购物车数据。

## 购物车功能

- 用户可以使用购物车一起结算下单
- 给购物车添加商品
- 用户可以查询自己的购物车
- 用户可以在购物车中修改购买商品的数量。 - 用户可以在购物车中删除商品
- 选中不选中商品
- 在购物车中展示商品优惠信息
- 提示购物车商品价格变化

## 数据模型分析

### 购物车项

![](./docs/assets/217.png)

```java
public class CartItemVo {
    private Long skuId;
    //是否选中
    private Boolean check = true;
    //标题
    private String title;
    //图片
    private String image;
    //商品套餐属性
    private List<String> skuAttrValues;
    //价格
    private BigDecimal price;
    //数量
    private Integer count;
    //总价
    private BigDecimal totalPrice;
    /**
     * 当前购物车项总价等于单价x数量
     * @return
     */
    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(count));
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
```



### 购物车

![](./docs/assets/221.png)

```java
public class CartVo {
    /**
     * 购物车子项信息
     */
    List<CartItemVo> items;
    /**
     * 商品数量
     */
    private Integer countNum;
    /**
     * 商品类型数量
     */
    private Integer countType;
    /**
     * 商品总价
     */
    private BigDecimal totalAmount;
    /**
     * 减免价格
     */
    private BigDecimal reduce = new BigDecimal("0.00");
    public List<CartItemVo> getItems() {
        return items;
    }
    public void setItems(List<CartItemVo> items) {
        this.items = items;
    }
    //总数量=遍历每个购物项总和
    public Integer getCountNum() {
        int count=0;
        if (items != null && items.size() > 0) {
            for (CartItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }
    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }
    //商品类型数量=遍历所有商品类型和
    public Integer getCountType() {
        int count=0;
        if (items != null && items.size() > 0) {
            for (CartItemVo item : items) {
                count += 1;
            }
        }
        return count;
    }
    public void setCountType(Integer countType) {
        this.countType = countType;
    }
    //总价为单个购物项总价-优惠
    public BigDecimal getTotalAmount() {
        BigDecimal total = new BigDecimal(0);
        if (items != null && items.size() > 0) {
            for (CartItemVo item : items) {
                total.add(item.getTotalPrice());
            }
        }
        total.subtract(reduce);
        return total;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public BigDecimal getReduce() {
        return reduce;
    }
    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
```

一个购物车是由各个购物项组成的，但是我们用`List`进行存储并不合适，因为使用`List`查找某个购物项时需要挨个遍历每个购物项，会造成大量时间损耗，为保证查找速度，我们使用`hash`进行存储

因此每一个购物项信息，都是一个对象，基本字段包括:

```json
{
	skuId: 2131241,
	check: true,
	title: "Apple iphone.....",
  defaultImage: "...",
  price: 4999,
	count: 1,
	totalPrice: 4999,
  skuSaleVO: {...} // 销售属性
}
```

Redis 有 5 种不同数据结构，这里选择哪一种比较合适呢?Map<String, List<String>>

\- 首先不同用户应该有独立的购物车，因此购物车应该以用户的作为 key 来存储，Value 是 用户的所有购物车信息。这样看来基本的`k-v`结构就可以了。
 \- 但是，我们对购物车中的商品进行增、删、改操作，基本都需要根据商品 id 进行判断， 为了方便后期处理，我们的购物车也应该是`k-v`结构，key 是商品 id，value 才是这个商品的 购物车信息。

综上所述，我们的购物车结构是一个双层 Map:  Map<String,Map<String,String>>

- 第一层 Map，Key 是用户 id

- 第二层 Map，Key 是购物车中商品 id，值是购物项数据

![](./docs/assets/218.png)



## ThreadLocal用户身份鉴别

### 用户身份鉴别方式

参考京东，在点击购物车时，会为临时用户生成一个`name`为`user-key`的`cookie`临时标识，过期时间为一个月，如果手动清除`user-key`，那么临时购物车的购物项也被清除，所以`user-key`是用来标识和存储临时购物车数据的

![](./docs/assets/222.png)

### 使用ThreadLocal进行用户身份鉴别信息传递

![](./docs/assets/219.png)

- 在调用购物车的接口前，先通过session信息判断是否登录，并分别进行用户身份信息的封装，并把`user-key`放在cookie中
- 这个功能使用拦截器进行完成

```
```

## 新增【接口】添加商品到购物车

### 接口逻辑

![](./docs/assets/223.svg)