# distributed-redis-tool

[![Build Status](https://travis-ci.org/crossoverJie/distributed-redis-tool.svg?branch=master)](https://travis-ci.org/crossoverJie/distributed-redis-tool)
[![codecov](https://codecov.io/gh/crossoverJie/distributed-redis-tool/branch/master/graph/badge.svg)](https://codecov.io/gh/crossoverJie/distributed-redis-tool)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/top.crossoverjie.opensource/distributed-redis-tool/badge.svg)](https://maven-badges.herokuapp.com/maven-central/top.crossoverjie.opensource/distributed-redis-tool/)


This is a simple distributed tools based on Redis.


## Distributed lock

* [Features](https://github.com/crossoverJie/distributed-redis-tool#features)
* [Non-blocking lock](https://github.com/crossoverJie/distributed-redis-tool#non-blocking-lock)
* [Blocking lock API](https://github.com/crossoverJie/distributed-redis-tool#blocking-lock)
* [Blocking lock, Custom block time](https://github.com/crossoverJie/distributed-redis-tool#blocking-lock-custom-block-time)


## Distributed limiting

* [Features](https://github.com/crossoverJie/distributed-redis-tool#features-1)
* [Native API](https://github.com/crossoverJie/distributed-redis-tool#native-api)
* [@ControllerLimit API](https://github.com/crossoverJie/distributed-redis-tool#controllerlimit)
* [@CommonLimit API](https://github.com/crossoverJie/distributed-redis-tool#controllerlimit)


## Contact

Mail: crossoverJie@gmail.com

![weixinchat.jpg](https://crossoverjie.top/uploads/weixinchat.jpg)



## Distributed lock

### Features

- [x] High performance.
- [x] No deadlock.
- [x] Support Redis cluster, single.
- [x] Non-blocking lock.
- [x] blocking lock.


### Quick start



maven dependency:

```xml
<dependency>
    <groupId>top.crossoverjie.opensource</groupId>
    <artifactId>distributed-redis-tool</artifactId>
    <version>1.0.2</version>
</dependency>
```

Set bean:

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

#### Non-blocking lock:

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

#### Blocking lock

```java
redisLock.lock(String key, String request);
```

#### Blocking lock, Custom block time

```java
redisLock.lock(String key, String request,int blockTime);
```


----

## Distributed limiting
### Features

- [x] High performance.
- [x] native API.
- [x] Annation API.
- [x] Support Redis cluster, single.
- [x] Suppport Spring4.x+


### Quick start

maven dependency:

```xml
<dependency>
    <groupId>top.crossoverjie.opensource</groupId>
    <artifactId>distributed-redis-tool</artifactId>
    <version>1.0.2</version>
</dependency>
```

Set bean:

```java
@Configuration
public class RedisLimitConfig {


    @Value("${redis.limit}")
    private int limit;


    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Bean
    public RedisLimit build() {
        //Need to get Redis connection 
        RedisClusterConnection clusterConnection = jedisConnectionFactory.getClusterConnection();
        JedisCluster jedisCluster = (JedisCluster) clusterConnection.getNativeConnection();
        RedisLimit redisLimit = new RedisLimit.Builder<>(jedisCluster)
                .limit(limit)
                .build();

        return redisLimit;
    }
}
```

#### Native API:

```java
  	
    boolean limit = redisLimit.limit();
    if (!limit){
        res.setCode(StatusEnum.REQUEST_LIMIT.getCode());
        res.setMessage(StatusEnum.REQUEST_LIMIT.getMessage());
        return res ;
    }
```

Other apis:

#### @ControllerLimit

```java
    @ControllerLimit
    public BaseResponse<OrderNoResVO> getOrderNoLimit(@RequestBody OrderNoReqVO orderNoReq) {
        BaseResponse<OrderNoResVO> res = new BaseResponse();
        res.setReqNo(orderNoReq.getReqNo());
        if (null == orderNoReq.getAppId()){
            throw new SBCException(StatusEnum.FAIL);
        }
        OrderNoResVO orderNoRes = new OrderNoResVO() ;
        orderNoRes.setOrderId(DateUtil.getLongTime());
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        res.setDataBody(orderNoRes);
        return res ;
    }
```

Used for `@RequestMapping`.

#### @CommonLimit

```java
@CommonLimit
public void anyMethod(){}
```

It can be used for any Methods.




