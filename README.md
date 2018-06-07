# distributed-redis-tool

[![Build Status](https://travis-ci.org/crossoverJie/distributed-redis-tool.svg?branch=master)](https://travis-ci.org/crossoverJie/distributed-redis-tool)
[![codecov](https://codecov.io/gh/crossoverJie/distributed-redis-tool/branch/master/graph/badge.svg)](https://codecov.io/gh/crossoverJie/distributed-redis-tool)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/top.crossoverjie.opensource/distributed-redis-tool/badge.svg)](https://maven-badges.herokuapp.com/maven-central/top.crossoverjie.opensource/distributed-redis-tool/)
[![](https://badge.juejin.im/entry/5ae3d0c56fb9a07aae14ea59/likes.svg?style=flat-square)](https://juejin.im/post/5ae3d08b6fb9a07a9e4cf71c)


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
* [@SpringControllerLimit](https://github.com/crossoverJie/distributed-redis-tool#springcontrollerlimit)
* [@CommonLimit API](https://github.com/crossoverJie/distributed-redis-tool#controllerlimit)


Visit this [website](https://www.google.com/search?newwindow=1&ei=SdjjWu3XKYXSU_SKgIgH&q=Distributed+site%3AcrossoverJie.top&oq=Distributed+site%3AcrossoverJie.top&gs_l=psy-ab.3...9719.30867.0.31049.47.33.7.0.0.0.519.5126.4-11j1.12.0....0...1.1j4.64.psy-ab..31.0.0....0.ltPqVipC_iE) for more information.


## ChangeLog

### v1.0.4
- Upgrade distributed lock API.
- Support connection pool.
- Improve distributed lock performance :zap:.

### v1.0.3

- Upgrade API.
- Add Anation.
- Improve performance :zap: .


## Contact

Mail: crossoverJie@gmail.com

![](https://ws1.sinaimg.cn/large/006tKfTcly1frz6eaf3s4j308c0au0ss.jpg)



## Distributed lock

### Features

- [x] High performance.
- [x] No deadlock.
- [x] Support Redis cluster, single.
- [x] Non-blocking lock.
- [x] blocking lock.
- [x] Support connection pool.
- [x] Suppport Spring4.x+.


### Quick start



maven dependency:

```xml
<dependency>
    <groupId>top.crossoverjie.opensource</groupId>
    <artifactId>distributed-redis-tool</artifactId>
    <version>1.0.4</version>
</dependency>
```

Set bean:

```java
@Configuration
public class RedisLockConfig {
    private Logger logger = LoggerFactory.getLogger(RedisLockConfig.class);
    
    
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    
    @Bean
    public RedisLock build() {
        RedisLock redisLock = new RedisLock.Builder(jedisConnectionFactory,RedisToolsConstant.SINGLE)
                .lockPrefix("lock_")
                .sleepTime(100)
                .build();

        return redisLock;
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
    <version>1.0.4</version>
</dependency>
```

1. Set bean:

```java
@Configuration
public class RedisLimitConfig {
    private Logger logger = LoggerFactory.getLogger(RedisLimitConfig.class);
    
    @Value("${redis.limit}")
    private int limit;
    
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    
    @Bean
    public RedisLimit build() {
        RedisLimit redisLimit = new RedisLimit.Builder(jedisConnectionFactory, RedisToolsConstant.SINGLE)
                .limit(limit)
                .build();
        return redisLimit;
    }
}
```

2. Scan `com.crossoverjie.distributed.intercept` package.

```java
@ComponentScan(value = "com.crossoverjie.distributed.intercept")
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


#### @SpringControllerLimit

If you are using native Spring:

```java
    @SpringControllerLimit(errorCode = 200,errorMsg = "request has limited")
    @RequestMapping("/createOptimisticLimitOrderByRedis/{sid}")
    @ResponseBody
    public String createOptimisticLimitOrderByRedis(@PathVariable int sid) {
        logger.info("sid=[{}]", sid);
        int id = 0;
        try {
            id = orderService.createOptimisticOrderUseRedis(sid);
        } catch (Exception e) {
            logger.error("Exception",e);
        }
        return String.valueOf(id);
    }
```

Spring xml:

```xml
   <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/**"/> 
            <bean class="com.crossoverjie.distributed.intercept.SpringMVCIntercept"/>
        </mvc:interceptor>
    </mvc:interceptors>
```

#### @CommonLimit

```java
@CommonLimit
public void anyMethod(){}
```

It can be used for any Methods.




