package com.chapter6.time;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Component
@Service
public class TireOrderTime {
    @Autowired
    private TaskTime taskTime;

    /**
     * 定时任务方法
     * @Scheduled :设置定时任务
     * cron属性：cron表达式 定时任务触发时间的string 的表达式
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void schedubedMethod() throws Throwable {
        System.out.println("来一发");
        taskTime.doTest();
    }


}
