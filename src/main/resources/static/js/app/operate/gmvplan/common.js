toastr.options = {
    "closeButton": true,
    "progressBar": true,
    "positionClass": "toast-top-center",
    "preventDuplicates": true,
    "timeOut": 1500,
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};
function getYearHistory(year) {
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

function getPlanDetail(year) {
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

function weight(year) {
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


// 修改用到的
var totalFlag = false;
function updateDetail() {
    var json = new Array();
    $("input[name='newGmvValue']").each(function() {
        json.push($(this).val());
    });
    var year = $("#predictDate").val();
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