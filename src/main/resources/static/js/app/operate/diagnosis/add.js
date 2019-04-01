$(function() {

});
init_date("beginDt", "yyyy", 2,2,2);
init_date("endDt", "yyyy", 2,2,2);
// 周期切换时间控件
$("#periodType").change(function () {
    var periodType = $("#periodType option:selected").val();
    $("#beginDt").remove();
    $("#endDt").remove();

    $("#startDate").append("<input class=\"form-control js-datepicker m-b-10\" type=\"text\" id=\"beginDt\" name=\"beginDt\"/>");
    $("#endDate").append("<input class=\"form-control js-datepicker m-b-10\" type=\"text\" id=\"endDt\" name=\"endDt\"/>");
    if(periodType == "Y") {
        init_date("beginDt", "yyyy", 2,2,2);
        init_date("endDt", "yyyy", 2,2,2);
    }else if(periodType == "M") {
        init_date("beginDt", "yyyy-mm", 1,2,1);
        init_date("endDt", "yyyy-mm", 1,2,1);
    }
});
