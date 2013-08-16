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

		save: function (e) {
			this.hideErrors();
			this.model.set(this.newAttributes());
			if (this.model.isNew()) {
				this.model.save(null,{
					success:function(model, response, options) {
						model.set({number:response.number});
					}
				});
				if (!this.model.validationError) {
					app.tickets.add(this.model);
				}
	        } else {
	            this.model.save(this.model.changedAttributes(), {patch:true});
	        }
	        if (this.model.validationError) {
				this.showErrors(this.model.validationError);
			}else{
				this.$addEditModal.modal("hide");	
			}
		},

		showErrors: function(errors) {
        	_.each(errors, function (error) {

	            var alertContainer = $('#ticket-alert-container');
	            alertContainer.append("<div class='alert alert-error'><a class='close' data-dismiss='alert'>×</a><strong>Error: </strong>" + error.message + "</div>") ;
	        
	            var controlGroup = $('#' + error.name);
	            controlGroup.parent().parent().addClass("has-error");
        	}, this);
 		},

		hideErrors: function () {
	        var alertContainer = $('#ticket-alert-container');
	        alertContainer.html('');

	        $('.form-group').removeClass('has-error');	
	    },

		newAttributes: function () {
			return {
				title : $('#addEditModal #title').val().trim(), //in this case we cannot "cache" the selection in initialize() cause initialized is fired in the construction and only after that the html is appended to the modal (see app.AppView.add or app.TicketRowView.edit)
				description : $('#addEditModal #description').val().trim()
			};
		}

	});
})(jQuery);
