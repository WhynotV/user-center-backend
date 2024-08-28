package com.whynotgu.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author V'jie
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 8461945348543977969L;

    private String userAccount;
    private String userPassword;

}
