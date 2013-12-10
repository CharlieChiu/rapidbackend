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

<bean id="SocialUtility" class="com.rapidbackend.socialutil.core.SocialUtility">
    <property name="redisInitializer" ref="RedisInitializer"></property>
    <property name="serviceRegistry" ref="SocialUtilityServiceRegistry"></property>
</bean>

<bean id="SocialUtilityServiceRegistry" class="com.rapidbackend.socialutil.install.service.ServiceRegistry">
        <property name="serviceBeans">
            <list>
<#list serviceBeans as serviceBean>
                <ref bean="${serviceBean}"></ref>
</#list>
            </list>
        </property>
</bean>

<bean id="FeedContentTruncater" class="com.rapidbackend.socialutil.model.util.ContentTruncater">
        <!-- <property name="contentLengthLimit" value="10"></property> -->
</bean>

<bean id="SocialUtilModelTypeFinder" class="com.rapidbackend.socialutil.model.util.ModelTypeFinder">
</bean>

</beans>