"use strict";
layui.define(["layer"], function (exports) {
    var okLayer = {
        /**
         * confirm()函数二次封装
         * @param content
         * @param yesFunction
         */
        confirm: function (content, yesFunction) {
            var options = {skin: okLayer.skinChoose(), icon: 3, title: "提示", anim: okLayer.animChoose()};
            layer.confirm(content, options, yesFunction);
        },
        /**
         * open()函数二次封装,支持在table页面和普通页面打开
         * @param title
         * @param content
         * @param width
         * @param height
         * @param successFunction
         * @param endFunction
         */
        open: function (title, content, width, height, successFunction, endFunction) {
            layer.open({
                title: title,
                type: 2,
                maxmin: true,
                shade: 0.5,
                anim: 1,
                area: [width, height],
                content: content,
                zIndex: layer.zIndex,
                skin: "layui-layer-molv",
                success: successFunction,
                end: endFunction
            });
        },
        openTick: function (obj) {
            layer.open({
                title: obj.title,
                type: obj.type,
                maxmin: true,
                shade: 0.5,
                anim: 1,
                area: [obj.width, obj.height],
                content: obj.content,
                zIndex: layer.zIndex,
                skin: "layui-layer-molv",
                success: obj.successFunction,
                end: obj.endFunction
            });
        },
        /**
         * msg()函数二次封装
         */
        // msg弹窗默认消失时间
        time: 1000,
        // 绿色勾
        greenTickMsg: function (content, callbackFunction) {
            var options = {icon: 1, time: okLayer.time, anim: okLayer.animChoose()};
            layer.msg(content, options, callbackFunction);
        },
        // 红色叉
        redCrossMsg: function (content, callbackFunction) {
            var options = {icon: 2, time: okLayer.time, anim: okLayer.animChoose()};
            layer.msg(content, options, callbackFunction);
        },
        // 黄色问号
        yellowQuestionMsg: function (content, callbackFunction) {
            var options = {icon: 3, time: okLayer.time, anim: okLayer.animChoose()};
            layer.msg(content, options, callbackFunction);
        },
        // 灰色锁
        grayLockMsg: function (content, callbackFunction) {
            var options = {icon: 4, time: okLayer.time, anim: okLayer.animChoose()};
            layer.msg(content, options, callbackFunction);
        },
        // 红色哭脸
        redCryMsg: function (content, callbackFunction) {
            var options = {icon: 5, time: okLayer.time, anim: okLayer.animChoose()};
            layer.msg(content, options, callbackFunction);
        },
        // 绿色笑脸
        greenLaughMsg: function (content, callbackFunction) {
            var options = {icon: 6, time: okLayer.time, anim: okLayer.animChoose()};
            layer.msg(content, options, callbackFunction);
        },
        // 黄色感叹号
        yellowSighMsg: function (content, callbackFunction) {
            var options = {icon: 7, time: okLayer.time, anim: okLayer.animChoose()};
            layer.msg(content, options, callbackFunction);
        },
        // 等待数据的
        waitTickMsg: function (content, callbackFunction) {
            var options = {icon: 16 , time: okLayer.time, anim: okLayer.animChoose()};
            layer.msg(content, options, callbackFunction);
        },
        /**
         * 皮肤选择
         * @returns {string}
         */
        skinChoose: function () {
            var storage = window.localStorage;
            var skin = storage.getItem("skin");
            if (skin == 1) {
                // 灰白色
                return "";
            } else if (skin == 2) {
                // 墨绿色
                return "layui-layer-molv";
            } else if (skin == 3) {
                // 蓝色
                return "layui-layer-lan";
            } else if (!skin || skin == 4) {
                // 随机颜色
                var skinArray = ["", "layui-layer-molv", "layui-layer-lan"];
                return skinArray[Math.floor(Math.random() * skinArray.length)];
            }
        },
        /**
         * 唐胖胖版皮肤
         */
        skinChooseByTSC: function(){
            return "layui-layer-molv";
        },
        /**
         * 动画选择
         * @returns {number}
         */
        animChoose: function () {
            var storage = window.localStorage;
            var anim = storage.getItem("anim");
            var animArray = ["0", "1", "2", "3", "4", "5", "6"];
            if (animArray.indexOf(anim) > -1) {
                // 用户选择的动画
                return anim;
            } else if (!anim || anim == 7) {
                // 随机动画
                return 0;
            }
        },
        /**
         * 唐胖胖版
         */
        animChooseByTSC: function (amin) {
            return amin;
        }


    }

    exports("okLayer", okLayer);
});
