$(function () {
    var errmsg=$("#errormsg").val();
    if(null!=errmsg&&errmsg!='')
    {
        $MB.n_warning(errmsg);
    }
    init_date('taskDate', 'yyyy-mm-dd', 0, 2, 0);
    $("#taskDate").datepicker('setEndDate', new Date());
    initTable();
});

let chart1;
let chart1a;
let chart1b;
let chart2;
let chart3;
let userValueOption;
let chart4;

function initTable() {
    var settings = {
        url: '/qywxDaily/getHeadList',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {taskDate: $("#taskDate").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'headId',
            title: 'ID',
            visible: false
        }, {
            field: 'taskDateStr',
            title: '日期'
        }, {
            field: 'totalNum',
            title: '建议推送人数（人）'
        }, {
            field: 'successNum',
            title: '实际成功推送人数（人）'
        }, {
            field: 'convertNum',
            title: '推送转化人数（人）'
        }, {
            field: 'convertRate',
            title: '推送转化率（%）'
        }, {
            field: 'convertAmount',
            title: '推送转化金额（元）'
        }, {
            field: 'effectDays',
            title: '效果观察天数（天）'
        }, {
            title: '配置校验状态',
            align: 'center',
            formatter: function (value, row, indx) {
                var currDate=getNowFormatDate();
                var res = "-";
                if(row.touchDtStr ===currDate&&"通过"===row.validateLabel) {
                    res = "<span class=\"badge bg-success\"><a style='text-decoration: none;cursor: pointer;pointer-events: none;color:#fff;'>"+row.validateLabel+"</a></span>";
                }else if(row.touchDtStr ===currDate&&"未通过"===row.validateLabel)
                {
                    res = "<span class=\"badge bg-danger\"><a onclick='gotoConfig()' style='color: #fff;text-decoration: underline;cursor: pointer;'>"+row.validateLabel+"</a></span>";
                }else
                {
                    res='-';
                }
                return res;
            }
        },{
            field: 'status',
            title: '任务执行状态',
            align: 'center',
            formatter: function (value, row, indx) {
                var res;
                switch (value) {
                    case "todo":
                        res = "<span class=\"badge bg-info\">待执行</span>";
                        break;
                    case "done":
                        res = "<span class=\"badge bg-success\">已执行</span>";
                        break;
                    case "finished":
                        res = "<span class=\"badge bg-primary\">已结束</span>";
                        break;
                    case "timeout":
                        res = "<span class=\"badge bg-gray\">过期未执行</span>";
                        break;
                    default:
                        res = "-";
                        break;
                }
                return res;
            }
        }]
    };
    $MB.initTable('qywxDailyTable', settings);
}

function resetQuery() {
    $("#taskDate").val("");
    $MB.refreshTable("qywxDailyTable");
}

$("#btn_query").click(function () {
    $MB.refreshTable("qywxDailyTable");
});

/**
 * 任务效果查看
 */
$("#btn_catch").click(function () {
    let selected = $("#qywxDailyTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选查看效果的任务！');
        return;
    }
    // let status = selected[0].status;
    // if (status != 'done' && status != 'finished') {
    //     $MB.n_warning("只有已执行，已结束状态可查看任务效果！");
    //     return;
    // }
    let headId = selected[0].headId;
    window.location.href = "/page/qywxDaily/effect?headId=" + headId;
});

/**
 * 预览执行
 */
$("#btn_insight").click(function () {
    var selected = $("#qywxDailyTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要预览执行的任务！');
        return;
    };

    let headId = selected[0].headId;
    let status = selected[0].status;
    let taskDate = selected[0].taskDateStr;
    let totalNum = selected[0].totalNum;

    $("#headId").val(headId);

    if(totalNum==0)
    {
        $MB.n_warning('当前任务没有建议推送的用户！');
        return;
    }

    //打开遮罩层
    $MB.loadingDesc('show', '策略生成中，请稍候...');
    //获取预览数据
    $.get("/qywxDaily/getTaskOverViewData", {headId: headId}, function (r) {
        if(r.code == 200) {
             let chartTitle="<i class='mdi mdi-alert-circle-outline'></i>当前任务日期："+r.data.taskDate+"，根据用户成长引擎的计算，有"+r.data.userNum+"个用户到达成长的节点，需要通过企业微信进行个性化沟通。";
            $("#overviewInfo").html('').append(chartTitle);

             //本日用户成长目标
             initUserTargetChart(r.data);

            //本日用户差异群组
            initUserDiffChart(r.data);

            //本日用户推送策略
            initUserStrategy(r.data);

            //初始化步骤组件
            setStep(status,taskDate);

            //加载当前任务所涉及的成员
            getAllQywxUserList(headId);

            //打开modal
            $("#viewPush_modal").modal('show');
        }else {
            //提示错误信息
            $MB.n_danger(r.msg);
        }
        $MB.loadingDesc('hide');
    });

});

function initUserTargetChart(data) {
    chart1 = echarts.init(document.getElementById("chart1"), 'macarons');
    let option = getChart1Option(data.spuList,data.spuCountList,"成长目标的类目分布");
    chart1.setOption( option );

    //为chart1增加点击事件
    chart1.on('click', function(params) {
        //刷新右侧产品条图
        refreshChart2(params.name);
    });


    chart1a = echarts.init(document.getElementById("chart1a"), 'macarons');
    let optionChart1a = getChart1aOption(data,"成长目标的类型分布(1)");
    chart1a.setOption( optionChart1a );

    chart1b = echarts.init(document.getElementById("chart1b"), 'macarons');
    let optionChart1b = getChart1bOption(data,"成长目标的类型分布(2)");
    chart1b.setOption( optionChart1b );

    chart2 = echarts.init(document.getElementById("chart2"), 'macarons');
    option = getChart2Option(data.prodList,data.prodCountList,"成长目标的商品分布");
    chart2.setOption( option );
}

/**
 * 刷新按产品展示的条形图
 */
function refreshChart2(spuName)
{
    $.get("/qywxDaily/getProdCountBySpu", {headId:  $("#headId").val(),spuName:spuName}, function (r) {
        chart2 = echarts.init(document.getElementById("chart2"), 'macarons');
        let option = getChart2Option(r.data.prodList,r.data.prodCountList,"成长目标的商品分布");
        chart2.setOption( option );
    });
}

function initUserDiffChart(data)
{
    chart3 = echarts.init(document.getElementById("chart3"), 'macarons');
    userValueOption = getChart3Option(data.userValueList,data.userValueLabelList,data.userValueCountList,"类目用户的价值分布");
    chart3.setOption( userValueOption );

    //为chart1增加点击事件
    chart3.on('click', function(params) {
        //获取当前点击的userValue
        let userValue=userValueOption.series[params.seriesIndex].userValues[params.dataIndex];
        //刷新右侧表格
        refreshMatrixTable(userValue);
    });

    //渲染表格数据
    applyMatrixTable(data.matrixResult);
}

function refreshMatrixTable(userValue) {
    $.get("/qywxDaily/getMatrixData", {headId:  $("#headId").val(),userValue:userValue}, function (r) {
        applyMatrixTable(r.data);
    });
}

function applyMatrixTable(matrixResult)
{
    //构造表格的数据
    let code="<tr><th>项目</th><th colspan='"+matrixResult.columnTitle.length+"'>用户对类目的生命周期阶段</th></tr><tr><th>用户下一次转化的活跃度节点</th>";
    $.each(matrixResult.columnTitle,function(index,value){
        code+="<th>"+value+"</th>";
    });
    code+="</tr>";

    $("#matrixHead").html('').append(code);

    code='';
    $.each(matrixResult.rows,function(index,value){
        let row=value;
        code+="<tr>";
        $.each(row,function (index,value) {
            code+="<td>"+value+"</td>";
        })
        code+="</tr>";
    });
    $("#matrixTableData").html('').append(code);

}

function initUserStrategy(data)
{
    chart4 = echarts.init(document.getElementById("chart4"), 'macarons');
    let option = getChart4Option(data,"需要执行推送的成员分布");
    chart4.setOption( option );

    var settings = {
        pagination: true,
        sidePagination: "client",
        pageNumber: 1,
        pageSize: 5,
        pageList: [5, 10, 25, 50, 100],
        columns: [{
            field: 'couponMin',
            title: '门槛'
        }, {
            field: 'couponDeno',
            title: '面额'
        }, {
            field: 'ucnt',
            title: '人数'
        }]
    };
    $("#couponStatTable").bootstrapTable('destroy').bootstrapTable(settings);
    $("#couponStatTable").bootstrapTable('load', data.statsByCoupon);

}

function getChart1Option(spuList,spuCountList,chartTitle)
{
    var option = {
        title: {
            text: chartTitle,
            x: 'center',
            y: 'bottom',
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
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {left:100,right:45},
        xAxis: {
            name: '人数',
            type: 'value'
        },
        yAxis: {
            name: '类目',
            type: 'category',
            data: spuList
        },
        series: [
            {
                type: 'bar',
                data: spuCountList
            }
        ]
    };
    return option;
}


function getChart1aOption(data,chartTitle)
{
    var option = {
        title: {
            text: chartTitle,
            x: 'center',
            y: 'bottom',
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
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {left:100,right:45},
        xAxis: {
            name: '人数',
            type: 'value'
        },
        yAxis: {
            name: '类型',
            type: 'category',
            data: data.growthTypeLabelList
        },
        series: [
            {
                type: 'bar',
                data: data.growthTypeCountList
            }
        ]
    };
    return option;
}



function getChart1bOption(data,chartTitle)
{
    var option = {
        title: {
            text: chartTitle,
            x: 'center',
            y: 'bottom',
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
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {left:100,right:45},
        xAxis: {
            name: '人数',
            type: 'value'
        },
        yAxis: {
            name: '类型',
            type: 'category',
            data: data.growthSeriesTypeLabelList
        },
        series: [
            {
                type: 'bar',
                data: data.growthSeriesTypeCountList
            }
        ]
    };
    return option;
}



function getChart2Option(prodList,prodCountList,chartTitle)
{
    var option = {
        title: {
            text: chartTitle,
            x: 'center',
            y: 'bottom',
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
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {left:100,right:45},
        xAxis: {
            name: '人数',
            type: 'value'
        },
        yAxis: {
            name: '商品',
            type: 'category',
            data: prodList
        },
        series: [
            {
                type: 'bar',
                data: prodCountList
            }
        ]
    };
    return option;
}

function getChart3Option(userValueList,userValueLabelList,userValueCountList,chartTitle)
{
    return {
        title: {
            text: chartTitle,
            x: 'center',
            y: 'bottom',
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
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {left:100,right:45},
        xAxis: {
            name: '人数',
            type: 'value'
        },
        yAxis: {
            name: '用户价值',
            type: 'category',
            data: userValueLabelList
        },
        series: [
            {
                type: 'bar',
                data: userValueCountList,
                userValues:userValueList
            }
        ]
    };
}

function getChart4Option(data,chartTitle)
{
    return {
        title: {
            text: chartTitle,
            x: 'center',
            y: 'bottom',
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
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        grid: {left:100,right:45},
        xAxis: {
            name: '人数',
            type: 'value'
        },
        yAxis: {
            name: '成员',
            type: 'category',
            data: data.qywxUserList
        },
        series: [
            {
                type: 'bar',
                data: data.qywxCountList
            }
        ]
    };

}


function gotoConfig() {
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: "去完成配置？"
    }, function () {
        location.href = "/page/daily/config";
    });
}

function getNowFormatDate() {
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year  + month  + strDate;
    return currentdate;
}

////////////////////////////////预览执行//////////////////////////////////
/**
 * 打开预览推送面板
 */
var stepObj=null;
$("#viewPush_modal").on("shown.bs.modal", function () {
    $("#effectDays").ionRangeSlider({
        min: 1,
        max: 10,
        from: 5,
        skin: "modern"
    });
    init();

    chart1.resize();
    chart1a.resize();
    chart1b.resize();
    chart2.resize();
    chart3.resize();
    chart4.resize();
});

/**
 * 隐藏预览推送面板
 */
$("#viewPush_modal").on("hidden.bs.modal", function () {
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
    $.get("/qywxDaily/getPushInfo", {}, function (r) {
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
let current_status='';
let current_taskDt='';
let currDay=getNowFormatDate();
function setStep(status,taskDate) {
    current_status = status;
    current_taskDt=taskDate;
    //如果状态为todo 且 任务为当前日期，则显示 推送页
    if(status === 'todo'&&current_taskDt==currDay) {
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
    //概要
    if(current_step === 0) {
        $("#prevStepBtn").attr("style", "display:none;");
        $("#nextStepBtn").attr("style", "display:inline-block");
        $("#pushMsgBtn").attr("style", "display:none;");
        $("#step1").attr("style", "display:block;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:none;");
        stepObj.setActive(0);
    }
    //明细
    if(current_step === 1) {
        $("#prevStepBtn").attr("style", "display:inline-block");
        $("#pushMsgBtn").attr("style", "display:none;");
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:block;");
        $("#step3").attr("style", "display:none;");

        if(current_status === 'todo'&&current_taskDt==currDay) {
            $("#nextStepBtn").attr("style", "display:inline-block;");
            getUserStrategyList($("#headId").val());
        }else {
            $("#nextStepBtn").attr("style", "display:none;");
            getUserStrategyList($("#headId").val());
        }
        stepObj.setActive(1);
    }
    //推送
    if(current_step === 2) {
        if(current_status === 'todo'&&current_taskDt==currDay) {
            $.get("/qywxDaily/validUserGroupForQywx", {}, function(r) {
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

/**
 * 确认复选框被选中
 */
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
    $.get("/qywxDaily/submitTask", {
        headId: $("#headId").val(), pushMethod: $("input[name='pushMethod']:checked").val(),
        pushPeriod: $("#pushPeriod").find("option:selected").val(),
        effectDays: $("#effectDays").val()
    }, function (r) {
        if (r.code === 200) {
            $MB.n_success("启动推送成功！");
            setTimeout(function () {
                window.location.href = "/page/qywxDaily/list";
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

function getUserStrategyList(pheadId) {
    let settings = {
        url: '/qywxDaily/getDetailList',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  //页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    headId: pheadId,
                    qywxUserId: $("#qywxUserSelect").find("option:selected").val(),
                    }
            };
        },
        columns: [
            {
                field: 'qywxContractId',
                title: '企业微信客户ID',
                valign: "middle",
                width: 100
            },
            {
                field: 'qywxUserName',
                title: '推送成员',
                width: 300
            },
            {
                field: 'textContent',
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
    $("#userListTable").bootstrapTable('destroy').bootstrapTable(settings);
}

/**
 * 单个用户成长洞察
 * @param user_id
 * @param head_id
 */
function growthInsight(user_id,head_id)
{
    $.get("/qywxDaily/getTaskDt", {headId: head_id}, function (res) {
        var taskDt = res.data;
        if(res.code === 200 && taskDt !== undefined && taskDt !== '') {
            window.open("/page/personInsight?headId="+head_id+"&userId=" + user_id + "&taskDt=" + taskDt, "_blank");
        }else {
            $MB.n_warning("获取任务日期有误！");
        }
    });

}

function getAllQywxUserList(pheadId) {
    $.get("/qywxDaily/getQywxUserList", {headId: pheadId}, function (r) {
        $.each(r.data,function (index,value) {
            $("#qywxUserSelect").append("<option id="+value.qywxUserId+">"+value.qywxUserName+"</option>");

            $("#qywxUserSelect").change(function() {
                //重新加载数据
                getUserStrategyList(pheadId);
            });
        })
    });
}