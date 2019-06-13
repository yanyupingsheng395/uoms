/**
 * 乘法：序号 name
 * 加法：序号 name=KPI名称/维度名称
 * 仅过滤：序号 KPI名称/过滤
 */

$(function () {
    getKpi();
});

// 设定诊断问题-获取指标
function getKpi() {
    $.get("/diag/getKpi", {}, function (r) {
        var code = "<option>请选择</option>";
        $.each(r.data, function (k, v) {
            code += "<option value='"+k+"'>"+v+"</option>";
        });
        $("#targetKpi").html("").html(code);
        $("#targetKpi").selectpicker('refresh');
    });
}

// 设定诊断问题-选择维度
function dimSelected() {
    $('#dimModal').modal('show');
}

$("#dimModal").on('show.bs.modal', function () {
    $("#dimension").selectpicker('val', '');
    $("#dimensionVal").selectpicker('val', '');
});

$("#dimModal").on('shown.bs.modal', function () {
    getDimensionList();
});

// 获取维度
function getDimensionList() {
    $.get("/diag/getDimension", {}, function (r) {
        var code = "<option>请选择</option>";
        $.each(r.data, function (k, v) {
            code += "<option value='"+k+"'>"+v+"</option>";
        });
        $("#dimension").html("").html(code);
        $("#dimension").selectpicker('refresh');
    });
}

$("#dimension").change(function() {
    var key = $(this).find("option:selected").val();
    if(key != "") {
        getDimensionVal(key);
    }
});

// 获取维度值
function getDimensionVal(key) {
    $.get("/diag/getDimensionVal", {key: key}, function (r) {
        var code = "";
        $.each(r.data, function (k, v) {
            code += "<option value='"+k+"'>"+v+"</option>";
        });
        $("#dimensionVal").html("").html(code);
        $("#dimensionVal").selectpicker('refresh');
    });
}

// 用于存储已选的值
var temp = "";
function addDimension() {
    var k_code = $("#dimension").find("option:selected").val();
    var v_code = $("#dimensionVal").selectpicker('val');
    var codeStr = k_code + ":" + v_code;
    if(temp.indexOf(codeStr) == -1) {
        temp += codeStr + "|";
        var k_text = $("#dimension").find("option:selected").text();
        var textArr = new Array();
        $("#dimensionVal").find("option").each(function (k, v) {
            if(v_code.indexOf($(this).val()) > -1) {
                textArr.push($(this).text());
            }
        });
        if($("#dimDataTable").find("tr").length == 0) {
            $("#conditions").show();
        }
        var v_text = textArr.join("|");
        var code = "<tr><input type='hidden' name='dimensions' value='&dimCode="+k_code+"&dimName="+k_text+"&dimValues="+v_code+"&dimValueDisplay="+v_text+"'/>";
        code += "<td class='text-left'>"+k_text + "&nbsp;:&nbsp;" + v_text +"</td><td class=\"text-right\"><i class=\"mdi mdi-close\" onclick='removeTd(this)'></i></td>";
        code += "</tr>";
        $("#dimDataTable").append(code);
        $("#dimModal").modal('hide');
    }else {
        toastr.warning("已有相同维度值在列表中！");
        $("#dimModal").modal('hide');
    }
}

function removeTd(dom) {
    $(dom).parent().parent().remove();
    if($("#dimDataTable").find("tr").length == 0) {
        $("#conditions").hide();
    }
}

$("#targetKpi").change(function () {
    $("#targetKpiTxt").val($(this).find("option:selected").val());
});

validate();
var validator;
function validate() {
    var icon = "<i class='mdi mdi-close-circle'></i> ";
    var rule = {
        rules: {
            diagName: {
                required: true
            },
            beginDt: {
                required: true
            },
            endDt: {
                required: true
            },
            targetKpiTxt: {
                required: true
            }
        },
        messages: {
            diagName: {
                required: icon + "请输入诊断名称"
            },
            beginDt: {
                required: icon + "请选择开始时间"
            },
            endDt: {
                required: icon + "请选择结束时间"
            },
            targetKpiTxt: {
                required: icon + "请选择指标"
            }
        }
    };
    validator = $("#formTable").validate(rule);
}

function getDimensionInfo() {
    var arr = new Array();
    $("#dimDataTable").find("tr").each(function(k, v) {
        arr.push($(this).find("td:eq(0)").text());
    });
    return arr.join(";");
}

function getDimAndVal() {
    var arr = new Array();
    $("#dimDataTable").find("tr").each(function(k, v) {
        var flag = false;
        var dimensions = $(this).find("input[name='dimensions']").val();
        var o = new Object();
        var t = dimensions.split("&");
        $.each(t, function (k1, v1) {
            if(v1 != "") {
                flag = true;
                var tempArr = v1.split("=");
                o[tempArr[0]] = tempArr[1];
            }
        });
        if(flag) {
            arr.push(o);
        }
    });
    return arr;
}

// 保存诊断初始信息
function saveDiagInfo(dom) {
    var flag = validator.form();
    if(flag) {
        $("#diagAddName").html("").html("寻找关键部分");
        var conditionList = getDimAndVal();
        var dimDisplayName = getDimensionInfo();
        var formData = $("#formTable").serialize() + "&dimDisplayName=" + dimDisplayName + "&conditions=" + JSON.stringify(conditionList);
        $.post("/diag/add", formData, function(r) {
            $("#period_type").html("").html($("#periodType option:selected").text());
            $("#date_area").html("").html($("#beginDt").val() + "&nbsp;到&nbsp;" + $("#endDt").val());

            $("#step1").attr("style", "display:none;");
            $("#step2").attr("style", "display:block;");
            $(dom).parent("div").removeClass("text-right").addClass("text-center");
            $(dom).remove();
            diagId = r.data;
            createRootNode(conditionList);
            makeInitConditionTree("initConditionTree", conditionList);
        });
    }
}

function makeInitConditionTree(treeId, data) {
    var treeArr = [];
    // 条件
    $.each(data, function (k, v) {
        var o = new Object();
        o.id = k + 1;
        o.name = v.dimName + ":" + v.dimValueDisplay;
        o.pId = 0;
        treeArr.push(o);
    });
    treeArr.push({id:0, pId:-1, name:'过滤条件'});
    createWhereInfoTree(treeId, treeArr);
}
