-- depreacted
-- -----------------------------------------------------
-- Table `${dbName}`.`${followableConfig}`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `${dbName}`.`followabledescriptor` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `modified` BIGINT(20) NOT NULL DEFAULT '0' ,
  `created` BIGINT(20) NOT NULL DEFAULT '0' ,
  `info` LONGTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) 
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;