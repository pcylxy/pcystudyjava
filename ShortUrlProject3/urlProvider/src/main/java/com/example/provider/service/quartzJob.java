package com.example.provider.service;

import cn.hutool.core.date.DateUtil;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.staticmethod.sqlsessionFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class quartzJob extends QuartzJobBean {
    @Autowired
    private  UserMapper userMapper;
    @Override
    protected void executeInternal(org.quartz.JobExecutionContext context) throws JobExecutionException {
/*        StringJoiner joiner = new StringJoiner(" | ")
                .add("---HelloJob---")
                .add(context.getTrigger().getKey().getName())
                .add(DateUtil.formatDate(new Date()));*/
        try {
            /*SqlSessionFactory sqlSessionFactory = sqlsessionFactory.getsqlsessionFactory();
            SqlSession sqlSession = sqlSessionFactory.openSession();
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);*/
            userMapper.deleteExpired(System.currentTimeMillis());
/*            sqlSession.commit();
            sqlSession.close();*/
        } catch (Exception e) {
            throw new RuntimeException("System.currentTimeMillis is invalid!");
        }
    }
}
