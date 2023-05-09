function makeRoom() {
    var checkedMembers = Array();
    $("input.members:checked").each(function(){
        checkedMembers.push($(this).val());
    });

    var payload = {memberList: checkedMembers};
    console.log(payload);

    $.ajax({
        url: "/api/v1/groupchat/rooms",
        method: "POST",
        dataType: "JSON",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payload),
        success: function (data) {
            if (data) {
                console.log(data);
            } else {
                console.log("nothing");
            }
        },
        error: function () {
            console.log("error");
        }
    })
}

