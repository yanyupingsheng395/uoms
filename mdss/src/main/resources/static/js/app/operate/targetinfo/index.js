$(function () {
    var settings = {
        url: "/target/getPageList",
        method: 'post',
        cache: false,
        pagination: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,
                pageNum: (params.offset / params.limit) + 1
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'ID',
            title: 'ID',
            visible: false
        },{
            field: 'NAME',
            title: '目标名称'
        },{
            title: '目标周期',
            formatter: function (values, row, index) {
                if(row["END_DT"] == null) {
                    return row["START_DT"];
                }else {
                    return row["START_DT"] + "至" + row["END_DT"];
                }
            }
        },{
            field: 'KPI_NAME',
            title: '指标'
        },{
            field: 'TARGET_VAL',
            title: '目标值'
        },{
            field: 'DIMENSIONS',
            title: '维度&值',
            formatter: function (value, row, index) {
                if(value == null || value == "") {
                    return "-";
                }
                if(value.length >= 10) {
                    var newVal = value.substr(0, 10) + "...";
                    var title = value.split("|").join("<br/>");
                    return "<a style='color: #000000;border-bottom: 1px solid' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-original-title=\""+title+"\">"+newVal+"</a>";
                }else if(value != null && value != ""){
                    return value;
                }
            }
        },{
            field: 'STATUS',
            title: '状态',
            formatter: function (value, row, index) {
                switch (value) {
                    case "0":
                        return "<span class=\"badge label-default\">待拆解</span>";
                        break;
                    case "1":
                        return "<span class=\"badge label-primary\">拆解计算中</span>";
                        break;
                    case "2":
                        return "<span class=\"badge label-success\">执行中</span>";
                        break;
                    case "3":
                        return "<span class=\"badge label-success\">停止</span>";
                        break;
                    case "4":
                        return "<span class=\"badge label-warning\">失效</span>";
                        break;
                    case "-1":
                        return "<span class=\"badge label-danger\">异常</span>";
                        break;
                    default:
                        return "-";
                }
            }
        },{
            field: 'CREATE_DT',
            title: '创建时间'
        }],
        onLoadSuccess: function(data){
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $MB.initTable('targetTable', settings);
});

// 查看目标
function viewTarget() {
    var selected = $("#targetTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要查看的目标！');
        return;
    }
    if (selected_length > 1) {
        $MB.n_warning('一次只能查看一个目标！');
        return;
    }
    var tgtId = selected[0]["ID"];
    window.location.href = "/target/detail?id=" + tgtId;
}

function deleteData(){
    var selected = $("#targetTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要删除的目标！');
        return;
    }

    var ids = "";
    for (var i = 0; i < selected_length; i++) {
        ids += selected[i].ID;
        if (i !== (selected_length - 1)) ids += ",";
    }
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定删除选中的目标?"
    }, function () {
        delData(ids);
    });
}

// 点击删除按钮，将数据状态更新为-2
function delData(id) {
    $.get("/target/deleteDataById", {id: id}, function (r) {
        if(r.code == 200) {
            $MB.n_success("删除成功！");
            $('#targetTable').bootstrapTable('refresh');
        }else {
            $MB.n_danger("未知异常！");
        }
    });
}

function addTarget() {
    window.location.href = "/page/target/add";
}