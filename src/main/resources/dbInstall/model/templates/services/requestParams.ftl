<?xml version="1.0" encoding="UTF-8"?>
<beans 
  xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context"
  xmlns:tx="http://www.springframework.org/schema/tx"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd
        http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">
<#list modelParamsList as modelParams>
<!-- params for model  ${modelParams.modelName} -->
    <#list modelParams.params as param>
    <#if param.skipInSchemaGenerate> <!--Generator: commented out by because we already have one param ${param.beanName} with the same name and type</#if>
<bean id="${param.beanName}" class="${param.class.name}">
  <property name="name" value="${param.name}"></property>
</bean>
<#if param.skipInSchemaGenerate> --> 
    </#if>
    </#list>
</#list>

</beans>