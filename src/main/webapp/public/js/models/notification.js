var app = app || {};

(function () { "use strict";

	app.Notification = Backbone.Model.extend({
		
		defaults: {
			title: "Title",
			text: "Message",
			type: ""
		}
	});
})();
