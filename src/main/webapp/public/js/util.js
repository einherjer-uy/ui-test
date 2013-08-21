var app = app || {};

(function ($) { "use strict";
	app.util = {

		CANCEL_POPOVER: "CANCEL_POPOVER",
		REJECT_POPOVER: "REJECT_POPOVER",
		APPROVE_POPOVER: "APPROVE_POPOVER",
		DONE_POPOVER: "DONE_POPOVER",

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
		}
	};
})(jQuery);