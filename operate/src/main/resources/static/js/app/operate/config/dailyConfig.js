$(function () {
    //获取系统的默认值,并初始化表单
    $.get("/push/getDailyProperties", function (r) {
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
            //预警手机号
            $("#alertPhone").val(data.alertPhone);
        }
    });


    $("#saveConfig").click(function () {
            let v_pushFlag="N";
            if($("#pushFlag").prop("checked")==true)
            {
                v_pushFlag='Y';
            }

            var datas={
                pushFlag: v_pushFlag,
                alertPhone:$("#alertPhone").val()
            };

            $.ajax({
                type: "post",
                url: "/push/updateDailyProperties",
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
    });

});



