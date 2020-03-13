package com.chapter6.model.sql;

import lombok.Data;

@Data
public class SqlTestCase {
    /**
     * 唯一识别
     */
    private Integer id;
    /**
     * 描述
     */
    private String describe;
    /**
     * 接口
     */
    private Integer uri;
    /**
     *是否需要测试数据准备
     *    --先保留字段，后期需要通过sql、脚本、手动等手段，进行数据的准备工作
     */
    /*private Integer ready;*/
    /**
     *类型
     *      0--散装可打包
     *      1--流程走起
     *      2--数据使用
     *      存储类型[1,2,3]
     */
    private String type;
    /**
     * 请求头变量
     *      类型为String
     */
    private String headvar;
    /**
     * webform变量
     *       form=1--{}
     */
    private String formvar;

}
