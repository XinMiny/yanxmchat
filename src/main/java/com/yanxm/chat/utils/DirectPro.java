package com.yanxm.chat.utils;
 
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class DirectPro {
	 public final static String EXCHANGE_NAME = "direct_exchange";//direct交换器名称
	    public final static Integer SEND_NUM = 10;//发送消息次数
	    private final static String QUEUE_NAME = "queue_name";

	    public static void main(String[] args) throws Exception {
	        //创建连接工厂，连接RabbitMQ
	    	ConnectionFactory connectionFactory = new ConnectionFactory();
	        //在本地机器创建socket连接
	        connectionFactory.setHost("localhost");
	        //建立socket连接
	        Connection connection = connectionFactory.newConnection();
	 
	        //创建Channel，含有处理信息的大部分API
	        Channel channel = connection.createChannel();
	        //声明一个Queue，用来存放消息 durable设为true持久化消息
	        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
	        //消息内容
	        String message = "hello, little qute rabbitmq!";
	        //发布消息
	        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
	        //发布消息成功提示信息
	        System.out.println("RABBITMQ客户端成功发送信息：" +  message);
	 
	        //关闭连接
	        channel.close();
	        connection.close();
	 

	    }
}
