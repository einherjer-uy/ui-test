var app = app || {};

(function ($) { "use strict";

	// The Application
	// ---------------

	// Our overall **AppView** is the top-level piece of UI.
	app.AppView = Backbone.View.extend({

		// Instead of generating a new element, bind to the existing skeleton of
		// the App already present in the HTML.
		el: '#ttApp',

		// Our template for the line of statistics at the bottom of the app.
		footerTemplate: _.template($('#footer-template').html()),

		// Delegated events for creating new items, and clearing completed ones.
		events: {
			'click #saveButton' : 'save'
			//'keypress #new-todo': 'createOnEnter',
			//'click #clear-completed': 'clearCompleted',
			//'click #toggle-all': 'toggleAllComplete'
		},

		// At initialization we bind to the relevant events on the `Todos`
		// collection, when items are added or changed. Kick things off by
		// loading any preexisting todos that might be saved in *localStorage*.
		initialize: function () {
			//this.allCheckbox = this.$('#toggle-all')[0];
			this.$title = this.$('#title');
			this.$footer = this.$('#footer');
			this.$main = this.$('#main');

			this.listenTo(app.tickets, 'add', this.addOne);
			this.listenTo(app.tickets, 'reset', this.addAll);
			//this.listenTo(app.tickets, 'change:completed', this.filterOne);
			//this.listenTo(app.tickets, 'filter', this.filterAll);
			this.listenTo(app.tickets, 'all', this.render);

			app.tickets.fetch(); //call server to fetch the collection, which will in turn trigger the update of the view
		},

		// Re-rendering the App just means refreshing the statistics -- the rest
		// of the app doesn't change.
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
			var view = new app.TicketView({ model: ticket });
			this.$('#ticket-list').append(view.render().el);
		},

		// Add all items in the **Tickets** collection at once.
		addAll: function () {
			this.$('#ticket-list').html('');
			app.tickets.each(this.addOne, this);
		},

		save: function (e) {
			//if (/*e.which !== ENTER_KEY || */!this.$input.val().trim()) {
			//	return;
			//}

			//var newTicket = app.tickets.create(this.newAttributes());
			//newTicket.fetch();

			var newTicket = new app.Ticket(this.newAttributes());
			newTicket.save({wait: true});
			newTicket.fetch();
			app.tickets.add(newTicket);
		},

		newAttributes: function () {
			return {
				title : this.$title.val().trim(),
				description : "desc",
				assignee : {username : "user@twitter.com"}
			};
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
