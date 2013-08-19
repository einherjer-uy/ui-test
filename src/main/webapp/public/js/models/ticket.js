var app = app || {};

(function () { "use strict";

	app.Ticket = Backbone.Model.extend({
		idAttribute: "number", //the rest api uses the "number" property as the ticket id

		urlRoot : "/tt/tickets",

		defaults: {
			number: null,
			title : "",
			description : "",
			project : {prefix : "TT"},
			status : "NEW",
			assignee : {username : "user@twitter.com"},
			type: "HARDWARE",
			priority: "LOW",
			due: null
		},
		
		validate: function (attrs) {
		   var errors = [];

		   if (!attrs.description) {
		       errors.push({name: 'description', message: 'Description field is mandatory.'});
			}

			console.log(errors);
			return errors.length > 0 ? errors : false;
		}

	});
})();
