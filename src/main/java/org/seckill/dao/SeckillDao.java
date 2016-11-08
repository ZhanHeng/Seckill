package org.seckill.dao;

import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

/**
 * Created by ZhanHeng on 2016/11/8.
 */
public interface SeckillDao {
    /**
     * 减库存
     *
     * @param seckillId
     * @param killTime
     * @return 如果影响结果行数>0，表示更新的记录行数
     */
    int reduceNumber(long seckillId, Date killTime);

    /**
     * 根据Id查询秒杀商品
     *
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     *
     * @param offset
     * @param limit
     * @return
     */

    List<Seckill> queryAll(int offset, int limit);
}
