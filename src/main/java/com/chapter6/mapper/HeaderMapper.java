package com.chapter6.mapper;

import com.chapter6.model.Header;
import com.chapter6.model.Test;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface HeaderMapper {
    List<Header> headerList();
    String headerById(int headerId);
}
