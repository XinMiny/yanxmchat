package com.yanxm.chat.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxm.chat.pojo.Users;
import com.yanxm.chat.pojo.vo.FriendRequestVO;
import com.yanxm.chat.pojo.vo.MyFriendsVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UsersMapperCustom extends BaseMapper<Users> {

	@Select("select sender.id as sendUserId, sender.username as sendUsername, sender.face_image as sendFaceImage, sender.nickname as sendNickname from friends_request fr left join  users sender on  fr.send_user_id = sender.id where  fr.accept_user_id = #{acceptUserId}")
	public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);
	@Select("select  u.id as friendUserId, u.username as friendUsername, u.face_image as friendFaceImage, u.nickname as friendNickname from my_friends mf left join  users u on  u.id = mf.my_friend_user_id where  mf.my_user_id = #{userId}")
	public List<MyFriendsVO> queryMyFriends(String userId);
	
	public void batchUpdateMsgSigned(List<String> msgIdList);
	
}