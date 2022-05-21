package com.hatsukoi.eshopblvd.order.listener;

import com.hatsukoi.eshopblvd.order.entity.Order;
import com.hatsukoi.eshopblvd.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author gaoweilin
 * @date 2022/05/20 Fri 11:41 AM
 */
@Slf4j
@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {
    @Autowired
    private OrderService orderService;

    /**
     * 创建订单的消息会进入延迟队列，最终发送至队列order.release.order.queue，因此我们对该队列进行监听，进行订单的关闭
     * @param order
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitHandler
    public void listener(Order order, Channel channel, Message message) throws IOException {
        log.info("收到过期的订单信息：准备关闭订单"+order.getOrderSn()+"==>"+order.getId());
        try{
            orderService.closeOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
}
