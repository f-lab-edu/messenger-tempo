function login() {
    var payload = {id: $("#userId").val(), password: $("#password").val()};

    $.ajax({
        url: "/api/v1/members/login",
        method: "POST",
        dataType: "JSON",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payload),
        success: function (data) {
            if (data) {
                console.log(data);
                var token = data["token"];
                localStorage.setItem("jwt-access-token", token);
                localStorage.setItem("user-id", data["id"]);
                location.href = "/chat-room-list";
            } else {
                console.log("nothing");
            }
        },
        error: function () {
            console.log("error");
        }
    })
}