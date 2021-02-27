init();
function init() {
    var settings = {
        url: "/welcome/getDataTableList",
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        clickToSelect: true,
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset
            };
        },
        columns: [{
            checkbox: true
        },{
            field: 'welcomeName',
            align: "center",
            title: '欢迎语名称',
        },{
            field: 'insertDt',
            align: "center",
            title: '配置时间',
        },{
            field: 'status',
            align: "center",
            title: '使用状态',
            formatter: function (value, row, index) {
                if(value === '0') {
                    return '<span class="badge bg-primary">未使用</span>';
                }
                if(value === '1') {
                    return '<span class="badge bg-info">使用中</span>';
                }
            }
        }]
    };
    $("#dataTable").bootstrapTable(settings);
}

// 删除欢迎语
function deleteWelcome() {
    var selected = $("#dataTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        $MB.confirm( {
            title: "<i class='mdi mdi-alert-outline'></i>提示：",
            content: "确定删除选中的记录?"
        }, function () {
            $.post( "/welcome/deleteById", {id: selected[0]['id']}, function (r) {
                if (r.code == 200) {
                    $MB.n_success( "删除成功！" );
                    $MB.refreshTable( 'dataTable' );
                }
            } );
        } );
    }
}

// 编辑欢迎语
function editWelcome() {
    var selected = $("#dataTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        window.location.href = "/page/qywxWelcome/edit?id=" + selected[0].id;
    }
}

// 启动&停用欢迎语 status: start, stop
function startWelcome() {
    var selected = $("#dataTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        $MB.confirm({
            title: "<i class='mdi mdi-alert-outline'></i>提示：",
            content: "确定启用当前选中记录，其它启用记录将停用？"
        }, function () {
            var status = selected[0]['status'];
            if(status === '1') {
                $MB.n_warning("当前记录已经是启用状态！");
            }else {
                $.post("/welcome/updateStatus", {id: selected[0].id, status: 'start'}, function (r) {
                    if(r.code == 200) {
                        $MB.n_success("启用成功！");
                        $MB.refreshTable('dataTable');
                    }
                });
            }
        });
    }
}

function stopWelcome() {
    var selected = $("#dataTable").bootstrapTable('getSelections');
    if(selected.length == 0) {
        $MB.n_warning("请先选择一条记录！");
    }else {
        $MB.confirm({
            title: "<i class='mdi mdi-alert-outline'></i>提示：",
            content: "确定停用当前选中记录？"
        }, function () {
            var status = selected[0]['status'];
            if(status === '0') {
                $MB.n_warning("当前记录已经是停用状态！");
            }else {
                $.post("/welcome/updateStatus", {id: selected[0].id, status: 'stop'}, function (r) {
                    if(r.code == 200) {
                        $MB.n_success("停用成功！");
                        $MB.refreshTable('dataTable');
                    }
                });
            }
        });
    }
}