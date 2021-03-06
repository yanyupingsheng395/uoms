
$(function () {
    getDimension();
});
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
            $MB.n_warning('请选择维度！');
            lightyear.loading("hide");
        }else {
            condition0();
            // 隐藏模态框
            $("#nodeAddModal").modal('hide');
        }
    }else if(operateType == "M") { // 乘法
        if($("#op3").find("option:selected").val() == null) {
            if($("#op3").find("option").length == 0) {
                $MB.n_warning('该指标无可再拆分的乘法公式，请选择别的拆分方式！');
                lightyear.loading("hide");
            }else {
                $MB.n_warning('请选择拆分公式！');
                lightyear.loading("hide");
            }
        } else {
            condition1();
        }
        // 隐藏模态框
    }else if(operateType == "F") { // 仅过滤
        condition2();
    }else{
        $MB.n_warning('请选择诊断方式！');
        lightyear.loading("hide");
    }
}

function resetTableData() {
    $("#selectedCondition").attr("style", "display:none;");
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
        obj["alarmFlag"] = "N";
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
                $MB.n_success(r.msg);
            }else {
                $MB.n_danger(r.msg);
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
        $("#selectedCondition").attr("style", "display:none;");
    }else {
        $("#selectedCondition").attr("style", "display:block;");
        $("#dataTable").html("").html(code);
    }
    conditionVal = new Array();
}

function alarmFlag(dom) {
    var flag = null;
    flag = jm.get_selected_node().data.alarmFlag;
    jm.enable_edit();
    if(flag === "Y") {
        flag = "N";
        jm.get_selected_node().data.alarmFlag = flag;
        $(dom).html("").html("<i class=\"fa fa-check-square\" data-item=\"2\"></i>&nbsp; 标记");
        jm.set_node_color(jm.get_selected_node().id, 'rgb(26, 188, 156)', '');
    }else if(flag === "N" || flag == undefined || flag == null) {
        flag = "Y";
        jm.get_selected_node().data.alarmFlag = flag;
        $(dom).html("").html("<i class=\"fa fa-close\" data-item=\"2\"></i>&nbsp; 取消");
        jm.set_node_color(jm.get_selected_node().id, 'red', '');
    }
    jm.disable_edit();
    updateAlarmFlag(flag);
}

function updateAlarmFlag(flag) {
    var nodeId =  jm.get_selected_node().id;
    $.post("/diagdetail/updateAlarmFlag", {diagId: diagId, nodeId: nodeId, flag: flag}, function (r) {
        if(r.code != 200) {
            $MB.n_danger("节点标记失败！");
        }
        // 隐藏操作按钮
        var e = document.getElementById("operateBtns");
        e.style.display = "none";
    });
}

function getParentCondition() {
    var array = jm.get_selected_node().data.conditions;
    var code = "";
    if(array != null && array != undefined) {
        $.each(array, function (k, v) {
            code += "<tr><td style='text-align: left;'>" + v.dimName.trim() + ":";
            code += v.dimValueDisplay.trim();
            var inheritFlag = (v.inheritFlag == undefined || v.inheritFlag == null) ? "":v.inheritFlag;
            code += "<input type='hidden' name='dimValues' value='"+v.dimValues+"'><input type='hidden' name='condition' value='"+v.dimCode+"'><input name='inheritFlag' type='hidden' value='"+ inheritFlag +"'></td><td></td></tr>";
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
        $MB.n_warning('未选择条件或值！');
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

        if($("#dataTable").find("tr").length == 0) {
            $("#selectedCondition").attr("style", "display:block;");
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
        }else if(v.inheritFlag == "A"){
            code += v.dimValueDisplay + "&nbsp;(加法拆分)"
        }else if(v.inheritFlag == "N"){
            code += v.dimValueDisplay;
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
    saveRedisHandleInfo(null,levelId, jm.get_selected_node().data.kpiCode, jm.get_selected_node().data.kpiName, null);
}

// 加法节点为叶子节点
function endNode(nodeId) {
    jm.enable_edit();
    jm.set_node_color(nodeId, '#8b95a5', '');
    jm.disable_edit();
}

/**
 * 根节点的handleType为F
 * @param whereinfo
 */
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
    saveDiagHandleInfo(handleInfo, null);
}

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
        obj["alarmFlag"] = "N";
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
                $MB.n_success(r.msg);
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
        $MB.n_warning('请选择过滤条件！');
        lightyear.loading('hide');
    }else {
        // createNode(nodeName, levelId, jm.get_selected_node().data.kpiCode, jm.get_selected_node().data.kpiName, false, null);
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
    var alarmFlag = "N";
    // var conditions = new Array();
    // if(condition != null) {
    //     conditions.push(condition);
    // }
    // conditions = conditions.concat(filterCondition());

    var data = new Object();
    data.kpiCode = kpiCode;
    data.kpiName = kpiName;
    data.kpiLevelId = kpiLevelId;
    data.alarmFlag = alarmFlag;
    data.conditions = condition;
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
function saveRedisHandleInfo(nodeName, levelId, kpiCode, kpiName, map) {
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
    saveDiagHandleInfo(handleInfo, operateType, map);
}
var flag = false;
function saveDiagHandleInfo(handleInfo, operateType, map) {
    $.post("/progress/saveDiagHandleInfo", {diagHandleInfo: JSON.stringify(handleInfo)}, function (res) {
        if(res.code == 500) {
            $MB.n_danger('操作失败，服务出现异常了，快反馈给系统运维人员吧！');
        }else {
            if(operateType == "A") {
                var levelId = handleInfo.kpiLevelId;
                $.get("/progress/generateDiagData", {diagId: diagId, kpiLevelId: levelId}, function (r) {
                    var kpiCode = r.data.kpiCode;
                    var kpiName = r.data.kpiName;
                    $.each(r.data.nodeList, function(k, v) {
                        var nodeName = levelId + " <i class='mdi mdi-key-plus'></i> " + $("#op4").find("option:selected").text() + "(" + v.name + ")";
                        var condition = r.data.whereinfo;
                        if(condition == null ) {
                            condition = new Array();
                        }
                        condition = condition.concat(getConditionList(v));
                        createNode(nodeName, levelId, kpiCode, kpiName, false, condition);
                    });
                });
            }
            if(operateType == "M" && !flag) {
                var levelId = handleInfo.kpiLevelId;
                $.get("/progress/generateDiagData", {diagId: diagId, kpiLevelId: levelId}, function (r) {
                    var condition = r.data.whereinfo;
                    var kpiCode1 = map["dismantPart1Code"];
                    var kpiCode2 = map["dismantPart2Code"];
                    var kpiName1 = map["dismantPart1Name"];
                    var kpiName2 = map["dismantPart2Name"];
                    var nodeName1 = levelId + " <i class='mdi mdi-key-remove'></i> " + kpiName1;
                    createNode(nodeName1, levelId, kpiCode1, kpiName1, false, condition);
                    var nodeName2 = levelId + " <i class='mdi mdi-key-remove'></i> " + kpiName2;
                    createNode(nodeName2, levelId, kpiCode2, kpiName2, false, condition);
                });
                flag = true;
            }
            if(operateType == "F") {
                var levelId = handleInfo.kpiLevelId;
                $.get("/progress/generateDiagData", {diagId: diagId, kpiLevelId: levelId}, function (r) {
                    var condition = r.data.whereinfo;
                    var nodeName = levelId + " <i class='mdi mdi-filter'></i> " + $("#currentNode").val();
                    var kpiCode = r.data.kpiCode;
                    var kpiName = r.data.kpiName;
                    createNode(nodeName, levelId, kpiCode, kpiName, false, condition);
                });
                flag = true;
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
    tmp.inheritFlag = "A";
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
    // 条件数组去除该记录
    conditionVal.forEach(function(item, index, arr){
        if(item == val) {
            conditionVal.splice(index, 1);
        }
    });

    if(conditionVal.length == 0) {
        if($("#dataTable").find("tr").length == 0) {
            $("#selectedCondition").attr("style", "display:none;");
        }
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
    var kpiName1 = map["dismantPart1Name"];
    var kpiName2 = map["dismantPart2Name"];
    var levelId = getKpiLevelId();
    var nodeName1 = levelId + " <i class='mdi mdi-key-remove'></i> " + kpiName1;
    var nodeName2 = levelId + " <i class='mdi mdi-key-remove'></i> " + kpiName2;
    $("#nodeAddModal").modal('hide');
    saveRedisHandleInfo(nodeName1, levelId, jm.get_selected_node().data.kpiCode, jm.get_selected_node().data.kpiName, map);
    saveRedisHandleInfo(nodeName2, levelId, jm.get_selected_node().data.kpiCode, jm.get_selected_node().data.kpiName, map);
    flag = false;
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
            $MB.n_warning(name+"无法按乘法进行诊断！");
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

            // 将操作按钮的标记初始化
            jm.enable_edit();
            var flag = jm.get_selected_node().data.alarmFlag;
            if(flag == undefined || flag == "N") {
                $("#alarmFlag").html("").html("<i class=\"fa fa-check-square\" data-item=\"2\"></i>&nbsp; 标记");
            }else if(flag == "Y"){
                $("#alarmFlag").html("").html("<i class=\"fa fa-close\" data-item=\"2\"></i>&nbsp; 取消");
            }
            jm.disable_edit();
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
    var period = getPeriodTypeVal() == 'M' ? '月':'日';
    $("#reasonPeriod").val(period);
    $("#reasonTimeBegin").val($("#beginDt").val());
    $("#reasonTimeEnd").val($("#endDt").val());
    reasonAddCondition();
    $("#nodeReasonAddModal").modal('show');
}

$("#nodeReasonAddModal").on("hidden.bs.modal", function () {
    $("#reasonKpiCode").val("");
    $("#reasonKpiName").val("");
    $("#reasonPeriod").val("");
    $("#reasonTimeBegin").val("");
    $("#reasonTimeEnd").val("");
    $("#selectedTable").html("").html("<tr><td><i class=\"mdi mdi-alert-circle-outline\"></i>暂无数据！</td></tr>");
});

function reasonAddBefore() {
    var selectedNode = jm.get_selected_node();
    $.get("/progress/checkReasonList", {kpiCode: selectedNode.data.kpiCode, kpiName: selectedNode.data.kpiName}, function(r) {
        if(!r.data) {
            $MB.n_warning('当前指标暂不支持进行原因探究！');
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