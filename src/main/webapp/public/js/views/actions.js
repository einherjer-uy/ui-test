var app = app || {};

(function ($) { "use strict";

	app.ActionsView = Backbone.View.extend({

		tagName: 'div',

		template: _.template($('#actions').html()),
		commentConfirmationTemplate: _.template($('#commentConfirmationTemplate').html()),
		yesNoConfirmationTemplate: _.template($('#yesNoConfirmationTemplate').html()),

		events: {
			'click #viewAction': 'view',
			'click #editAction': 'edit',
			//these events work because:
			//1) the binding uses jquery delegate so it works even if the element doesn't exist in the DOM
			//at the moment the View is instantiated (like in this case, the popover DOM is inserted by bootstrap javascrip)
			//2) the popover specifies the "container" property (see render() below) which makes the popover DOM a children
			//of this "container" which is what makes these event selectors find the element
			'click #cancelSpan #confirmOkButton': 'cancel', 
			'click #rejectSpan #confirmOkButton': 'reject',
			'click #approveSpan #confirmOkButton': 'approve',
			'click #doneSpan #confirmOkButton': 'done',

			'click #cancelSpan #confirmCancelButton': 'hideCancelPopover', 
			'click #rejectSpan #confirmCancelButton': 'hideRejectPopover',
			'click #approveSpan #confirmCancelButton': 'hideApprovePopover',
			'click #doneSpan #confirmCancelButton': 'hideDonePopover'
		},

		initialize: function () {

		},

		render: function () {
			this.$el.html(this.template(this.model.toJSON()));
			this.$viewAction = this.$("#viewAction");
			this.$editAction = this.$("#editAction");
			this.$cancelAction = this.$("#cancelAction");
			this.$rejectAction = this.$("#rejectAction");
			this.$approveAction = this.$("#approveAction");
			this.$doneAction = this.$("#doneAction");

			this.$viewAction.tooltip({placement:"bottom", title:"View"});
			this.$editAction.tooltip({placement:"bottom", title:"Edit"});
			this.$cancelAction.tooltip({placement:"bottom", title:"Cancel"});
			this.$rejectAction.tooltip({placement:"bottom", title:"Reject"});
			this.$approveAction.tooltip({placement:"bottom", title:"Approve"});
			this.$doneAction.tooltip({placement:"bottom", title:"Mark as Done"});

			var self = this;

			this.$cancelAction.popover({placement:"bottom", container: '#cancelSpan', title:"Confirmation", html:true,
				content:(this.cancelRequiresComment() ? this.commentConfirmationTemplate({mandatoryComment:true}) : this.yesNoConfirmationTemplate())});
			this.$cancelAction.on('shown.bs.popover', function () {
				self.$cancelAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
			});

			this.$rejectAction.popover({placement:"bottom", container: '#rejectSpan', title:"Confirmation", html:true,
				content:this.commentConfirmationTemplate({mandatoryComment:true})});
			this.$rejectAction.on('shown.bs.popover', function () {
				self.$rejectAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
			});

			this.$approveAction.popover({placement:"bottom", container: '#approveSpan', title:"Confirmation", html:true,
				content:this.commentConfirmationTemplate({mandatoryComment:false})});
			this.$approveAction.on('shown.bs.popover', function () {
				self.$approveAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
			});

			this.$doneAction.popover({placement:"bottom", container: '#doneSpan', title:"Confirmation", html:true,
				content:this.yesNoConfirmationTemplate()});
			this.$doneAction.on('shown.bs.popover', function () {
				self.$doneAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
			});

			if(!this.showViewEditOptions()) {
				this.$viewAction.hide();
				this.$editAction.hide();
			}
			if(app.loggedUser.role=="REQUESTOR") {
				this.$viewAction.hide();
				this.$approveAction.hide();
				this.$rejectAction.hide();
				this.$doneAction.hide();
			}
			if(app.loggedUser.role=="APPROVER") {
				this.$viewAction.hide();
				this.$doneAction.hide();
			}
			if(app.loggedUser.role=="EXECUTOR") {
				this.$editAction.hide();
				this.$cancelAction.hide();
				this.$approveAction.hide();
				this.$rejectAction.hide();
			}
			return this;
		},

		cancelRequiresComment: function() {
			return !(this.model.get("status")=="NEW" && app.loggedUser.role=="REQUESTOR"); //only when the user is a Requestor and the status is New we will delete the ticket. In any other case we require a comment.
		},

		//$addEditModal can be set after the view is initialized to customize its behavior
		showViewEditOptions: function() {
			return typeof this.$addEditModal != "undefined";
		},

		//$messagesDiv can be set after the view is initialized to customize its behavior
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
			var data = null;
			if (this.cancelRequiresComment()) {
				data = this.$("textarea").val().trim(); //selector works cause the popover specifies the "container" property, making it a child of $el; and then there's only one "textarea"
				if (!data) {
					if (this.showErrors()) {
			    		app.util.displayError(this.$messagesDiv, "Need to provide a comment to proceed");
			    	}
			    	return;	
			    }
			}
			var self = this;
		    $.ajax({
  				url: "/tt/tickets/"+this.model.get("number")+"/cancel",
  				type: "POST",
  				data: JSON.stringify({text:data}),
  				contentType: "application/json; charset=utf-8",
  				dataType: "json",
				success: function(data){
				    app.tickets.fetch({reset:true}); //fetch needed only in this case cause the ticket might have been removed on the server
				},
			    error: function(data) {
			    	if(self.showErrors()) {
			    		app.util.displayError(self.$messagesDiv, data.responseJSON.message);
			    	}
			    },
			    complete: function() {
			    	self.hideCancelPopover();
			    }
			});
		},
		hideCancelPopover: function () {
			this.$cancelAction.popover("hide");
		},

		//TODO: factorize all $.ajax calls
		reject: function () {
			var data = this.$("textarea").val().trim(); //selector works cause the popover specifies the "container" property, making it a child of $el; and then there's only one "textarea"
			if (!data) {
				if (this.showErrors()) {
		    		app.util.displayError(this.$messagesDiv, "Need to provide a comment to proceed");
		    	}
		    	return;	
		    }
			var self = this;
		    $.ajax({
  				url: "/tt/tickets/"+this.model.get("number")+"/reject",
  				type: "POST",
  				data: JSON.stringify({text:data}),
  				contentType: "application/json; charset=utf-8",
  				dataType: "json",
				success: function(data){
				    self.model.fetch();
				},
			    error: function(data) {
			    	if(self.showErrors()) {
			    		app.util.displayError(self.$messagesDiv, data.responseJSON.message);
			    	}
			    },
				complete: function() {
			    	self.hideRejectPopover();
			    }
			});
		},
		hideRejectPopover: function () {
			this.$rejectAction.popover("hide");
		},

		//TODO: factorize all $.ajax calls
		approve: function () {
			var self = this;
		    $.ajax({
  				url: "/tt/tickets/"+this.model.get("number")+"/approve",
  				type: "POST",
  				data: JSON.stringify({text:this.$("textarea").val().trim()}), //selector works cause the popover specifies the "container" property, making it a child of $el; and then there's only one "textarea"
  				contentType: "application/json; charset=utf-8",
  				dataType: "json",
				success: function(data){
				    self.model.fetch();
				},
			    error: function(data) {
			    	if(self.showErrors()) {
			    		app.util.displayError(self.$messagesDiv, data.responseJSON.message);
			    	}
			    },
				complete: function() {
			    	self.hideApprovePopover();
			    }
			});
		},
		hideApprovePopover: function () {
			this.$approveAction.popover("hide");
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
			    },
				complete: function() {
			    	self.hideDonePopover();
			    }
			});
		},
		hideDonePopover: function () {
			this.$doneAction.popover("hide");
		}

	});
})(jQuery);
