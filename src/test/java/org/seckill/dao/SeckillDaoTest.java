package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.seckill.dao.SeckillDao;
import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

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
        /**
         * org.apache.ibatis.binding.BindingException: Parameter 'offset' not found. Available parameters are [1, 0, param1, param2]
         * 参数没有找到，Java没有保存形参的记录
         * List<Seckill> queryAll(int offset, int limit) --> List<Seckill> queryAll(arg0, arg1)
         * 用@param注解 List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit);
         */
         List<Seckill> seckills = seckillDao.queryAll(0,100);
        for (Seckill seckill: seckills) {
            System.out.println(seckill);
        }
    }

    @Test
    public void reduceNumber() throws Exception {
        Date killTime = new Date();
        int updateNumber = seckillDao.reduceNumber(1000L,killTime);
        System.out.println(updateNumber);
    }


}