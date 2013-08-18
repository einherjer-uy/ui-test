var app = app || {};

(function ($) { "use strict";

	app.TicketRowView = Backbone.View.extend({

		tagName: 'tr',

		template: _.template($('#item-template').html()),

		events: {
			'click #action-edit': 'edit',
			'click #action-cancel': 'cancel',
			'click #action-approve': 'approve'
		},

		initialize: function () {
			// to improve performance run the needed selectors only once in the initialize or render funcions and store the references that will be used in other view methods
			// in this case we do it in initialize() (instead of in render()) cause the elements to be selected belong to a separate modal, unrelated to the view's "el" (<li> in this case)
			// in this case we don't use the syntax this.$(<selector>); cause the edit is done in a separate modal, unrelated to the view's "el" (<li> in this case)
			this.$addEditModal = $('#addEditModal'); 

			this.listenTo(this.model, 'change', this.render);
			//this.listenTo(this.model, 'destroy', this.close);
		},

		render: function () {
			this.$el.html(this.template(this.model.toJSON()));
			return this;
		},

		edit: function () {
			this.showModal(this.model);
		},

		//TODO: duplicated with AppView.showModal, move to util.js
		showModal: function(ticket) {
			this.$addEditModal.html(new app.TicketView({model: ticket}).render().el);
        	$("#dueDate").datetimepicker({
        		separator: "-",
				stepMinute: 30,
				controlType: 'select'
        	});
        	this.$addEditModal.modal({keyboard: false, backdrop: "static"});
		},

		cancel: function () {
			//this.model.destroy(); //in this case we never delete tickets from the database, we just mark them as completed
			this.model.save("status", "CANCELLED", {patch:true}); //sends only the specified fields to the server
		},

		approve: function () {
			this.model.save("status", "APPROVED", {patch:true}); //sends only the specified fields to the server
		},

	});
})(jQuery);
