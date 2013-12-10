-- -----------------------------------------------------
-- Table `${dbName}`.`followable`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `${dbName}`.`${followableConfig.name}` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `modified` BIGINT(20) NOT NULL ,
  `created` BIGINT(20) NOT NULL ,
  `createdby` INT(11) NOT NULL DEFAULT 0,
  <#list followableConfig.collumnsAppendToFollowableTable as collumn>
  `${collumn.name}` ${collumn.dataType} <#if collumn.notNull>NOT NULL</#if> <#if (collumn.defaultValue != "")> DEFAULT '${collumn.defaultValue}' </#if>,
  <#if collumn.createDbIndex>
  <#if collumn.unique>UNIQUE </#if>INDEX `${collumn.name}_idx<#if collumn.unique>_unq</#if>` (`${collumn.name}` ASC),
  </#if>
  </#list>
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;