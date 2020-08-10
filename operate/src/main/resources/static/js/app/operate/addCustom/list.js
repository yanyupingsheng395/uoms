getTableData();
function getTableData() {
    var settings = {
        url: "/addUser/getHeadPageList",
        cache: false,
        pagination: true,
        singleSelect: true,
        clickToSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset
            };
        },
        columns: [{
            checkbox: true
        },{
            field: 'taskName',
            align: "center",
            title: '任务名称',
        },{
            field: 'sendType',
            align: "center",
            title: '发送申请范围',
            formatter: function (value, row, index) {
                let res = "-";
                switch (value) {
                    case "1":
                        res = "筛选";
                        break;
                    case "0":
                        res = "全部";
                        break;
                }
                return res;
            }
        },{
            field: 'applyUserCnt',
            align: "center",
            title: '发送申请数量（人）'
        }, {
            field: 'applyPassCnt',
            align: "center",
            title: '通过申请数量（人）'
        }, {
            field: 'applyPassRate',
            align: "center",
            title: '申请通过率（%）'
        }, {
            field: 'addUserMethod',
            align: "center",
            title: '申请触发机制',
            formatter: function (value, row, index) {
                if(value === '0') {
                    return "通过企业微信自动添加好友";
                } else if(value === '1') {
                    return "通过短信推送带有二维码的页面 ";
                } else if(value === '2') {
                    return "用户发生购买后短信推送申请";
                }
            }
        },{
            field: 'taskStartDt',
            align: "center",
            title: '开始时间'
        }, {
            field: 'taskStatus',
            align: "center",
            title: '任务状态',
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
        }]
    };
    $("#dataTable").bootstrapTable(settings);
}

function deleteTask() {
    var selected = $("#dataTable").bootstrapTable('getSelections');
    if(selected[0].length === 0) {
        $MB.n_warning("请先选择一条记录！");
    }
    var id = selected[0].id;
    $MB.confirm({
        title: '提示:',
        content: '确认删除选中的记录？'
    }, function () {
        $.get("/addUser/deleteTask", {id: id}, function (r) {
            if(r.code === 200) {
                $MB.refreshTable('dataTable');
                $MB.n_success("删除任务成功！");
            }
        });
    });
}


function editTask() {
    var selected = $("#dataTable").bootstrapTable('getSelections');
    if(selected.length === 0) {
        $MB.n_warning("请先选择一条记录！");
        return false;
    }
    var id = selected[0].id;
    window.location.href = "/page/addCustom/edit?id=" + id;
}

$("#btn_effect").click(function () {
    var selected = $("#dataTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
        return false;
    }
    var id = selected[0].id;
    window.location.href = "/page/addCustom/effect?id=" + id;
});