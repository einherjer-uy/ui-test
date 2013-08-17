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
			status : "OPEN",
			assignee : {username : "user@twitter.com"}
		},
		
		validate: function (attrs) {
		   var errors = [];

		   if (!attrs.title) {
		       errors.push({name: 'title', message: 'Title field is mandatory.'});
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
