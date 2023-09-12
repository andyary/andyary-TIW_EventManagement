(function() { // avoid variables ending up in the global scope

	// page components
	var eventDetails, eventsList, wizard,
		pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		pageOrchestrator.start(); // initialize the components
		pageOrchestrator.refresh(); // display initial content
	}, false);


	// Constructors of view components

	function PersonalMessage(_username, messagecontainer) {
		this.username = _username;
		this.show = function() {
			messagecontainer.textContent = this.username;
		}
	}

	function EventsList(_alert, _listcontainer, _listcontainerbody, _tableform) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;
		this.tableform = _tableform;

		this.registerEvents = function(orchestrator) {
			this.tableform.querySelector("input[type='button'].ownevent").addEventListener('click', (e) => {
				eventtable = 1;
				orchestrator.refresh(eventtable, null);
			});
		
			this.tableform.querySelector("input[type='button'].notownevent").addEventListener('click', (e) => {
				eventtable = 2;
				orchestrator.refresh(eventtable, null);
			});
		
			this.tableform.querySelector("input[type='button'].booked").addEventListener('click', (e) => {
				eventtable = 3;
				orchestrator.refresh(eventtable, null);
			});
		}

		this.reset = function() {
			this.listcontainer.style.visibility = "hidden";
		}

		this.show = function(eventtable, next) {
			var self = this;
			if (!eventtable) {eventtable = 1} 
				makeCall("GET", "GetEventsData?tableform=" + eventtable, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							self.update(JSON.parse(req.responseText)); // self visible by
							// closure
							if (next) next(); // show the first element of the list
						} else {
							self.alert.textContent = message;
						}
					}
				}
			);
		};
		


		this.update = function(arrayEvents) {
			var l = arrayEvents.length, elem, i, row, titlecell, destcell, datecell, linkcell, anchor;
			if (l == 0) {
				alert.textContent = "No Events!";
			} else {
				this.listcontainerbody.innerHTML = ""; // empty the table body
				// build updated list
				var self = this;
				arrayEvents.forEach(function(event_) { // self visible here, not this
					row = document.createElement("tr");
					titlecell = document.createElement("td");
					titlecell.textContent = event_.Title;
					row.appendChild(titlecell);
					destcell = document.createElement("td");
					destcell.textContent = event_.Description;
					row.appendChild(destcell);
					datecell = document.createElement("td");
					datecell.textContent = event_.Date;
					row.appendChild(datecell);
					linkcell = document.createElement("td");
					anchor = document.createElement("a");
					linkcell.appendChild(anchor);
					linkText = document.createTextNode("Show");
					anchor.appendChild(linkText);
					//anchor.idevent = mission.id; // make list item clickable
					anchor.setAttribute('idevent', event_.idevent); // set a custom HTML attribute
					anchor.addEventListener("click", (e) => {
						// dependency via module parameter
						eventDetails.show(e.target.getAttribute("idevent")); // the list must know the details container
					}, false);
					anchor.href = "#";
					row.appendChild(linkcell);
					self.listcontainerbody.appendChild(row);
				});
				this.listcontainer.style.visibility = "visible";
			}
		}

		this.autoclick = function(idevent) {
			var e = new Event("click");
			var selector = "a[idevent='" + idevent + "']";
			var allanchors = this.listcontainerbody.querySelectorAll("a");
			var myAnchor = document.querySelector(selector);
			var anchorToClick =
				(idevent) ? document.querySelector(selector) : this.listcontainerbody.querySelectorAll("a")[0];
			anchorToClick.dispatchEvent(e);
		}

	}

	function EventDetails(options) {
		this.alert = options['alert'];
		this.detailcontainer = options['detailcontainer'];
		this.title = options['title'];
		this.description = options['description'];
		this.time = options['time'];
		this.date = options['date'];
		this.location = options['location'];
		this.maxattendees = options['maxattendees'];
		this.owner = options['owner'];
		this.eventactions = options['eventactions'];
		

		this.registerEvents = function(orchestrator) {
			
			this.eventactions.querySelector("input[type='button'].addbooking").addEventListener('click', (e) => {
							var self = this;
							makeCall("POST", 'CreateBooking', e.target.closest("form"),
								function(req) {
									if (req.readyState == 4) {
										var message = req.responseText; // error message or mission id
										if (req.status == 200) {
											orchestrator.refresh(eventtable, message); // id of the new mission passed
										} else {
											self.alert.textContent = message;
											self.reset();
										}
									}
								}
							);
			});
			
			this.eventactions.querySelector("input[type='button'].delbooking").addEventListener('click', (e) => {
							var self = this;
							makeCall("POST", 'RemoveBooking', e.target.closest("form"),
								function(req) {
									if (req.readyState == 4) {
										var message = req.responseText; // error message or mission id
										if (req.status == 200) {
											orchestrator.refresh(eventtable, message); // id of the new mission passed
										} else {
											self.alert.textContent = message;
											self.reset();
										}
									}
								}
							);
			});

			this.eventactions.querySelector("input[type='button'].addlike").addEventListener('click', (e) => {
							var self = this;
							makeCall("POST", 'CreateLike', e.target.closest("form"),
								function(req) {
									if (req.readyState == 4) {
										var message = req.responseText; // error message or mission id
										if (req.status == 200) {
											orchestrator.refresh(eventtable, message); // id of the new mission passed
										} else {
											self.alert.textContent = message;
											self.reset();
										}
									}
								}
							);
			});
			
			}


		

		this.show = function(idevent) {
			var self = this;
			makeCall("GET", "GetEventDetailsData?idevent=" + idevent, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var event_ = JSON.parse(req.responseText);
							self.update(event_); // self is the object on which the function
							// is applied
							self.detailcontainer.style.visibility = "visible";
							self.eventactions.idevent.value = event_.idevent;	
						} else {
							self.alert.textContent = message;

						}
					}
				}
			);
		};


		this.reset = function() {
			this.detailcontainer.style.visibility = "hidden";

		}

		this.update = function(m) {
			this.title.textContent = m.Title;
			this.description.textContent = m.Description;
			this.time.textContent = m.Time;
			this.date.textContent = m.Date;
			this.location.textContent = m.Location;
			this.maxattendees.textContent = m.maxattendees;
			this.owner.textContent = m.owner;
			}
	}
	
	

	  function Wizard(wizardId, alert) {
		// minimum date the user can choose, in this case now and in the future
		var now = new Date(),
		  formattedDate = now.toISOString().substring(0, 10);
		this.wizard = wizardId;
		this.alert = alert;
	
		this.wizard.querySelector('input[type="date"]').setAttribute("min", formattedDate);
	
	   this.registerEvents = function(orchestrator) {
		  // Manage previous and next buttons
		  Array.from(this.wizard.querySelectorAll("input[type='button'].next,  input[type='button'].prev")).forEach(b => {
			b.addEventListener("click", (e) => { // arrow function preserve the
			  // visibility of this
			  var eventfieldset = e.target.closest("fieldset"),
				valid = true;
			  for (i = 0; i < eventfieldset.elements.length; i++) {
				if (!eventfieldset.elements[i].checkValidity()) {
				  eventfieldset.elements[i].reportValidity();
				  valid = false;
				  break;
				}
			  }
			  if (valid) {
				this.changeStep(e.target.parentNode, (e.target.className === "next") ? e.target.parentNode.nextElementSibling : e.target.parentNode.previousElementSibling);
			  }
			}, false);
		  });

      // Manage submit button
		  this.wizard.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
			var eventfieldset = e.target.closest("fieldset"),
			  valid = true;
			for (i = 0; i < eventfieldset.elements.length; i++) {
			  if (!eventfieldset.elements[i].checkValidity()) {
				eventfieldset.elements[i].reportValidity();
				valid = false;
				break;
			  }
			}
	
			if (valid) {
			  var self = this;
			  makeCall("POST", 'CreateEvent', e.target.closest("form"),
				function(req) {
				  if (req.readyState == XMLHttpRequest.DONE) {
					var message = req.responseText; // error message or mission id
					if (req.status == 200) {
					  orchestrator.refresh(eventtable, message); // id of the new mission passed
					} else {
					  self.alert.textContent = message;
					  self.reset();
					}
				  }
				}
			  );
			}
		  });
		  // Manage cancel button
		  this.wizard.querySelector("input[type='button'].cancel").addEventListener('click', (e) => {
			e.target.closest('form').reset();
			this.reset();
		  });
		};
	
		this.reset = function() {
		  var fieldsets = document.querySelectorAll("#" + this.wizard.id + " fieldset");
		  fieldsets[0].hidden = false;
		  fieldsets[1].hidden = true;
		  fieldsets[2].hidden = true;
	
		}
	
		this.changeStep = function(origin, destination) {
		  origin.hidden = true;
		  destination.hidden = false;
		}
	  }
	
	function PageOrchestrator() {
		var eventtable, alertContainer = document.getElementById("id_alert");
		this.start = function() {
			personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
				document.getElementById("id_username"));
			personalMessage.show();

			eventsList = new EventsList(
				alertContainer,
				document.getElementById("id_listcontainer"),
				document.getElementById("id_listcontainerbody"),
				document.getElementById("id_tableform"));
			eventsList.registerEvents(this);

			eventDetails = new EventDetails({ // many parameters, wrap them in an
				// object
				alert: alertContainer,
				detailcontainer: document.getElementById("id_detailcontainer"),
				title: document.getElementById("id_title"),
				description: document.getElementById("id_description"),
				time: document.getElementById("id_time"),
				date: document.getElementById("id_date"),
				location: document.getElementById("id_location"),
				maxattendees: document.getElementById("id_maxattendees"),
				owner: document.getElementById("id_owner"),
				eventactions: document.getElementById("id_eventactions"),
			});
			eventDetails.registerEvents(this);
			
			wizard = new Wizard(document.getElementById("id_createventform"), alertContainer);
			wizard.registerEvents(this);

		};

		this.refresh = function(eventtable, currentEvent) {
			eventsList.reset();
			eventDetails.reset();
			if (typeof currentEvent === 'undefined') eventsList.show(eventtable, null);
			else eventsList.show(eventtable, function() {
				eventsList.autoclick(currentEvent);
			}); // closure preserves visibility of this
			wizard.reset();
		};
	}
})();
