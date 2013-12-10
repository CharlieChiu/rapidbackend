-- -----------------------------------------------------
-- Table `${dbName}`.`${followableConfig.feedByFollowableTableName}`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `${dbName}`.`${followableConfig.feedByFollowableTableName}` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `feedId` INT(11) NOT NULL DEFAULT 0 ,
  `followableId` INT(11) NOT NULL DEFAULT 0 ,
  `repostToFeedId` INT(11) DEFAULT 0,
  `created` BIGINT(20) NULL DEFAULT NULL ,
  `modified` BIGINT(20) NULL DEFAULT NULL ,
  INDEX `followableId_idx` (`followableId` ASC),
  INDEX `repostToId_Idx` (`repostToFeedId` ASC),
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;