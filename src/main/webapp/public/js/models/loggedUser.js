var app = app || {};

(function () { "use strict";

	app.LoggedUser = Backbone.Model.extend({
		idAttribute: "id",

		urlRoot : "/tt/loggedUser",

		defaults: {
			username: { emailAddress: "" },
			role : "",
			dashboardMode : "LIST",  //LIST or CARD
			firstName : "",
			lastName : "",
			notificationsEnabled : ""
		}
	});

	app.loggedUser = new app.LoggedUser();
})();
