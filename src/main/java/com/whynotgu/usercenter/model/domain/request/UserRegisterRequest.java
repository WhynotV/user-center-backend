package com.whynotgu.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author V'jie
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -706654280602509508L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
