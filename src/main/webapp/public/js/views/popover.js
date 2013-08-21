var app = app || {};

(function ($) { "use strict";

	app.PopoverView = Backbone.View.extend({

		tagName: 'div',

		commentConfirmationTemplate: _.template($('#commentConfirmationTemplate').html()),

		events: {
			'click #confirmOkButton': 'ok',
			'click #confirmCancelButton': 'cancel',
			'lostfocus': 'cancel'
		},

		/*initialize: function () {

		},*/

		render: function () {
			this.$el.html(this.commentConfirmationTemplate({mandatoryComment:true}));
			return this;
		},


		cancel: function () {
			this.$action.popover("hide");
		}


	});
})(jQuery);
