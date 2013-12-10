
-- -----------------------------------------------------

-- Table `${dbName}`.`user`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `${dbName}`.`user` (

  `id` INT(11) NOT NULL AUTO_INCREMENT ,

  `screenName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,

  `password` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,

  `email` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,

  `userUrl` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,

  `created` BIGINT(20) NOT NULL ,

  `modified` BIGINT(20) NOT NULL ,
  <#list followableConfig.collumnsAppendToFollowableTable as collumn>
  `${collumn.name}` ${collumn.dataType} <#if collumn.notNull>NOT NULL</#if> <#if (collumn.defaultValue != "")> DEFAULT '${collumn.defaultValue}' </#if>,
  <#if collumn.createDbIndex>
  <#if collumn.unique>UNIQUE </#if>INDEX `${collumn.name}_idx` (`${collumn.name}` ASC),
  </#if>
  </#list>
  PRIMARY KEY (`id`) ,

  UNIQUE INDEX `screenname_UNIQUE` (`screenName` ASC) )

ENGINE = InnoDB

AUTO_INCREMENT = 0

DEFAULT CHARACTER SET = utf8

COLLATE = utf8_unicode_ci;

