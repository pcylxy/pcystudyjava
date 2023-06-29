package com.example.staticmethod;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class sqlsessionFactory {
    private static org.apache.ibatis.session.SqlSessionFactory sqlSessionFactory;

    static {
        //静态代码块会随着类的加载而自动加载，并只会执行一次
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static org.apache.ibatis.session.SqlSessionFactory getsqlsessionFactory(){
        return sqlSessionFactory;
    }

}
