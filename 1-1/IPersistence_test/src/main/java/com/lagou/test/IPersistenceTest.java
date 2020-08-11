package com.lagou.test;

import com.lagou.dao.IUserDao;
import com.lagou.io.Resources;
import com.lagou.pojo.User;
import com.lagou.sqlSession.SqlSession;
import com.lagou.sqlSession.SqlSessionFactory;
import com.lagou.sqlSession.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;

public class IPersistenceTest {

    @Test
    public void test() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //调用
        User user = new User();
        user.setId(1);
        user.setUsername("张三");
      /*  User user2 = sqlSession.selectOne("user.selectOne", user);

        System.out.println(user2);*/

       /* List<User> users = sqlSession.selectList("user.selectList");
        for (User user1 : users) {
            System.out.println(user1);
        }*/

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        List<User> all = userDao.findAll();
        for (User user1 : all) {
            System.out.println(user1);
        }


    }

    SqlSessionFactory sqlSessionFactory;
    @Before
    public void before() throws PropertyVetoException, DocumentException, ClassNotFoundException {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
    }

    @Test
    public void insert() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        User user = new User();
        user.setId(10);
        user.setUsername("10");
        IUserDao mapper = sqlSession.getMapper(IUserDao.class);
        mapper.insert(user);
    }

    @Test
    public void update() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        User user = new User();
        user.setId(10);
        user.setUsername("udpate10");
        IUserDao mapper = sqlSession.getMapper(IUserDao.class);
        mapper.update(user);
    }

    @Test
    public void delete() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        User user = new User();
        user.setId(10);
        IUserDao mapper = sqlSession.getMapper(IUserDao.class);
        mapper.delete(user);
    }
}
