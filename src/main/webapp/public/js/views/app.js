var app = app || {};

(function ($) { "use strict";

	app.AppView = Backbone.View.extend({

		el: '#ttApp',

		events: {
			'click #addButton': 'add',
			'click #refreshButton': 'refresh',
			'click #ticketListTab': 'setTicketListTab',
			'click #ticketCardTab': 'setTicketCardTab'
		},

		initialize: function () {
			this.$addEditModal = $('#addEditModal');
			this.$messagesDiv = $('#dashboardMessages'); 
			this.$loggedUser = $('#loggedUser');

			this.listenTo(app.tickets, 'add', this.addOne);
			this.listenTo(app.tickets, 'reset', this.addAll);
			this.listenTo(app.tickets, 'all', this.render);

			app.loggedUser.fetch ({
				success: function () {
					app.tickets.fetch({  //call server to fetch the collection, which will in turn trigger the update of the view
							        success: function () {
							        	//Hide progress bar and black background
						                $('#pleaseWaitDialog').hide();
										$(".modal-backdrop").hide();
										
										$('#dashboardMessages').html('');
						            }	
					});

					if (app.loggedUser.get("dashboardMode") != "LIST" ) {
						$('#ticketListTab').removeClass("active");
						$('#list').removeClass("active");
				
						$('#ticketCardTab').addClass("active");
						$('#cards').addClass("active");
					};		

					$('#loggedUser').html(app.loggedUser.get("firstName") + ' ' + app.loggedUser.get("lastName") + '  |  ' + app.loggedUser.get("role").toLowerCase());				

					if (app.loggedUser.get("role") == app.util.ROLE_APPROVER || app.loggedUser.get("role") == app.util.ROLE_EXECUTOR) {
						$("#addButton").hide();
					}
				}
			});
		},

		refresh: function () {
			
			$('#pleaseWaitDialog').show();
			$(".modal-backdrop").show();

			app.tickets.reset();
			app.tickets.fetch({  //call server to fetch the collection, which will in turn trigger the update of the view
		        success: function (model, response, options) {

		        	//Hide progress bar and black background
	                $('#pleaseWaitDialog').hide();
					$(".modal-backdrop").hide();
					$('#dashboardMessages').html('');

					if (!app.tickets.length) {
						if ($('#pleaseWaitDialog').is(":hidden")) {
							$('#ticketTab').hide();
							app.util.displayInfo($('#dashboardMessages'), "No tickets found", false);	
						};
					}else{
						$('#ticketTab').show();
					}
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
		},

		add: function() {
			app.util.displayTicket(new app.Ticket(), this.$addEditModal, this.$messagesDiv);
		},
		
		setTicketListTab:function() {
			app.loggedUser.set("dashboardMode","LIST");
			$.ajax({
  				url: "/tt/loggedUser", 
  				type: "PUT",
  				data: JSON.stringify(app.loggedUser.toJSON()),
  				contentType: "application/json; charset=utf-8",
  				dataType: "json",
			});
		},

		setTicketCardTab:function() {
			app.loggedUser.set("dashboardMode","CARD");
			$.ajax({
  				url: "/tt/loggedUser", 
  				type: "PUT",
  				data: JSON.stringify(app.loggedUser.toJSON()),
  				contentType: "application/json; charset=utf-8",
  				dataType: "json",
			});
		}

	});
})(jQuery);
