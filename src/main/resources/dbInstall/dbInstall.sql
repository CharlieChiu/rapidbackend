SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `goodtaste`;

CREATE SCHEMA IF NOT EXISTS `goodtaste` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ;
USE `goodtaste` ;


-- -----------------------------------------------------
-- Table `goodtaste`.`feedsource`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `goodtaste`.`feedsource` (
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
-- Table `goodtaste`.`oauthapplication`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `goodtaste`.`oauthapplication` (
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
-- Table `goodtaste`.`oauthapplicationuser`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `goodtaste`.`oauthapplicationuser` (
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
-- Table `goodtaste`.`oauthtokenassociation`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `goodtaste`.`oauthtokenassociation` (
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

CREATE  TABLE IF NOT EXISTS `goodtaste`.`fileuploaded` (
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



-- -----------------------------------------------------

-- Table `goodtaste`.`user`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `goodtaste`.`user` (

  `id` INT(11) NOT NULL AUTO_INCREMENT ,

  `screenName` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,

  `password` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,

  `email` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,

  `userUrl` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,

  `created` BIGINT(20) NOT NULL ,

  `modified` BIGINT(20) NOT NULL ,
  PRIMARY KEY (`id`) ,

  UNIQUE INDEX `screenname_UNIQUE` (`screenName` ASC) )

ENGINE = InnoDB

AUTO_INCREMENT = 0

DEFAULT CHARACTER SET = utf8

COLLATE = utf8_unicode_ci;



-- -----------------------------------------------------
-- Table `goodtaste`.`userfeed`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `goodtaste`.`userfeed` (
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

-- -----------------------------------------------------
-- Table `goodtaste`.`usersubscription`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `goodtaste`.`usersubscription` (
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
COMMENT = 'usersubscription';

-- -----------------------------------------------------

-- Table `goodtaste`.`feed`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `goodtaste`.`userfeedcontent` (

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
  PRIMARY KEY (`id`) ,

  INDEX `useridIdx` (`userId` ASC)  )

ENGINE = InnoDB

AUTO_INCREMENT = 0

DEFAULT CHARACTER SET = utf8

COLLATE = utf8_unicode_ci;



-- -----------------------------------------------------
-- Table `goodtaste`.`comment`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `goodtaste`.`userfeedcomment` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `created` BIGINT(20) NOT NULL ,
  `modified` BIGINT(20) NOT NULL ,
  `feedId` INT(11) NOT NULL ,
  `userId` INT(11) NOT NULL COMMENT 'author id of this comment',
  `content` LONGTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `screenName` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  INDEX `feedId_idx` (`feedId` ASC),
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;
-- -----------------------------------------------------
-- Table `goodtaste`.`followable`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `goodtaste`.`group` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `modified` BIGINT(20) NOT NULL ,
  `created` BIGINT(20) NOT NULL ,
  `createdby` INT(11) NOT NULL DEFAULT 0,
  `name` VARCHAR(255) NOT NULL ,
  UNIQUE INDEX `name_idx_unq` (`name` ASC),
  `type` INT  ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `goodtaste`.`groupfeed`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `goodtaste`.`groupfeed` (
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

-- -----------------------------------------------------
-- Table `goodtaste`.`groupsubscription`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `goodtaste`.`groupsubscription` (
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
COMMENT = 'groupsubscription';

-- -----------------------------------------------------

-- Table `goodtaste`.`feed`

-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `goodtaste`.`groupfeedcontent` (

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
  `haha` VARCHAR(255)  ,
  `hoho` LONGTEXT  ,
  PRIMARY KEY (`id`) ,

  INDEX `useridIdx` (`userId` ASC)  )

ENGINE = InnoDB

AUTO_INCREMENT = 0

DEFAULT CHARACTER SET = utf8

COLLATE = utf8_unicode_ci;



-- -----------------------------------------------------
-- Table `goodtaste`.`comment`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `goodtaste`.`groupfeedcomment` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `created` BIGINT(20) NOT NULL ,
  `modified` BIGINT(20) NOT NULL ,
  `feedId` INT(11) NOT NULL ,
  `userId` INT(11) NOT NULL COMMENT 'author id of this comment',
  `content` LONGTEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NOT NULL ,
  `screenName` VARCHAR(512) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL ,
  `where` FLOAT  ,
  INDEX `feedId_idx` (`feedId` ASC),
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `goodtaste`.`profiles`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `goodtaste`.`profiles` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `created` BIGINT(20) NOT NULL ,
  `modified` BIGINT(20) NOT NULL ,
  `name` VARCHAR(255)  ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table `goodtaste`.`city`
-- -----------------------------------------------------

CREATE  TABLE IF NOT EXISTS `goodtaste`.`city` (
  `id` INT(11) NOT NULL AUTO_INCREMENT ,
  `created` BIGINT(20) NOT NULL ,
  `modified` BIGINT(20) NOT NULL ,
  `come` LONGTEXT  ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;



USE `goodtaste` ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
