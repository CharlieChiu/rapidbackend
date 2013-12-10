<bean id="Query${modelName}Pipeline" class="com.rapidbackend.core.process.StandardPipeline">
    <property name="handlers">
        <list>
             <ref bean="Query${modelName}Handler"></ref>   
        </list>
    </property>
</bean>
<bean id="Query${modelName}Handler" class="com.rapidbackend.socialutil.process.handler.db.QueryDbRecordHandler"><!-- read one ${modelName} in database-->
    <property name="dao" ref="${modelName}Dao"></property>
</bean>