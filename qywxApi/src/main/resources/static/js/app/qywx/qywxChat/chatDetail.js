var chartX_1=[];//日期
var chaty_1=[];//群总人数
var xdata_2=[];//日期
var ydata1_2=[];//新增人数
var ydata2_2=[];//退群人数
$(function () {
    //获取折线图数据并展示
    getDetailData();
    //获取群名称，群主等相关信息
    getChatBaseDetail();
});

/**
 * 获取群人数的详细数据
 *
 */
function getDetailData() {
    $.post("/qywxCustomer/getDetailData",{chatId:chatId},function (r) {
        if(r.code===200){
            let data=r.data;
            for (let i=0;i<data.length;i++){
                chartX_1.push(data[i].dayWid);
                chaty_1.push(data[i].groupNumber);
                xdata_2.push(data[i].dayWid);
                ydata1_2.push(data[i].addNumber);
                ydata2_2.push(data[i].outNumber);
                chart1(chartX_1,chaty_1);
                chart2(xdata_2,ydata1_2,ydata2_2);
            }
            $("#groupNumber").html(data[data.length-1].groupNumber);
            $("#addNumber").html(data[data.length-1].addNumber);
            $("#outNumber").html(data[data.length-1].outNumber);
        }
    })
}

//获取群名称，群创建时间
function getChatBaseDetail() {
    $.get( "/qywxCustomer/getChatBaseDetail?chatId="+chatId, {}, function (r) {
        if (r.code === 200) {
            var redata=r.data;
            document.getElementById("groupName").innerHTML="该群创建于："+redata.createTime+"&nbsp;&nbsp;&nbsp;&nbsp;群主："+redata.groupName+"&nbsp;&nbsp;&nbsp;&nbsp;群名称："+redata.groupName;
        }
    } );
}
//按条件查询
function gradeChange(){
    var objS = document.getElementById("datePeriod");
    var val = objS.options[objS.selectedIndex].value;
   if(val=="D"){
        $("#st_li").show();
        $("#ed_li").show();
   }else{
       $("#st_li").hide();
       $("#ed_li").hide();
   }
}

function chart1(chartX1, chaty1) {
    var option = {
        title: {
            text: '群人数变化柱图',
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
                type: 'cross',
                crossStyle: {
                    color: '#999'
                }
            }
        },
        legend: {
            data: []
        },
        grid: {
            top: '30%'
        },
        xAxis: [
            {
                name: '星期',
                type: 'category',
                data: chartX1,
                axisPointer: {
                    type: 'shadow'
                },
                splitLine:{show: false},
                splitArea : {show : false}
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '人数',
                min: 0,
                max: 2000,
                interval: 400,
                axisLabel: {
                    formatter: '{value}'
                },
                splitLine:{show: false},
                splitArea : {show : false}
            }
        ],
        series: [
            {
                name: '群人数',
                type: 'bar',
                data: chaty1
            }]
    };
    var chart = echarts.init(document.getElementById("chart1"), 'macarons');
    chart.setOption(option);
}


function chart2(xdata, ydata1, ydata2) {
    var option = {
        title: {
            text: '群人数变化曲线图',
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
            formatter: '{b} : {c}' + '人',
            axisPointer: {
                animation: false
            }
        },
        legend: {
            data: ['新增人数', '退群人数'],
        },
        xAxis: {
            name: '日期',
            type: 'category',
            data: xdata,
            splitLine:{show: false},
            splitArea : {show : false}
        },
        yAxis: {
            name: '人数',
            type: 'value'
        },
        series: [{
            name: '新增人数',
            data: ydata1,
            type: 'line'
        },{
            name: '退群人数',
            data: ydata2,
            type: 'line'
        }]
    };
    var chart = echarts.init(document.getElementById("chart2"), 'macarons');
    chart.on('legendselectchanged', function(params) {
        var option = this.getOption();
        this.setOption(option)
    });
    chart.setOption(option);
}
