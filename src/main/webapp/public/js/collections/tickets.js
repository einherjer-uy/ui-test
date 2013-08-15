var app = app || {};

(function () { "use strict";

	var TicketList = Backbone.Collection.extend({
		
		model: app.Ticket,

		url: "/tt/tickets"

	});

	app.tickets = new TicketList();
})();
