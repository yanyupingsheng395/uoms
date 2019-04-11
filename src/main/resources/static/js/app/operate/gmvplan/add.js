$(function(){
    stepInit();
});

$('#predictDate').datepicker({
    format: 'yyyy',
    language: "zh-CN",
    todayHighlight: true,
    autoclose: true,
    startView: 'years',
    maxViewMode:'years',
    minViewMode:'years'
});

function prev() {
    if($("#step2").attr("style") == "display:block;") {
        $("#prev").addClass("disabled");
        $("#step1").attr("style", "display:block;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:none;");
    }else if($("#step3").attr("style") == "display:block;") {
        $(this).attr("disabled", false);
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:block;");
        $("#step3").attr("style", "display:none;");
        getYearHistory();
        $("#next").removeClass("disabled");
    }
}
function next() {
    if($("#step1").attr("style") == "display:block;") {
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:block;");
        $("#step3").attr("style", "display:none;");
        getYearHistory();
        $("#prev").removeClass("disabled");
    }else if($("#step2").attr("style") == "display:block;") {
        confirmData();
    }
}

function stepInit() {
    $("#prev").addClass("disabled");
    $("#step1").attr("style", "display:block;");
    $("#step2").attr("style", "display:none;");
    $("#step3").attr("style", "display:none;");
}
function getYearHistory() {
    var year = $("#predictDate").val();
    $('#historyDataTable').bootstrapTable({
        url: '/gmvplan/getYearHistory?year=' + year,
        striped: true,
        uniqueId: 'attrValue',
        columns: [{
            title: '年份',
            field: 'yearId'
        },{
            title: 'GMV值',
            field: 'gmvValue',
            formatter: function (value, row, index) {
                return "¥&nbsp;" + parseFloat(value).toLocaleString();
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


function weight() {
    var year = $("#predictDate").val();
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
            title: 'GMV值',
            field: 'gmvValue'
        },{
            title: 'GMV目标占全年比例',
            field: 'gmvPct',
            formatter: function (value, row, index) {
                return value + "%";
            }
        },{
            title: '上年同月GMV值',
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

// 步骤2新增plan表和plan_detail表
function addPlanAndDetail() {
    var year = $("#predictDate").val();
    var gmv = $("#gmvValue2").val();
    var rate = $("#gmvRate2").val();
    $.post("/gmvplan/addPlanAndDetail", {year: year, gmv: gmv, rate: rate},function (r) {
        getPlanDetail();
    });
}

// 编辑GMV值
function editPlanDetail() {
    $("#planDetailData tbody tr").find("td:eq(1)").each(function (k,v) {
        v.innerHTML = "<input type='text' name='newGmvValue' class='form-control' value='"+v.innerText+"'/>";
    });
}

function getPlanAndDetail() {
    var year = $("#predictDate").val();
    $.post("/gmvplan/getPlanAndDetail", {year: year}, function (r) {
        if(r.msg) {
            confirmData();
        }else {
            addPlanAndDetail();
        }
    });
}

function confirmData() {
    $.confirm({
        title: '提示：',
        content: '查询到已有数据，即将被覆盖？',
        buttons: {
            confirm: {
                text: '确认',
                btnClass: 'btn-primary',
                action: function(){
                    overrideOldData();
                }
            },
            cancel: {
                text: '关闭'
            }
        }
    });
}

// 覆盖已有plan/detail数据
function overrideOldData() {
    var year = $("#predictDate").val();
    var gmv = $("#gmvValue2").val();
    var rate = $("#gmvRate2").val();
    $.post("/gmvplan/overrideOldData", {year: year, gmv: gmv, rate: rate}, function (r) {
        weight();
        getPlanDetail();
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:block;");
        $("#next").attr("style", "display:none;");
        $("#prev").attr("style", "display:none;");
    });
}

function updateDetail() {
    toastr.options = {
        "progressBar": true,
        "positionClass": "toast-top-center",
        "preventDuplicates": true,
        "timeOut": 1500,
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
    var json = new Array();
    $("input[name='newGmvValue']").each(function() {
        json.push($(this).val());
    });
    var year = $("#predictDate").val();
    $.post("/gmvplan/updateDetail", {year: year, gmv: JSON.stringify(json)}, function(r) {
        toastr.success("数据更新成功！");
        setTimeout(function () {
            $("#planDetailData tbody tr").find("td:eq(1)").each(function (k,v) {
                var val = $(this).find("input").val();
                $(this).find("input").remove();
                $(this).text(val);
            });
        }, 1500);
    });
}