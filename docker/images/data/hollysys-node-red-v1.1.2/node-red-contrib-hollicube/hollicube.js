module.exports = function (RED) {
	"use strict";
	var mqtt = require("mqtt");
	var util = require("util");
	var isUtf8 = require('is-utf8');

	var path = require("path");

	function matchTopic(ts, t) {
		if (ts == "#") {
			return true;
		}
		var re = new RegExp("^" + ts.replace(/([\[\]\?\(\)\\\\$\^\*\.|])/g, "\\$1").replace(/\+/g, "[^/]+").replace(/\/#$/, "(\/.*)?") + "$");
		return re.test(t);
	}

	function isArray(o){
		return Object.prototype.toString.call(o)=='[object Array]';
	}

	function HollicubeBrokerNode(n) {

		RED.nodes.createNode(this, n);

		// Configuration options passed by Node Red
		this.broker = n.broker;
		this.port = n.port;
		this.clientid = n.clientid;
		this.usetls = n.usetls;
		this.verifyservercert = n.verifyservercert;
		this.compatmode = n.compatmode;
		this.keepalive = n.keepalive;
		this.cleansession = n.cleansession;
		this.compactinterval = n.compactinterval;

		// Config node state
		this.brokerurl = "";
		this.connected = false;
		this.connecting = false;
		this.closing = false;
		this.options = {};
		this.queue = [];
		this.subscriptions = {};

		if (n.birthTopic) {
			this.birthMessage = {
				topic: n.birthTopic,
				payload: n.birthPayload || "",
				qos: Number(n.birthQos || 0),
				retain: n.birthRetain == "true" || n.birthRetain === true
			};
		}

		if (this.credentials) {
			this.username = this.credentials.user;
			this.password = this.credentials.password;
		}

		// If the config node is missing certain options (it was probably deployed prior to an update to the node code),
		// select/generate sensible options for the new fields
		if (typeof this.usetls === 'undefined') {
			this.usetls = false;
		}
		if (typeof this.compatmode === 'undefined') {
			this.compatmode = true;
		}
		if (typeof this.verifyservercert === 'undefined') {
			this.verifyservercert = false;
		}
		if (typeof this.keepalive === 'undefined') {
			this.keepalive = 60;
		} else if (typeof this.keepalive === 'string') {
			this.keepalive = Number(this.keepalive);
		}
		if (typeof this.cleansession === 'undefined') {
			this.cleansession = true;
		}

		// Create the URL to pass in to the MQTT.js library
		if (this.brokerurl === "") {
			if (this.usetls) {
				this.brokerurl = "mqtts://";
			} else {
				this.brokerurl = "mqtt://";
			}
			if (this.broker !== "") {
				this.brokerurl = this.brokerurl + this.broker + ":" + this.port;
			} else {
				this.brokerurl = this.brokerurl + "localhost:1883";
			}
		}

		if (!this.cleansession && !this.clientid) {
			this.cleansession = true;
			this.warn(RED._("hollicube.errors.nonclean-missingclientid"));
		}

		// Build options for passing to the MQTT.js API
		this.options.clientId = this.clientid || 'mqtt_' + (1 + Math.random() * 4294967295).toString(16);
		this.options.username = this.username;
		this.options.password = this.password;
		this.options.keepalive = this.keepalive;
		this.options.clean = this.cleansession;
		this.options.reconnectPeriod = RED.settings.mqttReconnectTime || 5000;
		this.options.connectTimeout = 30000;

		if (this.compatmode == "true" || this.compatmode === true) {
			this.options.protocolId = 'MQIsdp';
			this.options.protocolVersion = 3;
		}
		if (this.usetls && n.tls) {
			var tlsNode = RED.nodes.getNode(n.tls);
			if (tlsNode) {
				tlsNode.addTLSOptions(this.options);
			}
		}

		// If there's no rejectUnauthorized already, then this could be an
		// old config where this option was provided on the broker node and
		// not the tls node
		if (typeof this.options.rejectUnauthorized === 'undefined') {
			this.options.rejectUnauthorized = (this.verifyservercert == "true" || this.verifyservercert === true);
		}

		if (n.willTopic) {
			this.options.will = {
				topic: n.willTopic,
				payload: n.willPayload || "",
				qos: Number(n.willQos || 0),
				retain: n.willRetain == "true" || n.willRetain === true
			};
		}

		// Define functions called by MQTT in and out nodes
		var node = this;
		this.users = {};

		this.register = function (mqttNode) {
			node.users[mqttNode.id] = mqttNode;
			if (Object.keys(node.users).length === 1) {
				node.connect();
			}
		};

		this.deregister = function (mqttNode, done) {
			delete node.users[mqttNode.id];
			if (node.closing) {
				return done();
			}
			if (Object.keys(node.users).length === 0) {
				if (node.client && node.client.connected) {
					return node.client.end(done);
				} else {
					node.client.end();
					return done();
				}
			}
			done();
		};

		this.connect = function () {
			if (!node.connected && !node.connecting) {
				node.connecting = true;
				node.client = mqtt.connect(node.brokerurl, node.options);
				node.client.setMaxListeners(0);
				// Register successful connect or reconnect handler
				node.client.on('connect', function () {
					node.connecting = false;
					node.connected = true;
					node.log(RED._("hollicube.state.connected", { broker: (node.clientid ? node.clientid + "@" : "") + node.brokerurl }));
					for (var id in node.users) {
						if (node.users.hasOwnProperty(id)) {
							node.users[id].status({ fill: "green", shape: "dot", text: "node-red:common.status.connected" });
						}
					}
					// Remove any existing listeners before resubscribing to avoid duplicates in the event of a re-connection
					node.client.removeAllListeners('message');

					// Re-subscribe to stored topics
					for (var s in node.subscriptions) {
						if (node.subscriptions.hasOwnProperty(s)) {
							var topic = s;
							var qos = 0;
							for (var r in node.subscriptions[s]) {
								if (node.subscriptions[s].hasOwnProperty(r)) {
									qos = Math.max(qos, node.subscriptions[s][r].qos);
									node.client.on('message', node.subscriptions[s][r].handler);
								}
							}
							var options = { qos: qos };
							node.client.subscribe(topic, options);
						}
					}

					// Send any birth message
					if (node.birthMessage) {
						node.publish(node.birthMessage);
					}
				});
				node.client.on("reconnect", function () {
					for (var id in node.users) {
						if (node.users.hasOwnProperty(id)) {
							node.users[id].status({ fill: "yellow", shape: "ring", text: "node-red:common.status.connecting" });
						}
					}
				})
				// Register disconnect handlers
				node.client.on('close', function () {
					if (node.connected) {
						node.connected = false;
						node.log(RED._("hollicube.state.disconnected", { broker: (node.clientid ? node.clientid + "@" : "") + node.brokerurl }));
						for (var id in node.users) {
							if (node.users.hasOwnProperty(id)) {
								node.users[id].status({ fill: "red", shape: "ring", text: "node-red:common.status.disconnected" });
							}
						}
					} else if (node.connecting) {
						node.log(RED._("hollicube.state.connect-failed", { broker: (node.clientid ? node.clientid + "@" : "") + node.brokerurl }));
					}
				});

				// Register connect error handler
				node.client.on('error', function (error) {
					if (node.connecting) {
						node.client.end();
						node.connecting = false;
					}
					node.error(error);
				});
			}
		};

		this.subscribe = function (topic, qos, callback, ref) {
			ref = ref || 0;
			node.subscriptions[topic] = node.subscriptions[topic] || {};
			var sub = {
				topic: topic,
				qos: qos,
				handler: function (mtopic, mpayload, mpacket) {
					if (matchTopic(topic, mtopic)) {
						callback(mtopic, mpayload, mpacket);
					}
				},
				ref: ref
			};
			node.subscriptions[topic][ref] = sub;
			if (node.connected) {
				node.client.on('message', sub.handler);
				var options = {};
				options.qos = qos;
				node.client.subscribe(topic, options);
			}
		};

		this.unsubscribe = function (topic, ref) {
			ref = ref || 0;
			var sub = node.subscriptions[topic];
			if (sub) {
				if (sub[ref]) {
					node.client.removeListener('message', sub[ref].handler);
					delete sub[ref];
				}
				if (Object.keys(sub).length === 0) {
					delete node.subscriptions[topic];
					if (node.connected) {
						node.client.unsubscribe(topic);
					}
				}
			}
		};

		this.publish = function (msg) {
			// always publish in case we are temporarily disconnected
			if (!Buffer.isBuffer(msg.payload)) {
				if (typeof msg.payload === "object") {
					msg.payload = JSON.stringify(msg.payload);
				} else if (typeof msg.payload !== "string") {
					msg.payload = "" + msg.payload;
				}
			}

			var options = {
				qos: msg.qos || 0,
				retain: msg.retain || false
			};
			node.client.publish(msg.topic, msg.payload, options, function (err) {
				if (err) {
					node.error("error publishing message: " + err.toString());
				}
				return
			});
		};

		this.on('close', function (removed, done) {
			this.closing = true;

			if (this.connected) {
				this.client.end();
			} else if (this.connecting || node.client.reconnecting) {
				node.client.end();
			}
		});
	}

	RED.nodes.registerType("hollicube-broker", HollicubeBrokerNode, {
		credentials: {
			user: { type: "text" },
			password: { type: "password" }
		}
	});

	function HollicubeNode(n) {
		RED.nodes.createNode(this, n)
        this.topic = n.topic
        this.qos = n.qos || null
        this.retain = n.retain
		this.broker = n.broker
		this.domain = n.domain
		this.namespace = n.namespace
		this.pubtype = n.pubtype
        this.brokerConn = RED.nodes.getNode(this.broker)
        var node = this

		if (this.brokerConn) {
			this.status({ fill: "red", shape: "ring", text: "node-red:common.status.disconnected" })

			this.on('input', function (msg) {
				if (msg.qos) {
					msg.qos = parseInt(msg.qos)
					if ((msg.qos !== 0) && (msg.qos !== 1) && (msg.qos !== 2)) {
						msg.qos = null
					}
				}

				msg.qos = Number(node.qos || msg.qos || 0)
				msg.retain = node.retain || msg.retain || false
				msg.retain = ((msg.retain === true) || (msg.retain === "true")) || false

				if (msg.hasOwnProperty("payload")) {
					if (msg.payload.hasOwnProperty("nodes")) {
						if (!isArray(msg.payload.nodes)) {
							this.status({ fill: "red", shape: "ring", text: "Input message property 'nodes' muse be an array." })
							return
						}
					} else {
						this.status({ fill: "red", shape: "ring", text: "Input message must has 'nodes' property." })
						return
					}
				} else {
					this.status({ fill: "red", shape: "ring", text: "Input message must has 'payload' property." })
					return
				}

				var payload
				msg.topic = node.domain

				if (node.pubtype === '1') {
					msg.topic += '/Cloud/InformationModel'

					payload = {
						property: {
							session: node.domain,
							subject: "InformationModel-UpdateAll",
							target: "HiaCloud-InformationModel",
							sequenceId: 1,
							totalMessageCount: 1,
							currentMessageNumber: 1
						},
						message: {
							namespace: node.namespace,
							version: msg.payload.version || 1,
							nodes: []
						}
					}

					for (var i = 0; i < msg.payload.nodes.length; ++i) {
						var item = msg.payload.nodes[i]

						if (item.hasOwnProperty("code") && item.hasOwnProperty("title")) {
							payload.message.nodes.push({
								uri: '/' + node.namespace + '/' + item.code,
								name: item.code,
								"$name": item.title,
								datatype: item.datatype || "String",
								"$class": "SlDataPoint"
							})
						}
					}
				} else if(node.pubtype === '3') {
					msg.topic += '/Cloud/Snapshot'

					payload = {
						property: {
							session: node.domain,
							subject: "DataValue",
							target: "HiaCloud-Snapshot",
							sequenceId: 1
						},
						message: {
							namespace: node.namespace,
							values: []
						}
					}

					for (var i = 0; i < msg.payload.nodes.length; ++i) {
						var item = msg.payload.nodes[i]
						if (item.hasOwnProperty("code") && item.hasOwnProperty("value")) {
							payload.message.values.push({
								"id": item.code,
								"value": item.value,
								"date":item.date 
							})
						}
					}
				} else if(node.pubtype === '2') {
					msg.topic += '/Cloud/TS'

					payload = {
						property: {
							session: node.domain,
							subject: "DataValue",
							target: "HiaCloud-TS",
							sequenceId: 1
						},
						message: {
							namespace: node.namespace,
							values: []
						}
					}

					for (var i = 0; i < msg.payload.nodes.length; ++i) {
						var item = msg.payload.nodes[i]
						var timestamp = new Date().getTime()

						if (item.hasOwnProperty("code") && item.hasOwnProperty("value")) {
							payload.message.values.push({
								"id": item.code,
								value: {
									v: item.value,
									t: item.timestamp || timestamp
								}
							})
						}
					}
				} else {
					console.error("Invalid publish type " + node.pubtype)
					return
				}

				msg.payload = payload
				this.brokerConn.publish(msg)
				console.info("Published.")
			});

			if (this.brokerConn.connected) {
				node.status({ fill: "green", shape: "dot", text: "node-red:common.status.connected" });
			}
			node.brokerConn.register(node);
			this.on('close', function (done) {
				node.brokerConn.deregister(node, done);
			});
		} else {
			this.error(RED._("hollicube.errors.missing-config"));
		}
	}

	RED.nodes.registerType("hollicube", HollicubeNode);
}
