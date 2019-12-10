var ws;
var username;
var userId;

function connect() {
    userId = document.getElementById("userId").value;
    username = document.getElementById("username").value;
    
    var host = document.location.host;
    var pathname = document.location.pathname;
    var webPath = document.getElementById("webPath").value;
    ws = new WebSocket("ws://" + webPath + "/" + userId); //"localhost:8080"+"/websocket" 

    ws.onmessage = function(event) {
    var log = document.getElementById("log");
        console.log(event.data);
        log.innerHTML += event.data + "\n";
    };
}

function send() {
    var ChatId =  document.getElementById("chatId").value;
    var content = document.getElementById("msg").value;
    
//{"chatId":6,"mesage":"heyheyhey","messengerName":null,"timestamp":"2019-11-10T01:00:00","messengerImage":3}
    var content2 = {chatId: ChatId, message:content, messengerName:username, timestamp:"2019-11-10T01:00:00", messengerImage:0 };
    var myJSON = JSON.stringify(content2);
    ws.send(myJSON);
    document.getElementById("para").innerHTML = myJSON;
}