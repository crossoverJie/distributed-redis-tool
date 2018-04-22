package com.crossoverjie.distributed.limit;

import com.crossoverjie.distributed.util.ScriptUtil;
import redis.clients.jedis.JedisCommands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/04/2018 15:54
 * @since JDK 1.8
 */
public class RedisLimit<T extends JedisCommands> {

    /**
     * lua script
     */
    private String script;

    public RedisLimit() {
        buildScript();
    }



    private void limit(){

    }


    /**
     * read lua script
     */
    private void buildScript(){
        script = ScriptUtil.getScript("limit.lua") ;
    }
}
