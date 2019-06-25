$(function(){
    stepInit();
    getKpi();
    getDimension();
    validateRule();
});

function btnSelect() {
    $('#denModal').modal('show');
}

// 下一步
function next(dom) {
    var flag = validator.form();
    if(flag) {
        var startDt = $("#startDt").val();
        var endDt = $("#endDt").val();
        var periodType = $("#targetPeriod").find("option:selected").val();
        if(periodType == "year" || periodType == "month") {
            if(startDt == "") {
                $MB.n_warning("请选择目标日期！");
            }else {
                $("#step1").attr("style", "display:none;");
                $("#step2").attr("style", "display:block;");
                $(dom).html("").html("<i class='fa fa-save'></i>保存");
                $(dom).attr("onclick", "saveTarget()");
                $("#_tgtDate").html("").html($("input[name='startDt']").val());
                step2Init();
            }
        }else {
            if(startDt == "" || endDt == "") {
                $MB.n_warning("请选择开始或结束日期！");
            }else {
                $("#step1").attr("style", "display:none;");
                $("#step2").attr("style", "display:block;");
                $("#_tgtDate").html("").html($("input[name='startDt']").val() + "到" + $("input[name='endDt']").val());
                $(dom).text("完成");
                $(dom).attr("onclick", "saveTarget()");
                step2Init();
            }
        }
    }
}

$("#targetKpi").change(function () {
    $("#targetKpiTxt").val($(this).find("option:selected").val());
});

function step2Init() {
    $("#_kpi").html("").html($("#targetKpi").find("option:selected").text());
    var code = "";
    $("#dataTable").find("tr").each(function (k, v) {
        code += "<li>"+$(this).find("td:eq(0)").text()+"</li>";
    });
    if(code == "") {
        code = "暂无维度和值！";
    }
    $("#_dimensions").html("").html(code);

    var startDt = $("#startDt").val();
    var endDt = $("#endDt").val();
    var periodType = $("#targetPeriod").find("option:selected").val();

    var dimInfo = getDimInfo();
    var kpiCode = $("#targetKpi").find("option:selected").val();
    $.get("/target/getReferenceData", {kpiCode: kpiCode, startDt: startDt, endDt: endDt, period: periodType, dimInfo:JSON.stringify(dimInfo)}, function (r) {
        var code = "";
        $.each(r.data, function(k, v){
            var yearOnYear = v["yearOnYear"] == null ? "-" : v["yearOnYear"] + "%";
            var yearOverYear = v["yearOverYear"] == null ? "-" : v["yearOverYear"] + "%";
            code += "<tr><td>"+v["period"]+"</td><td>"+v["kpi"]+"</td><td>"+yearOnYear+"</td><td>"+yearOverYear+"</td></tr>";
        });
        if(code == "") {
            code += "<tr><td class='text-center' colspan='4'><i class='mdi mdi-alert-circle-outline'></i>没有参照值！</td></tr>";
        }
        $("#_reference").html("").html(code);
    });
}

function getDimInfo() {
    var result = new Array();
    $("#dataTable").find("input[name='dimensions']").each(function (k, v) {
        var arr = $(this).val().split("&");
        var o = new Object();
        $.each(arr, function (k1, v1) {
            if(k1 != "" && v1 != "") {
                var t = v1.split("=");
                o[t[0]] = t[1];
            }
        });
        result.push(o);
    });
    return result;
}

// 保存目标
function saveTarget() {
    var flag = validator2.form();
    if(flag) {
        var targetList = new Object();
        targetList.periodType = $("#targetPeriod").find("option:selected").val();
        targetList.startDt = $("input[name='startDt']").val();
        targetList.endDt = $("input[name='endDt']").val();
        targetList.name = $("input[name='name']").val();
        targetList.kpiCode = $("#targetKpi").find("option:selected").val();
        targetList.targetVal = $("input[name='targetVal']").val();

        var targetDimensionList = new Array();
        $("#dataTable").find("input[name='dimensions']").each(function (k, v) {
            var arr = $(this).val().split("&");
            var o = new Object();
            $.each(arr, function (k1, v1) {
                if(k1 != "" && v1 != "") {
                    o[v1.split("=")[0]] = v1.split("=")[1];
                }
            });
            targetDimensionList.push(o);
        });
        targetList.dimensionList = targetDimensionList;

        $.ajax({
            url: "/target/save",
            data: JSON.stringify(targetList),
            type: 'POST',
            contentType: "application/json;charset=utf-8",
            success: function (r) {
                $MB.n_success("保存成功！");
                setTimeout(function () {
                    location.href = "/page/target";
                }, 1500)
            }
        });
    }
}

// 获取指标
function getKpi() {
    $.get("/target/getKpi", {}, function (r) {
        var code = "<option>请选择</option>";
        $.each(r.data, function (k, v) {
            code += "<option value='"+k+"'>"+v+"</option>";
        });
        $("#targetKpi").html("").html(code);
        $("#targetKpi").selectpicker('refresh');
    });
}

// 获取维度
function getDimension() {
    $.get("/target/getDimension", {}, function (r) {
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
    $.get("/target/getDimensionVal", {key: key}, function (r) {
        var code = "";
        $.each(r.data, function (k, v) {
            code += "<option value='"+k+"'>"+v+"</option>";
        });
        $("#dimensionVal").html("").html(code);
        $("#dimensionVal").selectpicker('refresh');
    });
}
var dataDt = new Date();
init_date("startDt", "yyyy", 2, 2, 2);
$('#startDt').datepicker("setStartDate", dataDt);

// 时间周期选择事件
$("#targetPeriod").change(function () {
    $("#startDt").val("");
    $("#endDt").val("");
    $("#startDt").datepicker('destroy');
    $("#endDt").datepicker('destroy');
    var period = $(this).find("option:selected").val();
    if(period == 'year') {
        $("#dateLabel").html("").html("目标时间：");
        $("#endDtDiv").hide();
        init_date("startDt", "yyyy", 2, 2, 2);
        $('#startDt').datepicker("setStartDate", dataDt);
    }
    if(period == "month") {
        $("#dateLabel").html("").html("目标时间：");
        $("#endDtDiv").hide();
        init_date("startDt", "yyyy-mm", 1, 2, 1);
        $('#startDt').datepicker("setStartDate", dataDt);
    }
    if(period == "day") {
        $("#endDtDiv").show();
        $("#dateLabel").html("").html("开始时间：");
        var date = new Date();
        date.setDate(date.getDate() + 1);
        init_date_begin("startDt", "endDt", "yyyy-mm-dd",0,2 ,0);
        $('#startDt').datepicker("setStartDate", date);
        init_date_end("startDt", "endDt", "yyyy-mm-dd",0,2 ,0);
    }
});

var validator;
var $dataForm = $("#formData1");
function validateRule() {
    var period = $("#targetPeriod").find("option:selected").val();
    var icon = "<i class='mdi mdi-close-circle'></i> ";
    var rule = {
        rules: {
            name: {
                required: true,
                minlength: 2,
                maxlength: 64,
            },
            startDt: {required: true},
            targetKpiTxt: {required: true}
        },
        messages: {
            name: {
                required: icon + "请输入目标名称",
                minlength: icon + "名称长度2到64个字符"
            },
            startDt: {
                required: icon + "请输入目标时间"
            },
            endDt: {
                required: icon + "请输入目标时间"
            },
            targetKpiTxt: {
                required: icon + "请选择指标"
            }
        }
    };
    if(period == "day") {
        rule.rules.endDt = {required: true};
        rule.messages.endDt = {
            required: icon + "请输入目标时间"
        };
    }
    validator = $dataForm.validate(rule);
}

validate();
var validator2;
function validate() {
    var icon = "<i class='mdi mdi-close-circle'></i> ";
    var rule = {
        rules: {
            targetVal: {
                required: true,
                minlength: 2,
                maxlength: 10,
                digits: true
            }
        },
        messages: {
            targetVal: {
                required: icon + "请输入目标值",
                minlength: icon + "名称长度2到10个字符",
                digits: icon + "请输入数字"
            }
        }
    };
    validator2 = $("#formData2").validate(rule);
}

function addDimension() {
    var k_code = $("#dimension").find("option:selected").val();
    var k_text = $("#dimension").find("option:selected").text();
    var v_code = $("#dimensionVal").selectpicker('val');
    var textArr = new Array();
    $("#dimensionVal").find("option").each(function (k, v) {
        if(v_code.indexOf($(this).val()) > -1) {
            textArr.push($(this).text());
        }
    });

    if($("#dataTable").find("tr").length == 0) {
        $("#conditions").show();
    }
    var v_text = textArr.join(",");

    var flag = false;
    $("#dataTable").find("tr").each(function (k, v) {
        if($(this).find("td:eq(0)").text() == k_text + ":" + v_text) {
            flag = true;
            $MB.n_warning("已有相同维度值在列表中！");
        }
    });

    if(!flag) {
        var code = "<tr><input type='hidden' name='dimensions' value='&dimensionCode="+k_code+"&dimensionName="+k_text+"&dimensionValCode="+v_code+"&dimensionValName="+v_text+"'/>";
        code += "<td class='text-left'>"+k_text + ":" + v_text +"</td><td class=\"text-right\"><i class=\"mdi mdi-close\" onclick='removeTd(this)'></i></td>";
        code += "</tr>";
        $("#dataTable").append(code);
        $("#denModal").modal('hide');
    }
}
$("#denModal").on('show.bs.modal', function () {
    $("#dimension").selectpicker('val', '');
    $("#dimensionVal").selectpicker('val', '');
});

function removeTd(dom) {
    $(dom).parent().parent().remove();
    if($("#dataTable").find("tr").length == 0) {
        $("#conditions").hide();
    }
}

function stepInit() {
    $("#step1").attr("style", "display:block;");
    $("#step2").attr("style", "display:none;");
}
