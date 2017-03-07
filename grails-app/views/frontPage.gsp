<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>

    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <asset:stylesheet src="application.css"/>


</head>
<body id="student-login">

<div id="student-login-container">
    <div class="panel panel-default">
        <div class="panel-body">

            <input id="lecture-id-field" type="text" placeholder="Lecture ID"/><br/>
            <button id="joinButton" class="btn btn-success btn-block btn-lg" onclick="joinButtonClicked()">
                Join lecture
            </button>
        </div>

    </div>
    <div><g:link uri="/lecturer">I am a lecturer</g:link></div>
</div>
<script>
    function joinButtonClicked(){
        window.location = "${createLink(controller: 'lecture', action: 'connect')}/" +
                document.getElementById('lecture-id-field').value;
    }
</script>
<asset:javascript src="application.js"/>

</body>
</html>