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
init_date_begin("beginDt", "endDt","yyyy-mm", 1,2,1);
init_date_end("beginDt", "endDt","yyyy-mm", 1,2,1);

// 周期切换时间控件
$("#periodType").change(function () {
    var periodType = $("#periodType option:selected").val();
    $("#beginDt").remove();
    $("#endDt").remove();

    $("#startDate").append("<input class=\"form-control js-datepicker m-b-10\" type=\"text\" id=\"beginDt\" name=\"beginDt\"/>");
    $("#endDate").append("<input class=\"form-control js-datepicker m-b-10\" type=\"text\" id=\"endDt\" name=\"endDt\"/>");
    if(periodType == "D") {
        init_date_begin("beginDt", "endDt", "yyyy-mm-dd", 0,2,0);
        init_date_end("beginDt", "endDt", "yyyy-mm-dd", 0,2,0);
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

$("input[name='op2']").click(function () {
    var kpiCode = jm.get_selected_node().data.KPI_CODE;
    var option2Val = $(this).val();
    if(option2Val == "A") { // 加法过滤
        getDimension(); // 获取维度
        $("#add_condition").attr("style", "display:block;");
        $("#plus_condition").attr("style", "display:none;");
    }
    if(option2Val == "M") { // 乘法过滤
        getFormula(kpiCode);
        getDimension(); // 获取维度
        $("#plus_condition").attr("style", "display:block;");
        $("#add_condition").attr("style", "display:none;");
    }
    if(option2Val == "F" || option2Val == "") { // 仅过滤
        $("#op7").html("");
        $("#op7").selectpicker('refresh');
        var e = document.getElementById("operateBtns");
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
    var operateType = $("input[name='op2']:checked").val();
    if(operateType == "A") { // 加法
        condition0();
        // 隐藏模态框
        $("#nodeAddModal").modal('hide');
    }else if(operateType == "M") { // 乘法
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
    }else if(operateType == "F") { // 仅过滤
        condition2();
    }else if(operateType == ""){
        toastr.warning("请选择诊断方式！");
    }
}

function resetTableData() {
    $("#dataTable").html("").html("<tr><td><i class=\"mdi mdi-alert-circle-outline\"></i>暂无数据！</td></tr>");
}


$("#op5").change(function() {
    var code = $(this).find("option:selected").val();
    getValueList(code, "op6");
});

$("#op4").change(function() {
    var code = $(this).find("option:selected").val();
    getValueList(code, "op7");
});

// 根据维度value获取值类型
function getValueList(code, id) {
    $.get("/progress/getDiagDimValueList", {code: code}, function(r) {
        var code = "";
        $.each(r.data, function (k, v) {
            code += "<option value='" + k + "'>" + v + "</option>";
        });
        $("#" + id).html("").html(code);
        $("#" + id).selectpicker('refresh');
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
    $("#op7").html("");
    $("#op7").selectpicker('refresh');
    var e = document.getElementById("operateBtns");
    e.style.display = "none";

    // 弹窗中获取当前指标
    var selectedNode = jm.get_selected_node();
    $("#currentNodeId").val(selectedNode.id);
    $("#currentNode").val(jm.get_selected_node().data.KPI_NAME);

    // 设置选中为空，初始化选择框
    $("input[name='op2']:checked").removeAttr("checked");

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
        code += "<tr><td style='text-align: left;'>" + v.dim_name.trim() + ":";
        code += v.dim_value_display.trim();
        code += "<input type='hidden' name='dimValues' value='"+v.dim_values+"'><input type='hidden' name='condition' value='"+v.dim_code+"'><input name='inheritFlag' type='hidden' value='Y'></td><td></td></tr>";
    });
    return code;
}

// 获取加法维度
function getDimension() {
    $.get("/progress/getDiagDimList", null, function (r) {
        var code = "<option value=''>请选择</option>";
        $.each(r.data, function(k, v) {
            code += "<option value='" + k + "'> " + v + " </option>";
        });
        $("#op4").html("").html(code);
        $("#op4").selectpicker('refresh');

        $("#op5").html("").html(code);
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
        var conditionCodes = "";
        for(var i=0;i<arr.length;i++) {
            var t = $("#op6").find("option[value='"+arr[i]+"']").text();
            code += t + ",";
            conditionCodes += arr[i] + ","
        }
        code = code.substring(0, code.length - 1);
        conditionCodes = conditionCodes.substring(0, conditionCodes.length - 1);
        code += "<input name='dimValues' type='hidden' value='"+conditionCodes+"'><input name='condition' type='hidden' value='" + val + "'/><input name='inheritFlag' value='N' type='hidden'/></td><td><a style='color:#000000;cursor: pointer;' onclick='removeConditionList(\""+val+"\", this)'><i class='mdi mdi-close'></i></a></td></tr>";

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
        var tmp = new Object();
        var condition = $(this).find("td:eq(0)").find("input[name='condition']").val();
        var inheritFlag = $(this).find("td:eq(0)").find("input[name='inheritFlag']").val();
        var dimValues = $(this).find("td:eq(0)").find("input[name='dimValues']").val();
        var text = $(this).find("td:eq(0)").text();
        if(condition != undefined && inheritFlag != undefined) {
            tmp.dim_code = condition;
            tmp.dim_name = text.split(":")[0];
            tmp.dim_values = dimValues;
            tmp.dim_value_display = text.split(":")[1];
            tmp.inherit_flag = inheritFlag;
            dataArray.push(tmp);
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
        code += "<tr><td style='text-align: left;'>" + tmp[2].trim() + ":";
        if(tmp[4] == "N") {
            code += tmp[3].trim();
        }else if(tmp[4] == "Y"){
            code += tmp[3].trim() + "&nbsp;(继承至父节点)"
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
    saveRedisHandleInfo(null,levelId, jm.get_selected_node().data.KPI_CODE, jm.get_selected_node().data.KPI_NAME);
}

// 加法节点为叶子节点
function endNode(nodeId) {
    jm.enable_edit();
    jm.set_node_color(nodeId, '#8b95a5', '');
    jm.disable_edit();
}

function redisSaveRootNodeHandleInfo() {
    var handleInfo = new Object();
    var periodType = $("#periodType option:selected").val();
    var beginDt = $("#beginDt").val();
    var endDt = $("#endDt").val();
    handleInfo.diagId = diagId;
    handleInfo.kpiLevelId = 0;
    handleInfo.handleType = "F";
    handleInfo.handleDesc = "GMV指标概况";
    handleInfo.templateName = "GMV指标";
    handleInfo.periodType = periodType;
    handleInfo.beginDt = beginDt;
    handleInfo.endDt = endDt;
    handleInfo.kpiCode = "gmv";
    saveDiagHandleInfo(handleInfo, "F");
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
        obj["condition"] = tmp.data.CONDITION;
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

// 月份
function getDate(datestr){
    var temp = datestr.split("-");
    var date = new Date(temp[0],temp[1]);
    return date;
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
            node.topic = "0 "+kpiName;
            node.KPI_CODE = kpiCode;
            node.KPI_NAME = kpiName;
            node.KPI_LEVEL_ID = "0";
            nodeArr.push(node);
        }
    });
    return nodeArr;
}

function modalTest() {
    $("#modal").modal('show');
}
$('#modal').on('shown.bs.modal', function () {
    template2("chart1");
    template3_1("chart2");
    template3_2("chart3");
    template4_1("chart4");
    template4_2("chart5");
    template4_3("chart6");
});

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

    redisSaveRootNodeHandleInfo();
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
        $("#nodeAddModal").modal('hide');
        saveRedisHandleInfo(nodeName, levelId, jm.get_selected_node().data.KPI_CODE, jm.get_selected_node().data.KPI_NAME);
    }
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

// redis存储HandleInfo
function saveRedisHandleInfo(nodeName, levelId, kpiCode, kpiName) {
    // 封装HandleInfo
    var handleInfo = new Object();
    var periodType = $("#periodType option:selected").val();
    var beginDt = $("#beginDt").val();
    var endDt = $("#endDt").val();
    var handleDesc = "";
    var templateName = "";
    // M乘法 A加法 F过滤(无操作)
    var operateType = $("input[name='op2']:checked").val();
    if(operateType == "M") {
        handleDesc = kpiName + "按乘法拆分";
        templateName = "维度模板";
        handleInfo.formula1 = "";
    }
    if(operateType == "A") {
        handleDesc = kpiName + "按加法拆分";
        templateName = "指标模板";
        handleInfo.addDimCode = $("#op4").find("option:selected").val();
        if($("#op7").selectpicker('val') != null) { // 不选维度值
            handleInfo.addDimValues = $("#op7").selectpicker('val').join(",");
        }
    }
    if(operateType == "F") {
        handleDesc = kpiName + "按条件过滤";
        templateName = "条件过滤说明";
    }
    handleInfo.diagId = diagId;
    handleInfo.kpiLevelId = levelId;
    handleInfo.handleDesc = handleDesc;
    handleInfo.handleType = operateType;
    handleInfo.templateName = templateName;
    handleInfo.nodeId = null;
    handleInfo.periodType = periodType;
    handleInfo.beginDt = beginDt;
    handleInfo.endDt = endDt;
    handleInfo.whereinfo = filterCondition();
    handleInfo.kpiCode = kpiCode;

    // redis封装数据,返回模版文件
    saveDiagHandleInfo(handleInfo, operateType);
}

function saveDiagHandleInfo(handleInfo, operateType) {
    $.post("/progress/saveDiagHandleInfo", {diagHandleInfo: JSON.stringify(handleInfo)}, function (r) {
        if(r.code == 500) {
            toastr.error("存储redis发生错误！");
        }else {
            if(operateType == "A") {
                var levelId = handleInfo.kpiLevelId;
                $.get("/progress/generateDiagData", {diagId: diagId, kpiLevelId: levelId}, function (r) {
                    console.info(r);
                    var kpiCode = r.data.kpiCode;
                    var kpiName = r.data.kpiName;
                    $.each(r.data.nodeList, function(k, v) {
                        var nodeName = levelId + " <i class='mdi mdi-key-plus'></i> " + $("#op4").find("option:selected").text() + "(" + v.name + ")";
                        createNode(nodeName, levelId, kpiCode, kpiName, false);
                    });
                });
            }
        }
    });
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
    $("#nodeAddModal").modal('hide');

    saveRedisHandleInfo(nodeName1, levelId, jm.get_selected_node().data.KPI_CODE, jm.get_selected_node().data.KPI_NAME);
    saveRedisHandleInfo(nodeName2, levelId, jm.get_selected_node().data.KPI_CODE, jm.get_selected_node().data.KPI_NAME);
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
            e.style.top = event.pageY+'px';
            e.style.left = event.pageX+'px';
            e.style.display = 'block';
        }else {
            e.style.display = 'none';
            nodeClick();
        }
    });
}

function nodeClick() {
    var kpiLevelId = jm.get_selected_node().data.KPI_LEVEL_ID;
    $.get("/progress/generateDiagData", {diagId: diagId, kpiLevelId: kpiLevelId}, function (r) {
        console.log(r)
        viewChart(r.data);
    });
}

function viewChart(obj) {
    $("#modal").modal('show');
    var operateType = obj.periodType;
    if (operateType == "M") {
        operateType = "月";
    } else {
        operateType = "天";
    }
    $("#handleDesc").html("").html("<p class='h4'>" + obj.handleDesc + "</p>");
    $("#operateType").html("").html("<p class='h5'>周期: " + operateType + "</p>");
    $("#timePeriod").html("").html("<p class='h5'>时间: " + obj.beginDt + "&nbsp;到&nbsp;" + obj.endDt + "</p>");
    var isRoot = jm.get_selected_node().isroot;
    if (isRoot) { // 根节点
        $("#opdesc").html("").html("<p class='h5'>该周期内GMV为：" + obj.kpiValue + "元</p>");
        $("#template1").attr("style", "display:block;");
        $("#template2").attr("style", "display:none;");
        $("#template3").attr("style", "display:none;");

    } else {
        if (obj.handleType == "M") { // 乘法
            $("#template1").attr("style", "display:none;");
            $("#template2").attr("style", "display:block;");
            $("#template3").attr("style", "display:none;");

            // 条件
            var whereinfo = "<div class=\"col-md-12\"><table class='table table-sm'>";
            $.each(obj.whereinfo, function (k, v) {
                whereinfo += "<tr><td class='text-left'>" + v.dim_name + ":" + v.dim_value_display + "</td></tr>";
            });
            whereinfo += "</table></div>";
            if (obj.whereinfo.length != 0) {
                $("#whereinfo").html("").html(whereinfo);
            }

            // 变异系数
            var chartId = "covChart";
            covChart(chartId, obj);

            // 指标趋势图
            t2charts(obj);
            t2Cov(obj);
        } else if (obj.handleType == "A") { // 加法
            $("#template1").attr("style", "display:none;");
            $("#template2").attr("style", "display:none;");
            $("#template3").attr("style", "display:block;");
            t3chart1(obj, 't3chart1');
            if(obj.lineData.length != 0 && obj.lineAvgData.length != 0) {
                $("#t2chart").attr("style","display:block;");
                t3chart2(obj, 't3chart2');
            }else {
                $("#t2chart").attr("style","display:none;");
            }
            t3Cov(obj);
        } else if (obj.handleType == "F") { // 条件过滤
            $("#opdesc").html("").html("<p class='h5'>该周期内"+obj.kpiName+"为：" + obj.kpiValue + "</p>");
            $("#template1").attr("style", "display:block;");
            $("#template2").attr("style", "display:none;");
            $("#template3").attr("style", "display:none;");
        }
    }
}

function t3Cov(obj) {
    var code1 = "<tr class='active'><td></td>";
    var code2 = "<tr><td>变异系数</td>";
    var code3 = "<tr><td>相关系数</td>";
    $.each(obj.covData, function (k, v) {
        code1 += "<td> " + v.name + " </td>";
        code2 += "<td> "+ v.data +" </td>";
        code3 += "<td>" + obj.relateData[k].data + "</td>";
    });
    code1 += "</tr>";
    code2 += "</tr>";
    code3 += "</tr>";
    $("#covTable").html("").html(code1 + code2 + code3);
}

function t2Cov(obj) {
    var code1 = "<tr class='active'>";
    var code2 = "<tr>";
    $.each(obj.relate.data, function (k, v) {
        code1 += "<td> " + v.name + " </td>";
        code2 += "<td>" + v.data + "</td>";
    });
    code1 += "</tr>";
    code2 += "</tr>";
    $("#cov2Table").html("").html(code1 + code2);
}

function t2charts(obj) {
    $("#rate1").html("").html("末期比基期的变化率:" + obj.firChangeRate);
    $("#rate2").html("").html("末期比基期的变化率:" + obj.secChangeRate);
    $("#rate3").html("").html("末期比基期的变化率:" + obj.thirdChangeRate);
    makeT2Chart(obj,"t2chart1", obj.firYName,obj.firData, obj.firAvg, obj.firUp, obj.firDown);
    makeT2Chart(obj,"t2chart2", obj.secYName,obj.secData, obj.secAvg, obj.secUp, obj.secDown);
    makeT2Chart(obj,"t2chart3", obj.thirdYName,obj.thirdData, obj.thirdAvg, obj.thirdUp, obj.thirdDown);
}

function t3chart1(obj, chartId){
    var legendData = obj.legendData;
    var xAxisData = obj.xdata;
    var xAxisName = obj.xname;
    var yAxisName = "GMV值（元）";
    var seriesData = new Array();
    $.each(obj.areaData, function (k, v) {
        var obj = new Object();
        obj.name = v.name;
        obj.data = v.data;
        obj.type = 'line';
        obj.stack = '总量';
        obj.areaStyle = {normal: {}};
        seriesData.push(obj);
    });
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
    setTimeout(function () {
        chart.resize();
    }, 200);
}
function t3chart2(obj, chartId){
    var legendData = obj.legendData;
    var xAxisData = obj.xdata;
    var xAxisName = obj.xname;
    var yAxisName = "GMV值（元）";
    var seriesData = new Array();
    $.each(obj.lineData, function (k, v) {
        var obj = new Object();
        obj.name = v.name;
        obj.data = v.data;
        obj.type = 'line';
        seriesData.push(obj);
    });
    var len = xAxisData.length;
    $.each(obj.lineAvgData, function (k, v) {
        var obj = new Object();
        var name = v.name + "均线";
        obj.name = name;
        legendData.push(name);
        var data = new Array();
        for(var i=0; i<len;i++) {
            data.push(v.data);
        }
        obj.data = data;
        obj.type = 'line';
        seriesData.push(obj);
    });

    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
    setTimeout(function () {
        chart.resize();
    }, 200);
}

function makeT2Chart(obj,chartId,yName, data, avg, up, down) {
    var legendData = [yName, "均线"];
    var xAxisData = obj.xData;
    var xAxisName = obj.xName;
    var yAxisName = yName;
    var seriesData = new Array();
    var avgData = new Array();
    $.each(data, function () {
        avgData.push(avg);
    });
    var t1 = new Object();
    t1.name = yName;
    t1.type = 'line';
    t1.data = data;

    seriesData.push(t1);

    var t2 = new Object();
    t2.name = legendData[1];
    t2.type = 'line';
    t2.data = avgData;
    t2.markArea = {itemStyle:{
            color: '#48b0f7',
            opacity: 0.2
        },
        silent: true,
        label: {
            normal: {
                position: ['10%', '50%']
            }
        },
        data: [
            [{
                name: '',
                yAxis: up,
                x: '10%',
                itemStyle: {
                    normal: {
                        color: '#ff0000'
                    }
                },
            }, {
                yAxis: down,
                x: '90%'
            }]
        ]};
    seriesData.push(t2);
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
    setTimeout(function () {
        chart.resize();
    }, 200);
}

function template3_2(chartId) {
    var legendData = ["子品类一","子品类二","子品类三","子品类一均线","子品类二均线","子品类三均线"];
    var xAxisData = ["201901", "201902", "201903", "201904"];
    var xAxisName = "品类";
    var yAxisName = "GMV值（元）";
    var seriesData = [{name: '子品类一', type: 'line', data:[100, 200, 400, 320, 500]},
        {name: '子品类二', type: 'line', data:[300, 200, 500, 230, 430]},
        {name: '子品类三', type: 'line', data:[200, 400, 100, 330, 350]},
        {name: '子品类一均线', type: 'line', data:[200, 200, 200, 200, 200]},
        {name: '子品类二均线', type: 'line', data:[400, 400, 400, 400, 400]}];
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
}

// 乘法变异系数图
function covChart(chartId, obj) {
    var legendData = new Array();
    var xAxisData = obj.xData;
    var seriesData = new Array();
    $.each(obj.covNames, function (k, v) {
        legendData.push(v);
        var t = new Object();
        t.name = v;
        t.type = 'line';
        t.data = obj.covValues[k];
        seriesData.push(t);
    });
    var xAxisName = obj.xName;
    var yAxisName = "变异系数";
    var option = getOption(legendData,xAxisData,xAxisName,yAxisName,seriesData);
    var chart = echarts.init(document.getElementById(chartId), 'macarons');
    chart.setOption(option);
    setTimeout(function () {
        chart.resize();
    }, 200);
}


// 判断JM非空，保证在单击面板时，不被隐藏
$("#main").mousedown(function (event) {
    var event = event || window.event;
    var e = document.getElementById("operateBtns");
    if(event.button == "0" && e.style.display == "block" && jm.get_selected_node() == null){
        e.style.display = 'none';
    }
});

function inputCheck(dom) {
    if($(dom).val() == "") {
        $(dom).parent().addClass("has-error");
    }else {
        $(dom).parent().removeClass("has-error");
    }
}

function nextStep(dom) {
    if($("#diagName").val() != "") {
        $("#diagName").parent().removeClass("has-error");
        if($("#step1").attr("style") == "display:block;") {
            beforeNext(dom);
        }
    }else {
        $("#diagName").parent().addClass("has-error");
        toastr.warning("请输入诊断名称！");
    }
}

// 加法维度值创建节点
function createNodeByPlus() {

    if(operateType == "A") { // 创建加法的维度节点
        var kpiLevelId = handleInfo.kpiLevelId;
        createNodeByPlus(kpiLevelId);
    }
    $.get("/progress/generateDiagData", {diagId: diagId, kpiLevelId: kpiLevelId}, function (r) {
        $.each(r.data.nodeList, function(k, v) {
            var nodeName = v.name;
            var levelId = getKpiLevelId();
            var kpiCode = v.code;
            var kpiName = v.name;
            createNode(nodeName, levelId, kpiCode, kpiName, false);
        });
    });
}