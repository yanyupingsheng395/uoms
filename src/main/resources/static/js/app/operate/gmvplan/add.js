$(function(){
    $("#wizard").steps({
        onStepChanged: function (event, currentIndex, priorIndex) {
            if (currentIndex == 1) {
                getYearHistory();
                predictData();
            }
        }
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
});

function getYearHistory() {
    var year = $("#predictDate").val();
    $('#historyDataTable').bootstrapTable({
        url: '/gmvplan/getYearHistory?year=' + year,
        striped: true,
        uniqueId: 'attrValue',
        columns: [{
            title: '月份',
            field: 'yearId'
        },{
            title: 'GMV值',
            field: 'gmvValue'
        },{
            title: 'GMV增长率',
            field: 'gmvRate'
        }]
    });
}

function predictData() {
    var year = $("#predictDate").val();
    $.post("/gmvplan/getPredictData", {"year": year}, function (r) {
        var htmlCode = "";
        $.each(r, function (k, v) {
            htmlCode += "<tr><td>预测方法"+ k+1 +"</td><td>" + v.gmvTarget + "</td><td>" + v.targetRate + "</td></tr>";
        });
        $("#predictData").html("").html(htmlCode);
    });
}
