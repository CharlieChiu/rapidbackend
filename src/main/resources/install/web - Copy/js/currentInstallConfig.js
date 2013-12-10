{
  "dbName" : "goodtaste",
  "dbUri" : "jdbc:mysql://127.0.0.1:3306/",
  "username" : "root",
  "password" : "111111",
  "followableConfigs" : [ {
    "followableName" : "user",
    "referToAnotherFollowablesFeed" : false,
    "referringToFollowable" : "",
    "followableModelConfig" : [ {
      "name" : "profile",
      "createDbIndex" : false,
      "dataType" : "LONGTEXT",
      "type" : "text",
      "defaultValue" : "",
      "notNull" : true,
      "unique" : true
    } ],
    "followableFeedContentConfig" : [ ],
    "followableFeedCommentConfig" : [ {
      "name" : "why",
      "createDbIndex" : false,
      "dataType" : "varchar(255)",
      "type" : "varchar",
      "defaultValue" : "",
      "notNull" : true,
      "unique" : false
    }, {
      "name" : "score",
      "createDbIndex" : false,
      "dataType" : "FLOAT",
      "type" : "float",
      "defaultValue" : "",
      "notNull" : true,
      "unique" : false
    } ]
  }, {
    "followableName" : "tag",
    "referToAnotherFollowablesFeed" : true,
    "referringToFollowable" : "user",
    "followableModelConfig" : [ {
      "name" : "name",
      "createDbIndex" : true,
      "dataType" : "varchar(255)",
      "type" : "varchar",
      "defaultValue" : "",
      "notNull" : true,
      "unique" : true
    } ],
    "followableFeedContentConfig" : [ ],
    "followableFeedCommentConfig" : [ ]
  }, {
    "followableName" : "group",
    "referToAnotherFollowablesFeed" : false,
    "referringToFollowable" : "",
    "followableModelConfig" : [ {
      "name" : "name",
      "createDbIndex" : false,
      "dataType" : "varchar(255)",
      "type" : "varchar",
      "defaultValue" : "",
      "notNull" : true,
      "unique" : false
    } ],
    "followableFeedContentConfig" : [ {
      "name" : "attachment",
      "createDbIndex" : true,
      "dataType" : "varchar(255)",
      "type" : "varchar",
      "defaultValue" : "",
      "notNull" : true,
      "unique" : true
    } ],
    "followableFeedCommentConfig" : [ {
      "name" : "score",
      "createDbIndex" : false,
      "dataType" : "FLOAT",
      "type" : "float",
      "defaultValue" : "",
      "notNull" : false,
      "unique" : false
    }, {
      "name" : "commentToComment",
      "createDbIndex" : true,
      "dataType" : "int",
      "type" : "int",
      "defaultValue" : "",
      "notNull" : true,
      "unique" : true
    } ]
  } ]
}