package com.hatsukoi.eshopblvd.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/05/17 Tue 11:36 AM
 */
@Configuration
public class MyRabbitConfig {
    /**
     * 使用JSON序列化机制，进行消息转换
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange("stock-event-exchange", true, false);
    }

    /**
     * 解锁库存队列
     * @return
     */
    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue("stock.release.stock.queue",true,false,false);
    }

    /**
     * 延时队列（50min）
     * 锁定库存50min后消息来到解锁库存队列，由解锁库存服务来消费
     * @return
     */
    @Bean
    public Queue stockDelayQueue(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange","stock-event-exchange");
        args.put("x-dead-letter-routing-key","stock.release");
        args.put("x-message-ttl",300000);
        return new Queue("stock.delay.queue",true,false,false,args);
    }

    /**
     * 交换机和库存释放队列绑定
     * @return
     */
    @Bean
    public Binding stockReleaseBinding(){
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",
                null);
    }

    /**
     * 交换机和延迟队列绑定
     * @return
     */
    @Bean
    public Binding stockLockedBinding(){
        return  new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked",
                null);
    }
}
