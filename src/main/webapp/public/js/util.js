var app = app || {};

(function ($) { "use strict";
	app.util = {
		getDropdownInfo: function(path, out) {
			$.getJSON(path, function(result) {
			    $.each(result, function(item) {
			        out.push($("<option />").val(this.value).text(this.description));
			    });
			});
		}
	};
})(jQuery);