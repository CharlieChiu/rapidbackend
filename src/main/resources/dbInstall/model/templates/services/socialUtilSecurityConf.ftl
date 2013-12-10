<?xml version="1.0" encoding="UTF-8"?>

<beans 
  xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context"
  xmlns:tx="http://www.springframework.org/schema/tx"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd
        http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">
<bean id="SecurityManager" class="com.rapidbackend.security.shiro.SimpleSecurityManager" scope="prototype">
    <property name="realm" ref="UserLoginRealm"/><!--userloginrealm is a realm only supports the login command-->
    <property name="sessionManager" ref="DiabledSessionManager" />
</bean>
<!-- detect and called by spring -->
<bean id="lifecycleBeanPostProcessor" class="org.apache.shiro.spring.LifecycleBeanPostProcessor"/>

<!-- For simplest integration, so that all SecurityUtils.* methods work in all cases, -->
<!-- make the securityManager bean a static singleton.  DO NOT do this in web         -->
<!-- applications - see the 'Web Applications' section below instead.                 -->
<!--  Not using this util class for creating subject
<bean class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
    <property name="staticMethod" value="org.apache.shiro.SecurityUtils.setSecurityManager"/>
    <property name="arguments" ref="securityManager"/>
</bean>
-->
<bean id="UserLoginRealm" class="com.rapidbackend.socialutil.security.UserLoginRealm">
<property name="userDao" ref="UserDao"></property>
<property name="credentialsMatcher" ref="CredentialsMatcher"/>
<property name="supportedCommands">
<list>
<value>Login</value>
</list>
</property>
</bean>
<bean id="PasswordSalt" class="com.rapidbackend.security.shiro.MoreSimpleByteSource">
    <constructor-arg value="IAmSaltString"></constructor-arg> <!--warning this salt cann't be changed once you have create a user in database, otherwise you might never be able to login a registered user again-->
</bean>
<!-- password hash for shiro internal-->
<!--warning:com.rapidbackend.security.shiro.SimpleHashedCredentialsMatcher uses one salt for all users' passwords, you can override its getPasswordSalt method to implement your own salt management-->
<bean id="CredentialsMatcher" class="com.rapidbackend.security.shiro.SimpleHashedCredentialsMatcher">
    <property name="passwordSalt" ref="PasswordSalt"/>
    <property name="hashAlgorithmName" value="MD5"/><!-- variablename in super is HashAlgorithm, but the setter is setHashAlgorithmName()!!-->
    <property name="hashIterations" value="1"/>
</bean>
<!-- disable the scheduler in default shiro sessionManager let redis handle all the sessions. But we still keep one session manager to complie shiro's design-->
<bean id="DiabledSessionManager" class="com.rapidbackend.security.shiro.DisabledSessionManager">
</bean>
<bean id="PasswordEncrypter" class="com.rapidbackend.security.shiro.realms.PasswordEncrypter">
    <property name="encrypter" ref="CredentialsMatcher"/>
</bean>
<bean id="SessionConf" class="com.rapidbackend.security.session.SessionConf">
    <property name="sessionEnabled" value="true"></property>
</bean>
<bean id="UserStatusCacheConfig" class="com.rapidbackend.socialutil.feeds.config.UserStatusCacheConfig">
        <property name="journalFileName" value="UserStatusCache"></property>
</bean>
<bean id="UserStatusService" class="com.rapidbackend.socialutil.user.UserStatusService">
  <property name="userStatusCacheConfig" ref="UserStatusCacheConfig"></property>
</bean>
<bean id="LoginSchema" class="com.rapidbackend.core.request.RequestSchema">
  <property name="requiredParams" value="screenName|email,password"></property>
  <property name="optionalParams" value="sessionId"></property>
  <property name="command" value="Login"></property>
  <property name="autowireModelPropertyAsParam" value="true"></property>
  <property name="modelName" value="user"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
  <property name="modelTypeFinder" ref="SocialUtilModelTypeFinder"></property>
</bean>
<bean id="LoginPipeline" class="com.rapidbackend.core.process.StandardPipeline">
    <property name="timeout" value="1200000"></property><!-- command timeout -->
    <property name="handlers">
        <list>
            <ref bean="SessionInitHandler"/>
            <ref bean="SimpleLoginHandler"/>
            <ref bean="SessionStoreHandler"/>
        </list>
    </property>
</bean>
<bean id="LogoutSchema" class="com.rapidbackend.core.request.RequestSchema">
  <property name="requiredParams" value="sessionId,id"></property>
  <property name="command" value="Logout"></property>
  <property name="autowireModelPropertyAsParam" value="true"></property>
  <property name="modelName" value="user"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
  <property name="modelTypeFinder" ref="SocialUtilModelTypeFinder"></property>
</bean>
<bean id="LogoutPipeline" class="com.rapidbackend.core.process.StandardPipeline">
    <property name="timeout" value="1200000"></property><!-- command timeout -->
    <property name="handlers">
        <list>
            <ref bean="SessionDeletionHandler"/>
        </list>
    </property>
</bean>
<bean id="SessionDeletionHandler" class="com.rapidbackend.core.process.handler.session.SessionDeletionHandler">
    <property name="sessionStore"  ref="SessionStore"></property>
    <property name="sessionShardingkeyFactory" ref="SessionShardingkeyFactory"></property>
</bean>
<bean id="SessionCache" class="com.rapidbackend.cache.RedisCache">
    <property name="cacheMapper" ref="SessionCacheMapper"></property>
</bean>
<bean id="SessionCacheMapper" class="com.rapidbackend.socialutil.sharding.defaultmappers.SessionCacheMapper">
</bean>
<bean id="SessionStore" class="com.rapidbackend.security.session.SessionStore">
    <property name="redisCache"  ref="SessionCache"></property>
</bean>
<bean id="SessionInitHandler" class="com.rapidbackend.core.process.handler.session.SessionInitHandler">
    <property name="sessionStore"  ref="SessionStore"></property>
</bean>
<bean id="SessionStoreHandler" class="com.rapidbackend.core.process.handler.session.SessionStoreHandler">
    <property name="sessionStore"  ref="SessionStore"></property>
    <property name="sessionShardingkeyFactory" ref="SessionShardingkeyFactory"></property>
</bean>
<bean id="SimpleLoginHandler" class="com.rapidbackend.socialutil.process.handler.user.SimpleLoginHandler">
    <property name="userDao" ref="UserDao"></property>
    <property name="inboxServices">
        <list>
<#list inboxServiceBeans as serviceBean>
                <ref bean="${serviceBean}"/>
</#list>
        </list>
    </property>
</bean>

<bean id="SessionShardingkeyFactory" class="com.rapidbackend.core.process.handler.session.DefaultShardingkeyFactory">
    <property name="keyParam"  ref="id"></property><!-- default setting uses param 'id'-->
</bean>


<!-- follower session verify begin -->
<bean id="FollowerSessionStoreHandler" class="com.rapidbackend.core.process.handler.session.SessionStoreHandler"><!-- stores session for request creates by a 'follower' -->
    <property name="sessionStore"  ref="SessionStore"></property>
    <property name="sessionShardingkeyFactory" ref="FollowerSessionShardingkeyFactory"></property>
</bean>

<bean id="FollowerSessionShardingkeyFactory" class="com.rapidbackend.core.process.handler.session.DefaultShardingkeyFactory">
    <property name="keyParam"  ref="follower"></property><!-- default setting uses param 'id'-->
</bean>

<bean id="VerifyFollowerSessionPipeline" class="com.rapidbackend.core.process.StandardPipeline"> <!-- this pipeline verifies if a user session token is valid and will put the user object into the request's temporary data store -->
    
    <property name="handlers">
        <list>
            <ref bean="SessionInitHandler"/>
            <ref bean="VerifyUserSessionHandler"/>
        </list>
    </property>
</bean>

<bean id="VerifyUserSessionHandler" class="com.rapidbackend.socialutil.process.handler.user.VerifyUserSessionHandler">
    <property name="sessionStore"  ref="SessionStore"></property>
</bean>
<!-- follower session verify end -->

<!-- author session verify begin -->
<bean id="AuthorSessionStoreHandler" class="com.rapidbackend.core.process.handler.session.SessionStoreHandler"><!-- stores session for request creates by a 'follower' -->
    <property name="sessionStore"  ref="SessionStore"></property>
    <property name="sessionShardingkeyFactory" ref="AuthorSessionShardingkeyFactory"></property>
</bean>

<bean id="AuthorSessionShardingkeyFactory" class="com.rapidbackend.core.process.handler.session.DefaultShardingkeyFactory">
    <property name="keyParam"  ref="userId"></property><!-- default setting uses param 'id'-->
</bean>

<!-- the default session verification pipeline for common posts(including posts, comments,deletes,updates) -->
<bean id="VerifyAuthorSessionPipeline" class="com.rapidbackend.core.process.StandardPipeline"> <!-- this pipeline verifies if a user session token is valid and will put the user object into the request's temporary data store -->
    
    <property name="handlers">
        <list>
            <ref bean="SessionInitHandler"/>
            <ref bean="VerifyUserSessionHandler"/>
        </list>
    </property>
</bean>
<!-- author session verify end -->


</beans>