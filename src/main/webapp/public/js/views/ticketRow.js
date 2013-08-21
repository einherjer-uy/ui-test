var app = app || {};

(function ($) { "use strict";

	app.TicketRowView = Backbone.View.extend({

		tagName: 'tr',

		template: _.template($('#item-template').html()),

		initialize: function () {
			this.$addEditModal = $('#addEditModal');
			this.$messages = $('#dashboardMessages');

			this.listenTo(this.model, 'change', this.render);
		},

		render: function () {
			this.$el.html(this.template(this.model.toJSON()));
			var view = new app.ActionsView({ model: this.model });
			view.setParams(this.$addEditModal, this.$messages);
			this.$("#actions").append(view.render().el);
			return this;
		}

	});
})(jQuery);
