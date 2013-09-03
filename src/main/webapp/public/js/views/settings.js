var app = app || {};

(function ($) { "use strict";

	app.SettingsView = Backbone.View.extend({

		settingsTemplate: _.template($('#settingsTemplate').html()),

		el: '#settings',

		events: {
			'click .settingsOkButton': 'saveSettings',
			'click .settingsCancelButton': 'closeSettings'
		},

		initialize: function () {
			this.$settingsLink = this.$el.find("a");

			this.listenTo(app.loggedUser, 'change',this.render);
		},

		render: function () {
			this.$settingsLink.popover({
			    placement : 'bottom',
			    title : '',
			    html: true,
			    content :  this.settingsTemplate(app.loggedUser.toJSON())
			});
		},

		saveSettings: function (e) {
			var notificationsChecked = $('#allowNotifications').is(":checked");
		
			if (notificationsChecked != app.loggedUser.get("notificationsEnabled")) {
				this.$settingsLink.popover('destroy');
				app.loggedUser.set("notificationsEnabled",notificationsChecked);

				$('#pleaseWaitDialog').show();
				$(".modal-backdrop").show();

				$.ajax({
	  				url: "/tt/loggedUser", 
	  				type: "PUT",
	  				data: JSON.stringify(app.loggedUser.toJSON()),
	  				contentType: "application/json; charset=utf-8",
	  				dataType: "json",
	  				error: function (data){
	  					$('#pleaseWaitDialog').hide();
						$(".modal-backdrop").hide();
						$('#dashboardMessages').html('');
						app.util.displayError($('#dashboardMessages'), "Error updating settings.", false);
	  				},
                	success: function (data){
						$('#pleaseWaitDialog').hide();
						$(".modal-backdrop").hide();
						$('#dashboardMessages').html('');
						app.util.displayInfo($('#dashboardMessages'), "Settings updated.", false);
                	}
				});
			} else {
				this.$settingsLink.popover("hide");
			};
		},

		closeSettings: function (e) {
			this.$settingsLink.popover("hide");
		}
	});
})(jQuery);
