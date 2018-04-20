# distributed-lock-redis

[![Build Status](https://travis-ci.org/crossoverJie/distributed-redis-tool.svg?branch=master)](https://travis-ci.org/crossoverJie/distributed-redis-tool)
[![codecov](https://codecov.io/gh/crossoverJie/distributed-lock-redis/branch/master/graph/badge.svg)](https://codecov.io/gh/crossoverJie/distributed-lock-redis)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/top.crossoverjie.opensource/distributed-redis-lock/badge.svg)](https://maven-badges.herokuapp.com/maven-central/top.crossoverjie.opensource/distributed-redis-lock/)


This is a simple distributed lock based on Redis.



## Features

- [x] High performance.
- [x] No deadlock.
- [x] Support Redis cluster, single.
- [x] Non-blocking lock.
- [x] blocking lock.


## Quick start


### How to use?

maven dependency:

```xml
<dependency>
    <groupId>top.crossoverjie.opensource</groupId>
    <artifactId>distributed-redis-lock</artifactId>
    <version>1.0.0</version>
</dependency>
```

configure bean:

```java
@Configuration
public class RedisLockConfig {

    @Bean
    public RedisLock build(){
        RedisLock redisLock = new RedisLock() ;
        HostAndPort hostAndPort = new HostAndPort("127.0.0.1",7000) ;
        JedisCluster jedisCluster = new JedisCluster(hostAndPort) ;
        redisLock.setJedisCluster(jedisCluster) ;
        return redisLock ;
    }

}

```

Non-blocking lock like this:

```java
    @Autowired
    private RedisLock redisLock ;

    public void use() {
        String key = "key";
        String request = UUID.randomUUID().toString();
        try {
            boolean locktest = redisLock.tryLock(key, request);
            if (!locktest) {
                System.out.println("locked error");
                return;
            }


            //do something

        } finally {
            redisLock.unlock(key,request) ;
        }

    }

```

Other apis:


```java
//blocking lock
redisLock.lock(String key, String request);

//blocking lock, costom block time
redisLock.lock(String key, String request,int blockTime);

```

## Contact

Mail: crossoverJie@gmail.com

![weixinchat.jpg](https://crossoverjie.top/uploads/weixinchat.jpg)
