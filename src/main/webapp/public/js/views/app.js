var app = app || {};

(function ($) { "use strict";

	app.AppView = Backbone.View.extend({

		el: '#ttApp',

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
	            }	
	        });
		},

		render: function () {
			if (!app.tickets.length) {
				if ($('#pleaseWaitDialog').is(":hidden")) {
					$('#ticketTab').hide();
					app.util.displayInfo($('#dashboardMessages'), "No tickets found", false);	
				};
			}else{
				$('#ticketTab').show();
			}

			if (app.loggedUser && app.loggedUser.username && app.loggedUser.username.emailAddress) {
				this.$loggedUser.html(app.loggedUser.firstName + ' ' + app.loggedUser.lastName + '  |  '+ app.loggedUser.role.toLowerCase());	
			};
			
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

	});
})(jQuery);
