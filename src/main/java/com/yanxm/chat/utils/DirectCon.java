package com.yanxm.chat.utils;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * direct 消费者
 */
public class DirectCon {

    public final static String QUEUE_NAME = "queue_name";

    public static void main(String[] args) throws Exception {
    	ConnectionFactory connectionFactory = new ConnectionFactory();
        //在本地机器创建socket连接
        connectionFactory.setHost("localhost");
        //建立socket连接
        Connection connection = connectionFactory.newConnection();
 
        /* 创建Channel，含有处理信息的大部分API */
        Channel channel = connection.createChannel();
        //声明一个Queue，用来获取消息。QUEUE_NAME需要与Producer端相同 , durable设为true持久化消息
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        
        //从队列中异步获取消息，DefaultConsumer会设置一个回调来缓存消息。
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Consumer获取消息：" + message );
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);


    }
}