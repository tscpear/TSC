package com.chapter6.model.sql;

import lombok.Data;

@Data
public class SqlUri {
    /**
     * 唯一识别
     */
    private Integer id;


    /**
     *device--设备
     * 		1--知轮管理后台
     * 		2--门店端APP
     * 		3--司机端小程序（禁用）
     * 		4--司机端APP
     * 		5--pda端
     * 		6--商城管理后台
     * 		7--店铺管理后台
     * 		8--三包APP
     * 		9--知轮通
     * 		10--知轮互联
     * 	    11--H5
     */
    private Integer device;


    /**
     * uri--接口
     */
    private String uri;


    /**
     * 请求方法
     *      1--get
     *      2--post
     *      3--put
     */
    private Integer type;


    /**
     * 描述
     */
    private  String describe;


    /**
     * 接口尾部是否传参
     *      0--无
     *      1--固定
     *      2--手动
     *      3--自动
     */
    private String uripar;


    /**
     * 接口传参
     */
    private String urivar;


    /**
     * 请求头参数类型
     *      0--无参数
     *      1--固定参数
     *      2--自动变量
     *      3--手动变量
     */
    private Integer head;
    /**
     * 请求头传参是否存在变量
     *      head=0--前端不展示，null
     *      head=1--前端展示输入固定值
     *      head=2--前端展示并且存入依赖的接口和变量值：格式1-value
     *      head=3--前端展示输入默认值
     */
    private String headvar;
    /**
     * webfrom参数类型
     *      0--无参数
     *      1--固定参数
     *      2--自动参数
     *      3--手动参数
     *      存值格式[1,2,3]
     *      选择0，就无法选择其他，默认为0
     */
    private String form;
    /**
     * webfrom值
     *      form=0--前端展示，null
     *      form=1--前端展示，输入固定值{1：value1}
     *      form=2--前端展示并且存入依赖的接口和变量值：格式{1：value1,2:value2}
     *      form=3--前端展示,[变量1，变量2]
     *      存值格式{1:value1;2:value2}
     */
    private String formvar;
    /**
     * json传参类型
     *      0--无传参
     *      1--固定参数
     *      2--自动参数
     *      3--手动参数
     *      存值格式[1,2,3]
     *      选择0，就无法选择其他，默认为0
     */
    private String json;
    /**
     * json的值
     *      json=0--前端展示，null
     *      json=1--前端展示，输入固定值
     *      json=2--前端展示并且存入依赖的接口和变量值：格式[1：value1,2:value2]
     *      json=3--前端展示{变量1：路径}
     *      存值格式{1:value1;2:value2}
     */
    private String jsonvar;
    /**
     * 是否需要测试用例中奖测试数据缓存起来
     *      格式{变量名：变量路径}
     */
    private String value;
    /**
     * 记录返回值格式
     */
    private String response;


}
