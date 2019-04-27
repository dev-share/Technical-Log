<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" /> 
<link href="data:image/x-icon;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQEAYAAABPYyMiAAAABmJLR0T///////8JWPfcAAAACXBIWXMAAABIAAAASABGyWs+AAAAF0lEQVRIx2NgGAWjYBSMglEwCkbBSAcACBAAAeaR9cIAAAAASUVORK5CYII=" rel="icon" type="image/x-icon" /> 

<title>WebSocket/SockJS</title>
    <style type="text/css">
        #connect-container {
            float: left;
            width: 400px
        }

        #connect-container div {
            padding: 5px;
        }

        #console-container {
            float: left;
            margin-left: 5px;
            width: 800px;
        }

        #console {
        	background: black;
            color : yellow;
            border: 1px solid #03A9F4;
            border-right-color: #777878;
            border-bottom-color: #777878;
            height: 600px;
            overflow-y: scroll;
            padding: 5px;
            width: 100%;
        }

        #console p {
            padding: 0;
            margin: 0;
        }
    </style>
    <!-- <script src="http://cdn.jsdelivr.net/sockjs/1/sockjs.min.js"></script> -->
    <script src="https://cdn.bootcss.com/sockjs-client/1.1.1/sockjs.js"></script>
    <!-- <script src="https://cdn.jsdelivr.net/sockjs/1.0.0/sockjs.min.js"></script> -->
    <!-- <script src="https://cdn.jsdelivr.net/sockjs/0.3.4/sockjs.min.js"></script> -->
    <!-- <script src="https://cdn.jsdelivr.net/socket.io-client/1.3.2/socket.io.js"></script> -->
	<!-- <script src="http://cdn.sockjs.org/sockjs-0.3.min.js"></script> -->
	<!-- <script src="http://apps.bdimg.com/libs/sockjs-client/0.3.4/sockjs.min.js"></script> -->
    <script type="text/javascript">
        var websocket = null;
        var url = null;
        var transports = null;

        function common(flag) {
            document.getElementById('connect').disabled = flag;
            document.getElementById('disconnect').disabled = !flag;
            document.getElementById('send').disabled = !flag;
            document.getElementById('clear').disabled=false;
        }

        function connect() {
        	var token = "C16CEF0180A1BAA8F2CB1DEAAB002DD103225C579CD29B620E92666E3D62F69FF4D5F52E69FB4083378EB2F7092DEBD1";
            var protocol= document.getElementById("WebSocket").checked?document.getElementById("WebSocket").value:document.getElementById("SockJS").value;
            if(protocol&&protocol=="websocket"){
            	if (window.location.protocol == 'http:') {  
//             		url = "ws://192.168.0.193:8081/notice/websocket?token="+token;  
            		url = "ws://192.168.202.136:8088/smartmeeting/smart/websocket?token="+token; 
            	} else {  
            		url = "wss://192.168.202.136:8088/smartmeeting/smart/websocket?token="+token;  
            	}
            	websocket = new WebSocket(url,[],{devel: true, debug: "*",server:"token="+token});
            }else{
            	url= "http://192.168.202.136:8088/smartmeeting/smart/websocket";
                websocket = new SockJS(url,transports,{devel: true, debug: "*",server:"token="+token,timeout:60*1000,transports: transports});
            }
            websocket.onopen = function () {
            	common(true);
                log("--打开连接-----");
            };
            websocket.onmessage = function (event) {
                log("---接收收据: " + JSON.stringify(event));
            };
            websocket.onerror = function (evnt) {
            	log("---异常收据: " + JSON.stringify(event));
            };
            websocket.onclose = function (event) {
            	common(false);
                log("--关闭连接连接-----"+ transports +"-----\n"+ JSON.stringify(event));
            };
        }
        function updateTransport(transport) {
            transports = (!transport||transport == 'all') ?  ['xdr-streaming','xhr-streaming','eventsource','iframe-eventsource','htmlfile','iframe-htmlfile','xdr-polling','xhr-polling','iframe-xhr-polling','jsonp-polling'] : [transport];
        }
        function disconnect() {
            if (websocket != null) {
            	websocket.close();
            	websocket = null;
            }
            common(false);
        }
		function clean(){
			document.getElementById('console').innerHTML="";
			document.getElementById('clear').disabled=true;
		}
        function send() {
            if (websocket != null) {
                var message = document.getElementById('message').value;
                log("---发送收据: " + message);
                websocket.send(message);
            }
        }
        function log(message) {
            var console = document.getElementById('console');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(message));
            console.appendChild(p);
            while (console.childNodes.length > 20) {
                console.removeChild(console.firstChild);
            }
            console.scrollTop = console.scrollHeight;
        }
    </script>
</head>
<body>
<noscript>
	<h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websockets 
    rely on Javascript being enabled. Please enable
    Javascript and reload this page!</h2>
</noscript>
<div>
    <div id="connect-container">
        <input id="WebSocket" type="radio" name="type" value="websocket" onchange="javascript:{document.getElementById('transport').style.display='none';}"><label for="radio1">W3C WebSocket</label>
        <br>
        <input id="SockJS" type="radio" name="type" checked="checked" value="socketjs" onchange="javascript:{document.getElementById('transport').style.display='block';}"><label for="radio2">SockJS</label>
        <div id="transport">
            <span>SockJS transport:</span>
            <select id="protocol" onchange="updateTransport(this.value)">
              <option value="all">all</option>
              <option value="websocket">websocket</option>
              <option value="xdr-streaming">xdr-streaming</option>
              <option value="xhr-streaming">xhr-streaming</option>
              <option value="xdr-polling">xdr-polling</option>
              <option value="xhr-polling">xhr-polling</option>
              <option value="jsonp-polling">jsonp-polling</option>
              <option value="iframe-eventsource">iframe-eventsource</option>
              <option value="iframe-htmlfile">iframe-htmlfile</option>
              <option value="iframe-xhr-polling">iframe-xhr-polling</option>
              <option value="eventsource">eventsource</option>
			  <option value="htmlfile">htmlfile</option>
            </select>
        </div>
        <div>
            <button id="connect" onclick="connect();">连接</button>
            <button id="disconnect" disabled="disabled" onclick="disconnect();">断开</button>
            <button id="clear" disabled="disabled" onclick="clean();">清除</button>
        </div>
        <div>
            <textarea id="message" style="width: 350px;resize: none;height:200px;">这里有一条消息!</textarea>
        </div>
        <div>
            <button id="send" onclick="send();" disabled="disabled">发送</button>
        </div>
    </div>
    <div id="console-container">
    	<span><b>Web Socket 控制台</b></span>
        <div id="console"></div>
    </div>
</div>
</body>
</html>