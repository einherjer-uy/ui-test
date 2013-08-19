var app = app || {};

(function ($) { "use strict";

	app.AppView = Backbone.View.extend({

		el: '#ttApp',

		footerTemplate: _.template($('#footer-template').html()),

		events: {
			'click #addButton': 'add'
		},

		initialize: function () {
			this.$footer = this.$('#footer');
			this.$main = this.$('#main');
			this.$ticketList = this.$('#ticket-list');
			this.$addEditModal = $('#addEditModal');

			this.listenTo(app.tickets, 'add', this.addOne);
			this.listenTo(app.tickets, 'reset', this.addAll);
			this.listenTo(app.tickets, 'all', this.render);

			app.tickets.fetch(); //call server to fetch the collection, which will in turn trigger the update of the view
		},

		render: function () {
			//var completed = app.tickets.completed().length;
			//var remaining = app.tickets.remaining().length;

			if (app.tickets.length) {
				this.$main.show();
				this.$footer.show();

				this.$footer.html(this.footerTemplate({
					test : "test"
					//completed: completed,
					//remaining: remaining
				}));

				//this.$('#filters li a')
				//	.removeClass('selected')
				//	.filter('[href="#/' + (app.TodoFilter || '') + '"]')
				//	.addClass('selected');
			} else {
				this.$main.hide();
				this.$footer.hide();
			}

			//this.allCheckbox.checked = !remaining;
		},

		// Add a single ticket to the list by creating a view for it, and
		// appending its element to the `<ul>`.
		addOne: function (ticket) {
			var view = new app.TicketRowView({ model: ticket });
			this.$ticketList.append(view.render().el);
		},

		// Add all items in the **Tickets** collection at once.
		addAll: function () {
			this.$ticketList.html('');
			app.tickets.each(this.addOne, this);
		},

		add: function() {
			this.showModal(new app.Ticket());
		},
		
		//TODO: duplicated with TicketRowView.showModal, move to util.js
		showModal: function(ticket) {
			this.$addEditModal.html(new app.TicketView({model: ticket}).render().el);
        	$("#due").datetimepicker({
        		separator: "-",
				stepMinute: 30,
				controlType: 'select'
        	});
        	this.$addEditModal.modal({keyboard: false, backdrop: "static"});
		}

		/*filterOne: function (todo) {
			todo.trigger('visible');
		},

		filterAll: function () {
			app.todos.each(this.filterOne, this);
		},

		// Clear all completed todo items, destroying their models.
		clearCompleted: function () {
			_.invoke(app.todos.completed(), 'destroy');
			return false;
		},

		toggleAllComplete: function () {
			var completed = this.allCheckbox.checked;

			app.todos.each(function (todo) {
				todo.save({
					'completed': completed
				});
			});
		}*/
	});
})(jQuery);
