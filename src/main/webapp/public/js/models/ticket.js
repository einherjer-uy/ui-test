var app = app || {};

(function () { "use strict";

	app.Ticket = Backbone.Model.extend({
		defaults: {
			number: 1,
			title: "",
			description: ""
		},

	});
})();
