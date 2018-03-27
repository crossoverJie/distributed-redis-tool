# distributed-lock-redis

This is a simple distributed lock based on Redis.


## Quick start

### Install

```
git clone https://github.com/crossoverJie/distributed-lock-redis.git

mvn clean install
```

### How to use?

maven dependency:

```xml
<dependency>
    <groupId>com.crossoverjie.distributed-lock</groupId>
    <artifactId>redis-lock</artifactId>
    <version>1.0.0-SNAPSHOT</version>
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



## Features

- [x] High performance.
- [x] No deadlock.
- [x] Support Redis cluster, single.
- [x] Non-blocking lock.
- [ ] blocking lock.
