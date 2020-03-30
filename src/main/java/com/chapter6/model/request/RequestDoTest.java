package com.chapter6.model.request;

import lombok.Data;

@Data
public class RequestDoTest {
    /**
     * 多个用户的登入情况演算
     * 根据传入的账户类型
     * @storeAccount 门店账户/取货点
     * @storePassword 门店账户密码
     * @storeCodeword 门店账户验证码
     * @driverAccount 司机账户
     * @driverCodeword 司机账户验证码
     * @reStoreAccout 取货方账户
     * @reStorePassword 取货方账户密码
     * @reStoreCodeword 取货方账户验证码
     * @serviceAccount 服务车账号
     * @servicePassword 服务车账号密码
     * @serviceCodeword 服务车账号验证码
     * @tongAccount 知轮通账户
     * @tongPassword 知轮通账户密码
     * @threeAccount 三包账户
     * @threePassword  三包账户密码
     * @manageAccount 管理后台账户
     * @managePassword 管理后台账户密码
     * @fleetAccount 车队账号
     * @fleetPassword 车队账号密码
     * @shopAAccount 商城管理后台账号
     * @shopAPassword 商城管理后台账号密码
     * @shopBAccount 商城店铺后台账号
     * @shopBPassword 商城店铺后台账号密码
     */
    private String storeAccount;
    private String storePassword;
    private String storeCodeword;
    private String driverAccount;
    private String driverCodeword;
    private String reStoreAccout;
    private String reStorePassword;
    private String reStoreCodeword;
    private String serviceAccount;
    private String servicePassword;
    private String serviceCodeword;
    private String tongAccount;
    private String tongPassword;
    private String threeAccount;
    private String threePassword;
    private String manageAccount;
    private String managePassword;
    private String fleetAccount;
    private String fleetPassword;
    private String shopAAccount;
    private String shopAPassword;
    private String shopBAccount;
    private String shopBPassword;
    private int testCaseId;
    private int environment;
    private String testIdGroup;

}
