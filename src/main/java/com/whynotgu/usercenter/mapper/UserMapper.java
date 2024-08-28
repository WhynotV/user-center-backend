package com.whynotgu.usercenter.mapper;

import com.whynotgu.usercenter.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

/**
* @author V'jie
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-05-20 15:47:30
* @Entity generator.domain.User
*/
public interface UserMapper extends BaseMapper<User> {


    /**
     * 根据ID查询用户
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Integer id);


    /**
     * 更新用户信息
     * @param existUser
     */
    void update(User existUser);


    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @Delete("delete from user where id = #{id}")
    int delete(long id);
}




