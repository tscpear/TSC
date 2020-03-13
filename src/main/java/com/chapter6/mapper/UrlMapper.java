package com.chapter6.mapper;


import com.chapter6.model.ApiUtilData;
import com.chapter6.model.Url;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UrlMapper {
    /**
     * 通过设备和环境获取url
     */
    Url getUrl(int Id);
}
