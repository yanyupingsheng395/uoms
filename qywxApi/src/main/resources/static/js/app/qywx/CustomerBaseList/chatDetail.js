var chartX_1=["周一","周二","周三","周四","周五","周六","周日"];
var chaty_1=["10","3","34","23","11","1","6"];
var xdata_2=["周一","周二","周三","周四","周五","周六","周日"];
var ydata1_2=["10","3","34","23","11","1","6"];
var ydata2_2=["1","0","2","0","0","3","4"];
var xdata_3=["周一","周二","周三","周四","周五","周六","周日"];
var ydata1_3=["10","3","34","23","11","1","6"];
var ydata2_3=["1","0","2","0","0","3","4"];
$(function () {
    chart1(chartX_1,chaty_1);
    chart2(xdata_2,ydata1_2,ydata2_2);
    chart3(xdata_3,ydata1_3,ydata2_3);
    getChatBaseDetail();
});

function getChatBaseDetail() {
    $.get( "/qywxCustomer/getChatBaseDetail?chatId="+chatId, {}, function (r) {
        if (r.code === 200) {
            var redata=r.data;
            document.getElementById("groupName").innerHTML="该群创建于（"+redata.createTime+"),群主是（"+redata.groupName+"),群名称是（"+redata.groupName+")";
        }
    } );
}

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
                name: '日期',
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
                max: 250,
                interval: 50,
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
            selected: {'新增人数':true, '退群人数':true}
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
            type: 'value',
            splitLine:{show: false},
            splitArea : {show : false}
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
        var select_key = Object.keys(params.selected);
        var select_value = Object.values(params.selected);

        var current_name = params.name;
     /*   if(current_name === '新增人数') {
            if(select_value[select_key.indexOf(current_name)]) {
                console.log(option.legend)
                option.legend[0].selected['退群人数'] = false;
            }else {
                option.legend[0].selected['退群人数'] = true;
            }
        }
        if(current_name === '退群人数') {
            if(select_value[select_key.indexOf(current_name)]) {
                option.legend[0].selected['新增人数'] = false;
            }else {
                option.legend[0].selected['新增人数'] = true;
            }
        }*/
        this.setOption(option)
    });
    chart.setOption(option);
}


function chart3(xdata, ydata1, ydata2) {
    var option = {
        title: {
            text: '不同添加方式下的转化人数及转化率',
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
            data: ['发消息成员数', '发消息数'],
            selected: {'发消息成员数':true, '发消息数':true}
        },
        xAxis:[ {
            name: '日期',
            type: 'category',
            data: xdata,
            splitLine:{show: false},
            splitArea : {show : false}
        }],
        yAxis:  [
            {
                type: 'value',
                name: '人数',
                min: 0,
                max: 250,
                interval: 50,
                axisLabel: {
                    formatter: '{value}'
                },
                splitLine:{show: false},
                splitArea : {show : false}
            },
            {
                type: 'value',
                name: '消息数',
                min: 0,
                max: 25,
                interval: 5,
                axisLabel: {
                    formatter: '{value}%'
                },
                splitLine:{show: false},
                splitArea : {show : false}
            }
        ],
        series: [{
            name: '发消息成员数',
            data: ydata1,
            type: 'line'
        },{
            name: '发消息数',
            data: ydata2,
            type: 'line'
        }]
    };
    var chart = echarts.init(document.getElementById("chart3"), 'macarons');
    chart.on('legendselectchanged', function(params) {
        var option = this.getOption();
        var select_key = Object.keys(params.selected);
        var select_value = Object.values(params.selected);

        var current_name = params.name;
      /*  if(current_name === '发消息成员数') {
            if(select_value[select_key.indexOf(current_name)]) {
                console.log(option.legend)
                option.legend[0].selected['发消息数'] = false;
            }else {
                option.legend[0].selected['发消息数'] = true;
            }
        }
        if(current_name === '发消息数') {
            if(select_value[select_key.indexOf(current_name)]) {
                option.legend[0].selected['发消息成员数'] = false;
            }else {
                option.legend[0].selected['发消息成员数'] = true;
            }
        }*/
        this.setOption(option)
    });
    chart.setOption(option);
}

