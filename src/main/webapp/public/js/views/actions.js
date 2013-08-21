var app = app || {};

(function ($) { "use strict";

	app.ActionsView = Backbone.View.extend({

		tagName: 'div',

		template: _.template($('#actionsTemplate').html()),

		setParams: function ($addEditModal, $messages) {
			this.$addEditModal = $addEditModal;
			this.$messages = $messages;
		},

		events: {
			'click #viewAction': 'view',
			'click #editAction': 'edit',
			'click #cancelAction': 'cancel',
			//these events work because:
			//1) the binding uses jquery delegate so it works even if the element doesn't exist in the DOM
			//at the moment the View is instantiated (like in this case, the popover DOM is inserted by bootstrap javascrip)
			//2) the popover specifies the "container" property (see render() below) which makes the popover DOM a children
			//of this "container" which is what makes these event selectors find the element
			/*'click #cancelSpan #confirmOkButton': 'cancel', 
			'click #rejectSpan #confirmOkButton': 'reject',
			'click #approveSpan #confirmOkButton': 'approve',
			'click #doneSpan #confirmOkButton': 'done',

			'click #cancelSpan #confirmCancelButton': 'hideCancelPopover', 
			'click #rejectSpan #confirmCancelButton': 'hideRejectPopover',
			'click #approveSpan #confirmCancelButton': 'hideApprovePopover',
			'click #doneSpan #confirmCancelButton': 'hideDonePopover'*/
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

/*			var view = new app.PopoverView({ model: this.model});
			view.setParams(app.util.CANCEL_POPOVER, this.$cancelAction, this.$messages);
			this.$cancelAction.popover({placement:"left", container: '#cancelSpan', title:"Confirmation", html:true, 
				content:view.render().el});
			this.$cancelAction.on('shown.bs.popover', function () {
				self.$cancelAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
				//self.$(".tt-action").css("display","inline");
				/*self.$editAction.css("display","inline");
				self.$cancelAction.css("display","inline");
				self.$rejectAction.css("display","inline");
				self.$approveAction.css("display","inline");*/
//			});
			//this.$cancelAction.on('hidden.bs.popover', function () {
				//self.$(".tt-action").css("display","none");
			//});

			var view = new app.PopoverView({ model: this.model });
			view.setParams(app.util.REJECT_POPOVER, this.$rejectAction, this.$messages);
			this.$rejectAction.popover({placement:"left", container: '#rejectSpan', title:"Confirmation", html:true,
				content:view.render().el});
			this.$rejectAction.on('shown.bs.popover', function () {
				self.$rejectAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
			});

			view = new app.PopoverView({ model: this.model });
			view.setParams(app.util.APPROVE_POPOVER, this.$approveAction, this.$messages);
			this.$approveAction.popover({placement:"left", container: '#approveSpan', title:"Confirmation", html:true,
				content:view.render().el});
			this.$approveAction.on('shown.bs.popover', function () {
				self.$approveAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
			});

			view = new app.PopoverView({ model: this.model });
			view.setParams(app.util.DONE_POPOVER, this.$doneAction, this.$messages);
			this.$doneAction.popover({placement:"left", container: '#doneSpan', title:"Confirmation", html:true,
				content:view.render().el});
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

		cancel: function() {
			var view = new app.PopoverView({ model: this.model});
			view.setParams(app.util.CANCEL_POPOVER, this.$cancelAction, this.$messages);
			var popoverContent = view.render().el;
			this.$cancelAction.popover({placement:"left", container: '#cancelSpan', title:"Confirmation", html:true, content:popoverContent});
//			this.$cancelAction.on('shown.bs.popover', function () {
//				self.$cancelAction.tooltip("hide"); //hide the tooltip when the popover is shown, otherwise they overlap
				//self.$(".tt-action").css("display","inline");
				/*self.$editAction.css("display","inline");
				self.$cancelAction.css("display","inline");
				self.$rejectAction.css("display","inline");
				self.$approveAction.css("display","inline");*/
//			});
			//this.$cancelAction.on('hidden.bs.popover', function () {
				//self.$(".tt-action").css("display","none");
			//});
		},
		showViewEditOptions: function() {
			return typeof this.$addEditModal != "undefined";
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
        	$("#due").datetimepicker({
        		separator: "-",
				stepMinute: 30,
				controlType: "select"
        	});
        	this.$addEditModal.modal({keyboard: false, backdrop: "static"});
		}

	});
})(jQuery);
