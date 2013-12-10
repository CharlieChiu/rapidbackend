<bean id="Update${modelName}Pipeline" class="com.rapidbackend.core.process.StandardPipeline">
    <property name="handlers">
        <list>
             <ref bean="Update${modelName}Handler"></ref>   
        </list>
    </property>
</bean>
<bean id="Update${modelName}Handler" class="com.rapidbackend.socialutil.process.handler.db.UpdateSingleDbRecordHandler"><!-- update one ${modelName} in database-->
    <property name="dao" ref="${modelName}Dao"></property>
</bean>