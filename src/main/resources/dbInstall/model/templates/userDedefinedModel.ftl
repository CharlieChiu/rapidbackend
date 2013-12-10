-- -----------------------------------------------------
-- Table `${dbName}`.`${model.modelName}`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `${dbName}`.`${model.modelName}` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `created` BIGINT(20) NOT NULL ,
  `modified` BIGINT(20) NOT NULL ,
  <#list model.modelConfig as collumn>
  `${collumn.name}` ${collumn.dataType} <#if collumn.notNull>NOT NULL</#if> <#if (collumn.defaultValue != "")> DEFAULT '${collumn.defaultValue}' </#if>,
  <#if collumn.createDbIndex>
  <#if collumn.unique>UNIQUE </#if>INDEX `${collumn.name}_idx<#if collumn.unique>_unq</#if>` (`${collumn.name}` ASC),
  </#if>
  </#list>
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;