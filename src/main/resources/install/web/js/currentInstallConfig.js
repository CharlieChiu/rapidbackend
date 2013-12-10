{
  "dbName" : "goodtaste",
  "dbUri" : "jdbc:mysql://127.0.0.1",
  "username" : "root",
  "password" : "111111",
  "followableConfigs" : [ {
    "followableName" : "user",
    "referToAnotherFollowablesFeed" : false,
    "referringToFollowable" : null,
    "followableModelConfig" : [ ],
    "followableFeedContentConfig" : [ ],
    "followableFeedCommentConfig" : [ ]
  }, {
    "followableName" : "group",
    "referToAnotherFollowablesFeed" : false,
    "referringToFollowable" : null,
    "followableModelConfig" : [ {
      "name" : "name",
      "createDbIndex" : true,
      "dataType" : "VARCHAR(255)",
      "type" : "varchar",
      "defaultValue" : "",
      "notNull" : true,
      "unique" : true
    }, {
      "name" : "type",
      "createDbIndex" : false,
      "dataType" : "INT",
      "type" : "int",
      "defaultValue" : "",
      "notNull" : false,
      "unique" : false
    } ],
    "followableFeedContentConfig" : [ {
      "name" : "haha",
      "createDbIndex" : false,
      "dataType" : "VARCHAR(255)",
      "type" : "varchar",
      "defaultValue" : "",
      "notNull" : false,
      "unique" : false
    }, {
      "name" : "hoho",
      "createDbIndex" : false,
      "dataType" : "LONGTEXT",
      "type" : "text",
      "defaultValue" : "",
      "notNull" : false,
      "unique" : false
    } ],
    "followableFeedCommentConfig" : [ {
      "name" : "where",
      "createDbIndex" : false,
      "dataType" : "FLOAT",
      "type" : "float",
      "defaultValue" : "",
      "notNull" : false,
      "unique" : false
    } ]
  } ],
  "userDefinedModels" : [ {
    "modelConfig" : [ {
      "name" : "name",
      "createDbIndex" : false,
      "dataType" : null,
      "type" : "varchar",
      "defaultValue" : "",
      "notNull" : false,
      "unique" : false
    } ],
    "modelName" : "profiles",
    "installSql" : null
  }, {
    "modelConfig" : [ {
      "name" : "come",
      "createDbIndex" : false,
      "dataType" : null,
      "type" : "text",
      "defaultValue" : "",
      "notNull" : false,
      "unique" : false
    } ],
    "modelName" : "city",
    "installSql" : null
  } ]
}