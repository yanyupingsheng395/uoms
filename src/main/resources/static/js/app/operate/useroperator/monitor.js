String.prototype.endWith=function(str){
    if(str==null||str==""||this.length==0||str.length>this.length)
        return false;
    if(this.substring(this.length-str.length)==str)
        return true;
    else
        return false;
    return true;
};

init();
function init() {
    getSource();
    getBrand();
}

function getBrand() {
    var code = "";
    $.get("/useroperator/getBrand", {}, function (r) {
        $.each(r.data, function (k, v) {
            code += "<option value='"+v["BRAND_ID"]+"'>" + v["BRAND_NAME"] + " </option>";
        });
        $("#brand").html("").html(code);
    });
}

function getSource() {
    var code = "";
    $.get("/useroperator/getSource", {}, function (r) {
        $.each(r.data, function (k, v) {
            code += "<option value='"+v["SOURCE_ID"]+"'>" + v["SOURCE_NAME"] + " </option>";
        });
        $("#source").html("").html(code);
    });
}

var _jm = null;
load_jsmind();
function load_jsmind(){
    var mind = {
        meta:{
            version:'0.2'
        },
        format:'node_array',
        data:[
            {"id":"0", "isroot":true, "topic":"GMV"},
            {"id":"1", "parentid":"0", "topic":"用户数"},
            {"id":"2", "parentid":"0", "topic":"客单价"},
            {"id":"3", "parentid":"1", "topic":"留存率"},
            {"id":"4", "parentid":"2", "topic":"订单数"},
            {"id":"5", "parentid":"2", "topic":"订单价"},
            {"id":"6", "parentid":"5", "topic":"连带率"},
            {"id":"7", "parentid":"5", "topic":"件单价"}
        ]
    };
    var options = {
        container:'jsmind_container',
        editable:true,
        theme:'primary',
        shortcut:{
            handles:{
                test:function(j,e){
                    console.log(j);
                }
            },
            mapping:{
                test:89
            }
        }
    };
    _jm = jsMind.show(options,mind);
    _jm.disable_edit();
}
init_date("startDt", "yyyy", 2,2,2);
// 时间周期选择事件
$("#period").change(function () {
    $("#startDt").val("");
    $("#endDt").val("");
    $("#startDt").datepicker('destroy');
    $("#endDt").datepicker('destroy');
    var period = $(this).find("option:selected").val();
    if(period == 'Y') {
        $("#dateLabel").html("").html("时间：");
        $("#endDtDiv").hide();
        init_date("startDt", "yyyy", 2, 2, 2);
        // $('#startDt').datepicker("setStartDate", dataDt);
    }
    if(period == "M") {
        $("#dateLabel").html("").html("时间：");
        $("#endDtDiv").hide();
        init_date("startDt", "yyyy-mm", 1, 2, 1);
        // $('#startDt').datepicker("setStartDate", dataDt);
    }
    if(period == "D") {
        $("#endDtDiv").show();
        $("#dateLabel").html("").html("开始时间：");
        // var date = new Date();
        // date.setDate(date.getDate() + 1);
        init_date_begin("startDt", "endDt", "yyyy-mm-dd",0,2 ,0);
        // $('#startDt').datepicker("setStartDate", date);
        init_date_end("startDt", "endDt", "yyyy-mm-dd",0,2 ,0);
    }
});

$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
    var startDt = $("#startDt").val();
    if(startDt == "") {
        toastr.warning("请选择时间！");
    }else {
        if(!e.target.href.endWith("#overview")) {
            // 解决相同模板导致ID冲突
            if(!e.relatedTarget.href.endWith("#overview")) {
                var relatedTarget = e.relatedTarget.href.substring(e.relatedTarget.href.lastIndexOf("#"), e.relatedTarget.href.length);
                $(relatedTarget).html("");
            }
            var target = e.target.href.substring(e.target.href.lastIndexOf("#"), e.target.href.length);
            $(target).html($("#template").html());
            var kpiType = "";
            var unit = "";
            if (e.target.href.endWith("#gmv")) {
                kpiType = "gmv";
                unit = "元";
                $("#kpiName").html("").html("GMV");
            }
            if(e.target.href.endWith("#user-num")) {
                kpiType = "userCnt";
                unit = "人";
                $("#kpiName").html("").html("用户数");
            }
            if(e.target.href.endWith("#customer-unit-price")) {
                kpiType = "userPrice";
                unit = "元";
                $("#kpiName").html("").html("客单价");
            }
            if(e.target.href.endWith("#retention")) {
                kpiType = "retention";
                unit = "%";
                $("#kpiName").html("").html("留存率");
            }
            if(e.target.href.endWith("#order-num")) {
                kpiType = "orderCnt";
                unit = "个";
                $("#kpiName").html("").html("订单数");
            }
            if(e.target.href.endWith("#order-price")) {
                kpiType = "orderPrice";
                unit = "元";
                $("#kpiName").html("").html("订单价");
            }
            if(e.target.href.endWith("#joint-rate")) {
                kpiType = "jointRate";
                unit = "";
                $("#kpiName").html("").html("连带率");
            }
            if(e.target.href.endWith("#unit-price")) {
                kpiType = "unitPrice";
                unit = "元";
                $("#kpiName").html("").html("件单价");
            }
            kpiChart(kpiType);
            allChart(kpiType);
            fpChart(kpiType);
            spChart(kpiType);
            getKpiInfo(kpiType, unit);
            getKpiCalInfo(kpiType);
        }
    }
});

/**
 * 获取指标实际值，同环比，去年同期，上一周期值
 */
function getKpiInfo(kpiType, unit) {
    var periodType = $("#period").find("option:selected").val();
    var startDt = $("#startDt").val();
    var endDt = $("#endDt").val();
    $.get("/useroperator/getKpiInfo", {kpiType: kpiType, periodType: periodType, startDt: startDt, endDt: endDt}, function (r) {
        var yny = r.data["yny"];
        var yoy = r.data["yoy"];
        $("#actualVal").html("").html(r.data["kpiVal"] + "<span class='h5'>"+unit+"</span>");
        $("#lastYearPeriod").text(r.data["lastYearKpiVal"] == "--" ? r.data["lastYearKpiVal"] : r.data["lastYearKpiVal"] + unit);
        if(yny == "--") {
            $("#lastYearOnYear").html("").html(yny);
        }else {
            if(yny.indexOf("-") > -1) {
                $("#lastYearOnYear").html("").html("<span style=\"color:red;\"><i class=\"mdi mdi-menu-down mdi-18px\"></i>"+yny+"</span>");
            }else {
                $("#lastYearOnYear").html("").html("<span style=\"color:green;\"><i class=\"mdi mdi-menu-up mdi-18px\"></i>"+yny+"</span>");
            }
        }
        if(yoy == "--") {
            $("#lastPeriodYearOnYear").html("").html(yoy);
        }else {
            if(yny.indexOf("-") > -1) {
                $("#lastPeriodYearOnYear").html("").html("<span style=\"color:red;\"><i class=\"mdi mdi-menu-down mdi-18px\"></i>"+yoy+"</span>");
            }else {
                $("#lastPeriodYearOnYear").html("").html("<span style=\"color:green;\"><i class=\"mdi mdi-menu-up mdi-18px\"></i>"+yoy+"</span>");
            }
        }
        $("#lastPeriod").text(r.data["yoy"] == "--" ? r.data["yoy"] : r.data["yoy"] + unit);
    });
}


function kpiChart(kpiType) {
    var periodType = $("#period").find("option:selected").val();
    var startDt = $("#startDt").val();
    var endDt = $("#endDt").val();
    $.get("/useroperator/getKpiChart", {kpiType: kpiType, periodType: periodType, startDt: startDt, endDt: endDt}, function (r) {
        var seriesData = [];
        var o1 = new Object();
        o1.name = "本周期指标";
        o1.data = r.data['current'];
        o1.type = 'line';
        seriesData.push(o1);
        var o2 = new Object();
        o2.name = "去年同期指标";
        o2.data = r.data['last'];
        o2.type = 'line';
        seriesData.push(o2);
        var o3 = new Object();
        o3.name = "本周期均线";
        o3.data = r.data['currentAvg'];
        o3.type = 'line';
        o3.smooth = false;
        o3.itemStyle = {
            normal:{
                lineStyle:{
                    width:2,
                    type:'dotted'  //'dotted'虚线 'solid'实线
                }
            }
        };
        seriesData.push(o3);
        var o4 = new Object();
        o4.name = "去年同期均线";
        o4.data = r.data['lastAvg'];
        o4.type = 'line';
        o4.smooth = false;
        o4.itemStyle = {
            normal:{
                lineStyle:{
                    width:2,
                    type:'dotted'  //'dotted'虚线 'solid'实线
                }
            }
        };
        seriesData.push(o4);
        var option = getOption(["本周期指标", "去年同期指标", "本周期均线", "去年同期均线"], r.data['xData'], "日期", "指标值",seriesData);
        option.grid = {left:'12%'};
        var c = echarts.init(document.getElementById("kpiChart"), 'macarons');
        c.setOption(option);
    });
}

function allChart(kpiType) {
    var periodType = $("#period").find("option:selected").val();
    var startDt = $("#startDt").val();
    var endDt = $("#endDt").val();
    $.get("/useroperator/getSpAndFpKpi", {kpiType: kpiType, periodType: periodType, startDt: startDt, endDt: endDt}, function (r) {
        console.log(r)
        var seriesData = [];
        var o1 = new Object();
        o1.name = "总体";
        o1.data = r.data['kpiVal'];
        o1.type = 'line';
        seriesData.push(o1);
        var o2 = new Object();
        o2.name = "首购用户";
        o2.data = r.data['fpKpiVal'];
        o2.type = 'line';
        seriesData.push(o2);

        var o3 = new Object();
        o3.name = "非首购用户";
        o3.data = r.data['spKpiVal'];
        o3.type = 'line';
        seriesData.push(o3);
        var option = getOption(["总体", "首购用户", "非首购用户"], r.data['xData'], "日期", "指标值",seriesData);
        option.grid = {left:'12%'};
        var c = echarts.init(document.getElementById("allChart"), 'macarons');
        c.setOption(option);
    });
}

function fpChart(kpiType) {
    var periodType = $("#period").find("option:selected").val();
    var startDt = $("#startDt").val();
    var endDt = $("#endDt").val();
    $.get("/useroperator/getSpOrFpKpiVal", {kpiType:kpiType, isFp: 'Y',periodType: periodType, startDt: startDt, endDt: endDt}, function (r) {
        var seriesData = [];
        var o1 = new Object();
        o1.name = "本周期指标";
        o1.data = r.data['kpiVal'];
        o1.type = 'line';
        seriesData.push(o1);
        var o2 = new Object();
        o2.name = "去年同期指标";
        o2.data = r.data['lastKpiVal'];
        o2.type = 'line';
        seriesData.push(o2);

        var o3 = new Object();
        o3.name = "本周期均值";
        o3.data = r.data['avgKpiVal'];
        o3.type = 'line';
        o3.smooth = false;
        o3.itemStyle = {
            normal:{
                lineStyle:{
                    width:2,
                    type:'dotted'  //'dotted'虚线 'solid'实线
                }
            }
        };
        seriesData.push(o3);
        var o4 = new Object();
        o4.name = "去年同期均值";
        o4.data = r.data['avgLastKpiVal'];
        o4.type = 'line';
        o4.smooth = false;
        o4.itemStyle = {
            normal:{
                lineStyle:{
                    width:2,
                    type:'dotted'  //'dotted'虚线 'solid'实线
                }
            }
        };
        seriesData.push(o4);
        var option = getOption(["本周期指标", "去年同期指标", "本周期均值", "去年同期均值"], r.data['xData'], "日期", "指标值",seriesData);
        option.title = {
            text: '新客指标报告期变化趋势图',
            x:'center',
            y: 'bottom',
            textStyle:{
                //文字颜色
                color:'#000',
                //字体风格,'normal','italic','oblique'
                fontStyle:'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight:'normal',
                //字体系列
                fontFamily:'sans-serif',
                //字体大小
                fontSize:12
            }
        };
        option.grid = {left:'12%'};
        var c = echarts.init(document.getElementById("fpChart"), 'macarons');
        c.setOption(option);
    });
}

function spChart(kpiType) {
    var periodType = $("#period").find("option:selected").val();
    var startDt = $("#startDt").val();
    var endDt = $("#endDt").val();
    $.get("/useroperator/getSpOrFpKpiVal", {kpiType:kpiType, isFp: 'N',periodType: periodType, startDt: startDt, endDt: endDt}, function (r) {
        var seriesData = [];
        var o1 = new Object();
        o1.name = "本周期指标";
        o1.data = r.data['kpiVal'];
        o1.type = 'line';
        seriesData.push(o1);
        var o2 = new Object();
        o2.name = "去年同期指标";
        o2.data = r.data['lastKpiVal'];
        o2.type = 'line';
        seriesData.push(o2);

        var o3 = new Object();
        o3.name = "本周期均值";
        o3.data = r.data['avgKpiVal'];
        o3.type = 'line';
        o3.smooth = false;
        o3.itemStyle = {
            normal:{
                lineStyle:{
                    width:2,
                    type:'dotted'  //'dotted'虚线 'solid'实线
                }
            }
        };
        seriesData.push(o3);
        var o4 = new Object();
        o4.name = "去年同期均值";
        o4.data = r.data['avgLastKpiVal'];
        o4.type = 'line';
        o4.smooth = false;
        o4.itemStyle = {
            normal:{
                lineStyle:{
                    width:2,
                    type:'dotted'  //'dotted'虚线 'solid'实线
                }
            }
        };
        seriesData.push(o4);
        var option = getOption(["本周期指标", "去年同期指标", "本周期均值", "去年同期均值"], r.data['xData'], "日期", "指标值",seriesData);
        option.title = {
            text: '老客指标报告期变化趋势图',
            x:'center',
            y: 'bottom',
            textStyle:{
                //文字颜色
                color:'#000',
                //字体风格,'normal','italic','oblique'
                fontStyle:'normal',
                //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                fontWeight:'normal',
                //字体系列
                fontFamily:'sans-serif',
                //字体大小
                fontSize:12
            }
        };
        option.grid = {left:'12%'};
        var c = echarts.init(document.getElementById("spChart"), 'macarons');
        c.setOption(option);
    });
}

function getKpiCalInfo(kpiType) {
    var periodType = $("#period").find("option:selected").val();
    var startDt = $("#startDt").val();
    var endDt = $("#endDt").val();
    $.get("/useroperator/getKpiCalInfo", {kpiType:kpiType, isFp: 'N',periodType: periodType, startDt: startDt, endDt: endDt}, function (r) {
        $("#fpAbs").html("").html(r.data['fpAbs']);
        $("#fpContributeRate").html("").html(r.data['fpContributeRate']);
        $("#fpHb").html("").html(r.data['fpHb']);
        $("#fpTb").html("").html(r.data['fpTb']);
        $("#spAbs").html("").html(r.data['spAbs']);
        $("#spContributeRate").html("").html(r.data['spContributeRate']);
        $("#spHb").html("").html(r.data['spHb']);
        $("#spTb").html("").html(r.data['spTb']);
    });
}