<bean id="Delete${modelName}Pipeline" class="com.rapidbackend.core.process.StandardPipeline">
    <property name="handlers">
        <list>
             <ref bean="Delete${modelName}Handler"></ref>   
        </list>
    </property>
</bean>
<bean id="Delete${modelName}Handler" class="com.rapidbackend.socialutil.process.handler.db.DeleteSingleDbRecordHandler"><!-- delete one ${modelName} in database-->
    <property name="dao" ref="${modelName}Dao"></property>
</bean>