<bean id="Read${modelName}Pipeline" class="com.rapidbackend.core.process.StandardPipeline">
    <property name="handlers">
        <list>
             <ref bean="Read${modelName}Handler"></ref>   
        </list>
    </property>
</bean>
<bean id="Read${modelName}Handler" class="com.rapidbackend.socialutil.process.handler.db.SelectSingleDbRecordHandler"><!-- read one ${modelName} in database-->
    <property name="dao" ref="${modelName}Dao"></property>
</bean>