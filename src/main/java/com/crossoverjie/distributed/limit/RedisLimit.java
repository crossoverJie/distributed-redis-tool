package com.crossoverjie.distributed.limit;

import com.crossoverjie.distributed.util.ScriptUtil;
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
public class RedisLimit<T extends JedisCommands> {

    private T jedis;
    private int limit = 200;

    private static final int FAIL_CODE = 0;

    /**
     * lua script
     */
    private String script;

    public RedisLimit(int limit) {
        this.limit = limit;
        buildScript();
    }

    public RedisLimit() {
        buildScript();
    }


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

    public void setJedis(T jedis) {
        this.jedis = jedis;
    }
}
