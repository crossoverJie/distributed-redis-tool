package com.crossoverjie.distributed.lock.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.Collections;

/**
 * Function: distributed lock
 *
 * @author crossoverJie
 *         Date: 26/03/2018 11:09
 * @since JDK 1.8
 */
public class RedisLock<T extends JedisCommands> {


    private static final String LOCK_MSG = "OK";

    private static final Long UNLOCK_MSG = 1L;
    
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    private static final String LOCK_PREFIX = "lock_" ;

    /**
     * default sleep time
     */
    private static final int DEFAULT_SLEEP_TIME = 100 ;


    private T jedis;

    /**
     * time millisecond
     */
    private static final int TIME = 1000 ;

    /**
     * Non-blocking lock
     * @param key
     *        lock business type
     * @param request
     *        value
     *
     * @return
     *        true lock success
     *        false lock fail
     *     
     */
    public  boolean tryLock(String key, String request) {
        String result = this.jedis.set(LOCK_PREFIX + key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, 10 * TIME);

        if (LOCK_MSG.equals(result)){
            return true ;
        }else {
            return false ;
        }
    }


    /**
     * Non-blocking lock
     * @param key
     *        lock business type
     * @param request
     *        value
     * @param expireTime
     *        custom expireTime
     *
     * @return
     *        true lock success
     *        false lock fail
     *
     */
    public  boolean tryLock( String key, String request, int expireTime) {
        String result = this.jedis.set(LOCK_PREFIX + key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

        if (LOCK_MSG.equals(result)){
            return true ;
        }else {
            return false ;
        }
    }


    /**
     * blocking lock
     * @param key
     * @param request
     */
    public void lock(String key, String request) throws InterruptedException {

        for (;;){
            String result = this.jedis.set(LOCK_PREFIX + key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, 10 * TIME);
            if (LOCK_MSG.equals(result)){
                break ;
            }

            Thread.sleep(DEFAULT_SLEEP_TIME) ;
        }

    }

    /**
     * unlock
     * @param key
     *
     * @param request
     *        request must be the same as lock request
     * @return
     */
    public  boolean unlock(String key,String request){
        //lua script
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

        Object result = null ;
        if (jedis instanceof Jedis){
            result = ((Jedis)this.jedis).eval(script, Collections.singletonList(LOCK_PREFIX + key), Collections.singletonList(request));
        }else if (jedis instanceof JedisCluster){
            result = ((JedisCluster)this.jedis).eval(script, Collections.singletonList(LOCK_PREFIX + key), Collections.singletonList(request));
        }else {
            throw new RuntimeException("instance is error") ;
        }

        if (UNLOCK_MSG.equals(result)){
            return true ;
        }else {
            return false ;
        }
    }




    public void setJedis(T jedis) {
        this.jedis = jedis;
    }
}
