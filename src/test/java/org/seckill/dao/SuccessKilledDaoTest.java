package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by lenovo on 2016/11/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
    @Resource
    private SuccessKilledDao successKilledDao;

    /**
     * 第一次：insertCount = 1
     * 第二次：insertCount = 0
     */
    @Test
    public void insertSuccessKilled() throws Exception {
        int insertCount = successKilledDao.insertSuccessKilled(1000L,13509890675L);
        System.out.println("insertCount = "+insertCount);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        SuccessKilled sk =successKilledDao.queryByIdWithSeckill(1000L,13509890675L);
        System.out.println(sk);
        System.out.println(sk.getSeckill());
    }

}