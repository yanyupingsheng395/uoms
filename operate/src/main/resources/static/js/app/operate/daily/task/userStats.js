/**
 * 获取数据
 */
var stepObj=null;
$("#userStats_modal").on("shown.bs.modal", function () {
    $("#effectDays").ionRangeSlider({
        min: 1,
        max: 10,
        from: 5,
        skin: "modern"
    });
    init();
});

$("#userStats_modal").on("hidden.bs.modal", function () {
    stepObj.setActive(0);
    $("#prevStepBtn").attr("style", "display:none;");
    $("#nextStepBtn").attr("style", "display:inline-block");
    $("#pushMsgBtn").attr("style", "display:none;");
    $("#step1").attr("style", "display:block;");
    $("#step2").attr("style", "display:none;");
    $("#step3").attr("style", "display:none;");
    $("#effectDays").ionRangeSlider({
        min: 1,
        max: 10,
        from: 5,
        skin: "modern"
    });
    $("#push_ok").prop("checked", false);
    $("input[name='pushMethod']:checked").prop("checked", false);
    current_step = 0;
});

function init() {
    let effectRange = $("#effectDays").data("ionRangeSlider");
    $.get("/daily/getPushInfo", {}, function (r) {
        let code = "";
        r.data['timeList'].forEach((v, k) => {
            code += "<option value='" + v + "'>" + v + "</option>";
        });
        $("#pushPeriod").html('').append(code);
        $('input[name="pushMethod"]').removeAttr("checked");
        $('input[name="pushMethod"][value="' + r.data.method + '"]').prop("checked", true);
        $("#pushPeriodDiv").hide();

        //有效天数滑块设置为默认值
        effectRange.update({
            min: 1,
            max: 10,
            from: r.data.effectDays,
            skin: "modern"
        });
    });
}

/**
 * 设置step
 */
let current_status = '';
function setStep(status) {
    current_status = status;
    if(status === 'todo') {
        stepObj=steps({
            el: "#pushSteps",
            data: [
                { title: "概要",description:""},
                { title: "明细",description:""},
                { title: "推送",description:"" }
            ],
            center: true,
            dataOrder: ["title", "line", "description"]
        });
    }else {
        stepObj=steps({
            el: "#pushSteps",
            data: [
                { title: "概要",description:""},
                { title: "明细",description:""}
                ],
            center: true,
            dataOrder: ["title", "line", "description"]
        });
    }
}

/**
 * 步骤发生改变
 */
var current_step = 0;
function changeStep(count) {
    current_step = current_step + count;
    if(current_step > 3) {
        current_step = 3;
    }
    if(current_step < 0) {
        current_step = 0;
    }
    if(current_step === 0) {
        $("#prevStepBtn").attr("style", "display:none;");
        $("#nextStepBtn").attr("style", "display:inline-block");
        $("#pushMsgBtn").attr("style", "display:none;");
        $("#step1").attr("style", "display:block;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:none;");
        stepObj.setActive(0);
    }
    if(current_step === 1) {
        $("#prevStepBtn").attr("style", "display:inline-block");
        $("#pushMsgBtn").attr("style", "display:none;");
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:block;");
        $("#step3").attr("style", "display:none;");
        if(current_status === 'todo') {
            $("#nextStepBtn").attr("style", "display:inline-block;");
            generateStragegy();
        }else {
            $("#nextStepBtn").attr("style", "display:none;");
            getUserStrategyList();
        }
        stepObj.setActive(1);
    }
    if(current_step === 2) {
        if(current_status === 'todo') {
            $.get("/daily/validUserGroup", {}, function(r) {
                if(r.code == 200) {
                    if(r.data) {
                        $MB.n_warning("成长组配置验证未通过！");
                        return false;
                    }else {
                        $("#prevStepBtn").attr("style", "display:inline-block;");
                        $("#nextStepBtn").attr("style", "display:none;");
                        $("#pushMsgBtn").attr("style", "display:inline-block;");
                        $("#step1").attr("style", "display:none;");
                        $("#step2").attr("style", "display:none;");
                        $("#step3").attr("style", "display:block;");
                        stepObj.setActive(2);
                    }
                }
            });
        }
    }
}

$("#push_ok").change(function () {
    let flag = $(this).is(':checked');
    if(flag) {
        $("#pushMsgBtn").removeAttr("disabled");
    }else {
        $("#pushMsgBtn").attr("disabled", true);
    }
});

/**
 * 启动群组推送
 */
function submitData() {
    if(!$("#push_ok").is(':checked')) {
        $MB.n_warning("请先勾选我要推送！");
        return;
    }
    if($("input[name='pushMethod']:checked").val() === undefined) {
        $MB.n_warning("请先选择推送方式！");
        return;
    }
    $("#btn_push").attr("disabled", true);
    $("#push_msg_modal").modal('hide');

    $MB.loadingDesc('show', '推送中，请稍候...');
    $.get("/daily/submitData", {
        headId: $("#headId").val(), pushMethod: $("input[name='pushMethod']:checked").val(),
        pushPeriod: $("#pushPeriod").find("option:selected").val(),
        effectDays: $("#effectDays").val()
    }, function (r) {
        if (r.code === 200) {
            $MB.n_success("启动推送成功！");
            setTimeout(function () {
                window.location.href = "/page/daily/task";
            }, 1000);
        } else {
            $("#btn_push").attr("disabled", false);
            $MB.n_danger(r.msg);
        }
        $MB.loadingDesc('hide');
    });
}

$('input[name="pushMethod"]').click(function () {
    if ($(this).val() == "FIXED") {
        $("#pushPeriodDiv").show();
    } else {
        $("#pushPeriodDiv").hide();
    }
});

function generateStragegy() {
    $MB.loadingDesc('show', '策略生成中，请稍候...');
    $.get("/daily/generatePushList", {headId: $("#headId").val()}, function (r) {
        if(r.code == 200) {
            //如果生成成功，则加载表格
            getUserStrategyList();
        }else {
            //提示错误信息
            $MB.n_danger(r.msg);
        }
        $MB.loadingDesc('hide');
    });
}

function getUserStrategyList() {
    let settings = {
        url: '/daily/getUserStrategyList',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  //页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    headId: function () {
                        return $("#headId").val();
                    }
                }
            };
        },
        columns: [
            {
                field: 'userId',
                title: '用户ID',
                valign: "middle",
                width: 100
            },
            {
                field: 'recProdName',
                title: '推荐商品',
                width: 300
            },
            {
                field: 'couponMin',
                title: '补贴',
                width: 100,
                formatter: function (value, row, idx) {
                    if(row.couponMin==''||row.couponMin=='null'||row.couponMin==null)
                    {
                        return "无";
                    }else
                    {
                        return  row.couponDeno+"元";
                    }
                }

            },
            {
                field: 'orderPeriod',
                title: '建议触达时段（时）',
                width: 100
            },
            {
                field: 'smsContent',
                title: '推送预览',
                formatter: function (value, row, index) {
                    return longTextFormat(value, row, index);
                }
            },
            {
                title: '成长洞察',
                width: 80,
                formatter: function (value, row, idx)
                {
                    return "<button class='btn btn-primary btn-xs' onclick='growthInsight(\""+row['userId']+"\",\""+ $("#headId").val() +"\")'>know how</button>";
                }
            }],
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $MB.initTable('userStrategyListTable', settings);
}

function growthInsight(user_id,head_id)
{
    $.get("/daily/getTouchDt", {headId: head_id}, function (res) {
        var taskDt = res.data;
        if(res.code === 200 && taskDt !== undefined && taskDt !== '') {
            window.open("/page/personInsight?userId=" + user_id + "&taskDt=" + taskDt, "_blank");
        }else {
            $MB.n_warning("获取任务日期有误！");
        }
    });

}

var chart1;
function getUserStatsData() {
    var selected = $("#dailyTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要预览执行的任务！');
        return;
    };

    var headId = selected[0].headId;
    $("#headId").val(headId);
    $.get("/daily/getUserStatsData", {headId: headId}, function (r) {
        if(r.code == 200) {
            $("#touchDt").val();
            $("#userNum").val();

            var chartTitle="今天是"+r.data.touchDt+",根据用户成长引擎的计算,有"+r.data.userNum+"个用户到达成长的节点，需要个性化推送培养。";
            chart1 = echarts.init(document.getElementById("chart1"), 'macarons');
            var option = getChart1Option(r.data,chartTitle);
            chart1.setOption( option );

            chart1.on('click', function (params) {
                $("#userValue").val(params.value[5]);
                $("#pathActive").val(params.value[6]);
                $("#lifecycle").val(params.value[7]);

                //刷新SPU表格和PROD表格
                $.get("/daily/refreshUserStatData", {
                    headId: $("#headId").val(),
                    userValue: params.value[5],
                    pathActive: params.value[6],
                    lifecycle: params.value[7]
                }, function (r) {
                    //加载标题
                    $("#spuTitle").text(r.data.groupName);
                    $("#prodTitle").text(r.data.prodGroupName);

                    $("#spuName").val(r.data.spuName);

                    let code = "";
                    //记载表格
                    r.data.spuList.forEach((v,k)=>{
                        code += "<tr><td><a style='cursor:pointer;' onclick=clickSpuName('"+v['spuName']+"')>"+v['spuName']+"</a></td><td>"+v['ucnt']+"</td></tr>";
                    });
                    if(code === "") {
                        code = "<tr class='text-center'><td colspan='2'>没有查询到相应的记录</td></tr>";
                    }
                    $("#spuTableData").html('').append(code);

                    code='';
                    r.data.prodList.forEach((v,k)=>{
                        code += "<tr><td>"+v['prodName']+"</td><td>"+v['ucnt']+"</td></tr>";
                    });
                    if(code === "") {
                        code = "<tr class='text-center'><td colspan='2'>没有查询到相应的记录</td></tr>";
                    }
                    $("#prodTableData").html('').append(code);
                });
            });

            //加载标题
            $("#spuTitle").text(r.data.groupName);
            $("#prodTitle").text(r.data.prodGroupName);

            //记载隐藏字段
            $("#userValue").val(r.data.userValue);
            $("#pathActive").val(r.data.pathActive);
            $("#lifecycle").val(r.data.lifecycle);
            $("#spuName").val(r.data.spuName);

            let code = "";
            //记载表格
            r.data.spuList.forEach((v,k)=>{
                code += "<tr><td><a  style='cursor:pointer;' onclick=clickSpuName('"+v['spuName']+"')>"+v['spuName']+"</a></td><td>"+v['ucnt']+"</td></tr>";
            });
            if(code === "") {
                code = "<tr class='text-center'><td colspan='2'>没有查询到相应的记录</td></tr>";
            }
            $("#spuTableData").html('').append(code);

            code='';
            r.data.prodList.forEach((v,k)=>{
                code += "<tr><td>"+v['prodName']+"</td><td>"+v['ucnt']+"</td></tr>";
            });
            if(code === "") {
                code = "<tr class='text-center'><td colspan='2'>没有查询到相应的记录</td></tr>";
            }
            $("#prodTableData").html('').append(code);
            setStep(selected[0].status);
            //打开modal
            $("#userStats_modal").modal('show');
        }else {
            //提示错误信息
            $MB.n_danger(r.msg);
        }
    });
}

/**
 * 点SPU名称进行刷新
 * @param spuName
 */
function clickSpuName(spuName)
{
    //刷新SPU表格和PROD表格
    $.get("/daily/refreshUserStatData2", {
        headId: $("#headId").val(),
        userValue:  $("#userValue").val(),
        pathActive:  $("#pathActive").val(),
        lifecycle: $("#lifecycle").val(),
        spuName: spuName
    }, function (r) {
        //刷新标题 表格
        $("#prodTitle").text(r.data.prodGroupName);

        let code = "";
        r.data.prodList.forEach((v,k)=>{
            code += "<tr><td>"+v['prodName']+"</td><td>"+v['ucnt']+"</td></tr>";
        });
        if(code === "") {
            code = "<tr class='text-center'><td colspan='2'>没有查询到相应的记录</td></tr>";
        }
        $("#prodTableData").html('').append(code);

    });
}

function getChart1Option(data,chartTitle)
{
    var option = {
        title: {
            text: chartTitle,
            x: 'center',
            y: 'top',
            textStyle: {
                //文字颜色
                color: '#000',
                //字体风格,'normal','italic','oblique'
                fontStyle: 'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight: 'normal',
                //字体系列
                fontFamily: 'sans-serif',
                //字体大小
                fontSize: 12
            }
        },
        legend: {
            top: 25,
            left: '50%',
            data: ['新用户', '复购用户']
        },
        xAxis: {
            type: 'value',
            name: '用户数（人）',
            splitLine: {
                lineStyle: {
                    type: 'dashed'
                }
            }

        },
        yAxis: {
            type: 'category',
            name: '下步成长节点',
            data: data.ylabel
        },
        visualMap: [
            {
                type: 'piecewise',
                show: true,
                left: 15,
                bottom: 0,
                orient: 'horizontal',
                inverse: true,
                dimension: 2,
                align: 'right',
                pieces: [
                    {value: 80, label: '重要'},
                    {value: 60, label: '主要'},
                    {value: 40, label: '普通'},
                    {value: 20, label: '长尾'}
                ],
                textGap: 10,
                inRange: {
                    symbolSize: [10, 80]
                },
                outOfRange: {
                    symbolSize: [10, 80],
                    color: ['rgba(255,255,255,.2)']
                },
                controller: {
                    inRange: {
                        color: ['#50a3ba']
                    },
                    outOfRange: {
                        color: ['#444']
                    }
                }
            }
         ],
        series: [{
            name: '新用户',
            data: data.fpUser,
            type: 'scatter',
            symbolSize: function (val) {
                return val[2];
            },
            emphasis: {
            label: {
                show: true,
                formatter: function (param) {
                    return param.data[4];
                },
                position: 'top'
            }
            },

        }, {
            name: '复购用户',
            data: data.rpUser,
            type: 'scatter',
            symbolSize: function (val) {
                return val[2];
            },
            emphasis: {
                label: {
                    show: true,
                    formatter: function (param) {
                        return param.data[4];
                    },
                    position: 'top'
                }
            }
        }]
    };
    return option;
}

$('#userStats_modal').on('shown.bs.modal',function(){
    chart1.resize()
})



