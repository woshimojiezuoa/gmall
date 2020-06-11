package com.atguigu.gmall.pms.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.pms.annotation.GmallCache;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;

@Aspect
@Component
public class CacheAspect {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;
    /**
     * 1.返回值object
     * 2.参数proceedingJoinPoint
     * 3.抛出异常Throwable
     * 4.proceedingJoinPoint.proceed(args)执行业务方法
     */
    @Around("@annotation(com.atguigu.gmall.pms.annotation.GmallCache)")
    public Object cacheAroundMethod(ProceedingJoinPoint point) throws Throwable{
        Object result = null;
                // 获取连接点签名
        MethodSignature signature = (MethodSignature)point.getSignature();
        // 获取连接点的GmallCache注解信息
        GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);
        //获取注解中的值
        String prefix = gmallCache.prefix();
        //组装成key
        String key = prefix + Arrays.asList(point.getArgs()).toString();
        
        //1.查询缓存，缓存中有则直接返回序列化后的结果，如果没有则返回null
        Class returnType = signature.getReturnType(); // 获取方法返回类型
        result = this.cacheHit(key, returnType);
        if(result!=null){
            return result;
        }
        //2.初始化分布式锁（防止击穿）
        RLock lock = redissonClient.getLock("lock" + Arrays.asList(point.getArgs()).toString());
        //加锁
        lock.lock();
        //3.（再次检查内存是否有，因为高并发下，可能在加锁这段时间内，已有其他线程放入缓存）查询缓存，缓存中有则直接返回序列化后的结果，如果没有则返回null
        //3.1 如果有则返回并释放锁
        result = this.cacheHit(key, returnType);
        if(result!=null){
            lock.unlock();
            return result;
        }
        //4.执行查询的业务逻辑从数据库查询（AOP加强的方法）
        result = point.proceed(point.getArgs());

        //4.1.把结果放入缓存
        this.stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(result));
            //5。释放锁
        lock.unlock();

        return result;
    }

    //
    public Object cacheHit(String key,Class returnType){
        String cache = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.isNoneBlank(cache)){
            // 有，则反序列化，直接返回
            // 不能使用parseArray<cache, T>，因为不知道List<T>中的泛型
            return JSON.parseObject(cache, returnType);
        }
        return null;
    }
}
