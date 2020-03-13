package com.chapter6.mapper;

import com.chapter6.model.request.ListParam;
import com.chapter6.model.request.RequestUri;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UriMapper {
    /**
     * 查看uri列表
     * @return
     */
    List<RequestUri> uriList(ListParam param);

    /**
     * 新增uri
     * @param uri
     */
    void insertUri(RequestUri uri);

    /**
     * 跟新uri
     * @param uri
     */
    void updataUri(RequestUri uri);

    /**
     * 查看uri总数
     * @return
     */
    int countUri();
    /**
     * 通过uri获取uriId
     */
    Integer getUriIdByUri(String uri);

    /**
     * 通过uri的name 查找信息
     */
    RequestUri getUriById(int id);

    /**
     * 删除接口
     */
    void delUri(int uriId);

    /**
     * 通过uriId 获取 uri名称
     */
    String getUriByUriId(int uriId);
    /**
     * 查询列表的总数
     */
    Integer getCountList(ListParam param);
}
