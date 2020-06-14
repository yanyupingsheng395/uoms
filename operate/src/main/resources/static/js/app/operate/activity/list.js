init_date('date', 'yyyy-mm-dd', 0,2,0)
$(function () {
    var errmsg=$("#errormsg").val();
    if(null!=errmsg&&errmsg!='')
    {
        $MB.n_warning(errmsg);
    }

    let settings = {
        url: "/activity/gePageOfHead",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,  //页码
                param: {name: $("#name").val(), date: $("#date").val(), status: $("#status").val()}
            };
        },
        columns: [[
            {
                checkbox: true,
                valign: "middle",
                rowspan: 2
            },
            {
                field: 'activityName',
                title: '活动名称',
                align: "center",
                valign: "middle",
                rowspan: 2
            },
            {
                field: 'effectFlag',
                visible: false
            },
            {
                title: "预售",
                align: "center",
                colspan: 5
            },
            {
                title: "正式",
                align: "center",
                colspan: 5
            }
        ], [{
            field: 'preheatNotifyDt',
            align: "center",
            title: '通知时间'
        },{
            field: 'preheatNotifyStatus',
            align: "center",
            title: '执行状态',
            formatter: function (value, row, index) {
                let res = "-";
                switch (value) {
                    case "edit":
                        res = "<span class=\"badge bg-info\">待计划</span>";
                        break;
                    case "todo":
                        res = "<span class=\"badge bg-primary\">待执行</span>";
                        break;
                    case "doing":
                        res = "<span class=\"badge bg-warning\">执行中</span>";
                        break;
                    case "done":
                        res = "<span class=\"badge bg-success\">执行完</span>";
                        break;
                    case "timeout":
                        res = "<span class=\"badge bg-gray\">过期未执行</span>";
                        break;
                }
                return res;
            }
        },{
            field: 'preheatStartDt',
            align: "center",
            title: '开始时间'
        }, {
            field: 'preheatEndDt',
            align: "center",
            title: '结束时间'
        }, {
            field: 'preheatStatus',
            align: "center",
            title: '执行状态',
            formatter: function (value, row, index) {
                let res = "-";
                switch (value) {
                    case "edit":
                        res = "<span class=\"badge bg-info\">待计划</span>";
                        break;
                    case "todo":
                        res = "<span class=\"badge bg-primary\">待执行</span>";
                        break;
                    case "doing":
                        res = "<span class=\"badge bg-warning\">执行中</span>";
                        break;
                    case "done":
                        res = "<span class=\"badge bg-success\">执行完</span>";
                        break;
                    case "timeout":
                        res = "<span class=\"badge bg-gray\">过期未执行</span>";
                        break;
                }
                return res;
            }
        }, {
            field: 'formalNotifyDt',
            align: "center",
            title: '通知时间'
        },{
            field: 'formalNotifyStatus',
            align: "center",
            title: '执行状态',
            formatter: function (value, row, index) {
                let res = "-";
                switch (value) {
                    case "edit":
                        res = "<span class=\"badge bg-info\">待计划</span>";
                        break;
                    case "todo":
                        res = "<span class=\"badge bg-primary\">待执行</span>";
                        break;
                    case "doing":
                        res = "<span class=\"badge bg-warning\">执行中</span>";
                        break;
                    case "done":
                        res = "<span class=\"badge bg-success\">执行完</span>";
                        break;
                    case "timeout":
                        res = "<span class=\"badge bg-gray\">过期未执行</span>";
                        break;
                }
                return res;
            }
        },{
            field: 'formalStartDt',
            align: "center",
            title: '开始时间'
        }, {
            field: 'formalEndDt',
            align: "center",
            title: '结束时间'
        }, {
            field: 'formalStatus',
            align: "center",
            title: '执行状态',
            formatter: function (value, row, index) {
                let res = "-";
                switch (value) {
                    case "edit":
                        res = "<span class=\"badge bg-info\">待计划</span>";
                        break;
                    case "todo":
                        res = "<span class=\"badge bg-primary\">待执行</span>";
                        break;
                    case "doing":
                        res = "<span class=\"badge bg-warning\">执行中</span>";
                        break;
                    case "timeout":
                        res = "<span class=\"badge bg-gray\">过期未执行</span>";
                        break;
                }
                return res;
            }
        }]]
    };
    $MB.initTable('activityTable', settings);
});

// 查询列表
function searchActivity() {
    $MB.refreshTable('activityTable');
}

// 重置查询条件
function resetActivity() {
    $("#date").val('');
    $("#name").val('');
    $("#status").find('option:selected').removeAttr('selected');
    $MB.refreshTable('activityTable');
}

// 创建计划
$("#btn_add").click(function () {
    window.location.href = "/page/activity/add";
});

$("#btn_edit").click(function () {
    let selected = $("#activityTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要编辑的活动！');
        return;
    }
    let headId = selected[0].headId;
    let preheatStatus = selected[0]['preheatStatus'];
    let formalStatus = selected[0]['formalStatus'];
    let preheatNotifyStatus = selected[0]['preheatNotifyStatus'];
    let formalNotifyStatus = selected[0]['formalNotifyStatus'];
    let flag = preheatStatus === 'done' && formalStatus === 'done' && preheatNotifyStatus === 'done' && formalNotifyStatus === 'done';
    if(!flag) {
        window.location.href = "/page/activity/edit?headId=" + headId;
    }else {
        $MB.n_warning("活动当前状态不允许修改计划！");
    }
});

$("#btn_plan").click(function () {
    let selected = $("#activityTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要执行的计划！');
        return;
    }
    let headId = selected[0].headId;
    let preheatStatus = selected[0]['preheatStatus'];
    let preheatNotifyStatus = selected[0]['preheatNotifyStatus'];
    let formalNotifyStatus = selected[0]['formalNotifyStatus'];
    let formalStatus = selected[0]['formalStatus'];
    let flag = true;
    if((preheatStatus !== '' && preheatStatus !== null) && (preheatNotifyStatus !== '' && preheatNotifyStatus !== null)) {
        if(preheatStatus === 'edit' && preheatNotifyStatus === 'edit' && formalNotifyStatus === 'edit' && formalStatus === 'edit') {
            flag = false;
        }
    }else {
        if(formalNotifyStatus === 'edit' && formalStatus === 'edit') {
            flag = false;
        }
    }
    if(flag) {
        window.location.href = "/page/activity/plan?id=" + headId;
    }else {
        $MB.n_warning("活动当前状态不允许执行计划！");
    }
});

$("#btn_delete").click(function () {
    let selected = $("#activityTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要删除的活动计划！');
        return;
    }
    let headId = selected[0].headId;
    let preheatStatus = selected[0].preheatStatus;
    let formalStatus = selected[0].formalStatus;
    let hasPreheat = selected[0].hasPreheat;

    if(!((hasPreheat === '1' && preheatStatus === 'edit' && formalStatus === 'edit') || (hasPreheat === '0' && formalStatus === 'edit'))) {
        $MB.n_warning("只有待计划的活动才可以被删除！");
        return;
    }
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除选中活动计划？'
    }, function () {
        $.post("/activity/deleteActivity", {headId: headId}, function (r) {
            if(r.code === 200) {
                $MB.n_success("删除成功！");
                $MB.refreshTable('activityTable');
            }else {
                $MB.n_danger("删除失败！");
            }
        });
    });
});

// 查看效果
$("#btn_effect").click(function () {
    let selected = $("#activityTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要查看的活动！');
        return;
    }
    let headId = selected[0].headId;
    let effectFlag=selected[0].effectFlag;
    let preheatStatus = selected[0]['preheatStatus'];
    let formalStatus = selected[0]['formalStatus'];
    let preheatNotifyStatus = selected[0]['preheatNotifyStatus'];
    let formalNotifyStatus = selected[0]['formalNotifyStatus'];
    let flag = preheatStatus != 'edit' || formalStatus != 'edit' || preheatNotifyStatus != 'edit' || formalNotifyStatus != 'edit';

    if(flag) {
        if(effectFlag==='N')
        {
            $MB.n_warning("效果尚未进行计算，请于首次推送完成后第二日再来查看！！");
        }else
        {
            window.location.href = "/page/activity/effect?headId=" + headId;
        }
    }else {
        $MB.n_warning("活动当前状态不允许查看效果！");
    }
});

$("#btn_view").click(function () {
    let selected = $("#activityTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要查看的活动！');
        return;
    }
    let headId = selected[0].headId;
    window.location.href = "/page/activity/view?headId=" + headId;
});