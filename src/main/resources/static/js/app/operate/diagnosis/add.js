/**
 * 乘法：序号 name
 * 加法：序号 name=KPI名称/维度名称
 * 仅过滤：序号 KPI名称/过滤
 */
// 流程图对象
var jm = null;
var diagId = 0;
toastr.options = {
    "progressBar": true,
    "positionClass": "toast-top-center",
    "preventDuplicates": true,
    "timeOut": 1500,
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};
init_date_begin("beginDt", "endDt","yyyy", 2,2,2);
init_date_end("beginDt", "endDt","yyyy", 2,2,2);

// 周期切换时间控件
$("#periodType").change(function () {
    var periodType = $("#periodType option:selected").val();
    $("#beginDt").remove();
    $("#endDt").remove();

    $("#startDate").append("<input class=\"form-control js-datepicker m-b-10\" type=\"text\" id=\"beginDt\" name=\"beginDt\"/>");
    $("#endDate").append("<input class=\"form-control js-datepicker m-b-10\" type=\"text\" id=\"endDt\" name=\"endDt\"/>");
    if(periodType == "Y") {
        init_date_begin("beginDt", "endDt", "yyyy", 2,2,2);
        init_date_end("beginDt", "endDt", "yyyy", 2,2,2);
    }else if(periodType == "M") {
        init_date_begin("beginDt", "endDt", "yyyy-mm", 1,2,1);
        init_date_end("beginDt", "endDt", "yyyy-mm", 1,2,1);
    }
});

function collapse(dom) {
    if($(dom).parents(".card").eq(0).find(".card-body").attr("style") == "display:none;") {
        $(dom).parents(".card").eq(0).find(".card-body").attr("style", "display:block;");
        $(dom).find("i").removeClass("mdi-menu-down").addClass("mdi-menu-up");

        chartResize(dom);
    }else {
        $(dom).parents(".card").eq(0).find(".card-body").attr("style", "display:none;");
        $(dom).find("i").removeClass("mdi-menu-up").addClass("mdi-menu-down");
    }
}

// 重置echart的大小
function chartResize(dom) {
    var chartId = $(dom).parents(".card").eq(0).find(".card-body").children("div").attr("id");
    window[""+chartId+""].resize();
}

// 屏蔽浏览器的右键事件
function doNothing(){
    window.event.returnValue=false;
    return false;
}

// 拆分方式
$("#op2").change(function() {
    console.log(jm.get_selected_node());
    var kpiCode = jm.get_selected_node().data.KPI_CODE;
    var option2Val = $(this).find("option:selected").val();
    if(option2Val == 0) { // 加法过滤
        getDimension(); // 获取维度
        $("#add_condition").attr("style", "display:block;");
        $("#plus_condition").attr("style", "display:none;");
    }
    if(option2Val == 1) { // 乘法过滤
        getFormula(kpiCode);
        getDimension(); // 获取维度
        $("#plus_condition").attr("style", "display:block;");
        $("#add_condition").attr("style", "display:none;");
    }
    if(option2Val == 2 || option2Val == "") { // 仅过滤
        getDimension();
        $("#plus_condition").attr("style", "display:none;");
        $("#add_condition").attr("style", "display:none;");
    }
});

// 提交数据到列表中
function beforeNext(dom) {
    $.post("/diag/add", $("#formTable").serialize(), function(r) {
        $("#period_type").html("").html($("#periodType option:selected").text());
        $("#date_area").html("").html($("#beginDt").val() + "&nbsp;到&nbsp;" + $("#endDt").val());

        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:block;");
        $(dom).remove();
        diagId = r.data;
        createRootNode();
    });
}

// 加入诊断
function addCondition() {
    var methodId = $("#op2 option:selected").val();
    if(methodId == "0") { // 加法
        condition0();
        // 隐藏模态框
        $("#nodeAddModal").modal('hide');
    }else if(methodId == "1") { // 乘法
        if($("#op3").find("option:selected").val() == null) {
            if($("#op3").find("option").length == 0) {
                toastr.warning("该指标无可再拆分的乘法公式，请选择别的拆分方式！");
            }else {
                toastr.warning("请选择拆分公式！");
            }
        } else {
            condition1();

        }
        // 隐藏模态框
    }else if(methodId == "2") { // 仅过滤
        condition2();
    }else if(methodId == ""){
        toastr.warning("请选择诊断方式！");
    }
}

function resetTableData() {
    $("#dataTable").html("").html("<tr><td><i class=\"mdi mdi-alert-circle-outline\"></i>暂无数据！</td></tr>");
}
function redisSaveConditions() {
    var dataArray = new Array();
    $("#dataTable").find("tr").each(function () {
        var code = $(this).find("td:eq(0)").find("input").val();
        var text = $(this).find("td:eq(0)").text();
        dataArray.push(code + ":" + text);
    });
    $.post("/diagcondition/redisCreate", {data: dataArray, diagId: diagId, nodeId: jm.get_selected_node().id}, function (r) {

    });
}

$("#op5").change(function() {
    var code = $(this).find("option:selected").val();
    getValueList(code);
});

// 根据维度value获取值类型
function getValueList(code) {
    $.get("/progress/getDiagDimValueList", {code: code}, function(r) {
        var code = "";
        $.each(r.data, function (k, v) {
            code += "<option value='" + k + "'>" + v + "</option>";
        });
        $("#op6").html("").html(code);
        $("#op6").selectpicker('refresh');
    });
}

// 保存所有节点信息
function saveNodes() {
    var nodeList = [];
    var rootNode = jm.get_root();
    nodeList.push(rootNode);
    iterationNode(rootNode, nodeList);
    var data = new Array();
    for(var i=0; i<nodeList.length; i++) {
        var tmp = nodeList[i];
        var obj = new Object();
        var parentId = getNodeParentId(tmp);
        obj["diagId"] = diagId;
        obj["nodeId"] = tmp.id;
        obj["nodeName"] = tmp.topic;
        obj["parentId"] = parentId;
        obj["kpiCode"] = tmp.data.KPI_CODE;
        obj["kpiName"] = tmp.data.KPI_NAME;
        obj["kpiLevelId"] = tmp.data.KPI_LEVEL_ID;
        obj["alarmFlag"] = "n";
        obj["conditions"] = tmp.data.CONDITION;
        data.push(obj);
    }

    $.ajax({
        url: "/diagdetail/save",
        type: "post",
        data: {
            json: JSON.stringify(data)
        },
        dataType : 'json',
        success: function (r) {
            console.log(r);
            if(r.code == 200) {
                toastr.success(r.msg);
            }else {
                toastr.error(r.msg);
            }
            setTimeout(function(){
                window.location.href = "/page/diagnosis/list";
            },1500)
        }
    });
}

function getNodeParentId(tmp) {
    if(tmp.parent != null) {return tmp.parent.id} else {return null};
}

// 遍历流程图节点信息
function iterationNode(node, nodeList) {
    var nodes = node.children;
    if(nodes == null) {
        return;
    }else {
        for(var i=0; i<nodes.length; i++) {
            nodeList.push(nodes[i]);
            iterationNode(nodes[i], nodeList)
        }
    }
}

function modalBefore() {
    var e = document.getElementById("operateBtns");
    e.style.display = "none";

    // 弹窗中获取当前指标
    var selectedNode = jm.get_selected_node();
    $("#currentNodeId").val(selectedNode.id);
    var topic = selectedNode.topic;
    if(topic.indexOf(" ") > -1) {
        topic = topic.substring(topic.lastIndexOf(" "), topic.length);
    }
    $("#currentNode").val(topic);

    // 设置选中为空，初始化选择框
    $("#op2").selectpicker('val', "");
    $("#op2").selectpicker('refresh');
    $("#plus_condition").attr("style", "display:none;");
    $("#add_condition").attr("style", "display:none;");

    resetTableData();
    // 获取父节点条件数据
    var code = getParentCondition();
    if(code == "") {
        $("#dataTable").html("").html("<tr><td><i class=\"mdi mdi-alert-circle-outline\"></i>暂无数据！</td></tr>");
    }else {
        $("#dataTable").html("").html(code);
    }

    conditionVal = new Array();
}

function alarmFlag(dom) {
    jm.enable_edit();
    if($(dom).text().trim() == "标记") {
        jm.get_selected_node().data.ALARM_FLAG = true;
        $(dom).html("").html("<span class='h5'><i class=\"mdi mdi-close\"></i>&nbsp;取消</span>");
        jm.set_node_color(jm.get_selected_node().id, 'red', '');
    }else {
        jm.get_selected_node().data.ALARM_FLAG = false;
        $(dom).html("").html("<span class='h5'><i class=\"mdi mdi-check\"></i>&nbsp;标记</span>");
        jm.set_node_color(jm.get_selected_node().id, 'rgb(26, 188, 156)', '');
    }
    jm.disable_edit();
}

function getParentCondition() {
    var array = jm.get_selected_node().data.CONDITION;
    var code = "";
    $.each(array, function (k, v) {
        var tmp = v.split(":");
        code += "<tr><td style='text-align: left;'>" + tmp[1].trim() + ":";
        code += tmp[2].trim();
        code += "<input type='hidden' name='condition' value='"+tmp[0].trim()+"'><input name='inheritFlag' type='hidden' value='Y'></td><td></td></tr>";
    });
    return code;
}

// 获取加法维度
function getDimension() {
    $.get("/progress/getDiagDimList", null, function (r) {
        var code = "";
        $.each(r.data, function(k, v) {
            code += "<option value='" + k + "'> " + v + " </option>";
        });
        $("#op4").html("").html(code);
        $("#op4").selectpicker('refresh');

        $("#op5").html("").html("<option value=''>请选择</option>" + code);
        $("#op5").selectpicker('refresh');

        getValueList($("#op5").find("option:selected").val());
    });
}

var conditionVal = new Array();

// 增加条件
function selectedCondition() {
    if($("#op5").find("option:selected").val() == "" || $("#op6").find("option:selected").val() == "") {
        toastr.warning("未选择条件或值！");
    }else {
        var arr = $("#op6").selectpicker('val');
        var condition = $("#op5").find("option:selected").text();
        var val = $("#op5").find("option:selected").val();
        var code = "<tr><td style='text-align: left;'>" + condition.trim() + ":";
        for(var i=0;i<arr.length;i++) {
            var t = $("#op6").find("option[value='"+arr[i]+"']").text();
            code += t + ",";
        }
        code = code.substring(0, code.length - 1);
        code += "<input name='condition' type='hidden' value='" + val + "'/><input name='inheritFlag' value='N' type='hidden'/></td><td><a style='color:#000000;cursor: pointer;' onclick='removeConditionList(\""+val+"\", this)'><i class='mdi mdi-close'></i></a></td></tr>";

        if($("#dataTable").find("tr td").text().indexOf("暂无数据") > -1) {
            $("#dataTable").html("").html(code);
        }else {
            $("#dataTable").append(code);
        }

        // 列表中删除
        $("#op5").find("option[value='"+val+"']").remove();
        $("#op5").selectpicker('refresh');

        $("#op6").html("");
        $("#op6").selectpicker('refresh');

        getValueList($("#op5").find("option:selected").val());
        conditionVal.push(val);
    }
}

// 获取条件
function filterCondition() {
    var dataArray = new Array();
    $("#dataTable").find("tr").each(function () {
        var condition = $(this).find("td:eq(0)").find("input[name='condition']").val();
        var inheritFlag = $(this).find("td:eq(0)").find("input[name='inheritFlag']").val();
        var text = $(this).find("td:eq(0)").text();
        if(condition != undefined && inheritFlag != undefined) {
            dataArray.push(condition + ":" + text + ":" + inheritFlag);
        }
    });
    return dataArray;
}

// 条件详情
function modalDetailBefore() {
    var e = document.getElementById("operateBtns");
    e.style.display = "none";
    var array = jm.get_selected_node().data.CONDITION;
    $("#kpiNameDetail").html("").html(jm.get_selected_node().data.KPI_NAME);
    var code = "";
    $.each(array, function (k, v) {
        var tmp = v.split(":");
        code += "<tr><td style='text-align: left;'>" + tmp[1].trim() + ":";
        if(tmp[3] == "N") {
            code += tmp[2].trim();
        }else if(tmp[3] == "Y"){
            code += tmp[2].trim() + "&nbsp;(继承至父节点)"
        }
        code += "</td></tr>";
    });
    if(code == "") {
        $("#dataTableDetail").html("").html("<tr><td><i class=\"mdi mdi-alert-circle-outline\"></i>暂无数据！</td></tr>");
    }else {
        $("#dataTableDetail").html("").html(code);
    }
}

// 加法条件
function condition0() {
    var levelId = getKpiLevelId();
    var nodeName = levelId + " <i class='mdi mdi-key-plus'></i> " + jm.get_selected_node().data.KPI_NAME + "(" + $("#op4 option:selected").text().trim() + ")";
    var nodeId = createNode(nodeName, levelId, jm.get_selected_node().data.KPI_CODE, jm.get_selected_node().data.KPI_NAME, true);
    chart0(levelId, nodeName, $("#op4 option:selected").text().trim());

    endNode(nodeId);
}


function endNode(nodeId) {
    jm.enable_edit();
    jm.set_node_color(nodeId, '#8b95a5', '');
    jm.disable_edit();
}

//
//saveNode("-1");

// 单步保存节点信息
function saveNode(nodeid) {
    // 加载动画
    lightyear.loading("show");
    var nodeList = [];
    var currentNode = jm.get_node(nodeid);
    nodeList.push(currentNode);

    var data = new Array();
    for(var i=0; i<nodeList.length; i++) {
        var tmp = nodeList[i];
        var obj = new Object();
        var parentId = getNodeParentId(tmp);
        obj["diagId"] = diagId;
        obj["nodeId"] = tmp.id;
        obj["nodeName"] = tmp.topic;
        obj["parentId"] = parentId;
        obj["kpiCode"] = tmp.data.KPI_CODE;
        obj["kpiName"] = tmp.data.KPI_NAME;
        obj["kpiLevelId"] = tmp.data.KPI_LEVEL_ID;
        obj["alarmFlag"] = "n";
        obj["conditions"] = tmp.data.CONDITION;
        data.push(obj);
    }

    $.ajax({
        url: "/diagdetail/save",
        type: "post",
        data: {
            json: JSON.stringify(data)
        },
        dataType : 'json',
        success: function (r) {
            if(r.code != 200) {
                toastr.error(r.msg);
            }
            lightyear.loading("hide");
        }
    });
}

// 取起始范围的随机数
function getRandom (m,n){
    var num = Math.floor(Math.random()*(m - n) + n);
    return num;
}

// 加法chart 数据
function getXaxis() {
    var x = new Array();
    var start = $("#beginDt").val();
    var end = $("#endDt").val();

    if($("#periodType option:selected").val() == "M") {
        var startTime = getDate(start);
        var endTime = getDate(end);
        while((endTime.getTime()-startTime.getTime())>=0){
            var year = startTime.getFullYear();
            var month = (startTime.getMonth() + 1).toString().length==1?"0"+(startTime.getMonth() + 1).toString():startTime.getMonth() + 1;
            x.push(year+"-"+month);
            startTime.setMonth(startTime.getMonth()+1);
        }
    }else {
        var startTime = new Date(start);
        var endTime = new Date(end);
        while((endTime.getTime()-startTime.getTime())>=0){
            var year = startTime.getFullYear();
            x.push(year);
            startTime.setFullYear(startTime.getFullYear()+1);
        }
    }
    return x;
}
// 月份
function getDate(datestr){
    var temp = datestr.split("-");
    var date = new Date(temp[0],temp[1]);
    return date;
}

function chart_condition1(id, name, desc1, desc2) {
    createChartDom(id, name);
    var xdata = getXaxis();
    var ydata1 = new Array();
    var ydata2 = new Array();
    for(var i=0; i<xdata.length; i++) {
        ydata1.push(getRandom(1000, 10000));
        ydata2.push(getRandom(1000, 10000));
    }
    var option1 = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                lineStyle: {
                    color: '#ddd'
                }
            },
            backgroundColor: 'rgba(255,255,255,1)',
            padding: [5, 10],
            textStyle: {
                color: '#7588E4',
            },
            extraCssText: 'box-shadow: 0 0 5px rgba(0,0,0,0.3)'
        },
        legend: {
            right: 20,
            orient: 'vertical',
            data: [desc1, desc2]
        },
        xAxis: {
            type: 'category',
            data: xdata,
            boundaryGap: false,
            splitLine: {
                show: true,
                interval: 'auto',
                lineStyle: {
                    color: ['#D4DFF5']
                }
            },
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#609ee9'
                }
            },
            axisLabel: {
                margin: 10,
                textStyle: {
                    fontSize: 14
                }
            }
        },
        yAxis: {
            type: 'value',
            splitLine: {
                lineStyle: {
                    color: ['#D4DFF5']
                }
            },
            axisTick: {
                show: false
            },
            axisLine: {
                lineStyle: {
                    color: '#609ee9'
                }
            },
            axisLabel: {
                margin: 0,
                textStyle: {
                    fontSize: 14
                }
            }
        },
        series: [{
            name: desc1,
            type: 'line',
            smooth: true,
            showSymbol: false,
            symbol: 'circle',
            symbolSize: 6,
            data: ydata1,
            areaStyle: {
                normal: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                        offset: 0,
                        color: 'rgba(199, 237, 250,0.5)'
                    }, {
                        offset: 1,
                        color: 'rgba(199, 237, 250,0.2)'
                    }], false)
                }
            },
            itemStyle: {
                normal: {
                    color: '#f7b851'
                }
            },
            lineStyle: {
                normal: {
                    width: 1
                }
            }
        }, {
            name: desc2,
            type: 'line',
            smooth: true,
            showSymbol: false,
            symbol: 'circle',
            symbolSize: 6,
            data: ydata2,
            areaStyle: {
                normal: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                        offset: 0,
                        color: 'rgba(216, 244, 247,1)'
                    }, {
                        offset: 1,
                        color: 'rgba(216, 244, 247,1)'
                    }], false)
                }
            },
            itemStyle: {
                normal: {
                    color: '#58c8da'
                }
            },
            lineStyle: {
                normal: {
                    width: 1
                }
            }
        }]
    };
    window["chart" + id] = echarts.init(document.getElementById('chart'+id+''), 'macarons');
    window["chart" + id].setOption(option1);
    $("#btn"+id+"").click();
}

function createChartDom(id, name) {
    // 收起兄弟节点的菜单
    $("#charts").find("div[role='tabpanel']").each(function () {
        if($(this).attr("class") == "panel-collapse collapse in"){
            $(this).removeClass("in");
        }
    });
    var str = "<div class=\"panel panel-primary\">\n" +
        "<div class=\"panel-heading\" role=\"tab\" id=\"heading"+id+"\">\n" +
        "   <h4 class=\"panel-title\">\n" +
        "      <a role=\"button\" data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse"+id+"\" aria-expanded=\"true\" aria-controls=\"collapse"+id+"\">\n" +
        "           " + name + "\n" +
        "      </a>\n" +
        "   </h4>\n" +
        "</div>\n" +
        "<div id=\"collapse"+id+"\" class=\"panel-collapse collapse in\" role=\"tabpanel\" aria-labelledby=\"heading"+id+"\">\n" +
        "    <div class=\"panel-body\">\n" +
        "       <div style=\"width:100%;height:300px;\" id=\"chart"+id+"\"></div>\n" +
        "    </div>\n" +
        "</div>\n" +
        "</div>";
    $("#charts").prepend(str);
}

// 加法chart
function chart0(id, name, desc) {
    createChartDom(id, name);

    var xdata = getXaxis();
    var ydata = new Array();
    for(var i=0; i<xdata.length; i++) {
        ydata.push(getRandom(1000, 10000));
    }

    var option0 = {
        color: ['#3398DB'],
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        dataset: {
            source: [
                [desc]
            ]
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                data : xdata,
                axisTick: {
                    alignWithLabel: true
                }
            }
        ],
        yAxis : [
            {
                type : 'value'
            }
        ],
        series : [
            {
                name: desc,
                type:'bar',
                barWidth: '60%',
                data: ydata
            }
        ]
    };
    window["chart" + id] = echarts.init(document.getElementById('chart'+id+''), 'macarons');
    window["chart" + id].setOption(option0);
}


// 乘法条件
function condition1() {
    var kpiCode = jm.get_selected_node().data.KPI_CODE;
    $.get("/progress/getKpi", {code: kpiCode}, function(r) {
        jsmind_refresh(r.data);
    });
}
// 获取根节点
function rootNode() {
    var nodeArr = new Array();
    $.ajax({
        url: "/progress/getRootNode",
        type: "GET",
        async: false,
        success: function (r) {
            var kpiCode = "";
            var kpiName = "";
            $.each(r.data, function (k, v) {
                kpiCode = k;
                kpiName = v;
            });
            var node = new Object();
            node.id = "-1";
            node.isroot = true;
            node.topic = "0 " + kpiName;
            node.KPI_CODE = kpiCode;
            node.KPI_NAME = kpiName;
            nodeArr.push(node);
        }
    });
    return nodeArr;
}

function createRootNode() {
    var mind = {
        "meta":{
            "name":"gmv_mind",
            "version":"0.2"
        },
        "format":"node_array",
        "data": rootNode()
    };
    var options = {
        container:'jsmind_container',
        editable:false,
        theme:'greensea',
        mode: "side"
    };
    jm = jsMind.show(options,mind);
    addEventListenerOfNode();

    saveNode("-1");
}

// 仅过滤条件
function condition2() {
    var levelId = getKpiLevelId();
    var nodeName = levelId + " <i class='mdi mdi-filter'></i> " + $("#currentNode").val();
    if(conditionVal.length == 0) {
        toastr.warning("请选择过滤条件！");
    }else {
        createNode(nodeName, levelId, jm.get_selected_node().data.KPI_CODE, jm.get_selected_node().data.KPI_NAME, false);
        chart_condition2(levelId, nodeName);
        $("#nodeAddModal").modal('hide');
    }
}

function chart_condition2(id, name) {
    $("#charts").find("div[role='tabpanel']").each(function () {
        if($(this).attr("class") == "panel-collapse collapse in"){
            $(this).removeClass("in");
        }
    });

    var str = "<div class=\"panel panel-primary\">\n" +
        "<div class=\"panel-heading\" role=\"tab\" id=\"heading"+id+"\">\n" +
        "   <h4 class=\"panel-title\">\n" +
        "      <a role=\"button\" data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse"+id+"\" aria-expanded=\"true\" aria-controls=\"collapse"+id+"\">\n" +
        "           " + name + "\n" +
        "      </a>\n" +
        "   </h4>\n" +
        "</div>\n" +
        "<div id=\"collapse"+id+"\" class=\"panel-collapse collapse in\" role=\"tabpanel\" aria-labelledby=\"heading"+id+"\">\n" +
        "    <div class=\"panel-body\">\n" +
        "       该过滤条件对应销售额为：36262元" +
        "    </div>\n" +
        "</div>\n" +
        "</div>";
    $("#charts").prepend(str);
}

// method过滤条件
function createNode(nodeName, levelId, kpiCode, kpiName,isLeaf) {
    var nodeId = getNodeId();
    var nodeName = nodeName;
    var parentId = $("#currentNodeId").val();
    var kpiCode = kpiCode;
    var kpiName = kpiName;
    var kpiLevelId = levelId;
    var alarmFlag = false;
    var conditions = filterCondition();

    var data = new Object();
    data.KPI_CODE = kpiCode;
    data.KPI_NAME = kpiName;
    data.KPI_LEVEL_ID = kpiLevelId;
    data.ALARM_FLAG = alarmFlag;
    data.CONDITION = conditions;
    data.IS_LEFAF = isLeaf;

    jm.enable_edit();
    jm.add_node(parentId, nodeId, nodeName, data);
    jm.disable_edit();
    addEventListenerOfNode();

    // 保存节点信息
    saveNode(nodeId);
    return nodeId;
}

function getNodeId() {
    var res = 0;
    $.ajax({
        url: "/progress/getNodeId",
        type: "get",
        async: false,
        success: function (r) {
            res = r.data;
        }
    });
    return res;
}

function getKpiLevelId() {
    var res = 0;
    $.ajax({
        url: "/progress/getKpiLevelId",
        data: {id: diagId},
        type: "get",
        async: false,
        success: function (r) {
            res = r.data;
        }
    });
    return res;
}

function removeConditionList(val, dom) {
    $(dom).parent().parent().remove();
    conditionVal.forEach(function(item, index, arr){
        if(item == val) {
            conditionVal.splice(index, 1);
        }
    });

    $.get("/progress/getDiagDimList", null, function (r) {
        var code = "";
        $.each(r.data, function(k, v) {
            if(conditionVal.length != 0) {
                conditionVal.forEach(function(item, index, arr){
                    if(item != k) {
                        code += "<option value='" + k + "'> " + v + " </option>";
                    }
                });
            }else {
                code += "<option value='" + k + "'> " + v + " </option>";
            }
        });
        $("#op5").html("").html("<option value=''>请选择</option>" + code);
        $("#op5").selectpicker('refresh');
        getValueList($("#op5").find("option:selected").val());
    });
}

function jsmind_refresh(map) {
    var kpiCode1 = map["DISMANT_PART1_CODE"];
    var kpiCode2 = map["DISMANT_PART2_CODE"];
    var kpiName1 = map["DISMANT_PART1_NAME"];
    var kpiName2 = map["DISMANT_PART2_NAME"];

    var levelId = getKpiLevelId();
    var nodeName1 = levelId + " <i class='mdi mdi-key-remove'></i> " + kpiName1;
    var nodeName2 = levelId + " <i class='mdi mdi-key-remove'></i> " + kpiName2;
    createNode(nodeName1, levelId, kpiCode1, kpiName1, false);
    createNode(nodeName2, levelId, kpiCode2, kpiName2, false);

    chart_condition1(levelId, levelId + " <i class='mdi mdi-key-remove'></i> " + kpiName1 + "*" + kpiName2, kpiName1, kpiName2);
    $("#nodeAddModal").modal('hide');
}

function getKpiComb(code) {
    $.get("/progress/getKpiComb", {code: code}, function (r) {
        $("#currentNodeId").val(r.data.k);

        var code = "<option value='"+r.data.k+"'>" + r.data.v + "</option>";
        $("#op1").html("").html(code);
        $('#op1').selectpicker('refresh');
    });
}

// 获取公式
function getFormula(kpiCode) {
    $.get("/progress/getFormula", {code: kpiCode}, function(r) {
        var code = null;
        if(r.data != null) {
            code = "<option value='" + kpiCode + "'>" + r.data + "</option>";
        }
        $("#op3").html("").html(code);
        $('#op3').selectpicker('refresh');
    });
}

function addEventListenerOfNode() {
    // 注册鼠标右键事件
    $("jmnode").mousedown(function (event) {

        // 动态设置节点为选中状态，参考api的mousedown_handle实现
        var element = event.target;
        var nodeid = jm.view.get_binded_nodeid(element);
        if(!!nodeid) {
            jm.select_node(nodeid);
        }else {
            jm.select_clear();
        }

        var event = event || window.event;
        var e = document.getElementById("operateBtns");
        if(event.button == "2"){
            if(jm.get_selected_node().data.IS_LEFAF) {
                $("#condition0").attr("style", "display:none;");
            }else {
                $("#condition0").attr("style", "display:block;");
            }
            e.style.top = event.pageY+'px';
            e.style.left = event.pageX+'px';
            e.style.display = 'block';
        }else {
            e.style.display = 'none';
        }
    });
}

// 判断JM非空，保证在单击面板时，不被隐藏
$("#main").mousedown(function (event) {
    var event = event || window.event;
    var e = document.getElementById("operateBtns");
    if(event.button == "0" && e.style.display == "block" && jm.get_selected_node() == null){
        e.style.display = 'none';
    }
});

function next(dom) {
    if($("#step1").attr("style") == "display:block;") {
        beforeNext(dom);
    }
}