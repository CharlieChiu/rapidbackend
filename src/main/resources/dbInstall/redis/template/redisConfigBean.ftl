<bean id="${beanId}" class="com.rapidbackend.util.comm.redis.client.RedisPoolConfig">
    <property name="port" value="${redisConfig.port}"></property>
    <property name="hostAddress" value="${redisConfig.hostAddress}"></property>
    <property name="targetName" value="${redisConfig.targetName}"></property>
    <property name="poolCapacity" value="${redisConfig.poolCapacity}"></property>
</bean>