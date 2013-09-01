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
			else {				
				if (attrs.description.length >3000) {
					errors.push({name: 'description', message: 'Description field must have at most 3000 characters.'});
				};
			}

			if (app.loggedUser.get("role") == app.util.ROLE_REQUESTOR) { //do not validate the due date when an approver is changing the priority
				if (attrs.due) {
					var nowDateTime = new Date();
					var sixHoursLater = new Date(nowDateTime.getTime() + 6*60*60000);
					var dueDate = new Date(attrs.due);
					if (sixHoursLater > dueDate) {	
						errors.push({name: 'due', message: 'Due field must be 6 hours greater than now.'});	
					}
				}
			}
			
			return errors.length > 0 ? errors : false;
		}

	});
})();
