<?xml version="1.0" encoding="UTF-8"?>
<beans 
  xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context"
  xmlns:tx="http://www.springframework.org/schema/tx"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd
        http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">
<!-- Please do not change this file unless you know how to hack source codes, change request schemas may result in some errors in request handling -->

<!-- GetFeedsSchema begin -->
<#list requestSchemaConfigs as requestSchemaConfig>
<bean id="CreateUserSchema" class="com.rapidbackend.core.request.RequestSchema">
  <property name="requiredParams" value="userId,content"></property>
  <property name="autowireModelPropertyAsParam" value="true"></property>
  <property name="command" value="CreateUser"></property>
</bean>
</#list>

</beans>