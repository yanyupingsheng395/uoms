
// 流程图对象
var jm = null;
var diagId = 0;

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
    var kpiCode = jm.get_selected_node().data.kpiCode;
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
// function beforeNext(dom) {
//     $.post("/diag/add", $("#formTable").serialize(), function(r) {
//         $("#period_type").html("").html($("#periodType option:selected").text());
//         $("#date_area").html("").html($("#beginDt").val() + "&nbsp;到&nbsp;" + $("#endDt").val());
//
//         $("#step1").attr("style", "display:none;");
//         $("#step2").attr("style", "display:block;");
//         $(dom).parent("div").removeClass("text-right").addClass("text-center");
//         $(dom).remove();
//         diagId = r.data;
//         createRootNode();
//     });
// }

// 加入诊断
function addCondition() {
    lightyear.loading("show");
    var operateType = $("input[name='op2']:checked").val();
    if(operateType == "A") { // 加法
        if($("#op4").find("option:selected").val() == "") {
            toastr.warning('请选择维度！');
            lightyear.loading("hide");
        }else {
            condition0();
            // 隐藏模态框
            $("#nodeAddModal").modal('hide');
        }
    }else if(operateType == "M") { // 乘法
        if($("#op3").find("option:selected").val() == null) {
            if($("#op3").find("option").length == 0) {
                toastr.warning('该指标无可再拆分的乘法公式，请选择别的拆分方式！');
                lightyear.loading("show");
            }else {
                toastr.warning('请选择拆分公式！');
                lightyear.loading("show");
            }
        } else {
            condition1();
        }
        // 隐藏模态框
    }else if(operateType == "F") { // 仅过滤
        condition2();
    }else{
        toastr.warning('请选择诊断方式！');
        lightyear.loading("show");
    }
}

function resetTableData() {
    $("#dataTable").html("").html("<tr><td><i class=\"mdi mdi-alert-circle-outline\"></i>暂无数据！</td></tr>");
}


$("#op5").change(function() {
    var code = $(this).find("option:selected").val();
    lightyear.loading('show');
    getValueList(code, "op6");
});

$("#op4").change(function() {
    var code = $(this).find("option:selected").val();
    lightyear.loading('show');
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
        lightyear.loading('hide');
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
        obj["kpiCode"] = tmp.data.kpiCode;
        obj["kpiName"] = tmp.data.kpiName;
        obj["kpiLevelId"] = tmp.data.kpiLevelId;
        obj["alarmFlag"] = "n";
        obj["conditions"] = tmp.data.conditions;
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
    $("#currentNode").val(selectedNode.data.kpiName);

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
    var array = jm.get_selected_node().data.conditions;
    var code = "";
    if(array != null && array != undefined) {
        $.each(array, function (k, v) {
            code += "<tr><td style='text-align: left;'>" + v.dimName.trim() + ":";
            code += v.dimValueDisplay.trim();
            code += "<input type='hidden' name='dimValues' value='"+v.dimValues+"'><input type='hidden' name='condition' value='"+v.dimCode+"'><input name='inheritFlag' type='hidden' value='Y'></td><td></td></tr>";
        });
    }
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
    });
}

var conditionVal = new Array();

// 增加条件
function selectedCondition() {
    if($("#op5").find("option:selected").val() == "" || $("#op6").find("option:selected").val() == "") {
        toastr.warning('未选择条件或值！');
    }else {
        var arr = $("#op6").selectpicker('val');
        var condition = $("#op5").find("option:selected").text();
        var val = $("#op5").find("option:selected").val();
        var code = "<tr><td style='text-align: left;'>" + condition.trim() + "&nbsp;:&nbsp;";
        var conditionCodes = "";
        for(var i=0;i<arr.length;i++) {
            var t = $("#op6").find("option[value='"+arr[i]+"']").text();
            code += t + "|";
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
            tmp.dimCode = condition;
            tmp.dimName = text.split(":")[0];
            tmp.dimValues = dimValues;
            tmp.dimValueDisplay = text.split(":")[1];
            tmp.inheritFlag = inheritFlag;
            dataArray.push(tmp);
        }
    });
    return dataArray;
}

// 条件详情
function modalDetailBefore() {
    var e = document.getElementById("operateBtns");
    e.style.display = "none";
    var array = jm.get_selected_node().data.conditions;
    $("#kpiNameDetail").html("").html(jm.get_selected_node().data.kpiName);
    var code = "";
    $.each(array, function (k, v) {
        code += "<tr><td style='text-align: left;'>" + v.dimName + ":";
        if(v.inheritFlag == "N") {
            code += v.dimValueDisplay;
        }else if(v.inheritFlag == "Y"){
            code += v.dimValueDisplay + "&nbsp;(继承至父节点)"
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
    saveRedisHandleInfo(null,levelId, jm.get_selected_node().data.kpiCode, jm.get_selected_node().data.kpiName);
}

// 加法节点为叶子节点
function endNode(nodeId) {
    jm.enable_edit();
    jm.set_node_color(nodeId, '#8b95a5', '');
    jm.disable_edit();
}

function redisSaveRootNodeHandleInfo(whereinfo) {
    var kpiCode = $("#targetKpi").find("option:selected").val();
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
    handleInfo.kpiCode = kpiCode;
    handleInfo.mainKpiCode = kpiCode;
    handleInfo.whereinfo = whereinfo;
    saveDiagHandleInfo(handleInfo, "F");
}
//
//saveNode("-1");

// 单步保存节点信息
function saveNode(nodeid) {
    // 加载动画
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
        obj["kpiCode"] = tmp.data.kpiCode;
        obj["kpiName"] = tmp.data.kpiName;
        obj["kpiLevelId"] = tmp.data.kpiLevelId;
        obj["alarmFlag"] = "n";
        obj["condition"] = tmp.data.conditions;
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
                toastr.success(r.msg);
            }
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
    var kpiCode = jm.get_selected_node().data.kpiCode;
    $.get("/progress/getKpi", {code: kpiCode}, function(r) {
        jsmind_refresh(r.data);
    });
}
// 获取根节点
function rootNode(dimList) {
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
            node.kpiCode = kpiCode;
            node.kpiName = kpiName;
            node.kpiLevelId = "0";
            node.conditions = dimList;
            nodeArr.push(node);
        }
    });
    return nodeArr;
}

function createRootNode(dimList) {
    var mind = {
        "meta":{
            "name":"gmv_mind",
            "version":"0.2"
        },
        "format":"node_array",
        "data": rootNode(dimList)
    };
    var options = {
        container:'jsmind_container',
        editable:false,
        theme:'greensea',
        mode: "side"
    };
    jm = jsMind.show(options,mind);
    addEventListenerOfNode();

    redisSaveRootNodeHandleInfo(dimList);
    saveNode("-1");
}

// 仅过滤条件
function condition2() {
    var levelId = getKpiLevelId();
    var nodeName = levelId + " <i class='mdi mdi-filter'></i> " + $("#currentNode").val();
    if(conditionVal.length == 0) {
        toastr.warning('请选择过滤条件！');
    }else {
        createNode(nodeName, levelId, jm.get_selected_node().data.kpiCode, jm.get_selected_node().data.kpiName, false, null);
        $("#nodeAddModal").modal('hide');
        saveRedisHandleInfo(nodeName, levelId, jm.get_selected_node().data.kpiCode, jm.get_selected_node().data.kpiName);
    }
}

// method过滤条件
function createNode(nodeName, levelId, kpiCode, kpiName,isLeaf, condition) {
    var nodeId = getNodeId();
    var nodeName = nodeName;
    var parentId = $("#currentNodeId").val();
    var kpiCode = kpiCode;
    var kpiName = kpiName;
    var kpiLevelId = levelId;
    var alarmFlag = false;
    var conditions = new Array();
    if(condition != null) {
        conditions.push(condition);
    }
    conditions = conditions.concat(filterCondition());

    var data = new Object();
    data.kpiCode = kpiCode;
    data.kpiName = kpiName;
    data.kpiLevelId = kpiLevelId;
    data.alarmFlag = alarmFlag;
    data.conditions = conditions;
    data.isLeaf = isLeaf;

    jm.enable_edit();
    jm.add_node(parentId, nodeId, nodeName, data);
    jm.disable_edit();
    addEventListenerOfNode();

    // 保存节点信息
    saveNode(nodeId);
    lightyear.loading("hide");
    return nodeId;
}

// redis存储HandleInfo
function saveRedisHandleInfo(nodeName, levelId, kpiCode, kpiName) {
    var op = $("#opDataType").val();
    var mainKpiCode = "";
    if(op == "edit") {
        mainKpiCode = $("#mainKpiCode").val();
    }else if(op == "add"){
        mainKpiCode = $("#targetKpi").find("option:selected").val();
    }
    // 封装HandleInfo
    var handleInfo = new Object();
    var periodType = $("#periodType option:selected").val() == null ? $("#periodType").val():$("#periodType option:selected").val();
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
    handleInfo.mainKpiCode = mainKpiCode;
    // redis封装数据,返回模版文件
    saveDiagHandleInfo(handleInfo, operateType);
}

function saveDiagHandleInfo(handleInfo, operateType) {
    $.post("/progress/saveDiagHandleInfo", {diagHandleInfo: JSON.stringify(handleInfo)}, function (r) {
        if(r.code == 500) {
            toastr.error('操作失败，服务出现异常了，快反馈给系统运维人员吧！');
        }else {
            if(operateType == "A") {
                var levelId = handleInfo.kpiLevelId;
                $.get("/progress/generateDiagData", {diagId: diagId, kpiLevelId: levelId}, function (r) {
                    console.log("========");
                    console.log(r);
                    var kpiCode = r.data.kpiCode;
                    var kpiName = r.data.kpiName;
                    $.each(r.data.nodeList, function(k, v) {
                        var nodeName = levelId + " <i class='mdi mdi-key-plus'></i> " + $("#op4").find("option:selected").text() + "(" + v.name + ")";
                        var condition = getConditionList(v);
                        createNode(nodeName, levelId, kpiCode, kpiName, false, condition);
                        // 加法将条件加入到列表
                    });
                });
            }
        }
    });
}

function getConditionList(v) {
    var tmp = new Object();
    tmp.dimCode = v.dimCode;
    tmp.dimName = v.dimName;
    tmp.dimValues = v.code;
    tmp.dimValueDisplay = v.name;
    tmp.inheritFlag = "N";
    return tmp;
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

    if(conditionVal.length == 0) {
        $("#dataTable").html("").html("<tr><td><i class=\"mdi mdi-alert-circle-outline\"></i>暂无数据！</td></tr>");
    }

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
    });
}

function jsmind_refresh(map) {
    var kpiCode1 = map["dismantPart1Code"];
    var kpiCode2 = map["dismantPart2Code"];
    var kpiName1 = map["dismantPart1Name"];
    var kpiName2 = map["dismantPart2Name"];

    var levelId = getKpiLevelId();
    var nodeName1 = levelId + " <i class='mdi mdi-key-remove'></i> " + kpiName1;
    var nodeName2 = levelId + " <i class='mdi mdi-key-remove'></i> " + kpiName2;
    createNode(nodeName1, levelId, kpiCode1, kpiName1, false, null);
    createNode(nodeName2, levelId, kpiCode2, kpiName2, false, null);
    $("#nodeAddModal").modal('hide');

    saveRedisHandleInfo(nodeName1, levelId, jm.get_selected_node().data.kpiCode, jm.get_selected_node().data.kpiName);
    saveRedisHandleInfo(nodeName2, levelId, jm.get_selected_node().data.kpiCode, jm.get_selected_node().data.kpiName);
}

// 获取公式
function getFormula(kpiCode) {
    var name = $("#currentNode").val();
    $.get("/progress/getFormula", {code: kpiCode}, function(r) {
        var code = null;
        if(r.data != null) {
            code = "<option value='" + kpiCode + "'>" + r.data + "</option>";
        }
        if(code == null) {
            toastr.warning(name+"无法按乘法进行诊断！");
        }
        $("#op3").html("").html(code);
        $('#op3').selectpicker('refresh');
    });
}

function addEventListenerOfNode() {
    // 节点单击事件
    $("jmnode").unbind('click');
    $("jmnode").bind('click', function () {
        nodeClick();
    });

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

function inputCheck(dom) {
    if($(dom).val() == "") {
        $(dom).parent().addClass("has-error");
    }else {
        $(dom).parent().removeClass("has-error");
    }
}

// function nextStep(dom) {
//     if($("#diagName").val() != "") {
//         $("#diagName").parent().removeClass("has-error");
//         if($("#step1").attr("style") == "display:block;") {
//             beforeNext(dom);
//         }
//     }else {
//         $("#diagName").parent().addClass("has-error");
//         toastr.warning('请输入诊断名称！');
//     }
// }

function deleteNode() {
    $.alert({
        title: '提示!',
        content: '演示环境，暂不支持删除节点！',
        theme: 'bootstrap'
    });

}

function reasonAdd() {
    // 隐藏操作菜单
    var e = document.getElementById("operateBtns");
    e.style.display = "none";
    var selectedNode = jm.get_selected_node();
    $("#reasonKpiCode").val(selectedNode.data.kpiCode);
    $("#reasonKpiName").val(selectedNode.data.kpiName);
    reasonAddCondition();
    $("#nodeReasonAddModal").modal('show');
}

function reasonAddBefore() {
    var selectedNode = jm.get_selected_node();
    $.get("/progress/checkReasonList", {kpiCode: selectedNode.data.kpiCode, kpiName: selectedNode.data.kpiName}, function(r) {
        if(!r.data) {
            toastr.warning('当前指标暂不支持进行原因探究！');
        }else {
            reasonAdd();
        }
    });
}

// 原因探究：选择维度以及值
function reasonAddCondition() {
    var array = jm.get_selected_node().data.conditions;
    if(array != undefined) {
        $("#kpiNameDetail").html("").html(jm.get_selected_node().data.kpiName);
        var code = "";
        $.each(array, function (k, v) {
            code += "<tr><td style='text-align: left;'>" + v.dimName + ":";
            code += v.dimValueDisplay;
            code += "<input name='dimValues' type='hidden' value='"+v.dimValues+"'>";
            code += "<input name='dimKey' type='hidden' value='"+v.dimCode+"'>";
            code += "</td></tr>";
        });
        if(code == "") {
            $("#selectedTable").html("").html("<tr><td><i class=\"mdi mdi-alert-circle-outline\"></i>暂无数据！</td></tr>");
        }else {
            $("#selectedTable").html("").html(code);
        }
    }
}