var stompClient = null;
var roomNumber = null;
var header = null;
var userId = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    //$("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    header = {
        Authorization: localStorage.getItem("jwt-access-token")
    };
    stompClient.connect(header, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/queue', function (response) {
            console.log(response);
            showGreeting(JSON.parse(response.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
    location.href="/chat-room-list";
}

function sendName() {
    stompClient.send("/app/groupChat", header, JSON.stringify({'content': $("#name").val(), 'roomId': roomNumber}));
}

function showGreeting(groupChat) {
    var senderUserId = groupChat.senderUserId;
    var content = groupChat.content;
    var timestamp = groupChat.createdAt;
    $("#greetings").append("<tr><td>" + senderUserId + " : " + content + " (" + timestamp + ")" + "</td></tr>");
}

function chatToString(chat) {
    return "" + chat.senderUserId + " : " + chat.content + " (" + chat.createdAt + ")";
}

$(function () {
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
});

$(document).ready(function() {
    connect();
});