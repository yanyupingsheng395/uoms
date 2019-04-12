$(function () {

    $('#touchDate').datepicker({
        format: 'yyyy-mm-dd',
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true
    });

    $("#touchDate").datepicker("setDate", new Date());
    $('#cdrq').text($('#touchDate').val());

    //获取当日需要运营的总人数
    $.getJSON("/op/getOpDayHeadInfo?daywid="+$('#touchDate').val(), function (resp) {
        if (resp.code==200){
            $('#cdrq').text($('#touchDate').val());
            $('#cdrs').text(resp.msg);
        }
    });

    //加载明细数据
    $.getJSON("/op/getOpDayDetailAllList?daywid="+$('#touchDate').val(), function (resp) {
        if (resp.code == 200) {
            $('#opdayTable').bootstrapTable('load', resp.data);
            var data = $('#opdayTable').bootstrapTable('getData', true);
            var merge =$(":radio[name='merge']:checked").val();
            //合并单元格
            mergeCells(data, merge, 1);
        }
    });

    //为刷新按钮绑定事件
    $("#btn_query").on("click",function () {

        //获取当日需要运营的总人数
        $.getJSON("/op/getOpDayHeadInfo?daywid=" + $('#touchDate').val(), function (resp) {
            if (resp.code == 200) {
                $('#cdrq').text($('#touchDate').val());
                $('#cdrs').text(resp.msg);
            }
        });

        //加载明细数据
        $.getJSON("/op/getOpDayDetailAllList?daywid=" + $('#touchDate').val(), function (resp) {
            if (resp.code == 200) {
                $('#opdayTable').bootstrapTable('load', resp.data);
                var data = $('#opdayTable').bootstrapTable('getData', true);
                //合并单元格
                var merge =$(":radio[name='merge']:checked").val();
                mergeCells(data, merge, 1);
            }
        });
    });

    $("#btn_downLoad").on("click",function () {
        $.alert({
            title: '提示：',
            content: '演示环境，不支持导出！',
            confirm: {
                text: '确认',
                btnClass: 'btn-primary'
            }
        });
    });

});

$('#opdayTable').bootstrapTable(
    {
        datatype: 'json',
        columns: [
            {
                field: 'PRODUCT_NAME',
                title: '产品名称'
            },
            {
            field: 'COUPON_DISPLAY',
            title: '优惠券'
           },
            {
                field: 'USER_ID',
                title: '用户ID'
            },
            {
            field: 'DISCOUNT_EFFORT',
            title: '优惠力度',
            formatter: function (value, row, index) {
                if (value == 'H') {
                    return "高";
                } else if (value == 'M') {
                    return "中";
                } else {
                    return "低";
                }
            }
        }
        //     {
        //     field: 'GROUP_TYPE',
        //     title: '分组标记',
        //     formatter: function (value, row, index) {
        //         if (value == 'T') {
        //             return "实验组";
        //         } else if (value == 'D') {
        //             return "对照组";
        //         } else {
        //             return "";
        //         }
        //     }
        // }
        ]
    });



function mergeCells(data,fieldName,colspan)
{
    //声明一个map计算相同属性值在data对象出现的次数和
    var sortMap = {};
    for(var i = 0 ; i < data.length ; i++){
        for(var prop in data[i]){
            if(prop == fieldName){
                var key = data[i][prop]
                if(sortMap.hasOwnProperty(key)){
                    sortMap[key] = sortMap[key] * 1 + 1;
                } else {
                    sortMap[key] = 1;
                }
                break;
            }
        }
    }
    for(var prop in sortMap){
    }
    var index = 0;
    for(var prop in sortMap){
        var count = sortMap[prop] * 1;
        $("#opdayTable").bootstrapTable('mergeCells',{index:index, field:fieldName, colspan: colspan, rowspan: count});
        index += count;
    }
}




