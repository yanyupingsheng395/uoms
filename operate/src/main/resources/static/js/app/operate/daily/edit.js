$(function () {
    getTipInfo();
});

Array.prototype.remove = function(val) {
    let index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};

/**
 * 初始化步骤条
 */
let step = steps({
    el: "#step",
    data: [
        { title: "选定群组", description: "" },
        { title: "群组推送", description: "" }
    ],
    space: 100,
    center: true,
    active: 0,
    dataOrder: ["line", "title", "description"]
});

function getTipInfo() {
    $.get("/daily/getTipInfo", {headId: headId}, function (r) {
        var data = r.data;
        if(data != null) {
            var actual = data['ACTUAL'] == null ? data['TOTAL'] : data['ACTUAL'];
            $("#touchDt").html('').append(data['TOUCHDT']);
            $("#totalNum").html('').append(data['TOTAL']);
            $("#optNum").html('').append(actual);
        }
    });
}



