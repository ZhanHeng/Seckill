-- 数据库初始化脚本
-- 创建数据库
CREATE DATABASE seckill;
-- 使用数据库
use seckill;

-- 创建秒杀库存表 innoDB引擎 utf8
CREATE TABLE seckill(
    seckill_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '秒杀商品id',
    name varchar(120) NOT NULL COMMENT '商品名称',
    number int NOT NULL COMMENT '库存数量',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    start_time timestamp NOT NULL COMMENT '秒杀开始时间',
    end_time timestamp NOT NULL COMMENT '秒杀结束时间',
    PRIMARY KEY (seckill_id),
    key idx_start_time (start_time),
    key idx_end_time (end_time),
    key idx_create_time (create_time)
)ENGINE=InnoDB AUTO_INCREMENT = 1000 DEFAULT CHARSET=utf8 COMMENT = '秒杀库存表';

-- 初始化数据
insert into seckill
    (name , number , start_time ,end_time)
values
    ('1000元秒杀iPhone 6',100 , '2016-11-11 00:00:00' , '2016-11-12 00:00:00'),
    ('500元秒杀ipad 2',200 , '2016-11-11 00:00:00' , '2016-11-12 00:00:00'),
    ('300元秒杀小米4',300 , '2016-11-11 00:00:00' , '2016-11-12 00:00:00'),
    ('200元秒杀红米Note',400 , '2016-11-11 00:00:00' , '2016-11-12 00:00:00'),
    ('1元秒杀150元店铺优惠券',500 , '2016-11-11 00:00:00' , '2016-11-12 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关信息，联合主键 innoDB引擎 utf8
CREATE TABLE success_killed(
    seckill_id bigint NOT NULL  COMMENT '秒杀商品id',
    user_phone bigint NOT NULL  COMMENT '用户手机号',
    state tinyint NOT NULL  COMMENT '状态标识：-1：无效  0：成功  1：已付款',
    create_time timestamp NOT NULL COMMENT '创建时间',
    PRIMARY KEY (seckill_id,user_phone),
    key idx_create_time (create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT = '秒杀成功明细表';

-- 为什么手写DDL，记录每次上线的DDL修改
-- 上线 V1.1
ALTER TABLE seckill
DROP index idx_create_time
