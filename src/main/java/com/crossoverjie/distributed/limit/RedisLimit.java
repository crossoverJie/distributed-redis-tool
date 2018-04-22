package com.crossoverjie.distributed.limit;

import com.crossoverjie.distributed.util.ScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.Collections;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/04/2018 15:54
 * @since JDK 1.8
 */
public class RedisLimit {

    private Object jedis;
    private int limit = 200;

    private static final int FAIL_CODE = 0;

    /**
     * lua script
     */
    private String script;

    private RedisLimit(Builder builder) {
        this.limit = builder.limit ;
        this.jedis = builder.jedis ;
        buildScript();
    }


    /**
     * limit traffic
     * @return
     */
    public boolean limit() {
        String key = String.valueOf(System.currentTimeMillis() / 1000);
        Object result = null;
        if (jedis instanceof Jedis) {
            result = ((Jedis) this.jedis).eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limit)));
        } else if (jedis instanceof JedisCluster) {
            result = ((JedisCluster) this.jedis).eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limit)));
        } else {
            //throw new RuntimeException("instance is error") ;
            return false;
        }

        if (FAIL_CODE != (Long) result) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * read lua script
     */
    private void buildScript() {
        script = ScriptUtil.getScript("limit.lua");
    }



    public static class Builder<T extends JedisCommands>{
        private T jedis = null ;

        private int limit = 200;


        public Builder(T jedis){
            this.jedis = jedis ;
        }

        public Builder limit(int limit){
            this.limit = limit ;
            return this;
        }

        public RedisLimit build(){
            return new RedisLimit(this) ;
        }

    }
}
