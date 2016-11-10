-- 秒杀的存储过程
DELIMITER $$
-- IN表示输入参数，out是输出参数
-- row_count() ： 返回上一条修改类型的sql影响的行数
-- 0 未修改，>0 表示修改行数 ,<0 sql出错
CREATE PROCEDURE  `seckill`.`execute_seckill`
  (in v_seckill_id BIGINT , IN v_phone BIGINT ,
   IN v_kill_time TIMESTAMP , OUT r_result INT)
  BEGIN
    DECLARE insert_count int DEFAULT 0;
    START TRANSACTION ;
    INSERT IGNORE INTO success_killed
    (seckill_id, user_phone, create_time)
      VALUES (v_seckill_id,v_phone,v_kill_time);
    SELECT  row_count() INTO insert_count;
    IF (insert_count = 0)THEN
      ROLLBACK ;
      SET r_result = -1 ;
    ELSEIF (insert_count < 0)THEN
      ROLLBACK ;
      SET r_result = -2 ;
    ELSE
      UPDATE seckill
        SET number = number-1
      WHERE seckill_id = v_seckill_id
      AND end_time>v_kill_time
      AND start_time<v_kill_time
      AND number>0;
      SELECT row_count() INTO insert_count;
      IF (insert_count = 0)THEN
        ROLLBACK ;
        SET r_result = 0 ;
      ELSEIF (insert_count < 0)THEN
        ROLLBACK ;
        SET r_result = -2 ;
      ELSE
        COMMIT ;
        SET r_result = 1 ;
      END IF ;
    END IF ;
  END ;
$$

DELIMITER ;
set @r_result = -3;
CALL execute_seckill(1003,13500008888,now(),@r_result);
-- 获取变量
SELECT @r_result;

-- 存储过程优化
-- 1.减少 事务行级锁持有的时间，让其尽可能短
-- 2.不要过度依赖存储过程（在银行大规模使用）
-- 3.简单的逻辑可以应用存储过程
-- 4.QPS: 一个秒杀单 接近 6000/qps