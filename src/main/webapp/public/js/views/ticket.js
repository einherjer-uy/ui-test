var app = app || {};

(function ($) { "use strict";

	app.TicketView = Backbone.View.extend({

		template: _.template($('#modal-template').html()),

		events: {
			'click #saveButton' : 'save',
			'click #closeButton' : 'close',
			'keyup #description' : 'descriptionModalCountChar'
		},

		initialize: function (options) {
			// to improve performance run the needed selectors only once in the initialize or render funcions and store the references that will be used in other view methods
			// in this case we do it in initialize() (instead of in render()) cause the elements to be selected belong to a separate modal, unrelated to the view's "el" (<li> in this case)
			// in this case we don't use the syntax this.$(<selector>); cause the edit is done in a separate modal, unrelated to the view's "el" (<li> in this case)
			this.attachmentsTemplate = _.template($('#attachmentsTemplate').html());
			this.$addEditModal = $('#addEditModal');
		},

		render: function () {
			this.$el.html(this.template(this.getTemplateData()));
			this.$description = this.$('#description'); //in this case we cannot "cache" the selection in initialize() cause initialized is fired in the construction and only after that the html is appended to the modal (see app.AppView.add or app.TicketRowView.edit), but we can use view.$ (shorthand for $(view.el).find) after $el is populated
			this.$type = this.$('#type');
			this.$priority = this.$('#priority');
			this.$due = this.$('#due');
			this.$comment = this.$("textarea#comment");
			this.$alertContainer = this.$('#ticket-alert-container');
			this.$descriptionCharNum = this.$('#descriptionCharNum');
			this.$uploadedFiles = this.$("#uploadedFiles");

			this.$("#duedate-datetimepicker").datetimepicker({
        		format: 'MM/dd/yyyy-hh:mm',
        		pickSeconds: false
        	});

			var self = this;
			this.renderAttachments();
			this.$('#fileupload').fileupload({
		        dataType: 'json',
		        url: "tt/tickets/"+self.model.get("number")+"/attachment",
		        done: function (e, data) {
		        	self.model.set("attachments", data.result);
		            self.renderAttachments();
		        },
		        progressall: function (e, data) {
		            var progress = parseInt(data.loaded / data.total * 100, 10);
		            if(progress==100){
		                self.$('#progress').hide();
		            }
		            else {
		                self.$('#progress').show();
		                self.$('#progress .bar').css('width',progress + '%');    
		            }
		        }
		    });

		    $.each(app.ticketTypes, function(item) {
		        self.$type.append(this);
		    });
		    this.$type.val(this.model.get("type"));
		    
		    $.each(app.ticketPriorities, function(item) {
		        self.$priority.append(this);
		    });
		    this.$priority.val(this.model.get("priority"));
		    
		    if (!this.model.isNew()) {
				this.$(".actions").append(new app.ActionsView({ model: this.model, onAddEditModal: true, $messages: this.$alertContainer }).render().el);
			}

			if(app.loggedUser.role==app.util.ROLE_REQUESTOR) {
				this.$("#commentDiv").hide();
			}
			else if(app.loggedUser.role==app.util.ROLE_APPROVER) {
				this.$description.prop("readonly",true);
				this.$due.prop("readonly",true);
				this.$type.prop("disabled",true);
			}
			else if(app.loggedUser.role==app.util.ROLE_EXECUTOR) {
				this.$description.prop("readonly",true);
				this.$due.prop("readonly",true);
				this.$type.prop("disabled",true);
				this.$priority.prop("disabled",true);
				this.$("#commentDiv").hide();
				this.$("#saveButton").hide();
			}

			return this;
		},

		renderAttachments: function() {
			var data = this.model.get("attachments");
			this.$uploadedFiles.html("");
			if (data && data.length>0) {
				this.$uploadedFiles.append(this.attachmentsTemplate({attachments: data, ticketNumber: this.model.get("number")}));
			}
			var self = this;
			this.$uploadedFiles.find("[id^=deleteAttach]").click( function() {
				$.ajax({
	  				url: "/tt/tickets/"+self.model.get("number")+"/attachment/"+$(this).attr("id").replace("deleteAttach",""), //note the need of $( ) around this to be able to use jQuery attr, only this doesn't work 
	  				type: "DELETE",
	  				data: null,
	  				contentType: "application/json; charset=utf-8",
	  				dataType: "json",
					success: function() {
						//careful here, we cannot do model.fetch and then renderAssessment cause fetch is an ajax (asynchronous) call,
						//so we need to use callbacks yo make sure the request has been completed
		            	self.model.fetch({
		            		success: function (model, response, options) {
		            			self.renderAttachments();
		            		}
		            	});
					},
				    error: function(data) {
				    	if (data.responseJSON && data.responseJSON.message) {
				    		app.util.displayError(self.$alertContainer, data.responseJSON.message);
				    	}
				    	else { //just in case the server missed to return a proper json with "message" value
				    		app.util.displayError(self.$alertContainer, "Unexpected server error");
				    	}
				    }
				});
			});
		},

		getTemplateData: function() {
			var templateData = this.model.toJSON();
			if (this.model.isNew()) {
				templateData.modalTitle = "Create ticket";
			}
			else {
				templateData.modalTitle = this.model.get("number") + ": Edit";
			}
			return templateData;
		},

		save: function (e) {
			this.hideErrors();
			if (app.loggedUser.role==app.util.ROLE_REQUESTOR) {
				this.model.set(this.newAttributes());
				var serverError;
				if (this.model.isNew()) {
					this.model.save(null, {
						success: function (model, response, options) {							
							var ticketNumber = response.number;
							app.tickets.fetch({  //call server to fetch the collection, which will in turn trigger the update of the view
						        success: function () {
						        	//Hide progress bar and black background
					                $('#pleaseWaitDialog').hide();
									$(".modal-backdrop").hide();
									
									app.util.displayInfo($('#dashboardMessages'), "Ticket " +  ticketNumber + " successfully created", false);
					            }	
					        });
							//model.set({number:response.number});
							//app.tickets.add(model);
						},
						error: function (model, xhr, options) {
							if (xhr.responseJSON && xhr.responseJSON.message) {
					    		serverError = xhr.responseJSON.message;
					    	}
					    	else { //just in case the server missed to return a proper json with "message" value
					    		serverError = "Unexpected server error";	
					    	}
						}
					});
		        } else {
		        	//doing something like this doesn't work cause model.save is an ajax (asynchronous call), the rest of the code
		        	//in app.TicketView.save executes before the "error" callback is called, so the serverError variable is not
		        	//updated by time the function checks its value to show the error at the end (on "else if(serverError) {" etc etc)
		            //this.model.save(this.model.changedAttributes(), { patch: true, wait: true,
		            //    error: function (model, xhr, options) {
					//		serverError = xhr.responseJSON.message;
				    //    }
		            //});
					var data = this.model.changedAttributes();
					if (data) { //if nothing changed don't even call the server
						$.ajax({
			  				url: "/tt/tickets/"+this.model.get("number"),
			  				type: "PATCH",
			  				async: false,
			  				//TODO: validar que changedAttributes tenga algo si no revienta por bad request
			  				data: JSON.stringify(data),
			  				contentType: "application/json; charset=utf-8",
			  				dataType: "json",
						    error: function(data) {
						    	if (data.responseJSON && data.responseJSON.message) {
						    		serverError = data.responseJSON.message;
						    	}
						    	else { //just in case the server missed to return a proper json with "message" value
						    		serverError = "Unexpected server error";	
						    	}
						    }
						});
					}
					else {
						serverError = "Save aborted cause apparently you haven't done any change";
					}
		        }
		        if (this.model.validationError) {
					this.showErrors(this.model.validationError);
				}
				else if(serverError) {
					app.util.displayError(this.$alertContainer, serverError);
				}
				else {
					this.$addEditModal.modal("hide");
					app.util.displayInfo($('#dashboardMessages'), "Ticket " + this.model.get("number") + " successfully updated", false);	
				}
			}
			else if (app.loggedUser.role==app.util.ROLE_APPROVER) {
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
					    app.util.displayInfo($('#dashboardMessages'), "Ticket " + this.model.get("number") + " successfully updated", false);	
					},
				    error: function(data) {
				    	if (data.responseJSON && data.responseJSON.message) {
				    		app.util.displayError(self.$alertContainer, data.responseJSON.message);
				    	}
				    	else { //just in case the server missed to return a proper json with "message" value
				    		app.util.displayError(self.$alertContainer, "Unexpected server error");
				    	}
				    }
				});
			}
		},

		showErrors: function(errors) {
        	_.each(errors, function (error) {
	            this.$alertContainer.append("<div class='alert alert-error'><a class='close' data-dismiss='alert'>&times;</a><strong>Error: </strong>" + error.message + "</div>") ;
	            if (error.name != '') {
	            	$('#' + error.name).parent().parent().addClass("error");	
	            };	            
        	}, this);
 		},

		hideErrors: function () {
	        this.$alertContainer.html('');
	        this.$alertContainer.removeClass('error');	
	    },

		newAttributes: function () {
			return {
				description: this.$description.val().trim(),
				type: this.$type.val(),
				priority: this.$priority.val(),
				due: this.$due.val(),
			};
		},

		descriptionModalCountChar: function() {
	    	var len = this.$description.val().length;
	        if (len >= 3000) {
	        	this.$description.val(this.$description.val().substring(0, 3000));
	        } else {
	        	this.$descriptionCharNum.text((3000 - len).toString()) ; 
	        }
		}

	});
})(jQuery);
