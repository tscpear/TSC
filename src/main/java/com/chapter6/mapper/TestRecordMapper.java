package com.chapter6.mapper;

import com.chapter6.model.request.RequestRecordTest;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TestRecordMapper {
    /**
     * 存入测试用例记录
     *
     * @param requestRecordTest
     */
    void insert(RequestRecordTest requestRecordTest);

    /**
     * 根据测testcaseId 和 测试组Id；
     */
    String getSaveVaule(RequestRecordTest requestRecordTest);

    /**
     * 根据测试用例的Id  用例组Id 用户组Id获取返回值
     */
    String getResponse(RequestRecordTest requestRecordTest);

    /**
     * 通过uriId+userGroupId+groupId 获取value
     */
    String getValue(RequestRecordTest requestRecordTest);

    /**
     * 通过uriId+userGroupId+groupId 获取整个测试记录
     */
    RequestRecordTest getTestRecord(RequestRecordTest requestRecordTest);
    /**
     * 通过record 查询testId 、期望值
     */
    List<RequestRecordTest> getTestRecordByRecord(long recordId);
    /**
     * 获取状态码的结果
     */
    String getStatusByRelyTestcaseId(Integer relyTestcaseId,long recordId);
    /**
     * 存放期望值对比结果
     */
    void updataExpectResult(String status,String response,String sql,long recordId,int testcaseId,long userGroupId);
}
