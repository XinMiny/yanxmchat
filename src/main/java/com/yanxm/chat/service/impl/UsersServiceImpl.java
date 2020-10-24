package com.yanxm.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanxm.chat.enums.MsgActionEnum;
import com.yanxm.chat.enums.MsgSignFlagEnum;
import com.yanxm.chat.enums.SearchFriendsStatusEnum;
import com.yanxm.chat.mapper.*;
import com.yanxm.chat.netty.ChatMsg;
import com.yanxm.chat.netty.DataContent;
import com.yanxm.chat.netty.UserChannelRel;
import com.yanxm.chat.pojo.FriendsRequest;
import com.yanxm.chat.pojo.MyFriends;
import com.yanxm.chat.pojo.Users;
import com.yanxm.chat.pojo.vo.FriendRequestVO;
import com.yanxm.chat.pojo.vo.MyFriendsVO;
import com.yanxm.chat.service.UsersService;
import com.yanxm.chat.utils.FastDFSClient;
import com.yanxm.chat.utils.FileUtils;
import com.yanxm.chat.utils.JsonUtils;
import com.yanxm.chat.utils.QRCodeUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersMapperCustom usersMapperCustom;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private ChatMsgMapper chatMsgMapper;



    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUsernameIsExist(String username) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username" , username);
        Users users = usersMapper.selectOne(queryWrapper);
        return users;
    }

    @Override
    public Users queryUserForLogin(String username, String pwd) {
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users saveUser(Users user) {

        String id = UUID.randomUUID().toString();

        // 为每个用户生成一个唯一的二维码
        String qrCodePath = "C://user" + id + "qrcode.png";
        // muxin_qrcode:[username]
        qrCodeUtils.createQRCode(qrCodePath, "qrcode:" + user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);

        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setQrcode(qrCodeUrl);

        user.setId(id);
        usersMapper.insert(user);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(Users user) {
        usersMapper.update(user,null);
        return queryUserById(user.getId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserById(String userId) {
        return usersMapper.selectById(userId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer preconditionSearchFriends(String myUserId, String friendUsername) {

        Users user = queryUserInfoByUsername(friendUsername);

        // 1. 搜索的用户如果不存在，返回[无此用户]
        if (user == null) {
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }

        // 2. 搜索账号是你自己，返回[不能添加自己]
        if (user.getId().equals(myUserId)) {
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }

        QueryWrapper<MyFriends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("myUserId" , myUserId);
        queryWrapper.eq("myFriendUserId" , user.getId());
        MyFriends myFriends = myFriendsMapper.selectOne(queryWrapper);

        if (myFriends != null) {
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }

        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfoByUsername(String username) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username" , username);
        Users users = usersMapper.selectOne(queryWrapper);
        return users;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendFriendRequest(String myUserId, String friendUsername) {

        // 根据用户名把朋友信息查询出来
        Users friend = queryUserInfoByUsername(friendUsername);

        // 1. 查询发送好友请求记录表
        QueryWrapper<FriendsRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sendUserId", myUserId);
        queryWrapper.eq("acceptUserId", friend.getId());
        FriendsRequest friendsRequest = friendsRequestMapper.selectOne(queryWrapper);

        if (friendsRequest == null) {
            // 2. 如果不是你的好友，并且好友记录没有添加，则新增好友请求记录

            String requestId = UUID.randomUUID().toString();

            FriendsRequest request = new FriendsRequest();
            request.setId(requestId);
            request.setSendUserId(myUserId);
            request.setAcceptUserId(friend.getId());
            request.setRequestDateTime(new Date());
            friendsRequestMapper.insert(request);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {
        return usersMapperCustom.queryFriendRequestList(acceptUserId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteFriendRequest(String sendUserId, String acceptUserId) {
        QueryWrapper<FriendsRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sendUserId", sendUserId);
        queryWrapper.eq("acceptUserId", acceptUserId);
        friendsRequestMapper.delete(queryWrapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void passFriendRequest(String sendUserId, String acceptUserId) {
        saveFriends(sendUserId, acceptUserId);
        saveFriends(acceptUserId, sendUserId);
        deleteFriendRequest(sendUserId, acceptUserId);

        Channel sendChannel = UserChannelRel.get(sendUserId);
        if (sendChannel != null) {
            // 使用websocket主动推送消息到请求发起者，更新他的通讯录列表为最新
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);

            sendChannel.writeAndFlush(
                    new TextWebSocketFrame(
                            JsonUtils.objectToJson(dataContent)));
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveFriends(String sendUserId, String acceptUserId) {
        MyFriends myFriends = new MyFriends();
        String recordId = UUID.randomUUID().toString();
        myFriends.setId(recordId);
        myFriends.setMyFriendUserId(acceptUserId);
        myFriends.setMyUserId(sendUserId);
        myFriendsMapper.insert(myFriends);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVO> queryMyFriends(String userId) {
        List<MyFriendsVO> myFirends = usersMapperCustom.queryMyFriends(userId);
        return myFirends;
    }

    @Override
    public String saveMsg(com.yanxm.chat.netty.ChatMsg chatMsg) {
        com.yanxm.chat.pojo.ChatMsg msgDB = new com.yanxm.chat.pojo.ChatMsg();
        String msgId = UUID.randomUUID().toString();
        msgDB.setId(msgId);
        msgDB.setAcceptUserId(chatMsg.getReceiverId());
        msgDB.setSendUserId(chatMsg.getSenderId());
        msgDB.setCreateTime(new Date());
        msgDB.setSignFlag(MsgSignFlagEnum.unsign.type);
        msgDB.setMsg(chatMsg.getMsg());

        chatMsgMapper.insert(msgDB);

        return msgId;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        usersMapperCustom.batchUpdateMsgSigned(msgIdList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<com.yanxm.chat.pojo.ChatMsg> getUnReadMsgList(String acceptUserId) {

        QueryWrapper<com.yanxm.chat.pojo.ChatMsg> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("signFlag", 0);
        queryWrapper.eq("acceptUserId", acceptUserId);

        List<com.yanxm.chat.pojo.ChatMsg> chatMsg = chatMsgMapper.selectList(queryWrapper);
        return  chatMsg;
    }
}
