package com.hatsukoi.eshopblvd.order.listener;

import com.hatsukoi.eshopblvd.order.service.OrderService;
import com.hatsukoi.eshopblvd.to.SeckillOrderTo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author gaoweilin
 * @date 2022/05/27 Fri 3:38 PM
 */
@Slf4j
@RabbitListener(queues = "order.seckill.order.queue")
@Component
public class OrderSeckillListener {

    @Autowired
    private OrderService orderService;
    @RabbitHandler
    public void listener(SeckillOrderTo seckillOrder, Channel channel, Message message) throws IOException {

        try{
            log.info("准备创建秒杀单的详细信息。。。");
            orderService.createSeckillOrder(seckillOrder);
            //手动调用支付宝收单；
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
}
