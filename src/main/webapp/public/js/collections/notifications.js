var app = app || {};

(function () { "use strict";

	var NotificationList = Backbone.Collection.extend({
		
		model: app.Notification,

		initialize: function () {
			$.pnotify.defaults.history = false;	 
			app.stack_bottomright = {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25};
		},

		add: function(notification) {

			var opts = {
	        	title: notification.get("title"),
	        	text: notification.get("text"),
	        	type: notification.get("type"),
	        	addclass: "stack-bottomright",
	        	stack: app.stack_bottomright
			};

			$.pnotify(opts);	
		}
	});

	app.notifications = new NotificationList();
})();
