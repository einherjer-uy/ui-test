var app = app || {};

(function () { "use strict";

	app.Ticket = Backbone.Model.extend({
		idAttribute: "number", //the rest api uses the "number" property as the ticket id

		urlRoot : "/tt/tickets",

		defaults: {
			number: null,
			project : {prefix : "PR1"},
			status : "OPEN"
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
