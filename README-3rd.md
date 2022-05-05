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

```java
/**
 * 请求拦截器
 * 在执行目标方法之前，判断用户的登录状态。并封装传递(用户信息)给controller
 * @author gaoweilin
 * @date 2022/05/05 Thu 3:10 AM
 */
public class CartInterceptor implements HandlerInterceptor {
    /**
     * 整条调用链条线程的local变量存入拦截器已经封装好的用户信息
     */
    public static ThreadLocal<UserInfoTO> threadLocal = new ThreadLocal<>();

    /**
     * 业务执行前逻辑
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 1. 在请求前拦截，封装用户信息（无论登录与否）
        UserInfoTO userInfoTO = new UserInfoTO();
        HttpSession session = request.getSession();
        Object memberTO = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (memberTO != null) {
            // 用户登陆了
            MemberTO loginUser = JSON.parseObject(JSON.toJSONString(memberTO), MemberTO.class);
            userInfoTO.setUserId(loginUser.getId());
        }

        // 2. 检查cookies里有没有临时用户userKey
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    userInfoTO.setUserKey(cookie.getValue());
                    // 只要已经有临时用户userkey了，tempUser字段就设置为true，这是为了临时用户userKey不要一直被持续更新
                    userInfoTO.setTempUser(true);
                }
            }
        }

        // 3. 无论有没有用户登录，都分配一个临时用户userKey
        if (StringUtils.isEmpty(userInfoTO.getUserKey())) {
            String userKey = UUID.randomUUID().toString();
            userInfoTO.setUserKey(userKey);
        }

        // 4. 将封装的用户信息放入threadlocal
        threadLocal.set(userInfoTO);
        return true;
    }

    /**
     * 业务执行后逻辑
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTO userInfoTO = threadLocal.get();
        // 如果这是第一次分配临时用户，将分配的userkey写入cookie，过期时间为30天
        if (!userInfoTO.isTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTO.getUserKey());
            cookie.setDomain(DomainConstant.COOKIE_DOMAIN);
            cookie.setDomain(DomainConstant.COOKIE_DOMAIN);
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}

```

```
浏览器有一个cookie；user-key；标识用户身份，一个月后过期；
* 如果第一次使用jd的购物车功能，都会给一个临时的用户身份；
* 浏览器以后保存，每次访问都会带上这个cookie；
*
* 登录：session有
* 没登录：按照cookie里面带来user-key来做。
* 第一次：如果没有临时用户，帮忙创建一个临时用户。
```

## 新增【接口】添加商品到购物车

### 接口逻辑

![](./docs/assets/223.svg)

- 若当前商品已经存在购物车，只需增添数量
- 否则需要查询商品购物项所需信息，并添加新商品至购物车

```java
/**
     * 商品sku加入购物车
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public CartItemVO addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        // 1. 根据userId来获取用户购物车，或者根据userKey获取离线购物车
        BoundHashOperations<String, Object, Object> cartOp = getCartOp();

        // 2. 获取到购物车后，查找购物车中有没有skuId对应的商品，购物车的数据结构为hash，key为skuId
        String jsonStr = (String) cartOp.get(skuId.toString());
        if (StringUtils.isEmpty(jsonStr)) {
            // 2.1 如果没有，异步查询sku的需要相关信息，存入redis
            CartItemVO cartItem = new CartItemVO();

            // 2.1.1 RPC调用商品服务查询skuInfo基本信息
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                CommonResponse resp = CommonResponse.convertToResp(productRpcService.getSkuInfo(skuId));
                SkuInfoTO data = resp.getData(new TypeReference<SkuInfoTO>() {
                });
                cartItem.setSkuId(data.getSkuId());
                cartItem.setCheck(true);
                cartItem.setTitle(data.getSkuTitle());
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setPrice(data.getPrice());
                cartItem.setCount(num);
            }, executor);

            // 2.1.2 RPC调用商品服务查询sku的销售属性
            CompletableFuture<Void> getSkuSaleAttrsWithValueTask = CompletableFuture.runAsync(() -> {
                CommonResponse resp = CommonResponse.convertToResp(productRpcService.getSkuSaleAttrsWithValue(skuId));
                List<String> data = resp.getData(new TypeReference<List<String>>() {
                });
                cartItem.setSkuAttr(data);
            }, executor);

            // 2.1.3 等异步操作都结束后将购物车项放入redis
            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrsWithValueTask).get();
            cartOp.put(skuId.toString(), JSON.toJSONString(cartItem));

            return cartItem;
        } else {
            // 2.2 如果有，将查找到的这项商品购物车项中数量累加一下，继续放入redis中
            CartItemVO cartItem = JSON.parseObject(jsonStr, CartItemVO.class);
            cartItem.setCount(cartItem.getCount() + num);
            cartOp.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }
    }

    /**
     * 获取当前要操作购物车的redis操作
     * 优先获取操作在线购物车，没有用户登陆了才获取操作离线购物车
     */
    private BoundHashOperations<String, Object, Object> getCartOp() {
        UserInfoTO userInfoTO = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTO.getUserId() != null) {
            // 有用户登陆, 要获取在线购物车，key为eshopblvd:cart:{userId}
            cartKey = CartConstant.CART_PREFIX + userInfoTO.getUserId();
        } else {
            // 有用户登陆, 要获取离线购物车，key为eshopblvd:cart:{userKey}
            cartKey = CartConstant.CART_PREFIX + userInfoTO.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }
```

## 新增【接口】获取整个购物车

```java
   /**
     * 获取当前购物车（在线/离线）
     * @return
     */
    @Override
    public CartVO getCart() throws ExecutionException, InterruptedException {
        CartVO cart = new CartVO();
        UserInfoTO userInfoTO = CartInterceptor.threadLocal.get();
        if (userInfoTO.getUserId() != null) {
            // 1. 有用户登录了，就将合并临时购物车（如果有），然后删除临时购物车
            // 用户购物车key
            String userCartKey = CartConstant.CART_PREFIX + userInfoTO.getUserId();
            // 临时购物车key
            String tempCartKey = CartConstant.CART_PREFIX + userInfoTO.getUserKey();
            // 获取临时购物车
            List<CartItemVO> tempCartItems = getCartItems(tempCartKey);
            if (tempCartItems != null) {
                // 临时购物车有数据，需要合并
                for (CartItemVO item: tempCartItems) {
                    // 优先加入用户购物车，目前有用户登陆，所以肯定加进用户购物车了
                    addToCart(item.getSkuId(), item.getCount());
                }
                // 删掉临时购物车
                redisTemplate.delete(tempCartKey);
            }
            // 最后获取用户购物车的所有购物项
            List<CartItemVO> cartItems = getCartItems(userCartKey);
            cart.setItems(cartItems);
        } else {
            // 2. 没有用户登陆
            // 直接获取临时购物车就好
            String tempCartKey = CartConstant.CART_PREFIX + userInfoTO.getUserKey();
            List<CartItemVO> cartItems = getCartItems(tempCartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }
```

