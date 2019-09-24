/**
 * 初始化步骤条
 */
let step = steps({
    el: "#step",
    data: [
        { title: "会员成长用户", description: "" },
        { title: "会员日策略生成", description: "" }
    ],
    space: 100,
    center: true,
    active: 0,
    dataOrder: ["line", "title", "description"]
});

getTipInfo();
function getTipInfo() {
    $.get("/member/getTipInfo", {headId: headId}, function (r) {
        let data = r.data;
        if(data != null) {
            $("#touchDt").html('').append(data['MEMBER_DATE']);
            $("#totalNum").html('').append(data['USER_COUNT'] == null ? "0" : data['USER_COUNT']);
        }
    });
}