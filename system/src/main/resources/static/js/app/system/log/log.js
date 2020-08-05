var ctx = "/";
$(function () {
    setUsername();
    var $logTableForm = $(".push-table-form");
    var settings = {
        url: ctx + "log/list",
        pageSize: 10,
        singleSelect: true,
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: params.offset / params.limit + 1,
                timeField: $logTableForm.find("input[name='timeField']").val(),
                username: $logTableForm.find("select[name='username']").find("option:selected").val(),
                operation: $logTableForm.find("input[name='operation']").val()
            };
        },
        columns: [{
            checkbox: true
        },
            {
                field: 'username',
                title: '操作用户'
            }, {
                field: 'operation',
                title: '描述'
            }, {
                field: 'time',
                title: '耗时（毫秒）'
            }, {
                field: 'ip',
                title: 'IP地址'
            }, {
                field: 'location',
                title: '操作地点'
            }, {
                field: 'createTime',
                title: '操作时间'
            }
        ]
    };

    $MB.initTable('logTable', settings);
    $MB.calenders('input[name="timeField"]', true, false);
});
function setUsername() {
    var code = "<option value=''>所有</option>";
    $.get("/user/findAllUser", {}, function (r) {
        $.each(r.data, function (k, v) {
            code += "<option value='" + v["userId"] + "'>" + v["username"] + "</option>";
        });
        $("#username").html("").append(code);
    });
}

function searchLog() {
    $MB.refreshTable('logTable');
}

function refreshLog() {
    $(".push-table-form")[0].reset();
    searchLog();
}

function exportLogExcel() {
    $.post(ctx + "log/excel", $(".push-table-form").serialize(), function (r) {
        if (r.code === 200) {
            window.location.href = "common/download?fileName=" + r.msg + "&delete=" + true;
        } else {
            $MB.n_warning(r.msg);
        }
    });
}

function exportLogCsv() {
    $.post(ctx + "log/csv", $(".push-table-form").serialize(), function (r) {
        if (r.code === 200) {
            window.location.href = "common/download?fileName=" + r.msg + "&delete=" + true;
        } else {
            $MB.n_warning(r.msg);
        }
    });
}