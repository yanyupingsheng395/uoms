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
        { title: "成长用户列表", description: "" },
        { title: "用户成长策略", description: "" }
    ],
    space: 100,
    center: true,
    active: 0,
    dataOrder: ["line", "title", "description"]
});

function getTipInfo() {
    $.get("/daily/getTipInfo", {headId: headId}, function (r) {
        let data = r.data;
        if(data != null) {
            $("#touchDt").html('').append(data['TOUCHDT']);
            $("#totalNum").html('').append(data['TOTAL'] == null ? "0" : data['TOTAL']);
        }
    });
}



