var app = app || {};

(function ($) { "use strict";

	app.TicketView = Backbone.View.extend({

		tagName: 'div',
		className: 'modal-dialog',

		template: _.template($('#modal-template').html()),

		events: {
			'click #saveButton' : 'save',
			'click #closeButton' : 'close'
		},

		initialize: function () {
			// to improve performance run the needed selectors only once in the initialize or render funcions and store the references that will be used in other view methods
			// in this case we do it in initialize() (instead of in render()) cause the elements to be selected belong to a separate modal, unrelated to the view's "el" (<li> in this case)
			// in this case we don't use the syntax this.$(<selector>); cause the edit is done in a separate modal, unrelated to the view's "el" (<li> in this case)
			this.$addEditModal = $('#addEditModal');
		},

		render: function () {
			this.$el.html(this.template(this.model.toJSON()));
			return this;
		},

/*
		// Close the `"editing"` mode, saving changes to the todo.
		close: function () {
			var value = this.$input.val().trim();

			if (value) {
				this.model.save({ title: value });
			} else {
				this.clear();
			}

			this.$el.removeClass('editing');
		},

		// If you hit `enter`, we're through editing the item.
		updateOnEnter: function (e) {
			if (e.which === ENTER_KEY) {
				this.close();
			}
		},d
*/
		save: function (e) {
			//if (/*e.which !== ENTER_KEY || */!this.$input.val().trim()) {
			//	return;
			//}

			//var newTicket = app.tickets.create(this.newAttributes());
			//newTicket.fetch();

			this.model.set(this.newAttributes());
			this.model.save({wait: true});
			this.model.fetch();
			app.tickets.add(this.model);
			this.$addEditModal.modal("hide");
		},

		newAttributes: function () {
			return {
				title : $('#addEditModal #title').val().trim(), //in this case we cannot "cache" the selection in initialize() cause initialized is fired in the construction and only after that the html is appended to the modal (see app.AppView.add or app.TicketRowView.edit)
				description : $('#addEditModal #description').val().trim()
			};
		},

		close: function() {

		}

	});
})(jQuery);
