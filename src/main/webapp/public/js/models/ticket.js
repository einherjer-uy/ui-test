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

		/*"manual"(jquery) ajax call
		add_like: function() {
		    var _this = this;
		    $.ajax({
		        type: 'post',
		        url: '/some/url/that/increments/the/likes',
		        success: function(data) {
		            _this.set('likes', data.likes);
		        },
		        error: function() {
		            // Whatever you need...
		        }
		    });
		}*/

	});
})();
