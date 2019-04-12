var year = null;

$(function(){
    stepInit();
    dateInit();
});

function dateInit() {
    var date = new Date();
    var current = date.getFullYear();
    date.setFullYear(date.getFullYear(), 12);
    var next = date.getFullYear();
    var code = "<option value='" + current + "'>" + current + "</option>";
    code += "<option value='" + next + "'>" + next + "</option>";
    $('#predictDate').html("").html(code);
    year = $("#predictDate").find("option:selected").val();
}

var step = 0;
// 上一步
function prev() {
    if(step > 0) {
        step = step - 1;
    }
    if(step == 0) {
        $("#step1").attr("style", "display:block;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:none;");
        $("#prev").addClass("disabled");
    }
}

// 下一步
function next() {
    if(step == 0) {
        step2();
    }
    if(step == 1) {
        step3();
    }
}

function step2() {
    // 判断是否有该年份的数据
    checkYear();
}

function step3() {
    $("#gmvValue2").val($("#gmvValue").val());
    $("#gmvRate2").val($("#gmvRate").val());

    $("#step1").attr("style", "display:none;");
    $("#step2").attr("style", "display:none;");
    $("#step3").attr("style", "display:block;");
    $("#next").attr("style", "display:none;");
    $("#prev").attr("style", "display:none;");
    $("#reback").attr("style", "display:none;");

    // 获取权重数据
    weight();
    // 插入数据
    addPlanAndDetail();
}

// 步骤2新增plan表和plan_detail表
function addPlanAndDetail() {
    var gmv = $("#gmvValue").val();
    var rate = $("#gmvRate").val();
    $.post("/gmvplan/addPlanAndDetail", {year: year, gmv: gmv, rate: rate},function (r) {
        getPlanDetail();
    });
}


$("#predictDate").change(function () {
    year = $("#predictDate").find("option:selected").val();
});


function checkYear() {
    $.post("/gmvplan/checkYear", {year: year}, function (r) {
        if(r.data != 0 || r.data != "0") {
            flag = true;
            toastr.warning(year + "年的目标记录已经存在，如需修改，请在列表界面选择修改或变更！");
        }else {
            $("#step1").attr("style", "display:none;");
            $("#step2").attr("style", "display:block;");
            $("#step3").attr("style", "display:none;");
            $("#prev").removeClass("disabled");
            // 获取历史数据
            getYearHistory();
            // 获取预测数据
            getPredictData();
            if(step < 2) {
                step = step + 1;
            }
        }
    });
}

function stepInit() {
    $("#prev").addClass("disabled");
    $("#step1").attr("style", "display:block;");
    $("#step2").attr("style", "display:none;");
    $("#step3").attr("style", "display:none;");
}


function getRandom (m,n){
    var num = Math.floor(Math.random()*(m - n) + n);
    return num;
}

// 获取预测数据
function getPredictData() {
    var predictVal = getRandom(1000, 10000);
    var predictRate = getRandom(0, 100) + "." + getRandom(0, 100);
    $("#predictVal").text(predictVal);
    $("#predictRate").text(predictRate + "%");

    $("#gmvValue").val(predictVal);
    $("#gmvValue2").val(predictVal);
    $("#gmvRate").val(predictRate);
    $("#gmvRate2").val(predictRate);
}