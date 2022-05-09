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

user-key 是随机生成的 id，不管有没有登录都会有这个 cookie 信息

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

新增商品:判断是否登录
 \- 是:则添加商品到后台 Redis 中，把 user 的唯一标识符作为 key。
 \- 否:则添加商品到后台 redis 中，使用随机生成的 user-key 作为 key

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

查询购物车列表:判断是否登录

- 否:直接根据 user-key 查询 redis 中数据并展示
- 是:已登录，则需要先根据 user-key 查询 redis 是否有数据
  - 有:需要提交到后台添加到 redis，合并数据，而后查询
  - 否:直接去后台查询 redis，而后返回

![](./docs/assets/224.svg)

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

## 新增【接口】切换购物项的选中态

```java
 @Override
    public void checkCartItem(Long skuId, Boolean checked) {
        BoundHashOperations<String, Object, Object> cartOp = getCartOp();
        // 获取目标购物项
        String jsonStr = (String) cartOp.get(skuId.toString());
        CartItemVO cartItem = JSON.parseObject(jsonStr, CartItemVO.class);
        // 改变其选中状态，更新redis
        cartItem.setCheck(checked);
        cartOp.put(skuId.toString(), JSON.toJSONString(cartItem));
    }
```

## 新增【接口】选中购物车项

```java
@RequestMapping("/checkCart")
public String checkCart(@RequestParam("isChecked") Integer isChecked,@RequestParam("skuId")Long skuId) {
    cartService.checkCart(skuId, isChecked);
    return "redirect:http://cart.gulimall.com/cart.html";
}

//修改skuId对应购物车项的选中状态
@Override
public void checkCart(Long skuId, Integer isChecked) {
    BoundHashOperations<String, Object, Object> ops = getCartItemOps();
    String cartJson = (String) ops.get(skuId.toString());
    CartItemVo cartItemVo = JSON.parseObject(cartJson, CartItemVo.class);
    cartItemVo.setCheck(isChecked==1);
    ops.put(skuId.toString(),JSON.toJSONString(cartItemVo));
}
```

## 新增【接口】修改购物项数量

```java
@RequestMapping("/countItem")
public String changeItemCount(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
    cartService.changeItemCount(skuId, num);
    return "redirect:http://cart.gulimall.com/cart.html";
}

@Override
public void changeItemCount(Long skuId, Integer num) {
    BoundHashOperations<String, Object, Object> ops = getCartItemOps();
    String cartJson = (String) ops.get(skuId.toString());
    CartItemVo cartItemVo = JSON.parseObject(cartJson, CartItemVo.class);
    cartItemVo.setCount(num);
    ops.put(skuId.toString(),JSON.toJSONString(cartItemVo));
}
```

## 新增【接口】删除购物车项

```java
@RequestMapping("/deleteItem")
public String deleteItem(@RequestParam("skuId") Long skuId) {
    cartService.deleteItem(skuId);
    return "redirect:http://cart.gulimall.com/cart.html";
}

@Override
public void deleteItem(Long skuId) {
    BoundHashOperations<String, Object, Object> ops = getCartItemOps();
    ops.delete(skuId.toString());
}
```

# 消息队列

## 应用场景

### 异步处理

用户注册操作和消息处理并行，提高响应速度

![](./docs/assets/225.png)

### 应用解耦

在下单时库存系统不能正常使用。也不影响正常下单，因为下单后，订单系统写入消息队列就不再关心其他的后续操作了。实现订单系统与库存系统的应用解耦

![](./docs/assets/226.png)

### 流量削峰

用户的请求，服务器接收后，首先写入消息队列。假如消息队列长度超过最大数量，则直接抛弃用户请求或跳转到错误页面

秒杀业务根据消息队列中的请求信息，再做后续处理

![](./docs/assets/227.png)

## 核心概念

大多应用中，可通过消息服务中间件来提升系统异步通信、扩展解耦能力

### 消息代理(message broker)

消息队列服务器实体，代理我们发送/接收消息

### 目的地(destination)

当消息发送者发送消息以后，将由消息代理接管，消息代理保证消息传递到指定目的地

消息队列主要有两种形式的目的地

- 队列(queue):点对点消息通信(point-to-point)
  - 消息发送者发送消息，消息代理将其放入一个队列中，消息接收者从队列中获取消息内容，消息读取后被移出队列
  - 消息只有唯一的发送者和接受者（最终拿到消息的），但并不是说只能有一个接收者（允许很多服务同时来监听）

- 主题(topic):发布(publish)/订阅(subscribe)消息通信
  - 发送者(发布者)发送消息到主题，多个接收者(订阅者)监听(订阅)这个主题，那么就会在消息到达时同时收到消息（多人监听，多人收到消息）

## 规范和协议

- JMS（Java Message Service）JAVA消息服务
  - 基于JVM消息代理的规范。ActiveMQ、HornetMQ是JMS实现
- AMQP（Advanced Message Queuing Protocol）
  - 高级消息队列协议，也是一个消息代理的规范，兼容JMS
  - RabbitMQ是AMQP的实现

![](./docs/assets/228.png)

## Spring支持

**spring-jms** 提供了对JMS的支持

**spring-rabbit**提供了对AMQP的支持

需要**ConnectionFactory**的实现来连接消息代理

提供**JmsTemplate**、**RabbitTemplate**来发送消息

**@JmsListener**(**JMS**)、**@RabbitListener**(**AMQP**)注解在方法上监听消息 代理发布的消息

**@EnableJms**、**@EnableRabbit**开启支持

## Spring Boot自动配置

Spring Boot自动配置 **JmsAutoConfiguration RabbitAutoConfiguration**

## 市面的MQ产品

**ActiveMQ**、**RabbitMQ**、**RocketMQ**、**Kafka**

# RabbitMQ

## 核心概念

![](./docs/assets/229.png)

- **Message**
  - 消息，消息是不具名的，它由消息头和消息体组成
  - 消息头，包括routing-key（路由键）、priority（相对于其他消息的优先权）、delivery-mode（指出该消息可能需要持久性存储）等
- Publisher
  - 消息的生产者，也是一个向交换器发布消息的客户端应用程序
- **Exchange**
  - 交换器，将生产者消息路由给服务器中的队列
  - 类型有direct(默认)，fanout, topic, 和headers，不同类型的Exchange转发消息的策略有所区别
- **Queue**
  - 消息队列，用来保存消息直到发送给消费者。它是消息的容器，也是消息的终点。一个消息可投入一个或多个队列。消息一直在队列里面，等待消费者连接到这个队列将其取走。
- **Binding**
  - 绑定，用于消息队列和交换器之间的关联
  - 一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则，所以可以将交换器理解成一个由绑定构成的路由表。
  - Exchange 和Queue的绑定可以是多对多的关系
- Connection
  - 网络连接，比如一个TCP连接，一个客户端建立一条长连接
- Channel
  - 信道，多路复用连接中的一条独立的双向数据流通道。信道是建立在真实的TCP连接内的虚拟连接，AMQP 命令都是通过信道 发出去的，不管是发布消息、订阅队列还是接收消息，这些动作都是通过信道完成。因为对于操作系统来说建立和销毁 TCP 都 是非常昂贵的开销，所以引入了信道的概念，以复用一条 TCP 连接，一个客户端建立一条连接，一条连接里可以用多条信道，在信道里传输数据
- Consumer
  - 消息的消费者，表示一个从消息队列中取得消息的客户端应用程序
- Virtual Host
  - 虚拟主机，表示一批交换器、消息队列和相关对象。
  - 虚拟主机是共享相同的身份认证和加 密环境的独立服务器域。每个 vhost 本质上就是一个 mini 版的 RabbitMQ 服务器，拥 有自己的队列、交换器、绑定和权限机制
  - vhost 是 AMQP 概念的基础，必须在连接时指定
  - RabbitMQ 默认的 vhost 是 /
- Broker
  - 消息队列服务器实体

![](./docs/assets/231.png)

## Docker安装RabbitMQ

```shell
docker run -d --name rabbitmq -p 5671:5671 -p 5672:5672 -p 4369:4369 -p 25672:25672 -p 15671:15671 -p 15672:15672 rabbitmq:management

docker update rabbitmq --restart=always
```

在15672端口访问其管理系统

## 运行机制

![](./docs/assets/232.png)

### AMQP 中的消息路由

AMQP 中消息的路由过程和 Java 开 发者熟悉的 JMS 存在一些差别， AMQP 中增加了 **Exchange** 和 **Binding** 的角色。生产者把消息发布 到 Exchange 上，消息最终到达队列 并被消费者接收，而 Binding 决定交 换器的消息应该发送到那个队列

### **Exchange 类型**

**Exchange**分发消息时根据类型的不同分发策略有区别，目前共四种类型:**direct**、fanout、**topic**、**headers** 。headers 匹配 AMQP 消息的 header 而不是路由键， headers 交换器和 direct 交换器完全一致，但性能差很多，目前几乎用不到了，所以直接 看另外三种类型

#### Direct Exchange

点对点模式：消息中的路由键(routing key)如果和 Binding 中的 binding key 一致， 交换器 就将消息发到对应的队列中。路由键与队 列名完全匹配，如果一个队列绑定到交换 机要求路由键为“dog”，则只转发 routing key 标记为“dog”的消息，不会转发 “dog.puppy”，也不会转发“dog.guard” 等等。它是完全匹配、单播的模式

![](./docs/assets/233.png)

#### Fanout Exchange

发布-订阅广播模式：每个发到 fanout 类型交换器的消息都 会分到所有绑定的队列上去。fanout 交 换器不处理路由键，只是简单的将队列 绑定到交换器上，每个发送到交换器的 消息都会被转发到与该交换器绑定的所 有队列上。很像子网广播，每台子网内 的主机都获得了一份复制的消息。 fanout 类型转发消息是最快的

![](./docs/assets/234.png)

#### Topic Exchange

发布-订阅模式：topic 交换器通过模式匹配分配消息的 路由键属性，将路由键和某个模式进行 匹配，此时队列需要绑定到一个模式上。 它将路由键和绑定键的字符串切分成单 词，这些**单词之间用点隔开**。它同样也 会识别两个通配符:符号“#”和符号 “*”。*#*匹配*0个或多个单词，*匹配一 个单词

![](./docs/assets/235.png)

### 实例

![](./docs/assets/236.png)

## Springboot整合RabbitMQ

1. 订单服务**引入** spring-boot-starter-amqp

   ```xml
   <dependency>
   		<groupId>org.springframework.boot</groupId>
   		<artifactId>spring-boot-starter-amqp</artifactId>
   </dependency>
   ```

引入amqp场景；RabbitAutoConfiguration 就会自动生效

给容器中自动配置了RabbitTemplate、AmqpAdmin、CachingConnectionFactory、RabbitMessagingTemplate

2. 配置文件

   所有的属性都是 spring.rabbitmq

   @ConfigurationProperties(prefix = "spring.rabbitmq")

```properties
# 指定rebbitmq服务器主机
spring.rabbitmq.host=192.168.31.162
#spring.rabbitmq.username=guest  默认值为guest
#spring.rabbitmq.password=guest	 默认值为guest
```

3. 加入注解开启功能 

   ```java
   @EnableRabbit
   ```

## AmqpAdmin管理组件的使用

### Exchange创建

```java
		@Autowired
    AmqpAdmin amqpAdmin;
		/**
     * 1、如何创建Exchange[hello-java-exchange]、Queue、Binding
     * 1）、使用 AmqpAdmin 进行创建
     * 2、如何收发消息
     */
    @Test
    public void createExchange() {
        //amqpAdmin
        //Exchange
        /**
         * DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
         */
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功", "hello-java-exchange");
    }
```

### 队列创建

```java
    @Test
    public void createQueue() {
        //public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
        Queue queue = new Queue("hello-java-queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功", "hello-java-queue");
    }
```

### Binding创建

```java
		@Test
    public void createBinding() {
        //(String destination【目的地】,
        // DestinationType destinationType【目的地类型】,
        // String exchange【交换机】,
        // String routingKey【路由键】,
        //Map<String, Object> arguments【自定义参数】)
        //将exchange指定的交换机和destination目的地进行绑定，使用routingKey作为指定的路由键
        Binding binding = new Binding("hello-java-queue",
                Binding.DestinationType.QUEUE,
                "hello-java-exchange",
                "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}]创建成功", "hello-java-binding");
    }
```

## rabbitTemplate消息发送处理组件的使用

默认的消息转化器是SimpleMessageConverter，对于对象以jdk序列化方式存储，若要以Json方式存储对象，就要自定义消息转换器

```java
@Configuration
public class AmqpConfig {
    @Bean
    public MessageConverter messageConverter() {
        //在容器中导入Json的消息转换器
        return new Jackson2JsonMessageConverter();
    }
}
```

```java
		@Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessageTest() {

        //1、发送消息，如果发送的消息是个对象，我们会使用序列化机制，将对象写出去。对象必须实现Serializable
        String msg = "Hello World!";

        //2、发送的对象类型的消息，可以转成一个json
        for (int i=0;i<10;i++){
            if(i%2 == 0){
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setName("哈哈-"+i);
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity,new CorrelationData(UUID.randomUUID().toString()));
            }else {
                OrderEntity entity = new OrderEntity();
                entity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", entity,new CorrelationData(UUID.randomUUID().toString()));
            }
            log.info("消息发送完成{}");
        }


    }
```

## @RabbitListener 监听消息

监听消息的方法可以有三种参数(不分数量，顺序)

• Object content, Message message, Channel channel

监听消息：使用@RabbitListener；必须有@EnableRabbit
*    @RabbitListener: 类+方法上（监听哪些队列即可）
*    @RabbitHandler：标在方法上（重载区分不同的消息）

- 在回调方法上标注@RabbitListener注解，并设置其属性queues，注册监听队列，当该队列收到消息时，标注方法遍会调用
- 可分别使用Message和保存消息所属对象进行消息接收，若使用Object对象进行消息接收，实际上接收到的也是Message
- 如果知道接收的消息是何种类型的对象，可在方法参数中直接加上该类型参数，也可接收到

```java
@Service
public class BookService {
    @RabbitListener(queues = {"admin.test"})
    public void receive1(Book book){
        System.out.println("收到消息："+book);
    }

    @RabbitListener(queues = {"admin.test"})
    public void receive1(Object object){
        System.out.println("收到消息："+object.getClass());
        //收到消息：class org.springframework.amqp.core.Message
    }
    
    @RabbitListener(queues = {"admin.test"})
    public void receive2(Message message){
        System.out.println("收到消息"+message.getHeaders()+"---"+message.getPayload());
    }
    
    @RabbitListener(queues = {"admin.test"})
    public void receive3(Message message,Book book){
        System.out.println("3收到消息：book:"+book.getClass()+"\n" +
                "message:"+message.getClass());
        //3收到消息：book:class cn.edu.ustc.springboot.bean.Book
		//message: class org.springframework.amqp.core.Message
    }
}
```

- 若消息中含有不同的对象，可以使用`@RabbitHandler`进行分别接收

```java
@RabbitListener(queues = {"admin.test"})
@Service
public class BookService {

    @RabbitHandler
    public void receive4(Book book){
        System.out.println("4收到消息：book:" + book);
    }

    @RabbitHandler
    public void receive5(Student student){
        System.out.println("5收到消息：student:" + student);
    }
```

```java
@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues：声明需要监听的所有队列
     *
     * org.springframework.amqp.core.Message
     *
     * 参数可以写一下类型
     * 1、Message message：原生消息详细信息。头+体
     * 2、T<发送的消息的类型> OrderReturnReasonEntity content；
     * 3、Channel channel：当前传输数据的通道
     *
     * Queue：可以很多人都来监听。只要收到消息，队列删除消息，而且只能有一个收到此消息
     * 场景：
     *    1）、订单服务启动多个；同一个消息，只能有一个客户端收到
     *    2)、 只有一个消息完全处理完，方法运行结束，我们就可以接收到下一个消息
     */
//    @RabbitListener(queues = {"hello-java-queue"})
    @RabbitHandler
    public void recieveMessage(Message message,
                               OrderReturnReasonEntity content,
                               Channel channel) throws InterruptedException {
        //{"id":1,"name":"哈哈","sort":null,"status":null,"createTime":1581144531744}
        System.out.println("接收到消息..."+content);
        byte[] body = message.getBody();
        //消息头属性信息
        MessageProperties properties = message.getMessageProperties();
//        Thread.sleep(3000);
        System.out.println("消息处理完成=>"+content.getName());
        //channel内按顺序自增的。
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("deliveryTag==>"+deliveryTag);

        //签收货物,非批量模式
        try {
            if(deliveryTag%2 == 0){
                //收货
                channel.basicAck(deliveryTag,false);
                System.out.println("签收了货物..."+deliveryTag);
            }else {
                //退货 requeue=false 丢弃  requeue=true 发回服务器，服务器重新入队。
                //long deliveryTag, boolean multiple, boolean requeue
                //签收了货物...6
                channel.basicNack(deliveryTag,false,true);
                //long deliveryTag, boolean requeue
//                channel.basicReject();
                System.out.println("没有签收了货物..."+deliveryTag);
            }

        }catch (Exception e){
            //网络中断
        }

    }

    @RabbitHandler
    public void recieveMessage2(OrderEntity content) throws InterruptedException {
        //{"id":1,"name":"哈哈","sort":null,"status":null,"createTime":1581144531744}
        System.out.println("接收到消息..."+content);
    }

}
```

## RabbitMQ消息确认机制-可靠抵达

- 保证消息不丢失，可靠抵达，可以使用事务消息，性能下降250倍，为此引入确认机制
- **publisher** confirmCallback 确认模式
- **publisher** returnCallback 未投递到 queue 退回模式(失败时触发回调)
- **consumer** ack机制

![](./docs/assets/237.png)

### 可靠抵达-ConfirmCallback

```
1、服务器收到消息就回调
*      1、spring.rabbitmq.publisher-confirms=true
*      2、设置确认回调ConfirmCallback
```

spring.rabbitmq.publisher-confirms=true // 开启发送端确认 

- 在创建 connectionFactory 的时候设置 PublisherConfirms(true) 选项，开启 confirmcallback 
- CorrelationData:用来表示当前消息唯一性
- 消息只要被 broker 接收到就会执行 confirmCallback，如果是 cluster 模式，需要所有 broker 接收到才会调用 confirmCallback
- 被 broker 接收到只能表示 message 已经到达服务器，并不能保证消息一定会被投递 到目标 queue 里。所以需要用到接下来的 returnCallback 

定制RabbitTemplate的confirmcallback

`CorrelationData`为消息的唯一标识，在发送消息时进行构建

```java
@Configuration
public class AmqpConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @PostConstruct  //此类创建完成后调用此方法
    public void initRabbitTemplate() {
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm CorrelationData:"+correlationData+"===>ack:"+ack+"====>cause:"+cause);
            }
//confirm CorrelationData:CorrelationData [id=e3812ddd-f9c3-4f11-9510-d130a8d8f4d6]===>ack:true====>cause:null
//confirm CorrelationData:CorrelationData [id=d9560acc-f745-4a62-87cf-b6420d58706d]===>ack:true====>cause:null
//confirm CorrelationData:CorrelationData [id=4cf4a709-62ac-4966-afb9-90ec6c62a35b]===>ack:true====>cause:null

        });
    }
}
```

### 可靠抵达-ReturnCallback

```
2、消息正确抵达队列进行回调
*      1、 spring.rabbitmq.publisher-returns=true // 开启发送端抵达队列的确认
*          spring.rabbitmq.template.mandatory=true // 只要抵达队列，以异步发送优先回调
*      2、设置确认回调ReturnCallback
```

spring.rabbitmq.publisher-returns=true

spring.rabbitmq.template.mandatory=true

- confrim 模式只能保证消息到达 broker，不能保证消息准确投递到目标 queue 里。在有 些业务场景下，我们需要保证消息一定要投递到目标 queue 里，此时就需要用到 return 退回模式。

- 这样如果未能投递到目标 queue 里将调用 returnCallback ，可以记录下详细到投递数 据，定期的巡检或者自动纠错都需要这些数据

- 如果是 cluster 模式，只要有一个投递不成功也会调用 returnCallback

  // * 只要消息没有投递给指定的队列，就触发这个失败回调

```java
rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("return callback...message:"+message+"===>replycode:"+replyCode+"===>replyText:"+replyText+"===>exchange:"+exchange+"===>routingKey:"+routingKey);
    }
});
```

整个rabbitmq的配置

```java
@Configuration
public class MyRabbitConfig {
    RabbitTemplate rabbitTemplate;

    @Bean
    @Primary
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(messageConverter());
        initRabbitTemplate();
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        //在容器中导入Json的消息转换器
        return new Jackson2JsonMessageConverter();
    }

    public void initRabbitTemplate(){
        //设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 只要消息抵达Broker就ack=true
             * @param correlationData 当前消息的唯一关联数据（这个是消息的唯一id）
             * @param ack  消息是否成功收到
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                /**
                 * 1、做好消息确认机制（pulisher，consumer【手动ack】）
                 * 2、每一个发送的消息都在数据库做好记录。定期将失败的消息再次发送一遍
                 */
                //服务器收到了
                //修改消息的状态
                System.out.println("confirm...correlationData["+correlationData+"]==>ack["+ack+"]==>cause["+cause+"]");
            }
        });
        //设置消息抵达队列的确认回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有投递给指定的队列，就触发这个失败回调
             * @param message   投递失败的消息详细信息
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容
             * @param exchange  当时这个消息发给哪个交换机
             * @param routingKey 当时这个消息用哪个路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                //报错误了。修改数据库当前消息的状态->错误。
                System.out.println("Fail Message["+message+"]==>replyCode["+replyCode+"]==>replyText["+replyText+"]===>exchange["+exchange+"]===>routingKey["+routingKey+"]");
            }
        });
    }
}

```

### 可靠抵达-消费端确认Ack机制

消费端确认（保证每个消息被正确消费，此时才可以broker删除这个消息）

- 消费者获取到消息，成功处理，可以回复Ack给Broker
  - basic.ack用于肯定确认;broker将移除此消息
  - basic.nack用于否定确认;可以指定broker是否丢弃此消息，可以批量
  - basic.reject用于否定确认;同上，但不能批量

- 默认自动ack，消息被消费者收到，就会从broker的queue中移除
- queue无消费者，消息依然会被存储，直到消费者消费
- 消费者收到消息，默认会自动ack。但是如果无法确定此消息是否被处理完成，或者成功处理。我们可以开启手动ack模式
  - 消息处理成功，ack()，接受下一个消息，此消息broker就会移除
  - 消息处理失败，nack()/reject()，重新发送给其他人进行处理，或者容错处理后ack
  - 消息一直没有调用ack/nack方法，broker认为此消息正在被处理，不会投递给别人，此时客户 端断开，消息不会被broker移除，会投递给别人

```
1、默认是自动确认的，只要消息接收到，客户端会自动确认，服务端就会移除这个消息

问题：
我们收到很多消息，自动回复给服务器ack，只有一个消息处理成功，宕机了。就会发生消息丢失；
消费者手动确认模式。只要我们没有明确告诉MQ，货物被签收。没有Ack，
消息就一直是unacked状态。即使Consumer宕机。消息不会丢失，会重新变为Ready，下一次有新的Consumer连接进来就发给他

spring.rabbitmq.listener.simple.acknowledge-mode=manual 手动签收

2、如何签收:
channel.basicAck(deliveryTag,false);签收；业务成功完成就应该签收
channel.basicNack(deliveryTag,false,true);拒签；业务失败，拒签
```

在默认情况下，消息如果消费到一半，服务器宕机，剩下的消息就会默认全部确认，会造成消息丢失，因此需要引入手动确认模式

```properties
# 消费端手动ack消息
spring.rabbitmq.listener.simple.acknowledge-mode=manual
```

只要没有明确通知服务器ack，消息就不会确认收货，可以通过`basicAck()`进行确认收货

```java
@RabbitHandler
public void receive4(Book book, Message message,Channel channel) throws IOException {
    try {
        Thread.sleep(100);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    System.out.println("4收到消息：book:" + book);
    //deliveryTag在通道内按顺序自增
    long deliveryTag = message.getMessageProperties().getDeliveryTag();
    System.out.println(deliveryTag);
    //通过deliveryTag进行确认，false表示不批量确认
    channel.basicAck(deliveryTag,false);
}
```

此外还可以使用`basicNack()`和`basicReject()`进行拒绝收货

# 订单服务

## Session登陆共享

订单生成 -> 支付订单 -> 卖家发货 -> 确认收货 -> 交易成功

搭建订单服务session共享环境

```xml
				<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>io.lettuce</groupId>
                    <artifactId>lettuce-core</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
        </dependency>
				<dependency>
						<groupId>org.springframework.session</groupId>
						<artifactId>spring-session-data-redis</artifactId>
				</dependency>
```

```yaml
spring:
  redis:
    host: 47.103.8.41
    port: 6379
  session:
    store-type: redis
```

```java
@Configuration
public class SessionConfig {
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();

        cookieSerializer.setDomainName("eshopblvd.com");
        cookieSerializer.setCookieName("ESHOPBLVDSESSION");

        return cookieSerializer;
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
```

```java
@EnableRedisHttpSession
```



## 订单的基本概念

电商系统涉及到 3 流，分别时信息流（商品信息、优惠信息），资金流（退款、付款），物流（发货、退货 ），而订单系统作为中枢将三者有机的集 合起来。 订单模块是电商系统的枢纽，在订单这个环节上需求获取多个模块的数据和信息，同时对这 些信息进行加工处理后流向下个环节，这一系列就构成了订单的信息流通

## 订单构成

#### 基本组成

![](./docs/assets/239.png)

#### 数据库结构

![](./docs/assets/240.png)

#### 用户信息

用户信息包括用户账号、用户等级、用户的收货地址、收货人、收货人电话等组成，用户账 户需要绑定手机号码，但是用户绑定的手机号码不一定是收货信息上的电话。用户可以添加 多个收货信息，用户等级信息可以用来和促销系统进行匹配，获取商品折扣，同时用户等级 还可以获取积分的奖励等

#### 订单基础信息

订单基础信息是订单流转的核心，其包括订单类型、父/子订单、订单编号、订单状态、订 单流转的时间等

1. 订单类型包括实体商品订单和虚拟订单商品等，这个根据商城商品和服务类型进行区 分。

2. 同时订单都需要做父子订单处理，之前在初创公司一直只有一个订单，没有做父子订 单处理后期需要进行拆单的时候就比较麻烦，尤其是多商户商场，和不同仓库商品的时候， 父子订单就是为后期做拆单准备的。 
3. 订单编号不多说了，需要强调的一点是父子订单都需要有订单编号，需要完善的时候 可以对订单编号的每个字段进行统一定义和诠释。
4. 订单状态记录订单每次流转过程，后面会对订单状态进行单独的说明。
5. 订单流转时间需要记录下单时间，支付时间，发货时间，结束时间/关闭时间等等

#### 商品信息

商品信息从商品库中获取商品的 SKU 信息、图片、名称、属性规格、商品单价、商户信息 等，从用户下单行为记录的用户下单数量，商品合计价格等。

#### 优惠信息 

优惠信息记录用户参与的优惠活动，包括优惠促销活动，比如满减、满赠、秒杀等，用户使 用的优惠券信息，优惠券满足条件的优惠券需要默认展示出来，具体方式已在之前的优惠券 篇章做过详细介绍，另外还虚拟币抵扣信息等进行记录。

为什么把优惠信息单独拿出来而不放在支付信息里面呢? 因为优惠信息只是记录用户使用的条目，而支付信息需要加入数据进行计算，所以做为区分。

#### 支付信息 

1. 支付流水单号，这个流水单号是在唤起网关支付后支付通道返回给电商业务平台的支 付流水号，财务通过订单号和流水单号与支付通道进行对账使用。
2. 支付方式用户使用的支付方式，比如微信支付、支付宝支付、钱包支付、快捷支付等。 支付方式有时候可能有两个——余额支付+第三方支付。
3. 商品总金额，每个商品加总后的金额;运费，物流产生的费用;优惠总金额，包括促 销活动的优惠金额，优惠券优惠金额，虚拟积分或者虚拟币抵扣的金额，会员折扣的金额等 之和;实付金额，用户实际需要付款的金额。

用户实付金额=商品总金额+运费-优惠总金额 

#### 物流信息

物流信息包括配送方式，物流公司，物流单号，物流状态，物流状态可以通过第三方接口来 获取和向用户展示物流每个状态节点。

## 订单状态

#### 待付款

用户提交订单后，订单进行预下单，目前主流电商网站都会唤起支付，便于用户快速完成支 付，需要注意的是待付款状态下可以对库存进行锁定，锁定库存需要配置支付超时时间，超 时后将自动取消订单，订单变更关闭状态。

#### 已付款**/**待发货

用户完成订单支付，订单系统需要记录支付时间，支付流水单号便于对账，订单下放到 WMS系统，仓库进行调拨，配货，分拣，出库等操作。

#### 待收货**/**已发货

仓储将商品出库后，订单进入物流环节，订单系统需要同步物流信息，便于用户实时知悉物 品物流状态

#### 已完成

用户确认收货后，订单交易完成。后续支付侧进行结算，如果订单存在问题进入售后状态

#### 已取消

付款之前取消订单。包括超时未付款或用户商户取消订单都会产生这种订单状态。

#### 售后中

用户在付款后申请退款，或商家发货后用户申请退换货

售后也同样存在各种状态，当发起售后申请后生成售后订单，售后订单状态为待审核，等待 商家审核，商家审核通过后订单状态变更为待退货，等待用户将商品寄回，商家收货后订单状态更新为待退款状态，退款到用户原账户后订单状态更新为售后成功。

## 订单流程

#### 订单流程概述

订单流程是指从订单产生到完成整个流转的过程，从而行程了一套标准流程规则。而不同的 产品类型或业务类型在系统中的流程会千差万别，比如上面提到的线上实物订单和虚拟订单 的流程，线上实物订单与 O2O 订单等，所以需要根据不同的类型进行构建订单流程。 不管类型如何订单都包括正向流程和逆向流程，对应的场景就是购买商品和退换货流程，正 向流程就是一个正常的网购步骤:订单生成–>支付订单–>卖家发货–>确认收货–>交易成功。 而每个步骤的背后，订单是如何在多系统之间交互流转 的，可概括如下图

![](./docs/assets/241.png)

#### 订单创建与支付

1. 订单创建前需要预览订单，选择收货信息等
2. 订单创建需要锁定库存，库存有才可创建，否则不能创建
3. 订单创建后超时未支付需要解锁库存
4. 支付成功后，需要进行拆单，根据商品打包方式，所在仓库，物流等进行拆单
5. 支付的每笔流水都需要记录，以待查账
6. 订单创建，支付成功等状态都需要给MQ发送消息，方便其他系统感知订阅

#### 逆向流程

1. 修改订单，用户没有提交订单，可以对订单一些信息进行修改，比如配送信息，优惠信息，及其他一些订单可修改范围的内容，此时只需对数据进行变更即可。
2. 订单取消，用户主动取消订单和用户超时未支付，两种情况下订单都会取消订 单，而超时情况是系统自动关闭订单，所以在订单支付的响应机制上面要做支付的限时处理，尤其是在前面说的下单减库存的情形下面，可以保证快速的释放库存。 另外需要需要处理的是促销优惠中使用的优惠券，权益等视平台规则，进行相应补 回给用户。
3. 退款，在待发货订单状态下取消订单时，分为缺货退款和用户申请退款。如果是 全部退款则订单更新为关闭状态，若只是做部分退款则订单仍需进行进行，同时生 成一条退款的售后订单，走退款流程。退款金额需原路返回用户的账户
4. 发货后的退款，发生在仓储货物配送，在配送过程中商品遗失，用户拒收，用户 收货后对商品不满意，这样情况下用户发起退款的售后诉求后，需要商户进行退款 的审核，双方达成一致后，系统更新退款状态，对订单进行退款操作，金额原路返 回用户的账户，同时关闭原订单数据。仅退款情况下暂不考虑仓库系统变化。如果 发生双方协调不一致情况下，可以申请平台客服介入。在退款订单商户不处理的情 况下，系统需要做限期判断，比如 5 天商户不处理，退款单自动变更同意退款。

## 订单登录拦截

因为订单系统必然涉及到用户信息，因此进入订单系统的请求必须是已经登录的，所以我们需要通过拦截器对未登录订单请求进行拦截

```java
@Component
public class LoginInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberResponseVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberResponseVo memberResponseVo = (MemberResponseVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (memberResponseVo != null) {
            loginUser.set(memberResponseVo);
            return true;
        }else {
            session.setAttribute("msg","请先登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}

@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");
    }
}
```

## 订单确认页（结算页）

![](./docs/assets/242.png)

![](./docs/assets/243.png)

可以发现订单结算页，包含以下信息:

1. 收货人信息:有更多地址，即有多个收货地址，其中有一个默认收货地址
2. 支付方式:货到付款、在线支付，不需要后台提供
3. 送货清单:配送方式(不做)及商品列表(根据购物车选中的 skuId 到数据库中查询)
4. 发票:不做
5. 优惠:查询用户领取的优惠券(不做)及可用积分(京豆)

### 新增【接口】获取订单确认页信息

#### 模型抽取

![](./docs/assets/245.png)

跳转到确认页时需要携带的数据模型

```java
public class OrderConfirmVo {

    @Getter
    @Setter
    /** 会员收获地址列表 **/
    private List<MemberAddressVo> memberAddressVos;

    @Getter @Setter
    /** 所有选中的购物项 **/
    private List<OrderItemVo> items;

    /** 发票记录 **/
    @Getter @Setter
    /** 优惠券（会员积分） **/
    private Integer integration;

    /** 防止重复提交的令牌 **/
    @Getter @Setter
    private String orderToken;

    @Getter @Setter
    Map<Long,Boolean> stocks;

    public Integer getCount() {
        Integer count = 0;
        if (items != null && items.size() > 0) {
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }


    /** 订单总额 **/
    //BigDecimal total;
    //计算订单总额
    public BigDecimal getTotal() {
        BigDecimal totalNum = BigDecimal.ZERO;
        if (items != null && items.size() > 0) {
            for (OrderItemVo item : items) {
                //计算当前商品的总价格
                BigDecimal itemPrice = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                //再计算全部商品的总价格
                totalNum = totalNum.add(itemPrice);
            }
        }
        return totalNum;
    }


    /** 应付价格 **/
    //BigDecimal payPrice;
    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
```

#### 数据获取

- 查询购物项、库存和收货地址都要调用远程服务，串行会浪费大量时间，因此我们使用`CompletableFuture`进行异步编排
- 可能由于延迟，订单提交按钮可能被点击多次，为了防止重复提交的问题，我们在返回订单确认页时，在`redis`中生成一个随机的令牌，过期时间为30min，提交的订单会携带这个令牌，我们将会在订单提交的处理页面核验此令牌

```java
@RequestMapping("/toTrade")
public String toTrade(Model model) {
    OrderConfirmVo confirmVo = orderService.confirmOrder();
    model.addAttribute("confirmOrder", confirmVo);
    return "confirm";
}

 @Override
    public OrderConfirmVo confirmOrder() {
        MemberResponseVo memberResponseVo = LoginInterceptor.loginUser.get();
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> itemAndStockFuture = CompletableFuture.supplyAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //1. 查出所有选中购物项
            List<OrderItemVo> checkedItems = cartFeignService.getCheckedItems();
            confirmVo.setItems(checkedItems);
            return checkedItems;
        }, executor).thenAcceptAsync((items) -> {
            //4. 库存
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            //skuId为key,是否有库存为value
            Map<Long, Boolean> hasStockMap = wareFeignService.getSkuHasStocks(skuIds).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
            confirmVo.setStocks(hasStockMap);
        }, executor);

        //2. 查出所有收货地址
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            List<MemberAddressVo> addressByUserId = memberFeignService.getAddressByUserId(memberResponseVo.getId());
            confirmVo.setMemberAddressVos(addressByUserId);
        }, executor);

        //3. 积分
        confirmVo.setIntegration(memberResponseVo.getIntegration());

        //5. 总价自动计算
        //6. 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);
        try {
            CompletableFuture.allOf(itemAndStockFuture, addressFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return confirmVo;
    }
```

#### 远程调用丢失请求头问题

![](./docs/assets/244.png)

`feign`远程调用的请求头中没有含有`JSESSIONID`的`cookie`，所以也就不能得到服务端的`session`数据，cart认为没登录，获取不了用户信息

[![img](https://github.com/NiceSeason/gulimall-learning/raw/master/docs/images/Snipaste_2020-10-10_21-36-18.png)](https://github.com/NiceSeason/gulimall-learning/blob/master/docs/images/Snipaste_2020-10-10_21-36-18.png)

[![img](https://github.com/NiceSeason/gulimall-learning/raw/master/docs/images/Snipaste_2020-10-10_21-47-54.png)](https://github.com/NiceSeason/gulimall-learning/blob/master/docs/images/Snipaste_2020-10-10_21-47-54.png)

```java
Request targetRequest(RequestTemplate template) {
  for (RequestInterceptor interceptor : requestInterceptors) {
    interceptor.apply(template);
  }
  return target.apply(template);
}
```

但是在`feign`的调用过程中，会使用容器中的`RequestInterceptor`对`RequestTemplate`进行处理，因此我们可以通过向容器中导入定制的`RequestInterceptor`为请求加上`cookie`。

```java
public class GuliFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //1. 使用RequestContextHolder拿到老请求的请求数据
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    if (request != null) {
                        //2. 将老请求得到cookie信息放到feign请求上
                        String cookie = request.getHeader("Cookie");
                        template.header("Cookie", cookie);
                    }
                }
            }
        };
    }
}
```

- `RequestContextHolder`为SpingMVC中共享`request`数据的上下文，底层由`ThreadLocal`实现

经过`RequestInterceptor`处理后的请求如下，已经加上了请求头的`Cookie`信息

[![img](https://github.com/NiceSeason/gulimall-learning/raw/master/docs/images/Snipaste_2020-10-10_21-55-45.png)](https://github.com/NiceSeason/gulimall-learning/blob/master/docs/images/Snipaste_2020-10-10_21-55-45.png)

#### Feign异步情况丢失上下文问题

[![img](https://github.com/NiceSeason/gulimall-learning/raw/master/docs/images/Snipaste_2020-10-10_22-08-32.png)](https://github.com/NiceSeason/gulimall-learning/blob/master/docs/images/Snipaste_2020-10-10_22-08-32.png)

- 由于`RequestContextHolder`使用`ThreadLocal`共享数据，所以在开启异步时获取不到老请求的信息，自然也就无法共享`cookie`了

在这种情况下，我们需要在开启异步的时候将老请求的`RequestContextHolder`的数据设置进去

[![img](https://github.com/NiceSeason/gulimall-learning/raw/master/docs/images/Snipaste_2020-10-10_22-13-47.png)](https://github.com/NiceSeason/gulimall-learning/blob/master/docs/images/Snipaste_2020-10-10_22-13-47.png)

#### 运费收件信息获取

数据封装

```java
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
```

在页面将选中地址的id传给请求

```java
@RequestMapping("/fare/{addrId}")
public FareVo getFare(@PathVariable("addrId") Long addrId) {
    return wareInfoService.getFare(addrId);
}

@Override
public FareVo getFare(Long addrId) {
    FareVo fareVo = new FareVo();
    R info = memberFeignService.info(addrId);
    if (info.getCode() == 0) {
        MemberAddressVo address = info.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        fareVo.setAddress(address);
        String phone = address.getPhone();
        //取电话号的最后两位作为邮费
        String fare = phone.substring(phone.length() - 2, phone.length());
        fareVo.setFare(new BigDecimal(fare));
    }
    return fareVo;
}
```

RPC调用，session传递解决方案

https://blog.csdn.net/syslijian/article/details/106466933

如何根据sessionID获取session

https://blog.csdn.net/zhong12270107/article/details/80838138

#### 代码实现

```java
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ThreadPoolExecutor executor;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.member.MemberService", check = false)
    private MemberService memberService;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.cart.CartService", check = false)
    private CartService cartService;

    @Override
    public OrderConfirmVO getOrderConfirmData(Long addrId) throws ExecutionException, InterruptedException {
        OrderConfirmVO orderConfirm = new OrderConfirmVO();
        MemberTO memberTO = LoginUserInterceptor.loginUser.get();
        // 1. 异步RPC调用会员服务获取登陆用户的地址列表
        CompletableFuture<Void> getAddressTask = CompletableFuture.runAsync(() -> {
            CommonResponse commonResponse = CommonResponse.convertToResp(memberService.getAddress(memberTO.getId()));
            List<MemberAddressVO> data = commonResponse.getData(new TypeReference<List<MemberAddressVO>>() {
            });
            // 1.2 根据addrId设置选中的地址，否则就设置默认地址
            for (MemberAddressVO address: data) {
                if (address.getDefaultStatus() && addrId == null) {
                    orderConfirm.setSelectedAddress(address);
                    break;
                }
                if (addrId != null && address.getId() == addrId) {
                    orderConfirm.setSelectedAddress(address);
                    break;
                }
            }
            orderConfirm.setAddresses(data);
        }, executor);

        // 2. 异步RPC调用购物车服务获取当前用户选中的购物项（包括库存）
        CompletableFuture<Void> orderItemsTask = CompletableFuture.runAsync(() -> {
            // TODO: 搞个拦截器透传sessionId过去
            CommonResponse resp = CommonResponse.convertToResp(cartService.getUserCartItems(memberTO.getId()));
            if (resp.getCode() == HttpStatus.SC_OK) {
                List<OrderItemVO> data = resp.getData(new TypeReference<List<OrderItemVO>>() {
                });
                orderConfirm.setItems(data);
            } else {
                log.error("调用RPC购物车服务失败，未传userId");
            }

        }, executor);

        // 3. 积分信息
        orderConfirm.setIntegration(memberTO.getIntegration());

        // 4. 查询运费
        // TODO: 调用ware服务根据地址去计算运费，这里先不写了
        orderConfirm.setFare(new BigDecimal(5.00));

        CompletableFuture.allOf(getAddressTask, orderItemsTask).get();
        return orderConfirm;
    }
}
```

#### 返回结果

```json
{
    "msg": "success",
    "code": 200,
    "data": {
        "addresses": [
            {
                "id": 1,
                "memberId": 3,
                "name": "收货人1",
                "phone": "139xxxxxxxx",
                "postCode": "200123",
                "province": "上海",
                "city": "上海市",
                "region": "浦东新区",
                "detailAddress": "陆家嘴",
                "areacode": "10",
                "defaultStatus": true
            },
            {
                "id": 2,
                "memberId": 3,
                "name": "收货人2",
                "phone": "136xxxxxxxx",
                "postCode": "200123",
                "province": "上海",
                "city": "上海市",
                "region": "黄浦区",
                "detailAddress": "南京西路",
                "areacode": "10",
                "defaultStatus": false
            }
        ],
        "items": [
            {
                "skuId": 11,
                "title": "Apple iPhone13 绿色 512GB",
                "image": "https://eshopblvd.oss-cn-shanghai.aliyuncs.com/2022-03-26/iphone-13-green-sku.png",
                "skuAttr": [
                    "颜色: 绿色",
                    "内存: 512GB"
                ],
                "price": 9399.0000,
                "count": 30,
                "totalPrice": 281970.0000,
                "weight": null,
                "hasStock": true
            },
            {
                "skuId": 9,
                "title": "Apple iPhone13 绿色 128GB",
                "image": "https://eshopblvd.oss-cn-shanghai.aliyuncs.com/2022-03-26/iphone-13-green-sku.png",
                "skuAttr": [
                    "颜色: 绿色",
                    "内存: 128GB"
                ],
                "price": 6999.0000,
                "count": 20,
                "totalPrice": 139980.0000,
                "weight": null,
                "hasStock": true
            },
            {
                "skuId": 13,
                "title": "Apple iPhone13 蓝色 256GB",
                "image": "https://eshopblvd.oss-cn-shanghai.aliyuncs.com/2022-03-26/iphone-13-green-sku.png",
                "skuAttr": [
                    "颜色: 蓝色",
                    "内存: 256GB"
                ],
                "price": 7799.0000,
                "count": 30,
                "totalPrice": 233970.0000,
                "weight": null,
                "hasStock": true
            }
        ],
        "integration": 10000,
        "count": 80,
        "totalPrice": 655920.0000,
        "fare": 5,
        "payPrice": 655815.0000,
        "selectedAddress": {
            "id": 1,
            "memberId": 3,
            "name": "收货人1",
            "phone": "139xxxxxxxx",
            "postCode": "200123",
            "province": "上海",
            "city": "上海市",
            "region": "浦东新区",
            "detailAddress": "陆家嘴",
            "areacode": "10",
            "defaultStatus": true
        },
        "orderToken": null
    }
}
```





## 接口幂等性

































































