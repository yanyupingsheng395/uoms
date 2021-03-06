$(function () {
    init_header();
    init_table1();
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
                rowspan: 3
            },
            {
                title: "数值（截止前日累计）",
                align:"center",
                colspan: 7
            }],
            [
                {
                    field: 'allStage',
                    title: '整体活动',
                    align: 'center',
                    valign: 'middle',
                    rowspan: 2
                },
                {
                    field: '',
                    title: '预售',
                    align: 'center',
                    valign: 'middle',
                    rowsapn: 1,
                    colspan: 3
                }, {
                    field: '',
                    title: '正式',
                    align: 'center',
                    valign: 'middle',
                    rowsapn: 1,
                    colspan: 3
                }
            ],
            [
                {
                    field: 'preheatAll',
                    title: '整体',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    field: 'preheatNotify',
                    title: '活动通知',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    field: 'preheatDuring',
                    title: '活动期间',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    field: 'normalAll',
                    title: '整体',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    field: 'normalNotify',
                    title: '活动通知',
                    align: 'center',
                    valign: 'middle',
                },
                {
                    field: 'normalDuring',
                    title: '活动期间',
                    align: 'center',
                    valign: 'middle',
                }
            ]

        ]
    };
    $("#effectTable1").bootstrapTable('destroy').bootstrapTable(settings);
    $.get("/activity/getEffectMainKpi", {headId: headId, kpiType: $("#kpiType").val()},function (r) {
        if(r.code === 200) {
            $("#effectTable1").bootstrapTable('load', r.data);
        }else {
            $MB.n_danger("获取数据异常！");
        }
    });
}

function init_header() {
    $.get("/activity/getEffectInfo", {headId: headId},function (r) {
        if(r.code === 200) {
            var data = r.msg;
            $("#effectInfo").html('').append('<i class="mdi mdi-alert-circle-outline"></i>从' + data["beginDt"] + '开始对'+data['userCount']+'个用户进行个性化推送培养。');
        }else {
            $MB.n_danger(r.msg);
        }
    });
}

$("#kpiType").change(function () {
    init_table1();
});