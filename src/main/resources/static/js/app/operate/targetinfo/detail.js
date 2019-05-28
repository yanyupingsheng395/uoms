$.get("/target/getDataById", {id: id}, function (r) {
    if(r.data["END_DT"] == undefined) {
        $("#date").html("").html(r.data["START_DT"]);
    }else {
        $("#date").html("").html(r.data["START_DT"] + "到" + r.data["END_DT"]);
    }
    $("#kpi").html("").html(r.data["KPI_NAME"]);
    var code = "";
    $.each(r.data["DIMENSIONS"], function (k, v) {
        code += "<li>"+v["DIMENSION_NAME"] + ":" + v["DIMENSION_VAL_NAME"]+"</li>";
    });
    if(code == "") {
        code += "<p style='margin-left: 20px;'><i class='mdi mdi-alert-circle-outline'></i>暂无维度及其值！</p>";
    }else {
        code = "<ol>" + code;
        code += "</ol>";
    }
    $("#dimensions").html("").html(code);
});

getReferenceData();
function getReferenceData() {
    $.get("/target/getReferenceDataById", {id: id}, function (r) {
        var code = "";
        $.each(r.data, function (k, v) {
            var yearOnYear = v['yearOnYear'] == null ? "-":v['yearOnYear'] + "%";
            var yearOverYear = v['yearOverYear'] == null ? "-":v['yearOverYear'] + "%";
            code += "<tr><td>"+v['period']+"</td><td>"+v['kpi']+"</td><td>"+yearOnYear+"</td><td>"+yearOverYear+"</td></tr>";
        });
        if(code == "") {
            code += "<tr><td class='text-center' colspan='4'><i class='mdi mdi-alert-circle-outline'></i>没有参照值！</td></tr>";
        }
        $("#reference").html("").html(code);
    });
}
getDismantData();
function getDismantData() {
    $.get("/target/getDismantData", {id: id}, function (r) {
        var chart = r.data['chart'];
        var option = getBarOption(chart['xAxisData'], chart['xAxisName'], chart['yAxisName'],  chart['yAxisData'], chart['xAxisName'], chart['yAxisName']);
        option.xAxis.axisLabel =  {
            interval:0,
            rotate:40
        };
        var c = echarts.init(document.getElementById("chart"), 'macarons');
        c.setOption(option);
        makeTable(r.data);
    });
}

function makeTable(data) {
    var code = "<thead><tr><th></th>";
    $.each(data['head'], function (k, v) {
        code += "<th>" + v + "</th>";
    });
    code += "</tr></thead>";
    code += "<tbody><tr><td>分解目标</td>";
    $.each(data.row1, function (k, v) {
        code += "<td>" + v + "</td>";
    });
    code += "</tr>";
    code += "<tr><td>权重指数</td>";
    $.each(data.row2, function (k, v) {
        code += "<td>" + v + "</td>";
    });
    code += "</tr>";
    code += "<tr><td>目标占比</td>";
    $.each(data.row3, function (k, v) {
        code += "<td>" + v + "</td>";
    });
    code += "</tr>";
    code += "</tbody>";
    if(data['head'].length != 0) {
        $("#dismantDataTable").html("").html(code);
    }else {
        $("#dismantDataTable").hide();
    }
}