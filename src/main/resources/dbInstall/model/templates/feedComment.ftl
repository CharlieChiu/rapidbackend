
-- -----------------------------------------------------
-- Table `${dbName}`.`comment`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `${dbName}`.`${followableConfig.feedCommentTableName}` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `created` BIGINT(20) NOT NULL ,
  `modified` BIGINT(20) NOT NULL ,
  `feedId` INT(11) NOT NULL ,
  `userId` INT(11) NOT NULL COMMENT 'author id of this comment',
  `content` LONGTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `screenName` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  <#list followableConfig.collumnsAppendToCommentTable as collumn>
  `${collumn.name}` ${collumn.dataType} <#if collumn.notNull>NOT NULL</#if> <#if (collumn.defaultValue != "")> DEFAULT '${collumn.defaultValue}' </#if>,
  <#if collumn.createDbIndex>
  <#if collumn.unique>UNIQUE </#if>INDEX `${collumn.name}_idx<#if collumn.unique>_unq</#if>` (`${collumn.name}` ASC),
  </#if>
  </#list>
  INDEX `feedId_idx` (`feedId` ASC),
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;