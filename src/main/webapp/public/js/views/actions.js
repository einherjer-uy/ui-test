var app = app || {};

(function ($) { "use strict";

	app.ActionsView = Backbone.View.extend({

		tagName: 'div',

		template: _.template($('#actionsTemplate').html()),

		initialize: function (options) {
			this.$messages = options.$messages;
			this.onAddEditModal = options.onAddEditModal;
			this.$addEditModal = $('#addEditModal');
		},

		events: {
			//NOTE 1. there are several viewAction, etc in the page (one per row) but backbone makes these selectors relative to the view.$el
			//NOTE 2. the binding uses jquery "on" (or "delegate" in older jquery) so it works even if the element doesn't exist in the DOM
			//	      at the moment the View is instantiated (like in this case, the DOM containing the elements selected here are created by the render() function)
			'click .viewAction': 'view', 
			'click .editAction': 'edit'
		},

		render: function () {
			this.$el.html(this.template(this.model.toJSON()));
			this.$viewAction = this.$(".viewAction");
			this.$editAction = this.$(".editAction");
			this.$cancelAction = this.$(".cancelAction");
			this.$rejectAction = this.$(".rejectAction");
			this.$approveAction = this.$(".approveAction");
			this.$doneAction = this.$(".doneAction");

			this.$viewAction.tooltip({placement:"bottom", title:"View"});
			this.$editAction.tooltip({placement:"bottom", title:"Edit"});
			this.$cancelAction.tooltip({placement:"bottom", title:"Cancel"});
			this.$rejectAction.tooltip({placement:"bottom", title:"Reject"});
			this.$approveAction.tooltip({placement:"bottom", title:"Approve"});
			this.$doneAction.tooltip({placement:"bottom", html:true, title:"<span style='white-space:nowrap'>Mark as Done</span>"});

			var self = this;

			var popoverView = new app.PopoverView({ model: this.model, type: app.util.CANCEL_POPOVER, $action: this.$cancelAction, $messages: this.$messages, parentView: this, onAddEditModal: this.onAddEditModal })
			//WATCH OUT, "container: '.cancelSpan'" doesn't work cause this is not handled by backbone, hence it's relative to the document, not
			//to the view.$el, so in this case there might be more than one match (one per row in the table actually). Anyway, as expained in popover.js
			//the container is not really relevant for event bining of the popovers (since they are removed from the DOM so there's only one "ok" and "cancel"
			//button in the dom at a time anyway); in theory it is only relevant from a visual perspective to ensure the popover remains together with 
			//the button that triggered it in the case of window resize, but apparently here it stays together even with container: false
			this.$cancelAction.popover({placement:this.popupPlacement(), container: false, title:"Confirmation", html:true, 
				content:popoverView.render().el});
			//works only with bootstrap 3
			//this.$cancelAction.on('shown.bs.popover', function () {
			//	self.$cancelAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
				//self.$(".tt-action").css("display","inline");
				/*self.$editAction.css("display","inline");
				self.$cancelAction.css("display","inline");
				self.$rejectAction.css("display","inline");
				self.$approveAction.css("display","inline");*/
			//});
			//this.$cancelAction.on('hidden.bs.popover', function () {
				//self.$(".tt-action").css("display","none");
			//});

			var popoverView = new app.PopoverView({ model: this.model, type: app.util.REJECT_POPOVER, $action: this.$rejectAction, $messages: this.$messages, parentView: this, onAddEditModal: this.onAddEditModal });
			this.$rejectAction.popover({placement:this.popupPlacement(), container: false, title:"Confirmation", html:true,
				content:popoverView.render().el});
			//works only with bootstrap 3
			//this.$rejectAction.on('shown.bs.popover', function () {
			//	self.$rejectAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
			//});

			popoverView = new app.PopoverView({ model: this.model, type: app.util.APPROVE_POPOVER, $action: this.$approveAction, $messages: this.$messages, parentView: this, onAddEditModal: this.onAddEditModal });
			this.$approveAction.popover({placement:this.popupPlacement(), container: false/*'.approveSpan'*/, title:"Confirmation", html:true,
				content:popoverView.render().el});
			//works only with bootstrap 3
			//this.$approveAction.on('shown.bs.popover', function () {
			//	self.$approveAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
			//});

			popoverView = new app.PopoverView({ model: this.model, type: app.util.DONE_POPOVER, $action: this.$doneAction, $messages: this.$messages, parentView: this, onAddEditModal: this.onAddEditModal });
			this.$doneAction.popover({placement:this.popupPlacement(), container: false/*'.doneSpan'*/, title:"Confirmation", html:true,
				content:popoverView.render().el});
			//works only with bootstrap 3
			//this.$doneAction.on('shown.bs.popover', function () {
			//	self.$doneAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
			//});

			if(this.onAddEditModal) {
				this.$viewAction.hide();
				this.$editAction.hide();
			}
			if(app.loggedUser.role==app.util.ROLE_REQUESTOR) {
				this.$viewAction.hide();
				this.$approveAction.hide();
				this.$rejectAction.hide();
				this.$doneAction.hide();
			}
			if(app.loggedUser.role==app.util.ROLE_APPROVER) {
				this.$viewAction.hide();
				this.$doneAction.hide();
			}
			if(app.loggedUser.role==app.util.ROLE_EXECUTOR) {
				this.$editAction.hide();
				this.$cancelAction.hide();
				this.$approveAction.hide();
				this.$rejectAction.hide();
			}
			return this;
		},

		popupPlacement: function () {
			return this.onAddEditModal ? "top" : "left";
		},

		edit: function () {
			this.showModal(this.model);
		},

		view: function () {
			this.showModal(this.model);
		},

		//TODO: duplicated with AppView.showModal, move to util.js
		showModal: function(ticket) {
			this.$messages.html('');
			this.$addEditModal.html(new app.TicketView({model: ticket}).render().el);
        	this.$addEditModal.modal({keyboard: false, backdrop: "static"});
		}

	});
})(jQuery);
