-- -----------------------------------------------------
-- Table `${dbName}`.`${followableConfig.subscriptionTableName}`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `${dbName}`.`${followableConfig.subscriptionTableName}` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `followable` INT(11) NOT NULL ,
  `follower` INT(11) NOT NULL ,
  `created` BIGINT(20) NOT NULL ,
  `modified` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`id`) ,
  INDEX `follower_idx` (`follower` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = '${followableConfig.subscriptionTableName}';