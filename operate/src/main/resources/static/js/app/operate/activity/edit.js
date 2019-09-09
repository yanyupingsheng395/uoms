let fromVal = 0;
$(function () {
    getDataById();
});

function updateData(actType, startDt, endDt, dateRange) {
    $.get("/activity/updateData", {headId:headId, actType: actType, startDt:startDt, endDt:endDt, dateRange: dateRange}, function (r) {
        console.log(r);
    });
}

/**
 * 根据head_id 获取数据
 */
function getDataById() {
    $MB.loading('show');
    $.get("/activity/getDataById", {headId: headId}, function (r) {
        var data = r.data;
        console.log(data);
        $("#actName").val(data['ACT_NAME']);
        $("#actName").attr('disabled', true);

        $("#actType").find("option[value='"+data['ACT_TYPE']+"']").prop("selected", "selected");
        $("#actType").attr('disabled', true);

        if(data['ACT_TYPE'] == 'own') {
            $("#ownActBeginDt").val(data['BEGIN_DT']);
            $("#ownActBeginDt").attr('disabled', true);

            $("#ownActEndDt").val(data['END_DT']);
            $("#ownActEndDt").attr('disabled', true);

            $("#btn_next_2_own").attr("style", "display:none");
            $("#btn_next_2_own_edit").attr("style", "display:inline-block");
        }else {
            $("#platActBeginDt").val(data['BEGIN_DT']);
            $("#platActBeginDt").attr('disabled', true);

            $("#platActEndDt").val(data['END_DT']);
            $("#platActEndDt").attr('disabled', true);

            $("#btn_next_2_plat").attr("style", "display:none");
            $("#btn_next_2_plat_edit").attr("style", "display:inline-block");
        }
        fromVal = data['DATE_RANGE'];
        $MB.loading('hide');
    });
}