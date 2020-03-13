package com.chapter6.mapper;

import com.chapter6.model.request.RequestUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    void insertToken(RequestUser requestUser);
}
