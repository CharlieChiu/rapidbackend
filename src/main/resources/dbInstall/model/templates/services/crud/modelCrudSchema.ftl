<bean id="Create${modelName}Schema" class="com.rapidbackend.core.request.RequestSchema">
  <property name="requiredParams" value="${requiredFieldNamesForCreate}"></property>
  <property name="autowireModelPropertyAsParam" value="true"></property>
  <property name="modelName" value="${modelName}"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
  <property name="modelTypeFinder" ref="SocialUtilModelTypeFinder"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
</bean>

<bean id="Update${modelName}Schema" class="com.rapidbackend.core.request.RequestSchema">
  <property name="requiredParams" value="id"></property>
  <property name="autowireModelPropertyAsParam" value="true"></property>
  <property name="modelName" value="${modelName}"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
  <property name="modelTypeFinder" ref="SocialUtilModelTypeFinder"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
</bean>

<bean id="Read${modelName}Schema" class="com.rapidbackend.core.request.RequestSchema">
  <property name="requiredParams" value="id"></property>
  <property name="autowireModelPropertyAsParam" value="true"></property>
  <property name="modelName" value="${modelName}"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
  <property name="modelTypeFinder" ref="SocialUtilModelTypeFinder"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
</bean>

<bean id="Delete${modelName}Schema" class="com.rapidbackend.core.request.RequestSchema">
  <property name="requiredParams" value="id"></property>
  <property name="autowireModelPropertyAsParam" value="true"></property>
  <property name="modelName" value="${modelName}"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
  <property name="modelTypeFinder" ref="SocialUtilModelTypeFinder"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
</bean>

<bean id="Query${modelName}Schema" class="com.rapidbackend.core.request.RequestSchema">
  <property name="requiredParams" value="modelQuery"></property>
  <property name="optionalParams" value="start,pageSize,idOrder"></property>
  <property name="autowireModelPropertyAsParam" value="true"></property>
  <property name="modelName" value="${modelName}"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
  <property name="modelTypeFinder" ref="SocialUtilModelTypeFinder"></property><!-- this property must be set if autowireModelPropertyAsParam's been set to true -->
</bean>