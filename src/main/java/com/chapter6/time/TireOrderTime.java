package com.chapter6.time;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class TireOrderTime {
    /**
     * 定时任务方法
     * @Scheduled :设置定时任务
     * cron属性：cron表达式 定时任务触发时间的string 的表达式
     */
    @Scheduled(cron = "0/2 * * * * ?")
    public void schedubedMethod(){
        System.out.println("定时器被触发");
    }


}
