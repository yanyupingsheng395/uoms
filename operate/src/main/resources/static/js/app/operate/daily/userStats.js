/**
 * 获取数据
 */

var chart1;
function getUserStatsData() {
    var selected = $("#dailyTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要查看的任务！');
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



