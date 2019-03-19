```javascript
function download(){
			xhr = null;
			if (window.XMLHttpRequest) {// code for all new browsers
				xhr = new XMLHttpRequest();
			} else if (window.ActiveXObject) {// code for IE5 and IE6
				xhr = new ActiveXObject("Microsoft.XMLHTTP");
			}
			if (xhr != null) {
				xhr.onreadystatechange= function(){
// 					console.log(xhr);
				};
				xhr.open("GET", "http://localhost:8531/monitor/1/health_report", false);
				xhr.send(null);
				if(xhr.readyState==4&&xhr.status==200){
					console.log(xhr);
 					console.log(xhr.getAllResponseHeaders());
// 					console.log(xhr.getResponseHeader('content-type'));
					var stream = xhr.getResponseHeader('content-type').search("application/octet-stream")!=-1;
					if(stream){
 						window.location.href="http://localhost:8531/monitor/1/health_report";
					}
				}
			} else {
				alert("Your browser does not support XMLHTTP.");
			}
		}
```
