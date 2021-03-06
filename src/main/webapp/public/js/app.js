var app = app || {};

$(function () { "use strict";  //$() shorthand for $(document).ready()
	app.ticketTypes = [];
	app.util.getDropdownInfo("/tt/ticketTypes", app.ticketTypes);
	app.ticketPriorities = [];
	app.util.getDropdownInfo("/tt/ticketPriorities", app.ticketPriorities);

	new app.AppView();
	new app.SettingsView();
	new app.DashoardListView();
	new app.DashoardCardsView();

	app.websocket  = (function(){
		var _ws = null;
		var processFunc = null;

		var _connect = function(process) {
			processFunc = processFunc || process;
			_ws = new WebSocket("ws://localhost:8080/tt/notifications");
			_ws.onopen = _onopen;
			_ws.onmessage = _onmessage;
			_ws.onclose = _onclose;
			_ws.onerror = _onerror;
		};

		var _onopen = function() {			
			console.log("open");
			//we need to tell the server info about the logged user
			//trying to do new WebSocket() and then send() doesn't work cause the websocket readystate would be CONNECTING
			//so, we need to send in the onopen callback
			_send("{\"method\":\"LOGIN\", \"userName\":\""+app.loggedUser.get("username").emailAddress+"\"}");
		};

		var _onmessage = function(m) {
			if (m.data) {
				processFunc(m.data);
			}
		};

		var _onclose = function(m) {
			console.log("close");
			_connect(); //the socket has a timeout, reconnect automatically onclose
		};

		var _onerror = function(e) {
			console.log("ERROR! : " + e);
		};
		
		var _send = function(message) {
			_ws.send(message);
		};

		return {
			connect:_connect,
			send:_send
		}	
	})();

	$.pnotify.defaults.history = false; //remove "redisplay" menu
	
	app.websocket.connect(
		function(data){
			var jsonData = JSON.parse(data);
			var opts = {
	        	title: jsonData.title,
	        	text: jsonData.text,
	        	type: jsonData.type,
	        	addclass: "stack-bottomright",
	        	stack: {"dir1": "up", "dir2": "left", "firstpos1": 25, "firstpos2": 25}
			};
			$.pnotify(opts);
		}
	);

	$("#logoutLink").click(
		function() {
			//cannot do this on the server side alone cause the server cannot know if the same user doesn't have a session in another device or browser
			app.websocket.send("{\"method\":\"LOGOUT\", \"userName\":\""+app.loggedUser.get("username").emailAddress+"\"}");
			window.location.href = "/logout"; 
		}
	);

});
