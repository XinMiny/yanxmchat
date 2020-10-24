package com.yanxm.chat.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("my_friends")
public class MyFriends implements Serializable {
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    private String myUserId;
    /**
     * 用户的好友id
     */
    private String myFriendUserId;


}