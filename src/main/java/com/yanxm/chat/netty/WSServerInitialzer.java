package com.yanxm.chat.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class WSServerInitialzer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		pipeline.addLast(new HttpServerCodec());
		// 对写大数据流的支持 
		pipeline.addLast(new ChunkedWriteHandler());

		pipeline.addLast(new HttpObjectAggregator(1024*64));
		
		//心跳
		pipeline.addLast(new IdleStateHandler(8, 10, 12));
		// 自定义的空闲状态检测
		pipeline.addLast(new HeartBeatHandler());

		pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
		
		// 处理事务
		pipeline.addLast(new ChatHandler());
	}

}
