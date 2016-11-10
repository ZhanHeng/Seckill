package org.seckill.dao;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.beans.Expression;

/**
 * Created by ZhanHeng on 2016/11/10.
 */
public class RedisDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JedisPool jedisPool;
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip , int port) {
        this.jedisPool = new JedisPool(ip,port);
    }

    public Seckill getSeckill(long seckillId){
         try {
             Jedis jedis = jedisPool.getResource();
             try {
                 String key = "seckill:"+seckillId;
                 //那到Key的字节数组 getByte[] ->反序列化 -> Object(Seckill)
                 //自定义使用Google的protostuff 反序列化
                 byte[] bytes =  jedis.get(key.getBytes());
                 if(bytes!=null){
                     Seckill seckill = schema.newMessage();
                     //调用这句话seckill就自动赋值了，即实现了反序列化，妙用工具类
                     ProtobufIOUtil.mergeFrom(bytes,seckill,schema);
                     return seckill;
                 }
             }finally {
                 jedis.close();
             }
          }catch (Exception e){
              logger.error(e.getMessage(),e);
          }
        return null;
    }

    public String putSeckill(Seckill seckill){
        //Object(Seckill) ->byte[]  序列化的过程
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckill.getSeckillId();
                //自定义使用Google的protostuff 序列化
                byte[] bytes =  ProtobufIOUtil.toByteArray(seckill,schema,LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                int timeout = 60*60; //超市缓存，1小时
                String result = jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

}
