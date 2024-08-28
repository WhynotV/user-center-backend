package com.whynotgu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whynotgu.usercenter.common.ErrorCode;
import com.whynotgu.usercenter.exception.BusinessException;
import com.whynotgu.usercenter.service.UserService;
import com.whynotgu.usercenter.model.domain.User;
import com.whynotgu.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.whynotgu.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author V'jie
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-05-20 15:47:30
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值：混淆密码
     */
    private static final String SALT = "gugu";



    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {
        // 1. 校验
        if (StringUtils.isAllBlank(userAccount,userPassword,checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }

        if (planetCode.length() > 5 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号过长");
        }

        //账户不能包含特殊字符
        String validPattern ="^[a-zA-Z][a-zA-Z0-9_]{4,19}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }

        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码与校验密码不同");
        }
        //账户不能重复
        QueryWrapper<User> quaryWrapper = new QueryWrapper<>();
        quaryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(quaryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }
        //星球编号不能重复
        quaryWrapper = new QueryWrapper<>();
        quaryWrapper.eq("planetCode",planetCode);
        count = userMapper.selectCount(quaryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号已存在");
        }


        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult){
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账密为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }

        //账户不能包含特殊字符
        String validPattern = "^[a-zA-Z][a-zA-Z0-9_]{4,19}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()) {
            return null;
        }

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 查询用户是否存在
        QueryWrapper<User> quaryWrapper = new QueryWrapper<>();
        quaryWrapper.eq("userAccount", userAccount);
        quaryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(quaryWrapper);
        // 用户不存在
        if (user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(user);

        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public User getById(Integer id) {
        return userMapper.getById(id);
    }


    @Override
    public User updateUser(User updateUser) {
        if (updateUser == null || updateUser.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        // 先查询用户是否存在
        User existUser = this.getById(updateUser.getId());
        if (existUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 更新允许修改的字段
        existUser.setUsername(updateUser.getUsername());
        existUser.setUserAccount(updateUser.getUserAccount());
        existUser.setAvatarUrl(updateUser.getAvatarUrl());
        existUser.setGender(updateUser.getGender());
        existUser.setPhone(updateUser.getPhone());
        existUser.setEmail(updateUser.getEmail());
        existUser.setUserStatus(updateUser.getUserStatus());
        existUser.setUserRole(updateUser.getUserRole());
        existUser.setCreateTime(updateUser.getCreateTime());

        userMapper.update(existUser);
        return existUser;
    }

    @Override
    public boolean delete(long id) {
        try {
            int result = userMapper.delete(id);
            if (result > 0) {
                log.info("用户删除成功，id: {}", id);
                return true;
            } else {
                log.warn("用户删除失败，id: {}，可能用户不存在", id);
                return false;
            }
        } catch (Exception e) {
            log.error("删除用户时发生异常，id: {}", id, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除用户失败");
        }
    }


}




