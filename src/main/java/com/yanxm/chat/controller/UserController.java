package com.yanxm.chat.controller;

import com.yanxm.chat.config.JWTToken;
import com.yanxm.chat.enums.OperatorFriendRequestTypeEnum;
import com.yanxm.chat.enums.SearchFriendsStatusEnum;
import com.yanxm.chat.pojo.ChatMsg;
import com.yanxm.chat.pojo.Users;
import com.yanxm.chat.pojo.bo.UsersBO;
import com.yanxm.chat.pojo.vo.MyFriendsVO;
import com.yanxm.chat.pojo.vo.UsersVO;
import com.yanxm.chat.service.UsersService;
import com.yanxm.chat.utils.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private FastDFSClient fastDFSClient;

	/**
	 * @Description: 登录
	 */
	@PostMapping("/login")
	public JSONResult login(@RequestBody Users user) throws Exception {

		// 0. 判断用户名和密码不能为空
		if (StringUtils.isBlank(user.getUsername())
				|| StringUtils.isBlank(user.getPassword())) {
			return JSONResult.errorMsg("用户名或密码不能为空...");
		}

		// 1. 判断用户名是否存在，如果存在就登录，如果不存在则注册
		Users users = usersService.queryUsernameIsExist(user.getUsername());
		if(users==null)
			return JSONResult.errorMsg("没有此用户");

		String salt = users.getSalt();
		//使用MD5 + salt + hash散列
		Md5Hash md3 = new Md5Hash(user.getPassword(), salt, 1024);
		String sign = md3.toHex();
		Map<String , String> map = new HashMap<>();
		map.put("username" , user.getUsername());
		String token = JWTUtil.getToken(map, sign);
		JWTToken jwtToken = new JWTToken(token);
		jwtToken.setUsers(users);
		try{
			SecurityUtils.getSubject().login(jwtToken);
			return JSONResult.build(200,token,users);
		}catch(AuthenticationException e){
			return JSONResult.errorMsg("登陆验证错误");
		}
		

	}
	
	/**
	 * @Description: 上传用户头像
	 */
	@PostMapping("/uploadFaceBase64")
	public JSONResult uploadFaceBase64(@RequestBody UsersBO userBO) throws Exception {
		
		// 获取前端传过来的base64字符串, 然后转换为文件对象再上传
		String base64Data = userBO.getFaceData();
		String userFacePath = "C:\\" + userBO.getUserId() + "userface64.png";
		FileUtils.base64Tofile(userFacePath, base64Data);
		
		// 上传文件到fastdfs
		MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
		String url = fastDFSClient.uploadBase64(faceFile);
		System.out.println(url);

		
		// 获取缩略图的url
		String thump = "_80x80.";
		String arr[] = url.split("\\.");
		String thumpImgUrl = arr[0] + thump + arr[1];
		
		// 更细用户头像
		Users user = new Users();
		user.setId(userBO.getUserId());
		user.setFaceImage(thumpImgUrl);
		user.setFaceImageBig(url);
		
		Users result = usersService.updateUserInfo(user);
		
		return JSONResult.ok(result);
	}
	
	/**
	 * @Description: 设置用户昵称
	 */
	@PostMapping("/setNickname")
	public JSONResult setNickname(@RequestBody UsersBO userBO) throws Exception {
		
		Users user = new Users();
		user.setId(userBO.getUserId());
		user.setNickname(userBO.getNickname());
		
		Users result = usersService.updateUserInfo(user);
		
		return JSONResult.ok(result);
	}
	
	/**
	 * @Description: 搜索好友接口, 根据账号做匹配查询而不是模糊查询
	 */
	@PostMapping("/search")
	public JSONResult searchUser(String myUserId, String friendUsername)
			throws Exception {
		
		// 0. 判断 myUserId friendUsername 不能为空
		if (StringUtils.isBlank(myUserId)
				|| StringUtils.isBlank(friendUsername)) {
			return JSONResult.errorMsg("");
		}

		Integer status = usersService.preconditionSearchFriends(myUserId, friendUsername);
		if (status == SearchFriendsStatusEnum.SUCCESS.status) {
			Users user = usersService.queryUserInfoByUsername(friendUsername);
			UsersVO userVO = new UsersVO();
			BeanUtils.copyProperties(user, userVO);
			return JSONResult.ok(userVO);
		} else {
			String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
			return JSONResult.errorMsg(errorMsg);
		}
	}
	
	
	/**
	 * @Description: 发送添加好友的请求
	 */
	@PostMapping("/addFriendRequest")
	public JSONResult addFriendRequest(String myUserId, String friendUsername)
			throws Exception {
		
		// 0. 判断 myUserId friendUsername 不能为空
		if (StringUtils.isBlank(myUserId)
				|| StringUtils.isBlank(friendUsername)) {
			return JSONResult.errorMsg("");
		}
		Integer status = usersService.preconditionSearchFriends(myUserId, friendUsername);
		if (status == SearchFriendsStatusEnum.SUCCESS.status) {
			usersService.sendFriendRequest(myUserId, friendUsername);
		} else {
			String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
			return JSONResult.errorMsg(errorMsg);
		}
		
		return JSONResult.ok();
	}
	
	/**
	 * @Description: 发送添加好友的请求
	 */
	@PostMapping("/queryFriendRequests")
	public JSONResult queryFriendRequests(String userId) {
		
		// 0. 判断不能为空
		if (StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("");
		}
		
		// 1. 查询用户接受到的朋友申请
		return JSONResult.ok(usersService.queryFriendRequestList(userId));
	}
	
	
	/**
	 * @Description: 接受方 通过或者忽略朋友请求
	 */
	@PostMapping("/operFriendRequest")
	public JSONResult operFriendRequest(String acceptUserId, String sendUserId,
												Integer operType) {
		
		// 0. acceptUserId sendUserId operType 判断不能为空
		if (StringUtils.isBlank(acceptUserId)
				|| StringUtils.isBlank(sendUserId)
				|| operType == null) {
			return JSONResult.errorMsg("");
		}
		
		// 1. 如果operType 没有对应的枚举值，则直接抛出空错误信息
		if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))) {
			return JSONResult.errorMsg("");
		}
		
		if (operType == OperatorFriendRequestTypeEnum.IGNORE.type) {
			// 2. 判断如果忽略好友请求，则直接删除好友请求的数据库表记录
			usersService.deleteFriendRequest(sendUserId, acceptUserId);
		} else if (operType == OperatorFriendRequestTypeEnum.PASS.type) {
			usersService.passFriendRequest(sendUserId, acceptUserId);
		}
		
		// 4. 数据库查询好友列表
		List<MyFriendsVO> myFirends = usersService.queryMyFriends(acceptUserId);
		
		return JSONResult.ok(myFirends);
	}
	
	/**
	 * @Description: 查询我的好友列表
	 */
	@PostMapping("/myFriends")
	public JSONResult myFriends(String userId) {
		// 0. userId 判断不能为空
		if (StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("");
		}
		
		// 1. 数据库查询好友列表
		List<MyFriendsVO> myFirends = usersService.queryMyFriends(userId);
		
		return JSONResult.ok(myFirends);
	}
	
	/**
	 * 
	 * @Description: 用户手机端获取未签收的消息列表
	 */
	@PostMapping("/getUnReadMsgList")
	public JSONResult getUnReadMsgList(String acceptUserId) {
		// 0. userId 判断不能为空
		if (StringUtils.isBlank(acceptUserId)) {
			return JSONResult.errorMsg("");
		}
		
		// 查询列表
		List<ChatMsg> unreadMsgList = usersService.getUnReadMsgList(acceptUserId);
		
		return JSONResult.ok(unreadMsgList);
	}
}
