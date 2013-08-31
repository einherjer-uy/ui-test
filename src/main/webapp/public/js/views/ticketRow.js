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
			
			this.$(".actions").append(new app.ActionsView({ model: this.model, onAddEditModal: false, $messages: this.$messages }).render().el);

			if (app.loggedUser.role==app.util.ROLE_REQUESTOR) {
				if (this.model.get("status")=="APPROVED") {
					this.$el.addClass("success");
				}
				if (this.model.get("status")=="REJECTED") {
					this.$el.addClass("error");
				}
			}
			else {
				if (this.model.get("unread")) {
					this.$el.addClass("unread");
				}
				else {
					this.$el.removeClass("unread");
				}
			}

			return this;
		}

	});
})(jQuery);
