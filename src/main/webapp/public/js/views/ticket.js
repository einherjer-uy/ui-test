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

		initialize: function (options) {
			// to improve performance run the needed selectors only once in the initialize or render funcions and store the references that will be used in other view methods
			// in this case we do it in initialize() (instead of in render()) cause the elements to be selected belong to a separate modal, unrelated to the view's "el" (<li> in this case)
			// in this case we don't use the syntax this.$(<selector>); cause the edit is done in a separate modal, unrelated to the view's "el" (<li> in this case)
			this.$addEditModal = $('#addEditModal');
			this.$outerMessages = options.$outerMessages;
		},

		render: function () {
			this.$el.html(this.template(this.model.toJSON()));
			this.$description = this.$('#description'); //in this case we cannot "cache" the selection in initialize() cause initialized is fired in the construction and only after that the html is appended to the modal (see app.AppView.add or app.TicketRowView.edit), but we can use view.$ (shorthand for $(view.el).find) after $el is populated
			this.$type = this.$('#type');
			this.$priority = this.$('#priority');
			this.$due = this.$('#due');
			this.$comment = this.$("textarea#comment");
			this.$alertContainer = this.$('#ticket-alert-container');

			var self = this;
			
		    $.each(app.ticketTypes, function(item) {
		        self.$type.append(this);
		    });
		    this.$type.val(this.model.get("type"));
		    
		    $.each(app.ticketPriorities, function(item) {
		        self.$priority.append(this);
		    });
		    this.$priority.val(this.model.get("priority"));
		    
		    if (!this.model.isNew()) {
		    	//pass $addEditModal = undefined to the view ctor cause we don't want the actions bar to open a new addEditModal (means view/edit options will be hidden)
				this.$("#actions").append(new app.ActionsView({ model: this.model, $messages: this.$alertContainer }).render().el);
			}

			if(app.loggedUser.role=="REQUESTOR") {
				this.$("#commentDiv").hide();
			}
			else if(app.loggedUser.role=="APPROVER") {
				this.$description.prop("readonly",true);
				this.$due.prop("readonly",true);
				this.$type.prop("disabled",true);
			}
			else if(app.loggedUser.role=="EXECUTOR") {
				this.$description.prop("readonly",true);
				this.$due.prop("readonly",true);
				this.$type.prop("disabled",true);
				this.$priority.prop("disabled",true);
				this.$("#commentDiv").hide();
				this.$("#saveButton").hide();
			}

			return this;
		},

		save: function (e) {
			if (app.loggedUser.role=="REQUESTOR") {
				this.hideErrors();
				this.model.set(this.newAttributes());
				var self = this;
				//var serverError = null;
				if (this.model.isNew()) {
					this.model.save(null, { //wait: true,
						success: function (model, response, options) {
							model.set({number:response.number});
						},
						error: function (model, xhr, options) {
							//serverError = xhr.responseJSON.message;
							app.util.displayError(self.$outerMessages, xhr.responseJSON.message);
						}
					});
					if (!this.model.validationError) {
						app.tickets.add(this.model);
					}
		        } else {
		            this.model.save(this.model.changedAttributes(), { patch: true, //wait: true,
		            	//success: function (model, response, options) {
						//	console.log('ok');
						//},
		                error: function (model, xhr, options) {
		                	//serverError = xhr.responseJSON.message;
							app.util.displayError(self.$outerMessages, xhr.responseJSON.message);
							//app.util.displayError(self.$alertContainer, xhr.responseJSON.message);
				        }
		            });
		        }
		        if (this.model.validationError) {
					this.showErrors(this.model.validationError);
				}
				//else if(serverError) {
				//	app.util.displayError(this.$alertContainer, serverError);
				//}
				else {
					this.$addEditModal.modal("hide");	
				}
			}
			else if (app.loggedUser.role=="APPROVER") {
				var self = this;
			    $.ajax({
	  				url: "/tt/tickets/"+this.model.get("number")+"/priority",
	  				type: "POST",
	  				data: JSON.stringify({priority: this.$priority.val(), text: this.$comment.val().trim()}),
	  				contentType: "application/json; charset=utf-8",
	  				dataType: "json",
					success: function(data){
						self.model.set("priority", self.$priority.val());
					    self.$addEditModal.modal("hide");
					},
				    error: function(data) {
				    	app.util.displayError(self.$alertContainer, data.responseJSON.message);
				    }
				});
			}
		},

		showErrors: function(errors) {
        	_.each(errors, function (error) {
	            this.$alertContainer.append("<div class='alert alert-error'><a class='close' data-dismiss='alert'>&times;</a><strong>Error: </strong>" + error.message + "</div>") ;
	            $('#' + error.name).parent().parent().addClass("has-error");
        	}, this);
 		},

		hideErrors: function () {
	        this.$alertContainer.html('');
	        this.$alertContainer.removeClass('has-error');
	    },

		newAttributes: function () {
			return {
				description: this.$description.val().trim(),
				type: this.$type.val(),
				priority: this.$priority.val(),
				due: this.$due.val(),
			};
		}

	});
})(jQuery);
