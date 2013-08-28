var app = app || {};

(function ($) { "use strict";

	app.AppView = Backbone.View.extend({

		el: '#ttApp',

		footerTemplate: _.template($('#footer-template').html()),

		events: {
			'click #addButton': 'add'
		},

		initialize: function () {
			this.$addEditModal = $('#addEditModal');
			this.$messagesDiv = $('#dashboardMessages'); 
			this.$loggedUser = $('#loggedUser');

			this.listenTo(app.tickets, 'add', this.addOne);
			this.listenTo(app.tickets, 'reset', this.addAll);
			this.listenTo(app.tickets, 'all', this.render);

	        app.tickets.fetch({  //call server to fetch the collection, which will in turn trigger the update of the view
		        success: function () {
		        	//Hide progress bar and black background
	                $('#pleaseWaitDialog').hide();
					$(".modal-backdrop").hide();
					
					$('#dashboardMessages').html('');

					if (!app.tickets.length) {
						$('#ticketTab').hide();
						app.util.displayInfo($('#dashboardMessages'), "No tickets found", false);
					}else{
						$('#ticketTab').show();
					}
	            }	
	        });
		},

		render: function () {
			//var completed = app.tickets.completed().length;
			//var remaining = app.tickets.remaining().length;

			if (app.loggedUser && app.loggedUser.username && app.loggedUser.username.emailAddress) {
				this.$loggedUser.html(app.loggedUser.firstName + ' ' + app.loggedUser.lastName + '  |  '+ app.loggedUser.role.toLowerCase());	
				console.log(app.loggedUser);
			};
			
			//this.allCheckbox.checked = !remaining;
			if (app.loggedUser.role == app.util.ROLE_APPROVER || app.loggedUser.role == app.util.ROLE_EXECUTOR) {
				this.$("#addButton").hide();
			}
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
