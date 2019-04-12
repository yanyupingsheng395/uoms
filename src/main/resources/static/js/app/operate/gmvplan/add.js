// 是否有该年份的数据标志
var flag = false;
var year = null;
$(function(){
    stepInit();
    dateInit();
});

toastr.options = {
    "closeButton": true,
    "progressBar": true,
    "positionClass": "toast-top-center",
    "preventDuplicates": true,
    "timeOut": 1500,
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};

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
    $("#gmvValue2").val($("#gmvValue1").val());
    $("#gmvRate2").val($("#gmvRate1").val());

    $("#step1").attr("style", "display:none;");
    $("#step2").attr("style", "display:none;");
    $("#step3").attr("style", "display:block;");
    $("#next").attr("style", "display:none;");
    $("#prev").attr("style", "display:none;");

    // 获取权重数据
    weight();
    // 插入数据
    addPlanAndDetail();
}

// 步骤2新增plan表和plan_detail表
function addPlanAndDetail() {
    var gmv = $("#gmvValue1").val();
    var rate = $("#gmvRate1").val();
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
function getYearHistory() {
    $('#historyDataTable').bootstrapTable({
        url: '/gmvplan/getYearHistory?year=' + year,
        striped: true,
        uniqueId: 'attrValue',
        columns: [{
            title: '年份',
            field: 'yearId'
        },{
            title: 'GMV值（元）',
            field: 'gmvValue',
            formatter: function (value, row, index) {
                return parseFloat(value).toLocaleString();
            }
        },{
            title: 'GMV增长率',
            field: 'gmvRate',
            formatter: function (value, row, index) {
                return value.toFixed(2) + "%";
            }
        }]
    });
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

    $("#gmvValue1").val(predictVal);
    $("#gmvValue2").val(predictVal);
    $("#gmvRate1").val(predictRate);
    $("#gmvRate2").val(predictRate);
}


function weight() {
    $.post("/gmvplan/getWeightIndex", {"year": year}, function (r) {
        var htmlCode1 = "<tr>";
        var htmlCode2 = "<tr>";
        $.each(r, function (k, v) {
            htmlCode1 += "<td>"+ v.monthId +"</td>";
            htmlCode2 += "<td>"+ v.indexValue +"</td>";
        });
        htmlCode1 += "</tr>";
        htmlCode2 += "</tr>";
        $("#weightData").html("").html(htmlCode1 + htmlCode2);
        $("#weightData").find("tr:eq(0)").addClass("active");
    });
}

function getPlanDetail() {
    var year = $("#predictDate").val();
    $('#planDetailData').bootstrapTable({
        url: '/gmvplan/getPlanDetail?year=' + year,
        striped: true,
        uniqueId: 'attrValue',
        columns: [{
            title: '月份',
            field: 'monthId'
        },{
            title: 'GMV值（元）',
            field: 'gmvValue'
        },{
            title: 'GMV目标占全年比例',
            field: 'gmvPct',
            formatter: function (value, row, index) {
                return value + "%";
            }
        },{
            title: '上年同月GMV值（元）',
            field: 'gmvTb'
        },{
            title: '同比上年同比增长率',
            field: 'gmvTbRate',
            formatter: function (value, row, index) {
                return value + "%";
            }
        }]
    });
}


// 编辑GMV值
function editPlanDetail() {
    $("#planDetailData tbody tr").find("td:eq(1)").each(function (k,v) {
        v.innerHTML = "<input type='text' name='newGmvValue' onchange='totalGmv()' class='form-control' value='"+v.innerText+"' onkeyup=\"value=value.match(/\\d+\\.?\\d{0,2}/,'')\"/>";
    });
    $("#saveBtn").removeAttr("disabled");
}

var totalFlag = false;
function totalGmv() {
    var total = 0;
    $("#planDetailData tbody tr").find("td:eq(1)").each(function (k,v) {
        total += parseFloat($(this).find("input").val());
    });
    $("#totalGmv").text(total.toFixed(2));
    var differ = Math.abs(total-parseFloat($("#gmvValue1").val())).toFixed(2);
    $("#totalDiffer").text(differ);

    if(differ != 0 || differ != "0") {
        $("#totalFooter").attr("style", "display:block;margin-top:20px;");
        totalFlag = true;
    }else {
        $("#totalFooter").attr("style", "display:none;");
        totalFlag = false;
    }
}

function reback() {
    if(step != 0) {
        $.confirm({
            title: '提示：',
            content: '确认离开当前页？',
            buttons: {
                confirm: {
                    text: '确认',
                    btnClass: 'btn-primary',
                    action: function(){
                        window.location.href = "/page/gmvplan";
                    }
                },
                cancel: {
                    text: '关闭'
                }
            }
        });
    }else {
        window.location.href = "/page/gmvplan";
    }
}
function updateDetail() {
    $("#saveBtn").attr("disabled","disabled");
    var json = new Array();
    $("input[name='newGmvValue']").each(function() {
        json.push($(this).val());
    });
    var condition = $("input[name='e']:checked").val();
    if(totalFlag && (condition == undefined)) {
        toastr.warning("请选择数据变更策略！");
    }else {
        $.post("/gmvplan/updateDetail", {year: year, gmv: JSON.stringify(json)}, function(r) {
            toastr.success("数据更新成功！");
            setTimeout(function () {
                $("#planDetailData tbody tr").find("td:eq(1)").each(function (k,v) {
                    var val = $(this).find("input").val();
                    $(this).find("input").remove();
                    $(this).text(val);
                });
            }, 1500);
            $("#totalFooter").attr("style", "display:none;");
            totalFlag = false;
            $("input[name='e']").removeAttr("checked");
        });
    }
}