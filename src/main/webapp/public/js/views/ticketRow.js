var app = app || {};

(function ($) { "use strict";

	app.TicketRowView = Backbone.View.extend({

		tagName: 'tr',

		template: _.template($('#item-template').html()),

		events: {
			'click #actionView': 'view',
			'click #actionEdit': 'edit',
			'click #actionCancel': 'cancel',
			'click #actionReject': 'reject',
			'click #actionApprove': 'approve',
			'click #actionDone': 'done'
		},

		initialize: function () {
			// to improve performance run the needed selectors only once in the initialize or render funcions and store the references that will be used in other view methods
			// in this case we do it in initialize() (instead of in render()) cause the elements to be selected belong to a separate modal, unrelated to the view's "el" (<li> in this case)
			// in this case we don't use the syntax this.$(<selector>); cause the edit is done in a separate modal, unrelated to the view's "el" (<li> in this case)
			this.$addEditModal = $('#addEditModal'); 
			this.$messagesDiv = $('#dashboardMessages'); 

			this.listenTo(this.model, 'change', this.render);
		},

		render: function () {
			this.$el.html(this.template(this.model.toJSON()));
			return this;
		},

		edit: function () {
			this.showModal(this.model);
		},

		view: function () {
			this.showModal(this.model);
		},

		//TODO: duplicated with AppView.showModal, move to util.js
		showModal: function(ticket) {
			this.$messagesDiv.html('');
			this.$addEditModal.html(new app.TicketView({model: ticket}).render().el);
        	$("#due").datetimepicker({
        		separator: "-",
				stepMinute: 30,
				controlType: "select"
        	});
        	this.$addEditModal.modal({keyboard: false, backdrop: "static"});
		},

		//TODO: factorize all $.ajax calls
		cancel: function () {
			var self = this;
		    $.ajax({
  				url: "/tt/tickets/"+this.model.get("number")+"/cancel",
  				type: "POST",
  				data: JSON.stringify({text:"comment"}),
  				contentType: "application/json; charset=utf-8",
  				dataType: "json",
				success: function(data){
				    app.tickets.fetch({reset:true}); //fetch needed only in this case cause the ticket might have been removed on the server
				},
			    error: function(data) {
			    	app.util.displayError(self.$messagesDiv, data.responseJSON.message);
			    }
			});
		},

		//TODO: factorize all $.ajax calls
		reject: function () {
			var self = this;
		    $.ajax({
  				url: "/tt/tickets/"+this.model.get("number")+"/reject",
  				type: "POST",
  				data: JSON.stringify({text:"comment"}),
  				contentType: "application/json; charset=utf-8",
  				dataType: "json",
				success: function(data){
				    this.render();
				},
			    error: function(data) {
			    	app.util.displayError(self.$messagesDiv, data.responseJSON.message);
			    }
			});
		},

		//TODO: factorize all $.ajax calls
		approve: function () {
			var self = this;
		    $.ajax({
  				url: "/tt/tickets/"+this.model.get("number")+"/approve",
  				type: "POST",
  				data: JSON.stringify({text:"comment"}),
  				contentType: "application/json; charset=utf-8",
  				dataType: "json",
				success: function(data){
				    this.render();
				},
			    error: function(data) {
			    	app.util.displayError(self.$messagesDiv, data.responseJSON.message);
			    }
			});
		},

		//TODO: factorize all $.ajax calls
		done: function () {
			var self = this;
		    $.ajax({
  				url: "/tt/tickets/"+this.model.get("number")+"/done",
  				type: "POST",
  				contentType: "application/json; charset=utf-8",
  				dataType: "json",
				success: function(data){
				    this.render();
				},
			    error: function(data) {
			    	app.util.displayError(self.$messagesDiv, data.responseJSON.message);
			    }
			});
		},

	});
})(jQuery);
