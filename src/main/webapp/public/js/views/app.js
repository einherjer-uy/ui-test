var app = app || {};

(function ($) { "use strict";

	app.AppView = Backbone.View.extend({

		el: '#ttApp',

		footerTemplate: _.template($('#footer-template').html()),

		events: {
			'click #addButton': 'add'
		},

		initialize: function () {
			this.$tableFooter = this.$('#tableFooter');
			this.$ticketTable = this.$('#ticketTable');
			this.$addEditModal = $('#addEditModal');
			this.$messagesDiv = $('#dashboardMessages'); 
			this.$loggedUser = $('#loggedUser');

			this.listenTo(app.tickets, 'add', this.addOne);
			this.listenTo(app.tickets, 'reset', this.addAll);
			this.listenTo(app.tickets, 'all', this.render);

	        app.tickets.fetch(); //call server to fetch the collection, which will in turn trigger the update of the view
		},

		render: function () {
			//var completed = app.tickets.completed().length;
			//var remaining = app.tickets.remaining().length;

			if (app.loggedUser && app.loggedUser.username && app.loggedUser.username.emailAddress) {
				this.$loggedUser.html(app.loggedUser.username.emailAddress + ' ('+ app.loggedUser.role.toLowerCase() +')');	
			};
			
			this.$messagesDiv.html('');

			//Hide progress bar and black background
			$('#pleaseWaitDialog').hide();
			$("#loadingModalBackdrop").remove(); //cannot do $(".modal-backdrop").remove(); cause this might affect the modal backdrops what will exist in the future (for instance the ones created by the add/edit ticket modal)

			if (app.tickets.length) {
				this.$ticketTable.show();
				this.$tableFooter.html(this.footerTemplate({
					test : "test"
					//completed: completed,
					//remaining: remaining
				}));

				//this.$('#filters li a')
				//	.removeClass('selected')
				//	.filter('[href="#/' + (app.TodoFilter || '') + '"]')
				//	.addClass('selected');
			} else {
				this.$ticketTable.hide();
				this.$tableFooter.hide();
				app.util.displayInfo(this.$messagesDiv, "No tickets found", false);
			}

			//this.allCheckbox.checked = !remaining;
			if (app.loggedUser.role == app.util.ROLE_APPROVER || app.loggedUser.role == app.util.ROLE_EXECUTOR) {
				this.$("#addButton").hide();
			}
		},

		addOne: function (ticket) {
			var view = new app.TicketRowView({ model: ticket });
			this.$ticketTable.append(view.render().el);
		},

		addAll: function () {
			this.$ticketTable.html('');
			app.tickets.each(this.addOne, this);
		},

		add: function() {
			this.showModal(new app.Ticket());
		},
		
		//TODO: duplicated with ActionsView.showModal, move to util.js
		showModal: function(ticket) {
			this.$messagesDiv.html('');
			this.$addEditModal.html(new app.TicketView({model: ticket}).render().el);
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
