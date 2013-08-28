var app = app || {};

(function ($) { "use strict";

	app.TicketCardView = Backbone.View.extend({

		tagName: 'div',
		className: 'span4',

		template: _.template($('#item-card-template').html()),

		initialize: function () {
			this.$addEditModal = $('#addEditModal');
			this.$messages = $('#dashboardMessages');

			this.listenTo(this.model, 'change', this.render);
		},

		render: function () {
			this.$el.html(this.template(this.model.toJSON()));
			this.$(".actions").append(new app.ActionsView({ model: this.model, onAddEditModal: false, $messages: this.$messages }).render().el);
			return this;
		}

	});
})(jQuery);
