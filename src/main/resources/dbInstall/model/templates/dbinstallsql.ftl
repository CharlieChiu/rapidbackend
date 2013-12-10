SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `${dbName}`;

CREATE SCHEMA IF NOT EXISTS `${dbName}` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ;
USE `${dbName}` ;


-- -----------------------------------------------------
-- Table `${dbName}`.`feedsource`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `${dbName}`.`feedsource` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `url` VARCHAR(500) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `content` LONGTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `created` BIGINT(20) NOT NULL ,
  `modified` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `${dbName}`.`oauthapplication`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `${dbName}`.`oauthapplication` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `owner` INT(11) NULL DEFAULT NULL ,
  `consumerKey` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `name` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `description` VARCHAR(500) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `icon` VARCHAR(500) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL COMMENT 'icon file url or location' ,
  `sourceUrl` VARCHAR(500) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `homepage` VARCHAR(500) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL COMMENT 'url' ,
  `organization` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `callbackUrl` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `type` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `accessType` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` BIGINT(20) NOT NULL ,
  `modified` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) )
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


-- -----------------------------------------------------
-- Table `${dbName}`.`oauthapplicationuser`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `${dbName}`.`oauthapplicationuser` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `userId` INT(11) NOT NULL ,
  `applicationId` INT(11) NOT NULL ,
  `accessType` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `code` VARCHAR(256) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `created` BIGINT(20) NOT NULL ,
  `modified` BIGINT(20) NOT NULL ,
  `uid` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `screenName` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `thirdpartyunique` (`applicationId` ASC, `uid` ASC) ,
  UNIQUE INDEX `thirdpartynameunique` (`applicationId` ASC, `screenName` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci
COMMENT = 'stores user profiles from third party user info provider';


-- -----------------------------------------------------
-- Table `${dbName}`.`oauthtokenassociation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `${dbName}`.`oauthtokenassociation` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `userId` INT(11) NOT NULL DEFAULT 0 ,
  `applicationId` INT(11) NOT NULL DEFAULT 0 ,
  `token` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL DEFAULT 'invalid' ,
  `created` BIGINT(20) NULL DEFAULT NULL ,
  `modified` BIGINT(20) NULL DEFAULT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE  TABLE IF NOT EXISTS `${dbName}`.`fileuploaded` (
`id` INT(11) NOT NULL AUTO_INCREMENT,
`userid` INT(11) NOT NULL ,
`modified` BIGINT(20) NOT NULL ,
`created` BIGINT(20) NOT NULL ,
`fileDescription` VARCHAR(512) NULL DEFAULT NULL ,
`fileLocation` VARCHAR(512) NOT NULL ,
PRIMARY KEY (`id`) ,
INDEX `userId_idx` (`userid` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;


<#list followableConfigs as followableConfig>
${followableConfig.followableTableSql}

${followableConfig.feedByFollowableTableSql}

${followableConfig.subscriptionTableSql}

${followableConfig.feedContentTableSql}

${followableConfig.feedCommentTableSql}
</#list>

<#list userDefinedModelConfigs as userDefinedModel>
${userDefinedModel.installSql}

</#list>


USE `${dbName}` ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
