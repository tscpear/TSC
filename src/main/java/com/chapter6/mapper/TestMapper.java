package com.chapter6.mapper;

import com.chapter6.model.request.ListParam;
import com.chapter6.model.request.RequestTestCase;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TestMapper {
    /**
     * 测试用例列表
     * @return
     */
    List<RequestTestCase> testList(ListParam param);
    /**
     * 新增测试用例
     * @param test
     */
    void addTestCase(RequestTestCase test);
    /**
     * 查看测试用例总数
     * @return
     */
    int testCount();

    /**
     * 删除测试用例
     * @param testId
     */
    void deleteTest(int testId);

    /**
     * 更新测试用例
     * @param requestTestCase
     */
    void updateTestCase(RequestTestCase requestTestCase);

    /**
     * 查找接口的测试用例数量
     */
    int findCountByApi(int uriId);

    /**
     * 通过接口获取测试用例
     */
    List<RequestTestCase> findTestCaseByUriId(int uriId);

    /**
     * 删除测试用例
     */
    void delTestCase(int uriId);

    /**
     * 由于删除接口导致的测试用例的删除
     */
    void delTestCaseByUriId(int uriId);
    /**
     * 通过用例的id获取uriId
     */
    Integer getUriIdById(int testCaseId);

    /**
     * 通过testcaseId获取测试用例的信息
     */
    RequestTestCase getTestCaseById(int testcaseId);
    /**
     * 获取到当前查询到测试用例数量--列表
     */
    Integer getCountList(ListParam param);
    /**
     * 通过testcaseId 获取依赖的测试用例
     */
    String getRelyByTestcaseId(Integer testcaseId);


}
