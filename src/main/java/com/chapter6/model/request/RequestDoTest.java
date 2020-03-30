package com.chapter6.model.request;

import lombok.Data;

@Data
public class RequestDoTest {
    /**
     * 多个用户的登入情况演算
     * 根据传入的账户类型
     * @storeAccount 门店账户/取货点
     * @storePassword 门店账户密码
     * @StoreCodeword 门店账户验证码
     * @driverAccount 司机账户
     * @driverCodeword 司机账户验证码
     * @ReStoreAccout 取货方账户
     * @ReStorePassword 取货方账户密码
     * @ReStoreCodeword 取货方账户验证码
     * @ServiceAccount 服务车账号
     * @ServicePassword 服务车账号密码
     * @ServiceCodeword 服务车账号验证码
     */
    private String storeAccount;
    private String storePassword;
    private String StoreCodeword;
    private String driverAccount;
    private String driverCodeword;
    private String ReStoreAccout;
    private String ReStorePassword;
    private String ReStoreCodeword;
    private String ServiceAccount;
    private String ServicePassword;
    private String ServiceCodeword;
    private int testCaseId;
    private int environment;
    private String testIdGroup;
}
