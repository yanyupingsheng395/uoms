$(function () {
    init_header();
    init_table1();
    init_table2();
});

// 初始化表格1
function init_table1() {
    var settings = {
        singleSelect: true,
        columns: [[
            {
                field: 'kpiName',
                title: '指标',
                valign:"middle",
                rowspan: 2
            },
            {
                title: "数值（截止前日累计）",
                align:"center",
                colspan: 3
            }],
            [
                {
                    field: 'allStage',
                    title: '整体活动',
                    align: 'center',
                    valign: 'middle'
                }, {
                    field: 'preheatStage',
                    title: '预热阶段',
                    align: 'center',
                    valign: 'middle'
                }, {
                    field: 'normalStage',
                    title: '正式阶段',
                    align: 'center',
                    valign: 'middle'
                }
            ]]
    };
    $("#effectTable1").bootstrapTable('destroy').bootstrapTable(settings);
    $.get("/activity/getEffectMainKpi", {headId: headId, pushKpi: $("#pushKpi1").val()},function (r) {
        if(r.code === 200) {
            $("#effectTable1").bootstrapTable('load', r.data);
        }else {
            $MB.n_danger("获取数据异常！");
        }
    });
}
// 初始化表格2
function init_table2() {
    var settings = {
        singleSelect: true,
        columns: [[
            {
                field: 'mainKpi',
                title: '指标',
                align:"center",
                valign:"middle",
                rowspan: 2
            },
            {
                field: 'kpiName',
                title: '辅助指标',
                valign:"middle",
                rowspan: 2,
                formatter: function (value, row, index) {
                    if(value === '推送转化金额（元）' || value === '推送转化人数（人）') {
                        return "-";
                    }else {
                        return value;
                    }
                }
            },
            {
                title: "数值（截止前日累计）",
                align:"center",
                colspan: 3
            }],
            [
                {
                    field: 'allStage',
                    title: '整体活动',
                    align: 'center',
                    valign: 'middle'
                }, {
                field: 'preheatStage',
                title: '预热阶段',
                align: 'center',
                valign: 'middle'
            }, {
                field: 'normalStage',
                title: '正式阶段',
                align: 'center',
                valign: 'middle'
            }
            ]]
    };
    $("#effectTable2").bootstrapTable('destroy').bootstrapTable(settings);
    $.get("/activity/getEffectAllKpi", {headId: headId, pushKpi: $("#pushKpi2").val()},function (r) {
        if(r.code === 200) {
            $("#effectTable2").bootstrapTable('load', r.data);
            $("#effectTable2").bootstrapTable('mergeCells', {index: 0, field: 'mainKpi', rowspan: 5});
            $("#effectTable2").bootstrapTable('mergeCells', {index: 5, field: 'mainKpi', rowspan: 5});
        }else {
            $MB.n_danger("获取数据异常！");
        }
    });
}
function init_header() {
    $.get("/activity/getEffectInfo", {headId: headId},function (r) {
        if(r.code === 200) {
            var data = r.data;
            var effectDt = data['effectDt'];
            var pushUcnt = data['pushUcnt'];
            var pushRoi = data['pushRoi'];
            var convertRate = data['convertRate'];
            var code = "截止到"+subEffectDt(effectDt, 0 ,4)+"年"+subEffectDt(effectDt, 4 ,6)+"月"+subEffectDt(effectDt, 6 ,8)+"日，总计对"+pushUcnt+"人次进行了推送培养，推送转化率为"+convertRate+"%，推送ROI为每条推送信息带来"+pushRoi+"元转化金额。";
            $("#effectInfo").html('').append(code);
        }else {
            $MB.n_danger("获取数据异常！");
        }
    });
}

function subEffectDt(effectDt, start, end) {
    return String(effectDt).substring(start, end);
}

$("#pushKpi1").change(function () {
    init_table1();
});

$("#pushKpi2").change(function () {
    init_table2();
});