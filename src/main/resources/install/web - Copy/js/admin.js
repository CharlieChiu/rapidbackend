///<reference path="~/install.html" />



function DBColumn(name, type, notNull, index, unique) {
    this.name = name;
    this.type = type;
    this.notNUll = notNull;
    this.index = index;
    this.unique = unique;
}
function templateRender() {
    var data = {
        screenName: "dhg",
    };
    var template = Hogan.compile("Follow @{{screenName}}.");
    var output = template.render(data);
    console.log(output);
}


var list = { "names": [{ "name": "xigua" }, { "name": "qiezi" }] };



var column = new DBColumn("hah", char, false, false, false);
var tempate = "";
//function create