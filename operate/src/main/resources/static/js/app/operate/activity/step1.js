
$("#btn_next_1").click(function () {
    let actType = $("#actType").find("option:selected").val();
    let actName = $("#actName").val();

    if(actType == '' && $("#actType").attr("style", "display:none;")) {
        $MB.n_warning("请选择活动类型！");
        return;
    }

    if(actName == '') {
        $MB.n_warning("请输入活动名称！");
        return;
    }

    if(actType == 'own') {
        $("#step1").attr("style", "display:none;");
        $("#step2_own").attr("style", "display:block;");

        init_date_begin("ownActBeginDt", "ownActEndDt", "yyyy-mm-dd",0,2,0);
        init_date_end("ownActBeginDt", "ownActEndDt", "yyyy-mm-dd",0,2,0);
        $("#ownActBeginDt").datepicker('setStartDate', new Date());

        var endDt = $("input[name='endDt']:checked").val();
        getUserDataCount('', endDt, 'chart1');
    }

    if(actType == 'plat') {
        $("#step1").attr("style", "display:none;");
        $("#step2_plat").attr("style", "display:block;");

        init_date_begin("platActBeginDt", "platActEndDt", "yyyy-mm-dd",0,2,0);
        init_date_end("platActBeginDt", "platActEndDt", "yyyy-mm-dd",0,2,0);
        $("#platActBeginDt").datepicker('setStartDate', new Date());

        getStartAndEndDate();
        var startDt = $("#platActBeginDt").val();
        var endDt = $("#platActEndDt").val();

        $("#date_range").ionRangeSlider({
            min: 0,
            max: 30,
            from: fromVal,
            onFinish: function (data) {
                let startDt = $("#platActBeginDt").val();
                let endDt = $("#platActEndDt").val();
                let dateRange = data.from_pretty;
                getWeightIdx(startDt, endDt, 'chart2', dateRange);
                getUserDataCount(startDt, endDt, 'chart3', dateRange);
            }
        });
        var dateRange = $("#date_range").val();
        getWeightIdx(startDt, endDt, 'chart2', dateRange);
        getUserDataCount(startDt, endDt, 'chart3', dateRange);
    }
    step.setActive(1);
});

function getStartAndEndDate() {
    $.ajax({
        url: '/activity/getStartAndEndDate',
        async:false,
        success: function (r) {
            var data = r.data;
            $("#platActBeginDt").val(data.start);
            $("#platActEndDt").val(data.end);
        }
    });
}