$(function () {
    $("#pushMsgBtn").hide();
    getPlanTable();
    getActivityName();
});

// 获取活动名称
function getActivityName() {
    $.get("/activity/getActivityName", {headId: headId}, function(r){
        $("#activityName").html(r.data);
    });
}

// 获取计划表数据
function getPlanTable() {
    var settings = {
        singleSelect: true,
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
                    {   if(row.stage==='preheat')
                        {
                            return '<span class="badge bg-success">预售</span>&nbsp;<span class="badge bg-info">活动通知</span>';
                        }else
                        {
                            return '<span class="badge bg-success">正式</span>&nbsp;<span class="badge bg-info">活动通知</span>';
                        }
                    }else
                    {
                        if(row.stage==='formal')
                        {
                            return '<span class="badge bg-success">正式</span>&nbsp;<span class="badge bg-info">活动期间</span>';
                        }else
                        {
                            return '<span class="badge bg-success">预售</span>&nbsp;<span class="badge bg-info">活动期间</span>';
                        }

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
                            res = "<span class=\"badge bg-danger\">过期未推送</span>";
                            break;
                        case "5":
                            res = "<span class=\"badge bg-danger\">终止</span>";
                            break;
                    }
                    return res;
                }
            },
            {
                field: 'successNum',
                title: '推送转化率(%)',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'successNum',
                title: '推送转化金额(元)',
                align: 'center',
                valign: 'middle'
            }
            ]
    };
    $("#planTable").bootstrapTable(settings);
    $.get("/activityPlan/getPlanList", {headId: headId},function (r) {
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
    let status =selected[0].planStatus;
    let planType=selected[0].planType;

    //待执行状态
    if(status==1)
    {
        //生成文案
        $MB.loadingDesc('show', '转化文案中，请稍后...');
        $.get("/activityPlan/transActivityDetail", {planId: planId}, function (r) {
            if(r.code == 200) {
                //如果生成成功，加载数据
                getUserGroupTable(planId,planType);
                getUserDetail(planId,planType,"-1");
                $("#planId").val(planId);
                $("#pushDiv").show();
                $("#pushMsgBtn").show();
                $("#view_push_modal").modal('show');
            }else {
                //提示错误信息
                $MB.n_danger(r.msg);
            }
            $MB.loadingDesc('hide');
        });
    }else
    {
        $MB.loadingDesc('show', '加载中，请稍后...');
        getUserGroupTable(planId,planType);
        getUserDetail(planId,planType,"-1");
        $("#planId").val(planId);
        $("#view_push_modal").modal('show');
        $MB.loadingDesc('hide');
    }
});

$("#view_push_modal").on("hidden.bs.modal", function () {
    $("#planId").val('');
    $("#pushDiv").hide();
    $("#pushMsgBtn").hide();
});

// 获取用户群组列表
function getUserGroupTable(planId,planType) {
    var columns=initGroupColumn(planType);

    $("#userGroupTable").bootstrapTable('destroy').bootstrapTable({
        url: '/activityPlan/getUserGroupList',
        columns: columns,
        fixedColumns: false,
        queryParams : function(params) {
            return {
                planId : planId
            }
        },
        onLoadSuccess:function(data){
            var n=data.length>2?data.length-2:data.length;
            $("a[data-toggle='tooltip']").tooltip();
            //合并单元格
            $( "#userGroupTable" ).bootstrapTable('mergeCells',{index:0, field:"prodActivityProp", colspan: 1, rowspan:n})
        }
    });
}


function initGroupColumn(planType) {
     var cols=[];

     cols.push( {
         field: 'prodActivityProp',
         title: '成长商品活动属性',
         align: "center",
         formatter: function (value, row, index) {
            if(value==='Y')
            {
                return '是';
            }else if(value==='N')
            {
                return '否';
            }else
            {
                return '-';
            }
         }
     });

     if(planType==='NOTIFY')
     {
         cols.push({
             field: 'groupName',
             title: '成长商品活动机制'
         });
     }else
     {
         cols.push({
             field: 'groupName',
             title: '推荐用户商品策略'
         });
     }
     cols.push(  {
         field: 'userNum',
         title: '人数（人）',
         align: 'center',
         formatter: function (value, row, index) {
             return "<a style='cursor: pointer;' onclick=\"clickUserGroup('"+row.planId+"','"+row.groupId+"','"+planType+"')\">" + value + "</a>";
         }
     });

     return cols;
}


/**
 * @param planId
 */
function getUserDetail(planId,planType,groupId){
    var columns=initDetailColumns(planType);
    let settings = {
        url: "/activityPlan/getDetailPage",
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
                param: {planId: planId,groupId:groupId}
            };
        },
        columns: columns,
        fixedColumns: false,
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $("#userDetailTable").bootstrapTable('destroy').bootstrapTable(settings);
}

function initDetailColumns(planType)
{
    var cols=[];
    cols.push({field: 'epbProductId', title: '商品ID'});
    cols.push({field: 'epbProductName', title: '商品名称'});
    cols.push({field: 'userId', title: '用户ID'});
    cols.push({field: 'prodActivityProp', title: '成长商品活动属性',align: "center",
        formatter: function (value, row, index) {
            if(value==='Y')
            {
                return '是';
            }else if(value==='N')
            {
                return '否';
            }else
            {
                return '-';
            }
        }
    });
    if(planType==='NOTIFY')
    {
        cols.push({
            field: 'groupName',
            title: '成长商品活动机制'
        });
    }else
    {
        cols.push({
            field: 'groupName',
            title: '推荐用户商品策略'
        });
    }

    cols.push({field: 'orderPeriod', title: '建议推送时段'});
    cols.push({
        field: 'smsContent',
        title: '推送内容',
        formatter: function (value, row, index) {
            if (value != null && value != undefined) {
                let temp = value.length > 20 ? value.substring(0, 20) + "..." : value;
                return '<a style=\'color: #000000;cursor: pointer;\' data-toggle="tooltip" data-html="true" title="" data-original-title="' + value + '">' + temp + '</a>';
            } else {
                return '-';
            }
        }
    });
    return cols;
}

/**
 * 点击用户群组的用户数刷新明细表格
 * @param groupId
 */
function clickUserGroup(planId,groupId,planType)
{
     //刷新表格
    getUserDetail(planId,planType,groupId);
}

$('input[name="pushMethod"]').click(function () {
    if ($(this).val() == "FIXED") {
        $("#pushPeriodDiv").show();
    } else {
        $("#pushPeriodDiv").hide();
    }
});

$("#push_ok").change(function () {
    let flag = $(this).is(':checked');
    if(flag) {
        $("#pushMsgBtn").removeAttr("disabled");
    }else {
        $("#pushMsgBtn").attr("disabled", true);
    }
});

$("#view_push_modal").on('shown.bs.modal', function () {
    $.get("/activityPlan/getDefaultPushInfo", {}, function (r) {
        let code = "";
        r.data['timeList'].forEach((v, k) => {
            code += "<option value='" + v + "'>" + v + "</option>";
        });
        $("#pushPeriod").html('').append(code);
        $('input[name="pushMethod"]').removeAttr("checked");
        $('input[name="pushMethod"][value="' + r.data.method + '"]').prop("checked", true);
        //去掉确认按钮的勾选状态
        $("#push_ok").removeAttr("checked");
        $("#pushPeriodDiv").hide();
    });
});

/**
 * 启动推送
 */
function submitData() {
    //禁用推送按钮
    $("#pushMsgBtn").attr("disabled", true);
    $MB.loadingDesc('show', '启动推送...');

    //获取planId
    var planId= $("#planId").val();
    var pushMethod=$("input[name='pushMethod']:checked").val();
    var pushPeriod=$("#pushPeriod").find("option:selected").val();

    $.post("/activityPlan/startPush", {planId: planId,pushMethod:pushMethod,pushPeriod:pushPeriod}, function (r) {
        if(r.code === 200) {
            //关闭loading
            $MB.loadingDesc('hide');
            //提示
            $MB.n_success("推送成功！");
            $("#pushMsgBtn").attr("disabled", true);
            //关闭弹出面板
            $("#view_push_modal").modal('hide');

            $('#planTable').bootstrapTable('destroy');
            getPlanTable();
        }else {
            $MB.loadingDesc('hide');
            $MB.n_danger(r.msg);
            //释放推送按钮
            $("#pushMsgBtn").removeAttr("disabled");
        }
    });
}

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
            window.location.href = "/page/activity/planEffect?planId=" + planId;
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
            $.post("/activityPlan/sopPlan", {planId: planId}, function (r) {
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

