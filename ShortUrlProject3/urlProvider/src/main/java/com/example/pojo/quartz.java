package com.example.pojo;

import com.example.provider.service.quartzJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;

import org.springframework.stereotype.Component;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Component
public class quartz {
    JobDetail jobDetail;
    Trigger trigger;

    @Bean
    public JobDetail helloJobDetail(){
         jobDetail = JobBuilder.newJob(quartzJob.class)
                .withIdentity("helloJob")
                .storeDurably()
                .usingJobData("data", "保密信息")
                .build();
        return jobDetail;
    }

    @Bean
    public Trigger helloJobTrigger(){
         trigger = (Trigger) TriggerBuilder.newTrigger()
                .forJob("helloJob")
                .withSchedule(simpleSchedule().withIntervalInHours(24)
                        .repeatForever())
                .build();

        return trigger;
    }
    public Scheduler helloScheduler() throws SchedulerException {
        Scheduler scheduler= StdSchedulerFactory.getDefaultScheduler();
        scheduler.scheduleJob(jobDetail,trigger);
        scheduler.start();//?
        return scheduler;
    }
}
