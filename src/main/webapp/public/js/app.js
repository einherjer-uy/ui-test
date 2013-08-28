var app = app || {};

$(function () { "use strict";  //$() shorthand for $(document).ready()
	app.ticketTypes = [];
	app.util.getDropdownInfo("/tt/ticketTypes", app.ticketTypes);
	app.ticketPriorities = [];
	app.util.getDropdownInfo("/tt/ticketPriorities", app.ticketPriorities);
	app.loggedUser = {};
	$.getJSON("/tt/loggedUser", function(result) {
		app.loggedUser = result;
	});

	new app.AppView();
	new app.DashoardListView();
	new app.DashoardCardsView();
});
