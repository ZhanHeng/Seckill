package org.seckill.dao;

import org.seckill.entity.SuccessKilled;

/**
 * Created by ZhanHeng on 2016/11/8.
 */
public interface SuccessKilledDao {
    /**
     * 插入购买明细，可过滤重复
     * @param seckillId
     * @param userPhone
     * @return 插入行数
     */
    int insertSuccessKilled(long seckillId , long userPhone);

    /**
     * 根据id查询Successkilled并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(long seckillId);
}
