var validator;
var $configForm = $("#config-form");

$.validator.addMethod("validateAlertPhone",function(value,element){

    if($("#openAlert").prop("checked")==true)
    {
        if(null==$("#alertPhoneNum").val()||$("#alertPhoneNum").val()=='')
        {
               return false;
        }
    }
    return true;
},"如果开启了预警，则预警手机号不能为空！");

$(function () {
    validateRule();

    //获取系统的默认值,并初始化表单
    $.get("/dailyConfig/getDailyProperties", function (r) {
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

          //是否防重复推送
            if("Y"==data.repeatPush)
            {
                $("#repeatPush").prop("checked",true);
            }else
            {
                $("#repeatPush").prop("checked",false);
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
            $("#alertPhoneNum").val(data.alertPhoneNum);

            //推送方式
            $("input[name='pushType']").removeProp("checked");
            $("input[name='pushType'][value='"+data.pushType+"']").prop("checked",true);

        }
    });


    $("#saveConfig").click(function () {
        var validator = $configForm.validate();
        var flag = validator.form();
        var ctx = "/";
        if (flag) {
            var v_pushFlag="N";
            if($("#pushFlag").prop("checked")==true)
            {
                v_pushFlag='Y';
            }

            var v_repeatPush="N";
            if($("#repeatPush").prop("checked")==true)
            {
                v_repeatPush='Y';
            }

            var v_openAlert="N";
            if($("#openAlert").prop("checked")==true)
            {
                v_openAlert='Y';
            }


            var datas={
                pushFlag: v_pushFlag,
                repeatPush:v_repeatPush,
                repeatPushDays:$("#repeatPushDays").val(),
                statsDays:$("#statsDays").val(),
                openAlert: v_openAlert,
                alertPhoneNum:$("#alertPhoneNum").val(),
                pushType:$("input[name='pushType']:checked").prop("value")
            };

            $.ajax({
                type: "post",
                url: "/dailyConfig/updateDailyProperties",
                data: JSON.stringify(datas),
                dataType: "json",
                headers: {
                    'Content-Type': 'application/json'
                },
                success: function (r) {
                    if (r.code === 200) {
                        $MB.n_success("更新成功！");
                    }else
                    {
                        $MB.n_danger("更新失败!");
                    }
                }
            });
        }
    });

});

function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $configForm.validate({
        rules: {
            repeatPushDays: {
                required: true,
                digits:true
            },
            statsDays: {
                required: true,
                digits:true
            },
            alertPhoneNum: "validateAlertPhone"
        },
        errorPlacement: function (error, element) {
            if (element.is(":checkbox") || element.is(":radio")) {
                error.appendTo(element.parent().parent());
            } else {
                error.insertAfter(element);
            }
        },
        messages: {
            repeatPushDays: {
                required: icon + "避免重复推送的天数不能为空",
                digits: icon + "避免重复推送的天数必须是数字"
            },
            statsDays: {
                required: icon + "效果统计天数不能为空",
                digits: icon + "效果统计天数必须是数字"
            }
        }
    });
}

