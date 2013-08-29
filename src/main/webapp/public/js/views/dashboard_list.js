var app = app || {};

(function ($) { "use strict";

	app.DashoardListView = Backbone.View.extend({

		el: '#list',

		initialize: function () {
			this.$ticketTable = this.$('#ticketTable');
			this.listenTo(app.tickets, 'add', this.addOne);
			this.listenTo(app.tickets, 'reset', this.addAll);
			this.listenTo(app.tickets, 'all', this.render);
		},

		render: function () {
			if (app.tickets.length) {
				this.$ticketTable.show();

				/*
					this.$tableFooter.html(this.footerTemplate({
					test : "test"
					//completed: completed,
					//remaining: remaining
				}));
				*/
			}
			/*
			else {
				this.$ticketTable.hide();
				this.$tableFooter.hide();
				app.util.displayInfo(this.$messagesDiv, "No tickets found", false);
			}
			*/
		},

		addOne: function (ticket) {
			var view = new app.TicketRowView({ model: ticket });
			this.$ticketTable.append(view.render().el);						
		},

		addAll: function () {
			this.$ticketTable.html('');
			app.tickets.each(this.addOne, this);				
		},
	});
})(jQuery);
