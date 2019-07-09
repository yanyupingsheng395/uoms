var ctx = "/";
$(function () {
    var $jobLogTableForm = $(".log-table-form");
    var settings = {
        url: ctx + "jobLog/list",
        pageSize: 10,
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: params.offset / params.limit + 1,
                beanName: $jobLogTableForm.find("input[name='beanName']").val(),
                methodName: $jobLogTableForm.find("input[name='methodName']").val(),
                status: $jobLogTableForm.find("select[name='status']").val()
            };
        },
        columns: [{
            checkbox: true
        },
            {
                field: 'jobId',
                title: '任务ID'
            }, {
                field: 'beanName',
                title: 'Bean名称'
            }, {
                field: 'methodName',
                title: '方法名称'
            }, {
                field: 'params',
                title: '参数'
            }, {
                field: 'status',
                title: '状态',
                formatter: function (value, row, index) {
                    if (value === '1') return '<span class="badge bg-danger">失败</span>';
                    if (value === '0') return '<span class="badge bg-success">成功</span>';
                }
            }, {
                field: 'error',
                title: '异常信息'
            }, {
                field: 'times',
                title: '耗时（毫秒）'
            }, {
                field: 'createTime',
                title: '创建时间'
            }
        ]
    };

    $MB.initTable('jobLogTable', settings);
});

function searchLog() {
    $MB.refreshTable('jobLogTable');
}

function refreshLog() {
    $("input[name='beanName']").val("");
    $("input[name='methodName']").val("");
    $("select[name='status']").find("option:selected").attr("selected", false);
    searchLog();
}

function deleteJobLog() {
    var selected = $("#jobLogTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请勾选需要删除的调度日志！');
        return;
    }
    var ids = "";
    for (var i = 0; i < selected_length; i++) {
        ids += selected[i].logId;
        if (i !== (selected_length - 1)) ids += ",";
    }

    $MB.confirm({
        title: "提示：",
        content: "确定删除选中的调度日志?"
    }, function () {
        $.post(ctx + 'jobLog/delete', {"ids": ids}, function (r) {
            if (r.code === 200) {
                $MB.n_success(r.msg);
                refreshLog();
            } else {
                $MB.n_danger(r.msg);
            }
        });
    });
}


function exportJobLogExcel() {
    $.post(ctx + "jobLog/excel", $(".jobLog-table-form").serialize(), function (r) {
        if (r.code === 0) {
            window.location.href = "common/download?fileName=" + r.msg + "&delete=" + true;
        } else {
            $MB.n_warning(r.msg);
        }
    });
}

function exportJobLogCsv() {
    $.post(ctx + "jobLog/csv", $(".jobLog-table-form").serialize(), function (r) {
        if (r.code === 0) {
            window.location.href = "common/download?fileName=" + r.msg + "&delete=" + true;
        } else {
            $MB.n_warning(r.msg);
        }
    });
}