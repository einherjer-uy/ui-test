var app = app || {};

(function ($) { "use strict";

	app.DashoardCardsView = Backbone.View.extend({

		el: '#cards',

		initialize: function () {
			this.$ticketCards = this.$('#ticketCards');
			this.listenTo(app.tickets, 'add', this.addOne);
		},

		addOne: function (ticket) {

			if (this.$ticketCards.children().length == 0 || this.$ticketCards.children().last().children().length == 3) {
				this.$ticketCards.append("<div class='row-fluid show-grid'></div>");		
			};	

			var view = new app.TicketCardView({ model: ticket });
			this.$ticketCards.children().last().append(view.render().el);								
		},

		addAll: function () {
			this.$ticketCards.html('');
			app.tickets.each(this.addOne, this);			
		},
	});
})(jQuery);
