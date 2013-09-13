var app = app || {};

(function () { "use strict";
	
	var AppRouter = Backbone.Router.extend({
		routes: {
			'browse/:ticket': 'viewTicket'
		},

		initialize: function () {
			this.$addEditModal = $('#addEditModal');
			this.$messages = $('#dashboardMessages'); 
		},

		viewTicket: function (param) {
			var ticket = app.tickets.get(param);
			app.util.displayTicket(ticket, this.$addEditModal, this.$messages);
		}
	});

	app.Router = new AppRouter();
	Backbone.history.start();
})();
