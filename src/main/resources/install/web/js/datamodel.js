///<reference path="~/install.html" />


var varchar;
var int;
var long;
var float;
var text;
var binary;

function initDbDataType() {
    varchar = "varchar";
    int = "int";
    long = "long";
    float = "float";
    text = "text";
    binary = "binary";
}

initDbDataType();

var selectionTemplate = "<select class=\"dataType-select fieldDataType\">" +
                    "<option value=\"{{type}}\">{{type}}</option>" +
                "</select>";
var selectionView = { type: [varchar, int, long, float, text, binary] };
var selectionContent = Mustache.render(selectionTemplate, selectionView);

function DbRecord() {
    this.defaultFieldDescriptors = { id: int, created: long, modified: long };
}

function ReservedModel(fields,modelName) {
    this.reservedFieldDescriptors = fields;
    this.modelName = modelName;
    
    this.columnName = function () {
        return reservedFieldDescriptors[0];
    }
}
ReservedModel.prototype = new DbRecord();

var feedsourceFields = { url: varchar, name: varchar, content: text };

var oauthapplicationFields = {
    owner: int, consumerKey: varchar, name: varchar, description: varchar, icon: varchar, sourceUrl: varchar, homepage: varchar,
    organization: varchar, callbackUrl: varchar, type: varchar, accessType: varchar
};

var oauthapplicationuserFields = {
    userId: int, applicationId: int, accessType: varchar, code: varchar, uid: varchar, screenName: varchar
};

var oauthtokenassociationFields = {
    userId: int, applicationId: int, token: varchar
}

var fileuploadedFields = {
    userid: int, fileDescription: varchar, fileLocation: varchar
}

var userFields = {
    screenName: varchar, password: varchar, email: varchar, userUrl: varchar
}

var userFeedContentFields = {
    userId: int, truncated: int, content: text, seedFeedId: int, replyToId: int, replyToScreenName: varchar, repostToId: int, repostToUserId: int,
    geoEnabled: int, latitude: float, longitude: float, location: varchar, sourceId: int, uniqueId: varchar
}
var userFeedFields = {
    feedId: int
}
var userSubscriptionFields = {
    followable: int, follower: int
}
var commentFields = {
    userId: int, feedId:int,content: text, screenName: varchar
}


var feedSource = new ReservedModel(feedsourceFields,"feedSource");
var oauthapplication = new ReservedModel(oauthapplicationFields, "oauthapplication");
var oauthapplicationuser = new ReservedModel(oauthapplicationFields, "oauthapplicationuser");
var oauthtokenassociation = new ReservedModel(oauthtokenassociation, "oauthtokenassociation");
var fileuploaded = new ReservedModel(fileuploadedFields, "fileuploaded");
var user = new ReservedModel(userFields, "user");
var userfeed = new ReservedModel(userFeedFields, "userfeed");
var userfeedcontent = new ReservedModel(userFeedContentFields, "userfeedcontent");
var userSubscription = new ReservedModel(userSubscriptionFields, "userSubscription");
var feedComment = new ReservedModel(commentFields,"feedComment");

var reservedTables = [feedSource, oauthapplication, oauthapplicationuser, oauthtokenassociation,
    fileuploaded];

var modelsReserved = new Array();
modelsReserved.push([feedSource, oauthapplication, oauthapplicationuser,
    oauthtokenassociation, fileuploaded, user, userfeed, userfeedcontent, userSubscription]);

var reservedModelTemplateText = $("#reservedModelTemplate").html();
var tableTemplate = $("#tableTemplate").html();

function createReservedTable(modelReserved) {
    var data = Mustache.render(reservedModelTemplateText, createDbRecordView(modelReserved));
    //console.log(data);
    return data;
}

function createDatabaseConfigTable() {
    var template = $("#databaseTemplate").html();
    var div = Mustache.render(template, new Object());
    return div;
}
function createDbRecordView(model) {
    var view = new Array();
    view["modelName"] = model.modelName;
    var reservedColumn = new Array();
    var defaultColumn = new Array();
    var selection = selectionContent;
    for (x in model.reservedFieldDescriptors) {
        var column = new Array();
        column["name"] = x + "";
        column["type"] = model.reservedFieldDescriptors[x];
        reservedColumn.push(column);
    }
    for (x in model.defaultFieldDescriptors) {
        var column = new Array();
        column["name"] = x + "";
        column["type"] = model.defaultFieldDescriptors[x];
        defaultColumn.push(column);
    }
    view["reservedColumn"] = reservedColumn;
    view["defaultColumn"] = defaultColumn;

    if (supportsAddField(model.modelName)) {
        view["supportsAddField"] = true;
    }
    return view;
}

function followableView(name) {
    console.log("---------->createFollowableView");
    console.log(name);
    var view = new Array();
    var followableModel;
    var feedContentModel;
    
    feedContentModel = userfeedcontent;
    followableModel = new ReservedModel();
    view["supportsAddField"] = true;
    view["showRemoveFollowable"] = true;
    if (name == "user") {
        view["disableFollowableName"] = true;
        view["disableFollowableFeedContentName"] = true;
        view["disableFollowableFeedCommentName"] = true;
        view["followableName"] = "user";
        view["removeFollowable"] = false;
        view["followableFeedContentModelName"] = "userfeedcontent";
        view["followableFeedCommentModelName"] = "userfeedComment";
        view["followableModelName"] = "user";
        followableModel = user;

        view["showRemoveFollowable"] = false;
    }

    view["feedContentModel"] = createDbRecordView(feedContentModel);
    view["followableModel"] = createDbRecordView(followableModel);
    view["feedCommentModel"] = createDbRecordView(feedComment);
    return view;
}

function supportsAddField(modelName){
    if(modelName == "user"||
        modelName == "userfeedcontent"){
        return true;
    }else{
        return false;
    }
}

var newRowView = new Array();
newRowView["selection"] = selectionContent;
var tdhtml = Mustache.render( $("#rowTemplate").html(),newRowView);

function createColumn() {
    var table = $(this).siblings(".table");
    $(table).children("tbody").append(
        tdhtml
        );
}

//userSubscription, userfeed, user, userfeedcontent

function submitConfig() {

    try {
        $("#installInfo").html("");//clean the out put info

        //checkNoNullInputs();

        var followableConfigs = getFollowableConfigs();

        var userDefinedModels = getUserDefinedModels();

        var data = JSON.stringify({
            userDefinedModels:userDefinedModels,followableConfigs: followableConfigs, username: $("#username").val(), password: $("#password").val(),
            dbName: $("#dbName").val(), dbUri: $("#dbUri").val()
        });

        $.ajax({
            type: 'POST',
            url: "Install",
            dataType: "text",
            data: "config=" + data,
            success: onSubmitSuccess,
            error: onSubmitError
        });
    } catch (e) {
        $("#installInfo").html(e.message);
    }
    
}

function getUserDefinedModel(modelDiv, userDefinedModels) {
    var model = $(modelDiv);
    var modelName = model.find(".modelName").first().val();

    var modelConfig = new Object();
    modelConfig.modelName = modelName;
    var modelTable = model.find(".table");
    modelConfig.modelConfig = getCustomFields(modelTable)

    userDefinedModels.push(modelConfig);
}

function getUserDefinedModels() {
    var userDefinedModels = new Array();
    var modelDivs = $("#userDefinedModels").find(".userDefinedModel");
    for (var i = 0; i < modelDivs.length; i++) {
        getUserDefinedModel(modelDivs[i], userDefinedModels);
    }
    return userDefinedModels;
}


function checkNoNullInputs() {
    $(":input").each(
        function () {
            if ($(this).hasClass("notNull")) {
                console.log(value);
                var value = $(this).val();
                if (!$.trim(value).length) {// field name is empty ,skip
                    $(this).addClass("error");
                }
            }
        });
}
function onSubmitSuccess(data, textstatus, jqxhr) {
    var content = "looks good, please check your database and run 'gradlew test' in the project folder";
    $("#installInfo").html(content);
}

function onSubmitError(jqXHR, textStatus, errorThrown) {
    var content = "return code:" + jqXHR.status+"<br/>";
    content += "status:" + textStatus + "<br/>";
    //console.log(errorThrown);
    content += "info: <br/>" + errorThrown;
    $("#installInfo").html(content);
}


function getHtml() {
    return "<html lang=\"en\">" + document.documentElement.innerHTML + "</html>";
}
function getFollowableConfig(followable,followableConfigs) {
    var followableDiv = $(followable);
    var followableName = followableDiv.find(".followableName").first().val();

    var followableConfig = new Object();
    followableConfig.followableName = followableName;

    var followableModelConfig = new Object();
    var followabeModelTable = followableDiv.find(".followableModel").find(".table");
    //followableModelConfig.customFields = getCustomFields(followabeModelTable);

    var followableFeedContentConfig = new Object();
    var followableFeedContentTable = followableDiv.find(".followableFeedContent").find(".table");
    //followableFeedContentConfig.customFields = getCustomFields(followableFeedContentTable);

    var followableFeedCommentConfig = new Object();
    var followableFeedCommentTable = followableDiv.find(".feedComment").find(".table");

    
    followableConfig.followableModelConfig = getCustomFields(followabeModelTable);
    followableConfig.followableFeedContentConfig = getCustomFields(followableFeedContentTable);
    followableConfig.followableFeedCommentConfig = getCustomFields(followableFeedCommentTable);


    followableConfig.referToAnotherFollowablesFeed = followableDiv.find(".referToAnotherFollowablesFeedCheckBox").is(':checked');
    followableConfig.referringToFollowable = followableDiv.find(".referringToFollowable").val();

    followableConfigs.push(followableConfig);
}


function getFollowableConfigs() {
    var followableConfigs = new Array();
    var followables = new Array();
    var followabeDivs = $('#addFollowables').children(".followable");
    followables = followables.concat( );
    for (var i = 0; i < followabeDivs.length; i++) {
        getFollowableConfig(followabeDivs[i], followableConfigs);
    }
    return followableConfigs;
}

function getCustomFields(modelTable) {
    var fields = new Array();

    $(modelTable).find(".customedField").each(
        function () {
            var fieldName = $(this).find(".fieldName").val();
            if (!$.trim(fieldName).length) {// field name is empty ,skip
                console.log("getCustomFields: field name is empty ,skipping");
                return null;
            }
            var dataType = $(this).find(".dataTypeSelector").val();
            var notNull = $(this).find(".notnullField").is(':checked');
            var createDbIndex = $(this).find(".indexField").is(':checked');
            var unique = $(this).find(".uniqueField").is(':checked');

            var field = new Object();

            field.name = fieldName;
            field.type = dataType;
            field.notNull = notNull;
            field.createDbIndex = createDbIndex;
            field.unique = unique;

            fields.push(field);
        }
        );

    return fields;
}
function registerEvents() {
    $(".addField").unbind('click',createColumn);
    $(".addField").bind('click', createColumn);

    $(".addNewFollowable").unbind('click',addNewFollowable);
    $(".addNewFollowable").bind('click', addNewFollowable);

    $(".followableName").unbind('keyup', setFollowableModelNames);
    $(".followableName").bind('keyup', setFollowableModelNames);
    //$(".referToAnotherFollowablesFeed").keyup(hideFeedContentTable);
    $(".referToAnotherFollowablesFeedCheckBox").unbind('click', hideFeedContentTable);
    $(".referToAnotherFollowablesFeedCheckBox").bind('click', hideFeedContentTable);

    $(".removeFollowable").unbind('click', removeFollowable);
    $(".removeFollowable").bind('click', removeFollowable);

    $(".addNewModel").unbind('click',addNewModel);
    $(".addNewModel").bind('click', addNewModel);

    $(".removeNewModel").unbind('click', removeNewModel);
    $(".removeNewModel").bind('click', removeNewModel);
}

var followableUser = followableView("user");
var followableTemplate = $("#followableTemplate").html();

var followableUserDiv = Mustache.render(followableTemplate, followableUser);

var modelTemplate = $("#newModelTemplate").html();

function addNewModel() {
    $("#userDefinedModels").append(modelTemplate);
    registerEvents();
}

function removeNewModel() {
    $(this).parent().remove();
}

function addNewFollowable() {
    var followable = followableView();
    var followableDiv = Mustache.render(followableTemplate, followable);
    $("#addFollowables").append(followableDiv);
    registerEvents();
}

function setFollowableModelNames() {
    var followableName = $(this).val();
    // set followablemodel name
    var followable = $(this).parent().parent().parent().parent();
    var followableModelName = followableName.trim();
    var followableFeedContentModelName = followableName.trim() + "feedcontent";
    var followableFeedCommentModelName = followableName.trim() + "feedcomment";
    
    followable.find(".followableModelName").html(followableModelName);
    followable.find(".followableFeedContentModelName").html(followableFeedContentModelName);
    followable.find(".followableFeedCommentModelName").html(followableFeedCommentModelName);
}
function removeFollowable() {
    $(this).parent().remove();
}

function hideFeedContentTable() {// this is not used any more because we currently don't support
    //$(this).parent().siblings(".modelTable").toggle();
    var followable = $(this).parent().parent().parent().parent();
    followable.find(".followableFeedContent").find(".modelTable").toggle();
    followable.find(".feedComment").find(".modelTable").toggle();// we also hide comment table
}