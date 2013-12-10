<?xml version="1.0" encoding="UTF-8"?>

<beans 
  xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context"
  xmlns:tx="http://www.springframework.org/schema/tx"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd
        http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">
<!-- use spring 2.5 to avoid rebuilding code-->
<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource" destroy-method="close">
    <property name="driverClass" value="com.mysql.jdbc.Driver"/>
    <property name="jdbcUrl" value="${dbUri}/${dbName}?characterEncoding=utf-8"/>
    <property name="user" value="${userName}"/>
    <property name="password" value="${password}"/>
    <property name="maxIdleTime" value="28000"/>
    <property name="maxConnectionAge" value="28000"/>
  </bean>
  <bean id="transactionTemplate" class="org.springframework.transaction.support.TransactionTemplate">
    <property name="transactionManager" ref="transactionManager"/>
  </bean>
<!-- Transaction manager for a single JDBC DataSource -->
  <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
  </bean>
  
  <!-- Activates @Transactional for DefaultImageDatabase -->
  <tx:annotation-driven transaction-manager="transactionManager"/>
  
  <!-- DAO RELATED START -->
  <bean id="defaultLobHandler" class="org.springframework.jdbc.support.lob.DefaultLobHandler" lazy-init="true"/>
  
  <bean id="simpleJdbcTemplate" name="simpleJdbcTemplate"
    class="org.springframework.jdbc.core.simple.SimpleJdbcTemplate">
    <constructor-arg ref="dataSource"></constructor-arg>
  </bean>
  
  <bean id="jdbcTemplate" name="jdbcTemplate"
    class="org.springframework.jdbc.core.JdbcTemplate">
    <constructor-arg ref="dataSource"></constructor-arg>
  </bean>
  
  <#list tableDaos as tableDao>
  <bean id="${tableDao.insertName}" name="${tableDao.insertName}"
    class="org.springframework.jdbc.core.simple.SimpleJdbcInsert">
    <constructor-arg ref="dataSource"></constructor-arg>
    <property name="tableName" value="${tableDao.tableName}"></property>
    <property name="generatedKeyNames" value="id"></property>
  </bean>
    
    <bean id="${tableDao.templateName}" name="${tableDao.templateName}"
    class="org.springframework.jdbc.core.JdbcTemplate">
    <constructor-arg ref="dataSource"></constructor-arg>
  </bean>
    
  <bean id="${tableDao.daoName}" name="${tableDao.daoName}"
    class="${tableDao.daoClassName}">
    <property name="tableName" value="${tableDao.tableName}"></property>
    <property name="jdbcTemplate" ref="${tableDao.templateName}"></property>
    <property name="simpleJdbcInsert" ref="${tableDao.insertName}"></property>
    <property name="typeFinder" ref="SocialUtilModelTypeFinder"></property>
  </bean>
  </#list>
  
  </beans>