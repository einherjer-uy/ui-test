var app = app || {};

(function ($) { "use strict";

	//This view doesn't need to be a separate view, it could be included in ActionsView (using proper selectors to match the events)
	//		The spans that contain the actions (viewSpan, editSpan, etc) that we added in order to set a "container" to the popover()
	//		is also useful here to define those selectors (e.g. ".cancelSpan .confirmOkButton", ".cancelSpan .confirmCancelButton", etc)
	//		given that the button will be contained by the span cause that's defined by the "container" parameter of the popover).
	//(Actually we wouldn't even need to use the containing span in the selector cause the way the popover works it is added and removed
	//from the DOM so actually only 1 "confirmOkButton" exists in the "body" at a time.)
	app.PopoverView = Backbone.View.extend({

		commentConfirmationTemplate: _.template($('#commentConfirmationTemplate').html()),
		yesNoConfirmationTemplate: _.template($('#yesNoConfirmationTemplate').html()),

		initialize: function (options) {
			this.type = options.type;
			this.$action = options.$action;
			this.$messages = options.$messages;
			this.parentView = options.parentView;
			this.onAddEditModal = options.onAddEditModal;
			this.$addEditModal = $('#addEditModal');
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
			return !(this.model.get("status")=="NEW" && app.loggedUser.role==app.util.ROLE_REQUESTOR); //only when the user is a Requestor and the status is New we will delete the ticket. In any other case we require a comment.
		},
		
		showErrors: function() {
			return this.$messages !== undefined;
		},

		closePopover: function() {
			this.$action.popover("hide");
			//THE PROBLEM HERE is that popover("hide") removes it from the DOM, so the events get lost, so, the ok/cancel 
			//		buttons only work the first time the popover (of a specific "action glyphicon") is show.
			//There are several solutions. Here I'm just calling render() on the parent view again, which will 
			//		in turn instantiate new PopupViews and  call popover() again on the buttons. There's no need to call stopListening
			//		cause the view doesn't "listenTo" any model that could keep a reference to the view preventing its garbage collection)
			//		If that were necessary we would need to keep a list of the 4 PopoverViews instantiated by the parent, and stopListening each of them
			this.parentView.render(); 
			//Another option would be to use jquery "on" and bind the clic event to an element that is not removed from the DOM (i.e. the container
			//		of the popover). E.g. this.$cancelAction.popover({... content:popoverView.render().el}).parent().on('click', 
			//		'button.confirmCancelButton', <data>, function(event) { event.data... };
			//		Since popover("hide") removes the popover from the DOM we don't worry about the fact that there might be more than one
			//		match for the "button.confirmCancelButton" selector, since only one popover and one button with that class will exist at a time
		},

		ok: function() {
			//Note1: make use of the parentView attribute that is here for a different reason but this way reause the markAsRead function easily.
			//Note2: it is possible to do something like $('.element').popover( ... ).click(function() { alert("click"); });
			//		anyway in our case it doesn't work cause after markAsRead the row is rerendered and the popover closes
    		this.parentView.markAsRead(); 

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
				success: function(data) {
				    app.tickets.fetch({reset:true}); //fetch needed cause the ticket might have been removed on the server
				    if(self.onAddEditModal) {
						self.$addEditModal.modal("hide");
					}
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
				    app.tickets.fetch({reset:true}); //fetch needed cause after the status has changed the server might determine the ticket in its new status shouldn't be returned/shown, otherwise a model.fetch() would be enough
				    if(self.onAddEditModal) {
						self.$addEditModal.modal("hide");
					}
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
				    app.tickets.fetch({reset:true}); //fetch needed cause after the status has changed the server might determine the ticket in its new status shouldn't be returned/shown, otherwise a model.fetch() would be enough
				    if(self.onAddEditModal) {
						self.$addEditModal.modal("hide");
					}
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
				    app.tickets.fetch({reset:true}); //fetch needed cause after the status has changed the server might determine the ticket in its new status shouldn't be returned/shown, otherwise a model.fetch() would be enough
				    if(self.onAddEditModal) {
						self.$addEditModal.modal("hide");
					}
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
