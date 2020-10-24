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
@TableName("users")
public class Users implements Serializable {

    @TableId(value = "id", type = IdType.UUID)
    private String id;
    private String username;
    private String password;
    /**
     * 头像
     */
    private String faceImage;
    private String faceImageBig;
    /**
     * 昵称
     */
    private String nickname;
    private String qrcode;
    private String cid;
    private Integer state;
    private String salt;
}