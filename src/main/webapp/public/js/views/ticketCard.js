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
			this.$el.html(this.template({number: this.model.get("number"), 
									description: this.model.get("description"), 
							   creatorFirstName: creatorFirstName,
							    creatorLastName: creatorLastName,
							        dateCreated: dateCreated
										})
			);			
			this.$(".actions").append(new app.ActionsView({ model: this.model, onAddEditModal: false, $messages: this.$messages }).render().el);
			this.$el.find('.timeago').timeago();
			return this;
		}

	});
})(jQuery);
