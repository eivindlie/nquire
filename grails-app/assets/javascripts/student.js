
/**
 * Created by Lavrans on 28.02.2017.
 *
 */

var socket;
var timeout;
var lastPaceFeedback = 0;

function initStudent() {
    document.body.onmousemove = mouseMoveHandler;

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
            case "similarQuestion":
                document.getElementById("modalOwnQuestion").innerHTML = msg.ownQuestion;
                document.getElementById("modalMatchedQuestion").innerHTML = msg.matchedQuestion;
                $('#similarQuestionModal').modal('show');
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
    // Timeout because button event requires focus; must be released first...
    $('#questionModal').modal('show');
    setTimeout(function() {document.getElementById('questionInput').focus()}, 500);
}

function slowerButtonClicked(){
    if(new Date().getTime() - lastPaceFeedback > 60*1000) {
        lastPaceFeedback = new Date().getTime();

        var message = JSON.stringify({
            type: "pace",
            value: 1.0
        });

        socket.send(message)
    }
}
function fasterButtonClicked(){
    if(new Date().getTime() - lastPaceFeedback > 60*1000) {
        lastPaceFeedback = new Date().getTime();

        var message = JSON.stringify({
            type: "pace",
            value: -1.0
        });

        socket.send(message);
    }
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

function forceSendQuestion() {
    var question = document.getElementById("modalOwnQuestion").innerHTML;
    var message = JSON.stringify({
            type: "question",
            force: true,
            question: question
        });
    socket.send(message);
}

function mouseMoveHandler(){
    document.getElementById("buttons-container").style.display = "block";

    clearTimeout(timeout);
    timeout = setTimeout(function(){
        document.getElementById("buttons-container").style.display = "none";
    },2000)

}
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip({
        placement : 'top'
    });
});
