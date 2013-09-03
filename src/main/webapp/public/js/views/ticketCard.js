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
			var creatorFirstName = "";
			var creatorLastName = "";
			var dateCreated = ""; 
			var log = this.model.get("log");

			if (log && log.length > 0) {
				creatorFirstName = log[log.length-1].user.firstName;
				creatorLastName = log[log.length-1].user.lastName;
				dateCreated = log[log.length-1].timestamp; 
			};
			this.$el.html(this.template(this.model.toJSON()));	

			this.$(".priority").tooltip({placement:"top", title: this.model.get("priority") });
			this.$(".duedate").tooltip({placement:"right", title: !this.model.get("due") ? "-" : this.model.get("due").substr(11,5) });

			this.$(".actions").append(new app.ActionsView({ model: this.model, onAddEditModal: false, $messages: this.$messages }).render().el);
			this.$el.find('.timeago').timeago();

			if (app.loggedUser.get("role")==app.util.ROLE_REQUESTOR) {
				if (this.model.get("status")=="APPROVED") {
					this.$(".thumbnail").addClass("tt-card-success");
				}
				if (this.model.get("status")=="REJECTED") {
					this.$(".thumbnail").addClass("tt-card-error");
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
