package com.example.config;


import com.example.provider.service.quartzJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
//容器启动了之后就去自动启动调度器，用springboot里面的监听器
public class StartApplicationListener implements ApplicationListener<ContextClosedEvent> {
    @Autowired
    private Scheduler scheduler;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {

        try { TriggerKey triggerKey = TriggerKey.triggerKey("trigger1", "group1");
            Trigger trigger = scheduler.getTrigger(triggerKey);//从调度器里面取触发器
            if (trigger == null) {
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 0/1 * ?"))//cron表达式
                        .startNow()
                        .build();
                JobDetail jobDetail = JobBuilder.newJob(quartzJob.class)
                        .withIdentity("job1", "group1")
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);

            }
            TriggerKey triggerKey2 = TriggerKey.triggerKey("trigger2", "group2");
            Trigger trigger2 = scheduler.getTrigger(triggerKey2);//从调度器里面取触发器
            if (trigger2 == null) {
                trigger2 = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey2)
                        .withSchedule(CronScheduleBuilder.cronSchedule("0/86400 * * * * ?"))//cron表达式
                        .startNow()
                        .build();
                JobDetail jobDetail2 = JobBuilder.newJob(quartzJob.class)
                        .withIdentity("job2", "group2")
                        .build();
                scheduler.scheduleJob(jobDetail2, trigger2);
                scheduler.start();}
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }
}
