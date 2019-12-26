$(function () {
    $("#refresh").click(function () {
       refresh();
    });

    refresh();
});

function refresh()
{
    //获取系统的默认值,并初始化表单
    $.get("/dailyConfig/refreshDailyProperties", function (r) {
        if(r.code===200)
        {
            var data=r.data;

            //是否推送
            if("Y"==data.pushFlag)
            {
                $("#pushFlag").prop("checked",true);
            }else
            {
                $("#pushFlag").prop("checked",false);
            }

            //防重复推送的天数
            $("#repeatPushDays").val(data.repeatPushDays);

            //效果统计的天数
            $("#statsDays").val(data.statsDays);

            //是否开启预警
            if("Y"==data.openAlert)
            {
                $("#openAlert").prop("checked",true);
            }else
            {
                $("#openAlert").prop("checked",false);
            }

            //推送手机号
            $("#alertPhone").val(data.alertPhone);

            //推送方式
            $("input[name='pushType']").removeProp("checked");
            $("input[name='pushType'][value='"+data.pushType+"']").prop("checked",true);

            //推送方法
            $("input[name='pushMethod']").removeProp("checked");
            $("input[name='pushMethod'][value='"+data.pushMethod+"']").prop("checked",true);

            //商品明细页链接模板
            $("#productUrl").val(data.productUrl);

            //包装短链是否需要包装成可唤醒淘宝APP
            if("Y"==data.isAliApp)
            {
                $("#isAliApp").prop("checked",true);
            }else
            {
                $("#isAliApp").prop("checked",false);
            }

            //短链是否返回模拟链接
            if("Y"==data.isTestEnv)
            {
                $("#isTestEnv").prop("checked",true);
            }else
            {
                $("#isTestEnv").prop("checked",false);
            }

            //模拟短链接的样例链接
            $("#demoShortUrl").val(data.demoShortUrl);

            //商品短名称的最大长度
            $("#prodNameLen").val(data.prodNameLen);
            //短链接的长度
            $("#shortUrlLen").val(data.shortUrlLen);


            $("#couponNameLen").val(data.couponNameLen);

            //短信内容的长度限制
            $("#smsLengthLimit").val(data.smsLengthLimit);

            //优惠券领用方式
            $("input[name='couponSendType']").removeProp("checked");
            $("input[name='couponSendType'][value='"+data.couponSendType+"']").prop("checked",true);
        }
    });
}