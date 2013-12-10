<bean id="Create${modelName}Pipeline" class="com.rapidbackend.core.process.StandardPipeline">
    <property name="handlers">
        <list>
             <ref bean="Create${modelName}Handler"></ref>   
        </list>
    </property>
</bean>
<bean id="Create${modelName}Handler" class="com.rapidbackend.socialutil.process.handler.db.CreateSingleDbRecordHandler"><!-- create one ${modelName} in database-->
    <property name="dao" ref="${modelName}Dao"></property>
    <property name="interceptors">
        <list><#if createInterceptors??><#list createInterceptors as createIntetceptor>
        <ref bean="${createIntetceptor}"/>
        </#list></#if>
        </list>
    </property>
</bean>