package com.wang.demo.dao;

import java.util.List;

import com.wang.demo.model.User;
import com.wang.mybatis.annotation.Mapper;
import com.wang.mybatis.annotation.Param;
import com.wang.mybatis.annotation.Select;

@Mapper
public interface UserMapper {

	@Select("select * from user where id>#{id}")
	public List<User> selectUsers(@Param("id") Integer id);
}
