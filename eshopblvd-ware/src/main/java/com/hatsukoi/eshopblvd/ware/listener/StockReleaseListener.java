package com.hatsukoi.eshopblvd.ware.listener;

import com.hatsukoi.eshopblvd.to.OrderTo;
import com.hatsukoi.eshopblvd.to.StockLockedTo;
import com.hatsukoi.eshopblvd.ware.service.WareSkuService;
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
 * @date 2022/05/20 Fri 2:13 AM
 */
@Slf4j
@Service
@RabbitListener(queues = "stock.release.stock.queue") // 监听库存释放消息队列
public class StockReleaseListener {
    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo stockLock, Message message, Channel channel) throws IOException {
        log.info("收到50min后自动解锁库存的消息");
        try {
            // 对于每个订单工作项释放库存锁定
            wareSkuService.unlockStock(stockLock);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {
        log.info("订单关闭准备解锁库存...");
        try{
            wareSkuService.unlockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
}
