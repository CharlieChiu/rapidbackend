<?xml version="1.0" encoding="UTF-8"?>

<beans 
  xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:context="http://www.springframework.org/schema/context"
  xmlns:tx="http://www.springframework.org/schema/tx"
  xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd
        http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">
<!-- please do not change this file unless you have hacked the related source code or you want to add your own commands-->
<#list modelCrudPipelines as modelCrudPipeline>
<!--START  create commands for model ${modelCrudPipeline.modelName}  -->
${modelCrudPipeline.schemas}

${modelCrudPipeline.createPipeline}

${modelCrudPipeline.readPipeline}

${modelCrudPipeline.updatePipeline}

${modelCrudPipeline.deletePipeline}

${modelCrudPipeline.queryPipeline}
<!--END  create commands for model ${modelCrudPipeline.modelName}  -->
</#list>
</beans>