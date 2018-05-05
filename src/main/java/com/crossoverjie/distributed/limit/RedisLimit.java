package com.crossoverjie.distributed.limit;

import com.crossoverjie.distributed.util.ScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
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

    private JedisConnectionFactory jedis;
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
     * @return if true
     */
    public boolean limit() {
        String key = String.valueOf(System.currentTimeMillis() / 1000);
        Object result = null;
        try {
            RedisClusterConnection clusterConnection = jedis.getClusterConnection();
            JedisCluster jedisCluster = (JedisCluster) clusterConnection.getNativeConnection();
            result = jedisCluster.eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limit)));
            if (FAIL_CODE != (Long) result) {
                return true;
            } else {
                return false;
            }
        }catch (InvalidDataAccessApiUsageException e){
        }
        Jedis jedisConn = (Jedis)jedis.getConnection().getNativeConnection() ;
        result = jedisConn.eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limit)));

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


    /**
     *  the builder
     * @param <T>
     */
    public static class Builder<T extends JedisCommands>{
        private JedisConnectionFactory jedis = null ;

        private int limit = 200;


        public Builder(JedisConnectionFactory jedis){
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
