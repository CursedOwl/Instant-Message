package com.im.deal.service;

import com.im.deal.entity.RedBag;
import com.im.deal.mapper.RedBagMapper;
import com.im.feign.client.UserClient;
import com.im.feign.entity.ResponseEntity;
import com.im.feign.entity.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedBagService {

    private final Long BEGIN_TIME=1698749497670L;

    @Autowired
    private RedBagMapper redBagMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisTemplate<String,Integer> integerRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private final Random random=new Random();

    private static final DefaultRedisScript<String> RED_BAG_SCRIPT=new DefaultRedisScript<>();

    static {
        RED_BAG_SCRIPT.setLocation(new ClassPathResource("grab.lua"));
        RED_BAG_SCRIPT.setResultType(String.class);
    }

    @Transactional
    public Tuple2<Boolean,String> sendRedBag(RedBag redBag){
//        TODO 在MySQL内存一份记录，实际收发以Redis为准
//        1.首先查询用户余额，这个过程或许需要加锁（分布式锁）以免脏读
        Integer MIN = 1;
        if(redBag.getPeople()* MIN >redBag.getAmount()*100){
            return Tuples.of(false,"红包人数过多");
        }
        RLock lock = redissonClient.getLock("lock:account:" + redBag.getAccount());
        try {
            if (lock.tryLock()) {
                ResponseEntity balanceCB = userClient.money(new UserRequest(redBag.getAccount()));
                log.info("返回信息:"+balanceCB);
                if (balanceCB.getSuccess()) {
                    Double balance = (Double) balanceCB.getData();
//        2.如果余额不足，例如小于红包金额，则返回错误信息，并且解除锁
                    if (balance < redBag.getAmount()) {
                        return Tuples.of(false, "余额不足");
                    }
                } else {
                    throw new RuntimeException("查询余额失败");
                }
//        3.如果余额足够，则扣除余额，并且将红包请求信息存入MySQL进行备份,注意红包需要设置全局唯一ID
                Long redBagId = redisTemplate.opsForValue().increment("redBagId", 1);
                if(redBagId==null){
                    return Tuples.of(false,"红包ID生成失败");
                }
                redBag.setId(redBagId+System.currentTimeMillis()-BEGIN_TIME);

                userClient.deductMoney(new UserRequest(redBag.getAccount(),redBag.getAmount()));

                log.info("RedBag信息:{}",redBag);
                redBagMapper.insertRedBag(redBag);
            }
        } finally {
//        4.解除分布式锁
            lock.unlock();
        }
//        5.将红包信息存入Redis，并且设置TTL，并且对TTL过期进行监听（当TTL过期的时候获取事件信息后处理）
        List<Integer> perBags = generatePerBag(redBag);
        integerRedisTemplate.opsForList().leftPushAll("redBag:"+redBag.getId(),perBags);
//        TODO TTL过期需要监听,同时还需要在Redis保存一份剩余金额，以便于在TTL过期的时候进行处理
        integerRedisTemplate.expire("redBag:"+redBag.getId(),1, TimeUnit.DAYS);
//        不应该设置ttl，list过期的时候需要监听然后手动删除
        integerRedisTemplate.opsForValue().set("redBag:amount:"+redBag.getId(),(int) (redBag.getAmount()*100));

        return Tuples.of(true,redBag.getId().toString());
    }


    public Tuple2<Boolean, Double> grabRedBag(Long account, Long redBagId) {

        //        1.首先查询红包是否存在，如果不存在则返回错误信息
        if(Boolean.FALSE.equals(redisTemplate.hasKey("redBag:" + redBagId))){
            return Tuples.of(false, (double) Response.SC_NOT_FOUND);
        }
//        2.pop出一个，然后查询amount余额，减去，如果结果为0则说明抢完，删除对应kv（此时list被自动删除）
//        如果余额不为0，则减了之后重新设入，注意整个过程需要保证原子性，这个过程需要使用Lua脚本
        String result = redisTemplate.execute(RED_BAG_SCRIPT, Collections.emptyList(), redBagId.toString(),account.toString());

        assert result!=null;
        Double money=Integer.parseInt(result)/100.00;
        log.info("Money:{}",money);
//        如果返回值为0说明失败了，返回Tuple false并且不进行业务操作
        if(money==0){
            return Tuples.of(false,0.00);
        }
//        如果不为0，则存入抢到的用户的账户内，加分布式锁
        RLock lock = redissonClient.getLock("lock:account:" + account);
        try {
            if (lock.tryLock()) {
                userClient.addMoney(new UserRequest(account,money));

            }
        } finally {
            lock.unlock();
        }
        return Tuples.of(true,money);


    }

    public List<Integer> generatePerBag(RedBag redBag){
//        扩大100倍避免浮点数操作
        int amount=(int) (redBag.getAmount() * 100-redBag.getPeople());
        Integer count = redBag.getPeople();
        List<Integer> perBags=new ArrayList<>(count);

        for (int i = 0; i < count-1; i++) {
            int perBag = random.nextInt(amount/2);
            perBags.add(perBag+1);
            amount-=perBag;
        }
        perBags.add(amount+1);

        return perBags;
    }
}
