$(function () {

    $('#startDt').datepicker({
        format: 'yyyy-mm-dd',
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true
    });

    $('#endDt').datepicker({
        format: 'yyyy-mm-dd',
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true
    });

    var today = new Date();
    $("#startDt").datepicker("setDate", new Date(today.getTime() - 24 * 60 * 60 * 1000 *30));
    $("#endDt").datepicker("setDate", new Date(today.getTime() - 24 * 60 * 60 * 1000));

    //初始化来源的列表
    initSourceList();

    //为下载按钮绑定事件
    $("#btn_downLoad").on("click",downLoadReport);

    //为查询按钮绑定事件
    $("#btn_query").on("click", queryReport);

    //触发一次查询
    $('#btn_query').trigger("click");
});

function  initSourceList() {
    //加载来源列表
    $.getJSON("/report/getSourceList", function (resp) {
        if (resp.code==200){
             var code = "<option value=''>所有</option>";
            $.each(resp.data, function (k, v) {
                code += "<option value='" + k + "'>" + v + "</option>";
            });
            $("#source").html("").html(code);
        }
    });
}

function queryReport() {
    var startDt= $('#startDt').val();
    var endDt= $('#endDt').val();
    var source=$("#source").find("option:selected").val();

    if(null==source || typeof(source)=="undefined" || "undefined"==source)
    {
        source="";
    }

    if(null==startDt||startDt=='')
    {
       //提示
        toastr.warning("开始日期不能为空！");
        return;
    }

    if(null==endDt||endDt=='')
    {
        //提示
        toastr.warning("结束日期不能为空！");
        return;
    }

    // //填充数据
    $.getJSON("/report/getBrandReportData?startDt="+startDt+"&endDt="+endDt+"&source="+source, function (resp) {
        if (resp.code == 200) {
            var code = "";
            $.each(resp.data, function(i, val) {
                code += "<tr> <td>"+val.BRAND_NAME+"</td> <td>"+val.BRAND_FEE+"</td> <td>"+val.BRAND_PCT+"</td> </tr>";
            });
            if(code == "") code = "<tr><td class='text-center' colspan='3'>没有找到匹配的记录</td></tr>";
            $('#brandbody').html('').append(code);
        }
    });
}

function downLoadReport()
{
    var startDt = $('#startDt').val();
    var endDt = $('#endDt').val();
    var source=$("#source").find("option:selected").val();

    if(null==startDt||startDt=='')
    {
        //提示
        toastr.warning("开始日期不能为空！");
        return;
    }

    if(null==endDt||endDt=='')
    {
        //提示
        toastr.warning("结束日期不能为空！");
        return;
    }

    //下载数据
    toastr.warning("非生产环境，不支持下载！");
}

