/**
 * 获取用户数量随时间变化图
 */
function getUserDataCount(startDt, endDt, chartId, dateRange) {
    $.get("/activity/getUserDataCount", {startDt: startDt, endDt: endDt, dateRange:dateRange}, function (r) {
        var chart = r.data;
        var seriesData = [{name: '',type: 'line', data: chart['yAxisData']}];
        var option = getOption(null, chart['xAxisData'], chart['xAxisName'], chart['yAxisName'], seriesData);
        option.grid = {right:'13%', top: '10%'};
        option.title = {
            text: '成长用户数量分布图',
            x:'center',
            y: 'bottom',
            textStyle:{
                color:'#000',
                fontStyle:'normal',
                fontWeight:'normal',
                fontFamily:'sans-serif',
                fontSize:12
            }
        };
        var echart = echarts.init(document.getElementById(chartId), 'macarons');
        echart.setOption(option);
        echart.resize();
    });
}

function getWeightIdx(startDt, endDt, chartId, dateRange) {
    $.get("/activity/getWeightIdx", {startDt: startDt, endDt: endDt, dateRange: dateRange}, function (r) {
        var chart = r.data;
        var seriesData = [{name: '',type: 'line', data: chart['yAxisData']}];
        var option = getOption(null, chart['xAxisData'], chart['xAxisName'], chart['yAxisName'], seriesData);
        option.grid = {right:'13%', top: '10%'};
        option.title = {
            text: '活动前后权重指数图',
            x:'center',
            y: 'bottom',
            textStyle:{
                color:'#000',
                fontStyle:'normal',
                fontWeight:'normal',
                fontFamily:'sans-serif',
                fontSize:12
            }
        };
        var echart = echarts.init(document.getElementById(chartId), 'macarons');
        echart.setOption(option);
        echart.resize();
    });
}

$('input[type=radio][name=endDt]').change(function() {
    getUserDataCount('',this.value, 'chart1');
});

$("#btn_search").click(function () {
    if($("#platActBeginDt").val() == '' || $("#platActEndDt").val() == '') {
        $MB.n_warning("请选择活动起止时间！");
        return;
    }
    var startDt = $("#platActBeginDt").val();
    var endDt = $("#platActEndDt").val();
    var dateRange = $("#date_range").val();
    getWeightIdx(startDt, endDt, 'chart2', dateRange);
    getUserDataCount(startDt, endDt, 'chart3', dateRange);
});

$("#btn_next_2_own").click(function () {
    if($("#ownActBeginDt").val() == '' || $("#ownActEndDt").val() == '') {
        $MB.n_warning("请选择活动起止时间！");
        return "";
    }
    var actType = $("#actType").find("option:selected").val();
    var startDt = $("#ownActBeginDt").val();
    var endDt = $("#ownActEndDt").val();
    var actName = $("#actName").val();
    addData(actName, actType, startDt, endDt, null);

    // 第三步
    step3();
});

$("#btn_next_2_plat").click(function () {
    if($("#platActBeginDt").val() == '' || $("#platActEndDt").val() == '') {
        $MB.n_warning("请选择活动起止时间！");
        return "";
    }
    var actName = $("#actName").val();
    var actType = $("#actType").find("option:selected").val();
    var startDt = $("#platActBeginDt").val();
    var endDt = $("#platActEndDt").val();
    var dateRange = $("#date_range").val();
    addData(actName, actType, startDt, endDt, dateRange);

    // 第三步
    step3();
});

function step3() {
    $MB.loadingDesc('show', '正在计算数据中...');
    setTimeout(function(){
        $MB.loadingDesc('hide');
        $("#step1").attr("style", "display:none;");
        $("#step2_own").attr("style", "display:none;");
        $("#step2_plat").attr("style", "display:none;");
        $("#step3").attr("style", "display:block;");

        getUserList();

        step.setActive(2);
    },1000);
}

