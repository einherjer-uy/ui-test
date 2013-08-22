var app = app || {};

(function ($) { "use strict";

	app.PopoverView = Backbone.View.extend({

		commentConfirmationTemplate: _.template($('#commentConfirmationTemplate').html()),
		yesNoConfirmationTemplate: _.template($('#yesNoConfirmationTemplate').html()),

		initialize: function (options) {
			this.type = options.type;
			this.$action = options.$action;
			this.$messages = options.$messages;
			this.parentView = options.parentView;

		},

		events: {
			//NOTE 1. there are several confirmOkButton, etc in the page (one per row) but backbone makes these selectors relative to the view.$el
			//NOTE 2. the binding uses jquery "on" (or "delegate" in older jquery) so it works even if the element doesn't exist in the DOM
			//        at the moment the View is instantiated (like in this case, the DOM containing the elements selected here are created by the render() function)
			'click .confirmOkButton': 'ok',
			'click .confirmCancelButton': 'closePopover'
		},

		render: function () {
			if (this.type==app.util.CANCEL_POPOVER) {
				if (this.cancelRequiresComment()) {
					this.$el.html(this.commentConfirmationTemplate({mandatoryComment:true}));	
				}
				else {
					this.$el.html(this.yesNoConfirmationTemplate());
				}
			}
			else if (this.type==app.util.REJECT_POPOVER) {
				this.$el.html(this.commentConfirmationTemplate({mandatoryComment:true}));	
			}
			else if (this.type==app.util.APPROVE_POPOVER) {
				this.$el.html(this.commentConfirmationTemplate({mandatoryComment:false}));	
			}
			else if (this.type==app.util.DONE_POPOVER) {
				this.$el.html(this.yesNoConfirmationTemplate());
			}
			return this;
		},

		cancelRequiresComment: function() {
			return !(this.model.get("status")=="NEW" && app.loggedUser.role=="REQUESTOR"); //only when the user is a Requestor and the status is New we will delete the ticket. In any other case we require a comment.
		},
		
		showErrors: function() {
			return this.$messages !== undefined;
		},

		closePopover: function() {
			this.$action.popover("hide");
			//FOR SOME REASON if I don't trigger the render of the parent again (which will in turn instantiate new PopupViews and 
			//call popover() again on the buttons) the events of the popover are lost after the popover is hidden for the first time
			//
			//(no need to call stopListening here cause the view doesn "listenTo" any model that could keep a reference to the view preventing its garbage collection)
			//this.stopListening();
//			this.parentView.render(); 
			//btw, tried this also that seems less brute force but didn't work, doesn't even show the popover after the first popover("destroy"), or if using "hide" instead of "destroy" still the events doesn't work
			//this.$action.popover("hide"); //or popover("destroy")
			//var popoverView = new app.PopoverView({ model: this.model, type: app.util.CANCEL_POPOVER, $action: this.$action, $messages: this.$messages, parentView: this.parentView})
			//this.$action.popover({placement:"left", container: false, title:"Confirmation", html:true, 
			//	content:popoverView.render().el});
		},

		ok: function() {
			if (this.type==app.util.CANCEL_POPOVER) {
				this.cancel();
			}
			else if (this.type==app.util.REJECT_POPOVER) {
				this.reject();
			}
			else if (this.type==app.util.APPROVE_POPOVER) {
				this.approve();
			}
			else if (this.type==app.util.DONE_POPOVER) {
				this.done();
			}
		},

		//TODO: factorize all $.ajax calls
		cancel: function () {
			var data = null;
			if (this.cancelRequiresComment()) {
				data = this.$("textarea").val().trim(); //selector works cause the popover specifies the "container" property, making it a child of $el; and then there's only one "textarea"
				if (!data) {
					if (this.showErrors()) {
			    		app.util.displayError(this.$messages, "Need to provide a comment to proceed");
			    	}
			    	this.closePopover();
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
			    		app.util.displayError(self.$messages, data.responseJSON.message);
			    	}
			    },
			    complete: function() {
			    	self.closePopover();
			    }
			});
		},

		//TODO: factorize all $.ajax calls
		reject: function () {
			var data = this.$("textarea").val().trim(); //selector works cause the popover specifies the "container" property, making it a child of $el; and then there's only one "textarea"
			if (!data) {
				if (this.showErrors()) {
		    		app.util.displayError(this.$messages, "Need to provide a comment to proceed");
		    	}
		    	this.closePopover();
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
			    		app.util.displayError(self.$messages, data.responseJSON.message);
			    	}
			    },
				complete: function() {
			    	self.closePopover();
			    }
			});
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
			    		app.util.displayError(self.$messages, data.responseJSON.message);
			    	}
			    },
				complete: function() {
			    	self.closePopover();
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
			    		app.util.displayError(self.$messages, data.responseJSON.message);
			    	}
			    },
				complete: function() {
			    	self.closePopover();
			    }
			});
		}

	});
})(jQuery);
