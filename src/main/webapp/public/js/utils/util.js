var app = app || {};

(function ($) { "use strict";
	app.util = {

		CANCEL_POPOVER: "CANCEL_POPOVER",
		REJECT_POPOVER: "REJECT_POPOVER",
		APPROVE_POPOVER: "APPROVE_POPOVER",
		DONE_POPOVER: "DONE_POPOVER",

		ROLE_REQUESTOR: "REQUESTOR",
		ROLE_APPROVER: "APPROVER",
		ROLE_EXECUTOR: "EXECUTOR",

		getDropdownInfo: function(path, out) {
			$.getJSON(path, function(result) {
			    $.each(result, function(item) {
			        out.push($("<option />").val(this.value).text(this.description));
			    });
			});
		},

		displayError: function(errorDiv, message, dismissable) {
			dismissable = dismissable || true;
			errorDiv.html('');
			errorDiv.append("<div class='alert alert-danger'>" +
				(dismissable ? "<a class='close' data-dismiss='alert'>&times;</a>" : "") +
				"<strong>Error: </strong>" +
				message +
				"</div>") ;
		},

		displayInfo: function(errorDiv, message, dismissable) {
			dismissable = dismissable || true;
			errorDiv.html('');
			errorDiv.append("<div class='alert alert-info'>" +
				(dismissable ? "<a class='close' data-dismiss='alert'>&times;</a>" : "") +
				"<strong>Info: </strong>" +
				message +
				"</div>") ;
		},

		markAsRead: function(model, $messages) {
			var self = this;
		    $.ajax({
  				url: "/tt/tickets/"+model.get("number")+"/read",
  				type: "POST",
  				contentType: "application/json; charset=utf-8",
  				dataType: "json",
				success: function(data){
				    model.set("unread", false); //no need to send a GET (model.fetch) cause the only change would be the "unread" flag. A server error that might prevent the correct change in the flag is not a problem cause in that case this "success" callback is not called anyway
				},
			    error: function(data) {
			    	if (data.responseJSON && data.responseJSON.message) {
			    		self.displayError($messages, data.responseJSON.message);
			    	}
			    	else { //just in case the server missed to return a proper json with "message" value
			    		self.displayError($messages, "Unexpected server error");
			    	}
			    }
			});
		},

		displayTicket: function(model, $addEditModal, $messages, markAsRead) {
			if (markAsRead) {
				this.markAsRead(model, $messages);	
			}
			$messages.html('');
			$addEditModal.html(new app.TicketView({model: model}).render().el);
        	$addEditModal.modal({keyboard: false, backdrop: "static"});
        	$addEditModal.on('hidden', function () {
			    app.Router.navigate("");
			})
		}

	};
})(jQuery);