var exec = require("cordova/exec");

var Base64ToPNG = function () {
    exec(function() {
        console.log("initialized");
    }, function(e) {
        console.log("error: " + e);
    }, "Base64ToPNG", "init", []);
};
Base64ToPNG.prototype.saveImage = function(win, fail, b64String, params) {
        exec(win, fail, "Base64ToPNG", "saveImage", [b64String, params]);
    };
	
module.exports = Base64ToPNG;
