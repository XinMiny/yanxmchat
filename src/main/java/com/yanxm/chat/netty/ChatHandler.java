package com.yanxm.chat.netty;


import com.yanxm.chat.SpringUtil;
import com.yanxm.chat.enums.MsgActionEnum;
import com.yanxm.chat.service.UsersService;
import com.yanxm.chat.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @Description:
 * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	// 用于记录和管理所有客户端的channle
	public static ChannelGroup users =
			new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg)
			throws Exception {
		String content = msg.text();
		Channel currentChannel = ctx.channel();
		DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
		Integer action = dataContent.getAction();

		if (action == MsgActionEnum.CONNECT.type) {
			// 	2.1  当websocket 第一次open的时候，初始化channel，把用的channel和userid关联起来
			String senderId = dataContent.getChatMsg().getSenderId();
			UserChannelRel.put(senderId, currentChannel);

			UserChannelRel.output();
		} else if (action == MsgActionEnum.CHAT.type) {
			//  2.2  聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
			ChatMsg chatMsg = dataContent.getChatMsg();
			String msgText = chatMsg.getMsg();
			String receiverId = chatMsg.getReceiverId();
			String senderId = chatMsg.getSenderId();
			
			// 保存消息到数据库，并且标记为 未签收
			UsersService userService = (UsersService)SpringUtil.getBean("usersServiceImpl");
			String msgId = userService.saveMsg(chatMsg);
			chatMsg.setMsgId(msgId);
			
			DataContent dataContentMsg = new DataContent();
			dataContentMsg.setChatMsg(chatMsg);
			

			// 从全局用户Channel关系中获取接受方的channel
			Channel receiverChannel = UserChannelRel.get(receiverId);
			if (receiverChannel == null) {
				// TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
			} else {
				// 当receiverChannel不为空的时候，从ChannelGroup去查找对应的channel是否存在
				Channel findChannel = users.find(receiverChannel.id());
				if (findChannel != null) {

					receiverChannel.writeAndFlush(
							new TextWebSocketFrame(
									JsonUtils.objectToJson(dataContentMsg)));
				} else {
					// 用户离线 TODO 推送消息
				}
			}
			
		} else if (action == MsgActionEnum.SIGNED.type) {
			//  2.3  签收消息类型，针对具体的消息进行签收，修改数据库中对应消息的签收状态[已签收]
			UsersService userService = (UsersService)SpringUtil.getBean("usersServiceImpl");
			// 扩展字段在signed类型的消息中，代表需要去签收的消息id，逗号间隔
			String msgIdsStr = dataContent.getExtand();
			String msgIds[] = msgIdsStr.split(",");
			
			List<String> msgIdList = new ArrayList<>();
			for (String mid : msgIds) {
				if (StringUtils.isNotBlank(mid)) {
					msgIdList.add(mid);
				}
			}
			
			System.out.println(msgIdList.toString());
			
			if (msgIdList != null && !msgIdList.isEmpty() && msgIdList.size() > 0) {
				// 批量签收
				userService.updateMsgSigned(msgIdList);
			}
			
		} else if (action == MsgActionEnum.KEEPALIVE.type) {
			//  2.4  心跳类型的消息
			System.out.println("收到来自channel为[" + currentChannel + "]的心跳包...");
		}
	}
	
	/**
	 * 获取客户端的channle，并且放到ChannelGroup中去进行管理
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("add.....");
		users.add(ctx.channel());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		
		String channelId = ctx.channel().id().asShortText();
		System.out.println("客户端被移除，channelId为：" + channelId);
		users.remove(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.channel().close();
		users.remove(ctx.channel());
	}
}
