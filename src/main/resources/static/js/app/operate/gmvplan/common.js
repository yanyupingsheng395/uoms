var flag = false;
toastr.options = {
    "closeButton": true,
    "progressBar": true,
    "positionClass": "toast-top-center",
    "preventDuplicates": true,
    "timeOut": 1500,
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};

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
        var code = htmlCode1 + htmlCode2;
        if(r.length != 0) {
            $("#weightData").html("").html(code);
            $("#weightData").find("tr:eq(0)").addClass("active");
        }
    });
}

function getPlanDetail() {
    $('#planDetailData').bootstrapTable({
        url: '/gmvplan/getPlanDetail?year=' + year,
        striped: true,
        uniqueId: 'attrValue',
        columns: [{
            title: '历史记录',
            field: 'isHistory',
            visible: false
        }, {
            title: '月份',
            field: 'monthId'
        },{
            title: 'GMV值（元）',
            field: 'gmvValue',
            formatter: function (value, row, index) {
                return "<a style='border-bottom:dashed 1px #000; color: #383838;cursor: pointer;' onclick='edit(this)'><i class='mdi mdi-lead-pencil'></i>" + value + "</a>";
            }
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
                if(value == null) {
                    return "-";
                }else {
                    return value + "%";
                }
            }
        }]
    });
}

function edit(dom) {
    var val = $(dom).text();
    var code = "<input type='text' onchange='totalGmv()' name='newGmvValue' value='"+val+"' class='form-control' onblur='removeInput(this, "+val+")'/>";
    $(dom).parent().html("").html(code);
    $("input[name='newGmvValue']").focus();
}

function removeInput(dom, old) {
    var val = $(dom).val();
    if(old == val) {
        $(dom).parent().html("").html("<a style='border-bottom:dashed 1px #000; color: #383838;cursor: pointer;' onclick='edit(this)'><i class='mdi mdi-lead-pencil'></i>" + val + "</a>");
    }else {
        $("#saveBtn").removeAttr("disabled");
        $(dom).parent().html("").html("<a style='border-bottom:dashed 1px #000; color: #8b95a5;cursor: pointer;' onclick='edit(this)'><i class='mdi mdi-lead-pencil'></i>" + val + "</a>");
    }
}

// 编辑GMV值
function editPlanDetail() {
    $("#planDetailData tbody tr").find("td:eq(1)").each(function (k,v) {
        v.innerHTML = "<input type='text' name='newGmvValue' onchange='totalGmv()' class='form-control' value='"+v.innerText+"' onkeyup=\"value=value.match(/\\d+\\.?\\d{0,2}/,'')\"/>";
    });
}

var totalFlag = false;
function totalGmv() {
    var total = 0;
    $("#planDetailData tbody tr").find("td:eq(1)").each(function (k,v) {
        if($(this).text() == "") {
            total += parseFloat($(this).find("input").val());
        }else {
            total += parseFloat($(this).text());
        }
    });
    $("#totalGmv").text(total.toFixed(2));
    var differ = Math.abs(total-parseFloat($("#gmvValue").val())).toFixed(2);
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

function reback_edit() {
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
}
function updateDetail() {
    var json = new Array();
    $("#planDetailData tbody tr").find("td:eq(1)").each(function (k,v) {
        json.push($(this).text().trim());
    });
    var condition = $("input[name='e']:checked").val();
    if(totalFlag && (condition == undefined)) {
        toastr.warning("请选择数据变更策略！");
    }else {
        $.post("/gmvplan/updateDetail", {year: year, gmv: JSON.stringify(json), method: $("input[name='e']:checked").val()}, function(r) {
            toastr.success("数据保存成功！");
            $("#totalFooter").attr("style", "display:none;");
            totalFlag = false;
            $("input[name='e']").removeAttr("checked");
            $("#saveBtn").attr("disabled","disabled");
        });
    }
}