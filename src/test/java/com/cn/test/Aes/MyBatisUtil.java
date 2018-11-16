package com.cn.test.Aes;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * User: cuixiaowei
 * Date: 2018/10/22
 * PackageName: com.cn.test.Aes
 */
public class MyBatisUtil {
    public static SqlSessionFactory getSqlSessionFactory(){
        // 获得环境配置文件流
        InputStream config = MyBatisUtil.class.getClassLoader().getResourceAsStream("spring/spring-dao.xml");
        // 创建sql会话工厂
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(config);
        return factory;
    }

    //获得会话
    public static SqlSession getSession(){
        return getSqlSessionFactory().openSession(true);
    }

    /**
     * 获得得sql会话
     * @param isAutoCommit 是否自动提交，如果为false则需要sqlSession.commit();rollback();
     * @return sql会话
     */
    public static SqlSession getSession(boolean isAutoCommit){
        return getSqlSessionFactory().openSession(isAutoCommit);
    }

}
