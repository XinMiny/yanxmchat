package com.yanxm.chat.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("friends_request")
public class FriendsRequest implements Serializable {
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    private String sendUserId;
    private String acceptUserId;
    /**
     * 发送请求的时间
     */
    private Date requestDateTime;
}