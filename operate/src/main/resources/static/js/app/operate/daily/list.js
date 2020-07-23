$(function () {
    var errmsg=$("#errormsg").val();
    if(null!=errmsg&&errmsg!='')
    {
        $MB.n_warning(errmsg);
    }
    init_date('touchDt', 'yyyy-mm-dd', 0, 2, 0);
    $("#touchDt").datepicker('setEndDate', new Date());
    initTable();
});

function initTable() {
    var settings = {
        url: '/daily/getPageList',
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
                param: {touchDt: $("#touchDt").val()}
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'headId',
            title: 'ID',
            visible: false
        }, {
            field: 'touchDtStr',
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
    $MB.initTable('dailyTable', settings);
}

function resetDaily() {
    $("#touchDt").val("");
    $MB.refreshTable("dailyTable");
}

$("#btn_query").click(function () {
    $MB.refreshTable("dailyTable");
});

$("#btn_catch").click(function () {
    var selected = $("#dailyTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选要查看效果的任务！');
        return;
    }
    let status = selected[0].status;
    if (status != 'done' && status != 'finished') {
        $MB.n_warning("只有已执行，已结束状态可查看任务效果！");
        return;
    }
    var headId = selected[0].headId;
    window.location.href = "/page/daily/effect?id=" + headId;
});

/**
 * 预览执行按钮
 */
$("#btn_insight").click(function () {
    var selected = $("#dailyTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要预览执行的任务！');
        return;
    };

    let headId = selected[0].headId;
    let status = selected[0].status;
    let touchDt = selected[0].touchDtStr;
    let totalNum = selected[0].totalNum;
    $("#headId").val(headId);

    if(totalNum==null || totalNum=='' ||totalNum==='0' || totalNum==0)
    {
        $MB.n_warning('当前任务没有建议推送的用户！');
        return;
    }

    //打开遮罩层
    $MB.loadingDesc('show', '策略生成中，请稍候...');
    //获取预览数据
    $.get("/daily/getUserStatsData", {headId: headId}, function (r) {
        if(r.code == 200) {
            let chartTitle="<i class='mdi mdi-alert-circle-outline'></i>今天是"+r.data.touchDt+",根据用户成长引擎的计算,有"+r.data.userNum+"个用户到达成长的节点，需要个性化推送培养。";
            $("#overviewInfo").html('').append(chartTitle);

            //本日用户成长目标
            initUserTargetChart(r.data);

            //本日用户差异群组
            initUserDiffChart(r.data);

            //本日用户推送策略
            initUserStrategy(r.data);

            //初始化步骤组件
            setStep(status,touchDt);

            //打开modal
            $("#userStats_modal").modal('show');
        }else {
            //提示错误信息
            $MB.n_danger(r.msg);
        }
        $MB.loadingDesc('hide');
    });
});

function gotoConfig() {
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: "去完成配置？"
    }, function () {
        location.href = "/page/dailyconfig";
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

function initUserTargetChart(data) {
    chart1 = echarts.init(document.getElementById("chart1"), 'macarons');
    let option = getChart1Option(data.spuList,data.spuCountList,"成长目标的类目分布");
    chart1.setOption( option );

    //为chart1增加点击事件
    chart1.on('click', function(params) {
        //刷新右侧产品条图
        refreshChart2(params.name);
    });

    chart2 = echarts.init(document.getElementById("chart2"), 'macarons');
    option = getChart2Option(data.prodList,data.prodCountList,"成长目标的商品分布");
    chart2.setOption( option );

    chart1a = echarts.init(document.getElementById("chart1a"), 'macarons');
    let optionChart1a = getChart1aOption(data,"成长目标的类型分布(1)");
    chart1a.setOption( optionChart1a );

    chart1b = echarts.init(document.getElementById("chart1b"), 'macarons');
    let optionChart1b = getChart1bOption(data,"成长目标的类型分布(2)");
    chart1b.setOption( optionChart1b );
}

/**
 * 刷新按产品展示的条形图
 */
function refreshChart2(spuName)
{
    $.get("/daily/getProdCountBySpu", {headId:  $("#headId").val(),spuName:spuName}, function (r) {
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
    $.get("/daily/getMatrixData", {headId:  $("#headId").val(),userValue:userValue}, function (r) {
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
