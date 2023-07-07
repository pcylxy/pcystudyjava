package com.example.config;

import lombok.Value;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;

@Configuration
public class quartzConfig {
    // Quartz配置文件路径
    private static final String QUARTZ_CONFIG = "config/quartz.properties";

/*    @Value("${task.enabled:true}")
    private boolean enabled;*/

    @Autowired
    private DataSource dataSource;

    @Bean
    public Scheduler scheduler() throws IOException {
return schedulerFactoryBean().getScheduler();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource);//数据源
        // 设置加载的配置文件
        schedulerFactoryBean.setConfigLocation(new ClassPathResource(QUARTZ_CONFIG));

        // 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
        schedulerFactoryBean.setOverwriteExistingJobs(true);

        schedulerFactoryBean.setStartupDelay(5);// 系统启动后，延迟5s后启动定时任务，默认为0

        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        schedulerFactoryBean.setOverwriteExistingJobs(true);

        // SchedulerFactoryBean在初始化后是否马上启动Scheduler，默认为true。如果设置为false，需要手工启动Scheduler
 /*       schedulerFactoryBean.setAutoStartup(enabled);*/
        schedulerFactoryBean.setSchedulerName("cluster_scheduler");//name
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("application");//
        schedulerFactoryBean.setQuartzProperties(quartzProperties());//读取配置文件
        schedulerFactoryBean.setTaskExecutor(schedulerThreadPool());//线程池
        schedulerFactoryBean.setStartupDelay(10);//延迟多久执行，不是立马
        return schedulerFactoryBean;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/spring-quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    public Executor schedulerThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setQueueCapacity(Runtime.getRuntime().availableProcessors());
        return executor;

    }
}
