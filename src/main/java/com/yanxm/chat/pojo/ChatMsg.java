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
@TableName("chat_msg")
public class ChatMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    private String sendUserId;
    private String acceptUserId;
    private String msg;
    /**
     * 消息是否签收状态
        1：签收
        0：未签收
     */
    private Integer signFlag;
    /**
     * 发送请求的事件
     */
    private Date createTime;
}