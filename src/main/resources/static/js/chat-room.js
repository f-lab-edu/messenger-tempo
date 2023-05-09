var stompClient = null;
var roomNumber = null;
var header = null;

function setConnected(connected) {
    roomNumber = $("#roomNumber").text();
    $("#roomNumber").prop("disabled", connected);
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
        stompClient.subscribe('/topic/room/'+roomNumber, function (response) {
            showGreeting(JSON.parse(response.body));
        });
        stompClient.subscribe('/user/queue', function (response) {
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

function loadMorePastChat() {
    var loadedLastChatId = $("#conversation > tbody > tr:first-child > td").attr("chat-id");
    console.log("loadedLastChatId = " + loadedLastChatId);
    var loadSize = 10;
    var payload = {nextId: loadedLastChatId, size: loadSize};

    $.ajax({
        url: "/api/v1/groupchat/"+roomNumber,
        method: "GET",
        dataType: "JSON",
        data: payload,
        success: function (data) {
            if (data) {
                console.log(data);
                data.list.forEach(function (chat) {
                    console.log(chat);
                    //th:chat-id="${chat.getId()}" th:text="|${chat.getSenderUserId()} : ${chat.getContent()} (${chat.getCreated_at()})|"
                    $("#conversation > tbody > tr:first").before("<tr><td chat-id='"+chat.id+"'>"+chatToString(chat)+"</td></tr>");
                });
                if (data.size < loadSize) {
                    $("#load_more_chat").prop("disabled", true);
                }
            } else {
                console.log("nothing");
            }
        },
        error: function () {
            console.log("error");
        }
    })
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

$(document).ready(function() {
    connect();
});