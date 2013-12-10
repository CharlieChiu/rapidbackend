<?xml version="1.0" encoding="UTF-8"?>

<beans 
  xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context"
  xmlns:tx="http://www.springframework.org/schema/tx"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd
        http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">
        
<context:annotation-config />

<#list redisConfigBeans as redisConfigBean>
${redisConfigBean}

</#list>

<bean id="RedisInitializer" class="com.rapidbackend.socialutil.core.cache.RedisInitializer">
    <property name="cacheConfigs">
        <list>
            <ref bean="MetadataCounter"/>
        </list>
    </property>
</bean>


<!-- model caches BEGIN-->
<bean id="FeedCache" class="com.rapidbackend.cache.RedisCache">
    <property name="cacheMapper" ref="FeedCacheMapper"></property>
</bean>
<bean id="FeedCacheMapper" class="com.rapidbackend.socialutil.sharding.defaultmappers.FeedCacheMapper">
</bean>

<bean id="UserCache" class="com.rapidbackend.cache.RedisCache">
<property name="cacheMapper" ref="UserCacheMapper"></property>
</bean>
<bean id="UserCacheMapper" class="com.rapidbackend.socialutil.sharding.defaultmappers.UserCacheMapper">
</bean>
<!-- model cache END -->

</beans>