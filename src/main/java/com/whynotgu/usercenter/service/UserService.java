package com.whynotgu.usercenter.service;

import com.whynotgu.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author V'jie
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-05-20 15:47:30
*/
public interface UserService extends IService<User> {



    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @param planetCode 星球编号
     * @return 新用户 id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword,String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 返回脱敏后的信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    User getById(Integer id);


    /**
     * 管理员更新用户信息
     * @param user
     * @return
     */
    User updateUser(User user);

    /**
     * 删除用户
     * @param id
     * @return
     */
    boolean delete(long id);
}
