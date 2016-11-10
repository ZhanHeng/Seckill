package org.seckill.service.impl;

import org.seckill.dao.RedisDao;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;


/**
 * 秒杀业务接口实现
 * Created by ZhanHeng on 2016/11/9.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //注入Service的依赖
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    //MD5盐值字符串,用来混淆md5
    private final String slat = "zn)*~ADFiouxcioa#!$sfasm^&^^.^^!)SCAdnc#(*$!*$!~#~(wefaA&BDs^XPSDFAQOa)*~ADFioufdj";

    private String getMD5(long seckillId){
        String base =String.valueOf(seckillId) + "/" + slat ;
        String md5 =DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,10);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        //缓存优化
        //1.访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill==null){
            //2.访问数据库
            seckill = seckillDao.queryById(seckillId);
            if (seckill==null){
                return new Exposer(false,seckillId);
            }else{
                //3.放入redis
                redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }
        //转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId) ;
        return new Exposer(true,md5 ,seckillId);
    }
    @Transactional
    /**
     * 使用注解控制事务方法的优点:
     * 1.开发团队达成一致的约定，明确标注实务方法的编程风格
     * 2.保证事务方法的执行时间尽可能的短,不要穿插其它的网络操作RPC/HTTP请求或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务,如:只有一条修改操作，只读操作
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        //SeckillId变了或者篡改了md5就匹配不上
        if(md5==null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存 + 购买逻辑
        Date nowTime = new Date();
        try {
            //记录购买行为
            int inserCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
            //唯一:seckillId,userPhone
            if (inserCount<=0){
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            }else{
                //减库存,热点商品竞争! 减少网络延迟，根据返回结果来判断是Commit还是RollBack
                int updateCount = seckillDao.reduceNumber(seckillId,nowTime);
                if (updateCount<=0){
                    //没有更新操作，秒杀结束! RollBack
                    throw new SeckillCloseException("seckill  is  close ");
                }else{
                    //秒杀成功  Commit
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
                }
            }//【运行期异常 Spring声明式事务帮助我们RollBack回滚】
        }catch (SeckillCloseException e1){ //提前对不同类的异常做catch
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        } catch (Exception e){
            logger.error(e.getMessage() , e );  //所有编译期异常，转化为运行期异常
            throw new SeckillException("seckill inner error"+e.getMessage());
        }
    }
}
