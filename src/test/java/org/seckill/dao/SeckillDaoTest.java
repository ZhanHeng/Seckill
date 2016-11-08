package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.seckill.dao.SeckillDao;
import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by ZhanHeng on 2016/11/8.
 * 1.配置spring和junit整合 spring-test,junit，junit启动时加载springIOC容器
 * 2.告诉junit,Spring的配置文件在哪里
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {
    //注入Dao类的依赖

    @Resource
    private SeckillDao seckillDao;

    @Test
    public void queryById() throws Exception {
        long id  = 1000 ;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void queryAll() throws Exception {

    }

    @Test
    public void reduceNumber() throws Exception {

    }



}