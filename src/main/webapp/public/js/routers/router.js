var app = app || {};

(function () { "use strict";
	
	var Workspace = Backbone.Router.extend({
		routes: {
			'*filter': 'setFilter'
		},

		setFilter: function (param) {
			// Set the current filter to be used
			app.TicketFilter = param || '';

			// Trigger a collection filter event, causing hiding/unhiding
			// of Todo view items
			app.tickets.trigger('filter');
		}
	});

	app.TicketRouter = new Workspace();
	Backbone.history.start();
})();
