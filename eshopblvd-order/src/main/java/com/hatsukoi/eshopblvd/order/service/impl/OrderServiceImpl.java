package com.hatsukoi.eshopblvd.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.pagehelper.PageHelper;
import com.hatsukoi.eshopblvd.api.cart.CartService;
import com.hatsukoi.eshopblvd.api.member.MemberService;
import com.hatsukoi.eshopblvd.api.product.ProductRpcService;
import com.hatsukoi.eshopblvd.exception.order.InvalidPriceException;
import com.hatsukoi.eshopblvd.exception.ware.NoStockException;
import com.hatsukoi.eshopblvd.order.config.AlipayTemplate;
import com.hatsukoi.eshopblvd.order.constant.OrderConstant;
import com.hatsukoi.eshopblvd.order.constant.OrderStatus;
import com.hatsukoi.eshopblvd.order.dao.OrderItemMapper;
import com.hatsukoi.eshopblvd.order.dao.OrderMapper;
import com.hatsukoi.eshopblvd.order.dao.PaymentInfoMapper;
import com.hatsukoi.eshopblvd.order.entity.*;
import com.hatsukoi.eshopblvd.exception.order.OrderTokenException;
import com.hatsukoi.eshopblvd.order.interceptor.LoginUserInterceptor;
import com.hatsukoi.eshopblvd.order.service.OrderService;
import com.hatsukoi.eshopblvd.order.vo.*;
import com.hatsukoi.eshopblvd.to.*;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.vo.MemberAddressVO;
import com.hatsukoi.eshopblvd.vo.OrderItemVO;
import com.hatsukoi.eshopblvd.ware.service.WareSkuRPCService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.http.HttpStatus;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/05/09 Mon 1:25 AM
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ThreadPoolExecutor executor;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.member.MemberService", check = false)
    private MemberService memberService;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.cart.CartService", check = false)
    private CartService cartService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ThreadLocal<OrderSubmitVO> orderSubmitThreadLocal = new ThreadLocal<>();

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.ware.service.WareSkuRPCService", check = false)
    private WareSkuRPCService wareSkuRPCService;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.product.ProductRpcService", check = false)
    private ProductRpcService productRpcService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

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

        // 5. 防重令牌，存到redis中
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberTO.getId(), token, 30, TimeUnit.MINUTES);
        orderConfirm.setOrderToken(token);

        CompletableFuture.allOf(getAddressTask, orderItemsTask).get();
        return orderConfirm;
    }

    @Transactional // 加上本地事务（由于seata不适合高并发场景，这里不用@GlobalTransactional）
    @Override
    public Order submitOrder(OrderSubmitVO orderSubmit) {
        orderSubmitThreadLocal.set(orderSubmit);
        // 校验防重令牌
        Long result = validateToken(orderSubmit);
        if (result != 0L) {
            // 令牌验证通过
            // 创建订单
            Order order = buildOrder();
            // 创建订单项
            List<OrderItem> orderItems = buildOrderItems(order);
            // 计算订单价格以及促销、优惠、积分等信息
            computePriceAndExtraData(order, orderItems);
            // 验价
            if (Math.abs(order.getPayAmount().subtract(orderSubmit.getPayPrice()).doubleValue()) < 0.01) {
                // 验价成功
                // 将订单和订单项数据插入数据库
                insertDataToDB(order, orderItems);
                // 锁库存
                CommonResponse resp = wareSkuLock(order, orderItems);
                if (resp.getCode() == HttpStatus.SC_OK) {
                    // 订单创建成功发消息
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order);
                    // TODO: 清除购物车中的商品
                    // TODO: 扣减积分
                    return order;
                } else {
                    throw new NoStockException(resp.getMsg());
                }
            } else {
                // 验价失败，本地事务回滚
                throw new InvalidPriceException();
            }
        } else {
            // 令牌验证失败，本地事务回滚
            throw new OrderTokenException();
        }
    }

    /**
     * 时间到了，关订单
     * @param order
     */
    @Override
    public void closeOrder(Order order) {
        // 由于要保证幂等性，因此要查询最新的订单状态判断是否需要关单
        Order newOrder = orderMapper.selectByPrimaryKey(order.getId());
        if (newOrder.getStatus() == OrderStatus.CREATE_NEW.getCode()) {
            // 如果订单还是新建状态就关单，更新该订单的状态
            newOrder.setStatus(OrderStatus.CANCLED.getCode());
            orderMapper.updateByPrimaryKeySelective(newOrder);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(order, orderTo);
            // 关闭订单后也需要解锁库存，因此发送消息进行库存服务对应的解锁
            try {
                //TODO 保证消息一定会发送出去，每一个消息都可以做好日志记录（给数据库保存每一个消息的详细信息）。
                //TODO 定期扫描数据库将失败的消息再发送一遍；
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
            } catch (Exception e) {
                //TODO 将没法送成功的消息进行重试发送。
            }
        }
    }

    @Override
    public PayVo buildPayData(String orderSn) {
        PayVo payVo = new PayVo();
        // 先根据订单号来获取订单
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andOrderSnEqualTo(orderSn);
        List<Order> orders = orderMapper.selectByExample(orderExample);
        Order order = orders.get(0);
        // 应付价格小数位进到2位
        BigDecimal price = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(price.toString());
        payVo.setOut_trade_no(order.getOrderSn());
        return payVo;
    }

    /**
     * 分页查询用户订单
     * @param params
     * @return
     */
    @Override
    public CommonPageInfo<OrderVo> getOrderList(Map<String, Object> params) {
        // 分页参数
        int pageNum = 1;
        int pageSize = 10;
        if (params.get("page") != null) {
            pageNum = Integer.parseInt(params.get("page").toString());
        }
        if (params.get("limit") != null) {
            pageSize = Integer.parseInt(params.get("limit").toString());
        }

        // 开启分页插件
        PageHelper.startPage(pageNum, pageSize);

        MemberTO memberTO = LoginUserInterceptor.loginUser.get();

        // 查询登陆用户的所有订单
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andMemberIdEqualTo(memberTO.getId());
        List<Order> orders = orderMapper.selectByExample(orderExample);

        List<OrderVo> collect = orders.stream().map(order -> {
            OrderVo orderVo = new OrderVo();
            OrderItemExample orderItemExample = new OrderItemExample();
            orderItemExample.createCriteria().andOrderSnEqualTo(order.getOrderSn());
            List<OrderItem> orderItems = orderItemMapper.selectByExample(orderItemExample);
            orderVo.setItems(orderItems);
            return orderVo;
        }).collect(Collectors.toList());

        return CommonPageInfo.convertToCommonPage(collect);
    }

    /**
     * 处理支付宝异步通知交易结果
     * @param pay
     */
    @Override
    public void handleAlipay(PayAsyncVo pay, HttpServletRequest request) throws AlipayApiException {
        boolean signVerified = signVerify(request);
        if (signVerified) {
            log.info("验证签名成功");
            savePaymentInfo(pay);
            // 如果通知支付成功的话，把订单的状态更新为已付款
            if (pay.getTrade_status().equals("TRADE_SUCCESS") || pay.getTrade_status().equals("TRADE_FINISHED")) {
                // UPDATE `oms_order` SET `status`=#{code} WHERE order_sn=#{out_trade_no}
                orderMapper.updateOrderStatus(pay.getOut_trade_no(), OrderStatus.PAYED.getCode());
            }
        } else {
            log.info("验证签名失败");
            throw new RuntimeException("验证签名失败");
        }
    }

    /**
     * 保存交易流水
     * @param pay
     */
    private void savePaymentInfo(PayAsyncVo pay) {
        // 保存交易流水「oms_payment_info」
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setAlipayTradeNo(pay.getTrade_no());
        paymentInfo.setOrderSn(pay.getOut_trade_no());
        paymentInfo.setPaymentStatus(pay.getTrade_status());
        paymentInfo.setCallbackTime(pay.getNotify_time());
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    private boolean signVerify(HttpServletRequest request) throws AlipayApiException {
        // 验签
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParam = request.getParameterMap();
        for (Iterator<String> iter = requestParam.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParam.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        // 调用SDK验证签名
        return AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type());
    }

    /**
     * 校验防重令牌
     * @param orderSubmit
     * @return
     */
    private Long validateToken(OrderSubmitVO orderSubmit) {
        MemberTO memberTO = LoginUserInterceptor.loginUser.get();

        // 验证防重令牌 - redis luna脚本原子操作：验证通过后删除令牌
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = orderSubmit.getOrderToken();
        String redisKey = OrderConstant.USER_ORDER_TOKEN_PREFIX + memberTO.getId(); // 防重令牌在redis中的key
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(redisKey), orderToken);

        return result;
    }

    /**
     * 锁定库存
     * @param order
     * @param orderItems
     */
    private CommonResponse wareSkuLock(Order order, List<OrderItem> orderItems) {
        // 构建锁库存TO
        WareSkuLockTo wareSkuLock = new WareSkuLockTo();
        wareSkuLock.setOrderSn(order.getOrderSn());
        // 所有要被锁库存的sku
        List<OrderItemVO> lock = orderItems.stream().map(item -> {
            OrderItemVO orderItem = new OrderItemVO();
            orderItem.setSkuId(item.getSkuId());
            orderItem.setCount(item.getSkuQuantity());
            orderItem.setTitle(item.getSkuName());
            return orderItem;
        }).collect(Collectors.toList());
        wareSkuLock.setLocks(lock);

        // 调远程服务锁库存
        return CommonResponse.convertToResp(wareSkuRPCService.wareSkuLock(wareSkuLock));
    }

    /**
     * 将订单和订单项数据保存数据库
     * @param order
     * @param orderItems
     */
    private void insertDataToDB(Order order, List<OrderItem> orderItems) {
        orderMapper.insertSelective(order);
        orderItemMapper.batchInsert(orderItems);
    }

    // 促销、优惠、积分
    private void computePriceAndExtraData(Order order, List<OrderItem> orderItems) {
        BigDecimal promotion = new BigDecimal("0.00");
        BigDecimal coupon = new BigDecimal("0.00");
        BigDecimal integration = new BigDecimal("0.00");
        BigDecimal total = new BigDecimal("0.00");
        Integer gift = 0;
        Integer growth = 0;

        for (OrderItem orderItem : orderItems) {
            integration = integration.add(orderItem.getIntegrationAmount());
            coupon = coupon.add(orderItem.getCouponAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            total = total.add(orderItem.getRealAmount());
            gift = gift + orderItem.getGiftIntegration();
            growth = growth + orderItem.getGiftGrowth();
        }

        // 订单总额
        order.setTotalAmount(total);
        // 应付总额 - 订单总额加上运费
        order.setPayAmount(total.add(order.getFreightAmount()));

        // 积分优惠相关信息
        order.setPromotionAmount(promotion);
        order.setCouponAmount(coupon);
        order.setIntegrationAmount(integration);
        order.setIntegration(gift);
        order.setGrowth(growth);
    }

    /**
     * 构建订单项列表
     * @return
     */
    private List<OrderItem> buildOrderItems(Order order) {
        MemberTO memberTO = LoginUserInterceptor.loginUser.get();
        CommonResponse resp = CommonResponse.convertToResp(cartService.getUserCartItems(memberTO.getId()));
        // 获取了购物车的所有选中购物项，且价格和库存是实时更新的
        List<OrderItemVO> data = resp.getData(new TypeReference<List<OrderItemVO>>() {
        });
        if (data != null && data.size() > 0) {
            List<OrderItem> collect = data.stream().map(item -> {
                OrderItem orderItem = new OrderItem();

                // 订单号
                orderItem.setOrderSn(order.getOrderSn());

                // spu信息
                CommonResponse spuResp = CommonResponse.convertToResp(productRpcService.getSpuInfoBySkuId(item.getSkuId()));
                SpuInfoTo spuInfoTo = spuResp.getData(new TypeReference<SpuInfoTo>() {
                });
                orderItem.setSpuId(spuInfoTo.getId());
                orderItem.setSpuName(spuInfoTo.getSpuName());
                orderItem.setSpuBrand(spuInfoTo.getBrandId().toString());
                orderItem.setCategoryId(spuInfoTo.getCatalogId());

                // sku信息
                orderItem.setSkuId(item.getSkuId());
                orderItem.setSkuName(item.getTitle());
                orderItem.setSkuPic(item.getImage());
                orderItem.setSkuPrice(item.getPrice());
                orderItem.setSkuQuantity(item.getCount());
                orderItem.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttr(), ";"));

                // 促销、优惠、积分 TODO: 后续优化这里的逻辑
                orderItem.setPromotionAmount(new BigDecimal("0.00"));
                orderItem.setCouponAmount(new BigDecimal("0.00"));
                orderItem.setIntegrationAmount(new BigDecimal("0.00"));

                // 经过优惠后的实际金额
                BigDecimal subtract = item.getTotalPrice()
                        .subtract(orderItem.getPromotionAmount())
                        .subtract(orderItem.getCouponAmount())
                        .subtract(orderItem.getIntegrationAmount().multiply(new BigDecimal("0.01")));
                orderItem.setRealAmount(subtract);

                // 成长值、积分 TODO: 后续优化这里的逻辑
                orderItem.setGiftIntegration(0);
                orderItem.setGiftGrowth(0);

                return orderItem;
            }).collect(Collectors.toList());
            return collect;
        } else {
            return null;
        }
    }

    /**
     * 构建订单实例
     * @return
     */
    private Order buildOrder() {
        OrderSubmitVO orderSubmit = orderSubmitThreadLocal.get();
        Order order = new Order();

        // 设置用户id
        MemberTO memberTO = LoginUserInterceptor.loginUser.get();
        order.setMemberId(memberTO.getId());

        // 生成订单号
        String orderSn = String.valueOf(System.currentTimeMillis()) + UUID.randomUUID().toString();
        order.setOrderSn(orderSn);

        // 时间
        order.setCreateTime(new Date());
        order.setModifyTime(new Date());

        // 用户名
        order.setMemberUsername(memberTO.getUsername());

        // 订单状态
        order.setStatus(OrderStatus.CREATE_NEW.getCode());

        // 自动确认时间
        order.setAutoConfirmDay(7);

        // 收货地址、运费 - 根据选中的收货地址来计算运费
        CommonResponse resp = CommonResponse.convertToResp(wareSkuRPCService.getAddrInfoAndFare(orderSubmit.getAddrId()));
        FareAddrInfoTO data = resp.getData(new TypeReference<FareAddrInfoTO>() {
        });
        if (data != null) {
            // 运费
            order.setFreightAmount(data.getFare());
            // 收货人姓名
            order.setReceiverName(data.getAddress().getName());
            // 收货人邮编
            order.setReceiverPostCode(data.getAddress().getPostCode());
            // 省份/直辖市
            order.setReceiverProvince(data.getAddress().getProvince());
            // 城市
            order.setReceiverCity(data.getAddress().getCity());
            // 区
            order.setReceiverRegion(data.getAddress().getRegion());
            // 详细地址
            order.setReceiverDetailAddress(data.getAddress().getDetailAddress());
        }
        // 订单备注
        order.setNote(orderSubmit.getNote());

        // 确认收货状态
        order.setConfirmStatus((byte) 0);

        // 删除状态
        order.setDeleteStatus((byte) 0);

        return order;
    }

}
































