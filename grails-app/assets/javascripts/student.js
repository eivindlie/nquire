
/**
 * Created by Lavrans on 28.02.2017.
 *
 */

var socket;
var timeout;

function initStudent() {
    socket = new WebSocket(url);
    console.log("Connecting...")

    socket.onopen = function(e) {
        console.log("Connection established")
        var msg = { "type": "connect",
            "lectureId": lectureId,
            "role": "student"
        }
        socket.send(JSON.stringify(msg))
    }

    socket.onmessage = function(e) {
        var msg = JSON.parse(e.data)

        switch(msg.type) {
            case "connected": // Successfully connected to lecture
                socket.send(JSON.stringify({"type": "requestPresentation"}));
                break;
            case "presentation": // Received presentation file
                setPresentation(msg.presentation);
                break;
        }
    }

    socket.onerror = function(e) {

    }
    socket.onclose = function(e) {
        console.log("Connection closed.");
    }

    window.onkeydown = function(e){
        if (e.keyCode == '39') {
            this.renderNextPage();
        }
        else if (e.keyCode == '37') {
            this.renderPreviousPage();
        }
    }

}

function hardButtonClicked(){
    console.log("button clicked");
}
function undoButtonClicked(){
    console.log("button clicked");
}
function questionButtonClicked(){
    console.log("button clicked");
}
function slowerButtonClicked(){
    console.log("button clicked");
}
function fasterButtonClicked(){
    console.log("button clicked");
}
function modalSaveButtonClicked(){
    var form = document.forms['questionForm'];

    var question  = form['questionInput'].value;

    var message = JSON.stringify(
        {type:"question", question: question}
    );
    socket.send(message);
    form['questionInput'].value="";

}

function mouseMoveHandler(){
    document.getElementById("buttons-container").style.display = "block";

    clearTimeout(timeout);
    timeout = setTimeout(function(){
        document.getElementById("buttons-container").style.display = "none";
    },2000)

}

