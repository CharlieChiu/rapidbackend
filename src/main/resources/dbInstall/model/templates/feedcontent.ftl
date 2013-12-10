-- -----------------------------------------------------

-- Table `${dbName}`.`feed`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `${dbName}`.`${followableConfig.feedContentTableName}` (

  `id` INT(11) NOT NULL AUTO_INCREMENT ,

  `userId` INT(11) NOT NULL DEFAULT '-1' COMMENT 'application user id' ,

  `truncated` INT(11) NULL DEFAULT '0' COMMENT 'truncate if the content exceeds a length limit' ,

  `content` LONGTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,

  `seedFeedId` INT(11) NULL DEFAULT '0' COMMENT 'the begining of this thread,0 if seed is itself' ,

  `replyToId` INT(11) NULL DEFAULT '0' ,

  `replyToScreenName` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,

  `repostToFeedId` INT(11) NULL DEFAULT '0' COMMENT 'repost to which feed' ,

  `repostToUserId` INT(11) NULL DEFAULT '0' COMMENT 'repost to which user' ,

  `geoEnabled` INT(11) NULL DEFAULT '0' ,

  `latitude` FLOAT NULL DEFAULT NULL COMMENT 'latitude' ,

  `longitude` FLOAT NULL DEFAULT NULL COMMENT 'lontitude' ,

  `location` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,

  `sourceId` INT(11) NULL DEFAULT NULL COMMENT 'which app or browser or ...does it  come from' ,

  `uniqueId` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT '' COMMENT 'the unique id inside the database\ncanbe md5 hash of the ID number or a uuid' ,

  `modified` BIGINT(20) NOT NULL DEFAULT '0' ,

  `created` BIGINT(20) NOT NULL DEFAULT '0' ,
  <#list followableConfig.collumnsAppendToFeedContentTable as collumn>
  `${collumn.name}` ${collumn.dataType} <#if collumn.notNull>NOT NULL</#if> <#if (collumn.defaultValue != "")> DEFAULT '${collumn.defaultValue}' </#if>,
  <#if collumn.createDbIndex>
  INDEX `${collumn.name}_idx` (`${collumn.name}` ASC),
  </#if>
  </#list>
  PRIMARY KEY (`id`) ,

  INDEX `useridIdx` (`userId` ASC)  )

ENGINE = InnoDB

AUTO_INCREMENT = 0

DEFAULT CHARACTER SET = utf8

COLLATE = utf8_unicode_ci;
