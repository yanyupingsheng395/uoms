var stepObj=null;
$(function () {
    //获取活动名称
    getActivityName();

    //获取计划列表
    getPlanTable();

});

// 获取活动名称
function getActivityName() {
    $.get("/qywxActivity/getActivityName", {headId: headId}, function(r){
        $("#activityName").html(r.data);
    });
}

// 获取计划表数据
function getPlanTable() {
    var settings = {
        singleSelect: true,
        pagination: false,
        columns: [
            {
                checkbox: true,
                valign: "middle",
            },
            {
                field: 'planType',
                title: '',
                visible: false
            },
            {
                field: 'effectFlag',
                title: '',
                visible: false
            },
            {
                field: 'planId',
                title: '',
                visible: false
            },
            {
                field: 'planDateWid',
                title: '推送日期',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'planType',
                title: '计划类型',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row, index) {
                    if (value === 'NOTIFY')
                    {
                        return '<span class="badge bg-primary">正式</span>&nbsp;<span class="badge bg-info">活动通知</span>';
                    }else
                    {
                        return '<span class="badge bg-success">正式</span>&nbsp;<span class="badge bg-cyan">活动期间</span>';

                    }
                }
            },
            {
                field: 'userCnt',
                title: '建议推送人数(人)',
                align: 'center',
                valign: 'middle'
            },  {
                field: 'successNum',
                title: '实际成功推送人数(人)',
                align: 'center',
                valign: 'middle'
            },
             {
                field: 'planStatus',
                title: '任务执行状态',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row, index) {
                    let res = "";
                    switch (value) {
                        case "0":
                            res = "<span class=\"badge bg-primary\">尚未计算</span>";
                            break;
                        case "1":
                            res = "<span class=\"badge bg-success\">待执行</span>";
                            break;
                        case "2":
                            res = "<span class=\"badge bg-warning\">执行中</span>";
                            break;
                        case "3":
                            res = "<span class=\"badge bg-info\">执行完</span>";
                            break;
                        case "4":
                            res = "<span class=\"badge bg-gray\">过期未执行</span>";
                            break;
                        case "5":
                            res = "<span class=\"badge bg-danger\">终止</span>";
                            break;
                    }
                    return res;
                }
            },
            {
                field: 'covRate',
                title: '推送转化率(%)',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'covAmount',
                title: '推送转化金额(元)',
                align: 'center',
                valign: 'middle'
            }
            ]
    };
    $("#planTable").bootstrapTable('destroy').bootstrapTable(settings);
    $.get("/qywxActivityPlan/getPlanList", {headId: headId},function (r) {
        if(r.code === 200) {
           $("#planTable").bootstrapTable('load', r.data);
        }else {
            $MB.n_danger("获取计划数据异常！");
        }
    });
}

/**
 * 预览执行
 */
$("#btn_process").click(function () {
    let selected = $("#planTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择要预览执行的推送计划！');
        return;
    }
    let planId = selected[0].planId;
    let planStatus =selected[0].planStatus;
    let planType=selected[0].planType;

    $("#planId").val(planId);
    $("#planStatus").val(planStatus);
    $("#planType").val(planType);

    //待执行状态
    if(planStatus==1)
    {
        //初始化进度条
        stepObj=steps({
            el: "#pushSteps",
            data: [
                { title: "成长用户详情",description:""},
                { title: "向成员派发推送任务",description:"" }
            ],
            center: true,
            dataOrder: ["title", "line", "description"]
        });

        //生成文案
        $MB.loadingDesc('show', '转化文案中，请稍候...');
        $.get("/qywxActivityPlan/transActivityDetail", {planId: planId}, function (r) {
            if(r.code == 200) {
                //加载成员列表

                //加载推送明细
                getUserDetail();
                //显示下一步按钮
                $("#next1Btn").show();

                //打开弹出面板
                $("#view_push_modal").modal('show');
            }else {
                //提示错误信息
                $MB.n_danger(r.msg);
            }
            $MB.loadingDesc('hide');
        });
    }else if(planStatus==0){
        $MB.n_warning('该活动尚未计算！');
        return;
    }else
    {
        $MB.loadingDesc('show', '加载中，请稍候...');
        //加载成员列表
        //记载推送明细
        getUserDetail();
        //隐藏下一步按钮
        $("#next1Btn").hide();
        //打开弹出面板
        $("#view_push_modal").modal('show');
        $MB.loadingDesc('hide');

    }
});

/**
 * 预览页  下一步按钮的点击事件
 */
$("#next1Btn").click(function () {
    stepObj.setActive(1);
    $("#pushSet").show();
    $("#pushdetail").hide();
    $("#pushBtn").show();
});
//点击"我确定推送"按钮，
$("#push_ok").change(function () {
    let flag = $(this).is(':checked');
    if(flag) {
        $("#pushBtn").removeAttr("disabled");
    }else {
        $("#pushBtn").attr("disabled", true);
    }
});

/**
 * @param planId
 */
function getUserDetail(){
    let planId=$("#planId").val()
    let settings = {
        url: "/qywxActivityPlan/getDetailPage",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 5,            //每页的记录行数（*）
        pageList: [5,10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,  //页码
                param: {planId: planId}
            };
        },
        columns: [
            {
                field: 'smsContent',
                title: '个性化消息内容',
                formatter: function (value, row, index) {
                    return longTextFormat(value, row, index);
                }
            },
            {
                field: 'followUserName',
                title: '推送消息的成员',
                align: "center",
            },
            {
                field: 'qywxContactName',
                title: '接收消息的用户',
                align: "center",
            },{
                field: 'recProdName',
                title: '推荐购买的商品',
                align: "center",
            }
            ],
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $("#userDetailTable").bootstrapTable('destroy').bootstrapTable(settings);
}

/**
 * 推送页 上一步按钮
 */
$("#pre2Btn").on('click',function () {
    stepObj.setActive(0);
    //加载导购数据

    //加载明细页数据
    getUserDetail();
    //显示明细页
    $("#pushdetail").show();
    $("#next1Btn").show();

    $("#pushSet").hide();
});


/**
 * 启动推送
 */
$("#pushBtn").on('click',function () {
//禁用推送按钮
    $("#pushBtn").attr("disabled", true);
    $MB.loadingDesc('show', '启动推送...');

    //获取planId
    var planId= $("#planId").val();
    $.post("/qywxActivityPlan/startPush", {planId: planId}, function (r) {
        if(r.code === 200) {
            //关闭loading
            $MB.loadingDesc('hide');
            //提示
            $MB.n_success("推送成功！");
            $("#pushBtn").attr("disabled", true);
            //关闭弹出面板
            $("#view_push_modal").modal('hide');

            $('#planTable').bootstrapTable('destroy');
            getPlanTable();
        }else {
            $MB.loadingDesc('hide');
            $MB.n_danger(r.msg);
            //释放推送按钮
            $("#pushBtn").removeAttr("disabled");
        }
    });
});

/**
 * 关闭面板
 */
$("#view_push_modal").on("hidden.bs.modal", function () {
    //隐藏预览执行面板，清空一些隐藏变量
    $("#planId").val('');
    $("#planStatus").val('');
    $("#planType").val('');
    //隐藏推送按钮
    $("#pushBtn").hide();
    $("#pushdetail").show();
    $("#pushSet").hide();

    if(null!=stepObj)
    {
        stepObj.setActive(0);
    }
});

function refreshPage()
{
    getPlanTable();
}

/**
 * 效果
 */
$("#btn_effect").click(function () {
    let selected = $("#planTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择要查看效果的推送计划！');
        return;
    }
    let planId = selected[0].planId;
    let status = selected[0].planStatus;
    let effectFlag = selected[0].effectFlag;

    if (status == '2' || status === '3') {
        if(effectFlag=='N')
        {
            $MB.n_warning("效果尚未进行计算，请于推送完成后第二日再来查看！");
        }else
        {
            window.location.href = "/page/qywxActivity/planEffect?planId=" + planId;
        }
    } else {
        $MB.n_warning("只有完成推送以后才能查看效果！");
    }
});

/**
* 终止
 */
$("#btn_stop").click(function () {
    let selected = $("#planTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择要中止的推送计划！');
        return;
    }
    let planId = selected[0].planId;
    let status = selected[0].planStatus;

    if (status == '1') {
        $MB.confirm({
            title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
            content: '确定要终止当前执行计划么?'
        }, function () {
            $MB.loadingDesc('show', '开始终止执行计划');
            $.post("/qywxActivityPlan/sopPlan", {planId: planId}, function (r) {
                if (r.code === 200) {
                    //关闭loading
                    $MB.loadingDesc('hide');
                    //提示
                    $MB.n_success("终止成功！");
                    $('#planTable').bootstrapTable('destroy');
                    getPlanTable();
                } else {
                    $MB.loadingDesc('hide');
                    $MB.n_danger(r.msg);
                }
            })
        });
    }
    else {
        $MB.n_warning("只有待执行的计划才能终止！");
    }
});