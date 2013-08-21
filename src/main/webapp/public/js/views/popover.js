var app = app || {};

(function ($) { "use strict";

	app.PopoverView = Backbone.View.extend({

		commentConfirmationTemplate: _.template($('#commentConfirmationTemplate').html()),
		yesNoConfirmationTemplate: _.template($('#yesNoConfirmationTemplate').html()),

		setParams: function (type, $action, $messages) {
			this.type = type;
			this.$action = $action;
			this.$messages = $messages;
		},

		/*events: {
			'click #confirmOkButton': 'ok',
			'click #confirmCancelButton': 'close'
		},*/

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
			var self = this;
			//this.$("#confirmOkButton").on("click", this.ok);
			this.$("#confirmCancelButton").on("click", function () {
				self.$action.popover("hide");
			});
			return this;
		},

		cancelRequiresComment: function() {
			return !(this.model.get("status")=="NEW" && app.loggedUser.role=="REQUESTOR"); //only when the user is a Requestor and the status is New we will delete the ticket. In any other case we require a comment.
		},
		
		showErrors: function() {
			return typeof this.$messages != "undefined";
		},

		close: function() {
			self.$action.popover("hide");
		},

		ok: function() {
			if (self.type==app.util.CANCEL_POPOVER) {
				self.cancel();
			}
			else if (self.type==app.util.REJECT_POPOVER) {
				self.reject();
			}
			else if (self.type==app.util.APPROVE_POPOVER) {
				self.approve();
			}
			else if (self.type==app.util.DONE_POPOVER) {
				self.done();
			}
		},

		//TODO: factorize all $.ajax calls
		cancel: function () {
			var data = null;
			if (self.cancelRequiresComment()) {
				data = self.$("textarea").val().trim(); //selector works cause the popover specifies the "container" property, making it a child of $el; and then there's only one "textarea"
				if (!data) {
					if (self.showErrors()) {
			    		app.util.displayError(this.$messages, "Need to provide a comment to proceed");
			    	}
			    	self.close();
			    	return;	
			    }
			}
			//var self = this;
		    $.ajax({
  				url: "/tt/tickets/"+self.model.get("number")+"/cancel",
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
			    	self.close();
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
		    	this.close();
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
			    	self.close();
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
			    	self.close();
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
			    	self.close();
			    }
			});
		}

	});
})(jQuery);
