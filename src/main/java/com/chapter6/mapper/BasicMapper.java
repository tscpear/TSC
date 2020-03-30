package com.chapter6.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface BasicMapper {
    /**
     * 获取basic
     */
    String basic(Integer environment,Integer device);
}
