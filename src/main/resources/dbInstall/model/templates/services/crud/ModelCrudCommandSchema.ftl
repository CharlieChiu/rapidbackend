<?xml version="1.0" encoding="UTF-8"?>
<beans 
  xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context"
  xmlns:tx="http://www.springframework.org/schema/tx"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd
        http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">

<!-- Please do not change this file unless you know how to hack the codes, change request schemas may result in errors during request handling -->
<bean id="SocialUtilModelTypeFinder" class="com.rapidbackend.socialutil.model.util.ModelTypeFinder">
</bean>

<#list modelCrudSchemas as modelCrudSchema>

${modelCrudSchema}

</#list>

</beans>