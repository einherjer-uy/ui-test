var app = app || {};

(function ($) { "use strict";

	app.ActionsView = Backbone.View.extend({

		tagName: 'div',

		template: _.template($('#actions').html()),

		events: {
			'click #actionView': 'view',
			'click #actionEdit': 'edit',
			'click #actionCancel': 'cancel',
			'click #actionReject': 'reject',
			'click #actionApprove': 'approve',
			'click #actionDone': 'done'
		},

		initialize: function () {

		},

		render: function () {
			this.$el.html(this.template(this.model.toJSON()));
			this.$actionView = this.$("#actionView");
			this.$actionEdit = this.$("#actionEdit");
			this.$actionCancel = this.$("#actionCancel");
			this.$actionReject = this.$("#actionReject");
			this.$actionApprove = this.$("#actionApprove");
			this.$actionDone = this.$("#actionDone");

			if(!this.showViewEditOptions()) {
				this.$actionView.hide();
				this.$actionEdit.hide();
			}
			if(app.loggedUser.role=="REQUESTOR") {
				this.$actionView.hide();
				this.$actionApprove.hide();
				this.$actionReject.hide();
				this.$actionDone.hide();
			}
			if(app.loggedUser.role=="APPROVER") {
				this.$actionView.hide();
				this.$actionDone.hide();
			}
			if(app.loggedUser.role=="EXECUTOR") {
				this.$actionEdit.hide();
				this.$actionCancel.hide();
				this.$actionApprove.hide();
				this.$actionReject.hide();
			}
			return this;
		},

		showViewEditOptions: function() {
			return typeof this.$addEditModal != "undefined";
		},

		showErrors: function() {
			return typeof this.$messagesDiv != "undefined";
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
			//this.$actionCancel.popover();//{placement:"bottom", title:"xxx", html:"<h2>content</h2>", delay: { show: 500, hide: 0 }});

			/*var self = this;
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
			    	if(self.showErrors()) {
			    		app.util.displayError(self.$messagesDiv, data.responseJSON.message);
			    	}
			    }
			});*/
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
				    self.model.fetch();
				},
			    error: function(data) {
			    	if(self.showErrors()) {
			    		app.util.displayError(self.$messagesDiv, data.responseJSON.message);
			    	}
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
				    self.model.fetch();
				},
			    error: function(data) {
			    	if(self.showErrors()) {
			    		app.util.displayError(self.$messagesDiv, data.responseJSON.message);
			    	}
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
				    self.model.fetch();
				},
			    error: function(data) {
			    	if(self.showErrors()) {
			    		app.util.displayError(self.$messagesDiv, data.responseJSON.message);
			    	}
			    }
			});
		},

	});
})(jQuery);
