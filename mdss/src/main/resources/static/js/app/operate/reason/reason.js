$(function () {
    var settings = {
        url: "/reason/list",
        method: 'post',
        singleSelect: true,
        cache: false,
        pagination: true,
        sidePagination: "server",
        pageNumber: 1,            //初始化加载第一页，默认第一页
        pageSize: 10,            //每页的记录行数（*）
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit )+ 1,  //页码
                param: {reasonName: $("input[name='reasonName']").val()}
            };
        },
        columns: [
            {checkbox: true}, {
                field: 'reasonName',
                title: '编号'}, {
                field: 'progress',
                title: '进度',
                formatter: function (value, row, index) {
                    return value == null ? "" : value + "%"
                }
            } ,{
                // R表示计算中 F表示计算完成
                field: 'status',
                title: '状态',
                formatter: function (value, row, index) {
                    if (value == "R") {
                        return "<span class='badge label-primary'>计算中</span>";
                    }else if (value == "F") {
                        return "<span class='badge label-primary'>完成</span>";
                    }else if (value == "E") {
                        return "<span class='badge label-danger'>错误</span>";
                    }
                }
            },  {
                field: 'kpiName',
                title: '指标'
            },{
                field: 'periodName',
                title: '周期',
                formatter: function (value, row, index) {
                    return row.beginDt+"至"+row.endDt;
                }
            },{
                field: 'source',
                title: '来源'
            }, {
                field: 'dimDisplayName',
                title: '维度&值',
                formatter: function (value, row, index) {
                    if(null == value || value == "" || value == undefined) {
                        return "-";
                    }else {
                        if(value.length >= 10) {
                            var newVal = value.substr(0, 10) + "...";
                            var title = value.replace(";", ";&nbsp;&nbsp;");
                            return "<a style='color: #000000;border-bottom: 1px solid' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-placement=\"bottom\" data-original-title=\""+title+"\">"+newVal+"</a>";
                        }else {
                            return value;
                        }
                    }
                }
            },{
                field: 'createDt',
                title: '创建时间'
            }],onLoadSuccess: function(data){
            $("a[data-toggle='tooltip']").tooltip();
        }
    };

    $MB.initTable('reasonTable',settings);

    //为刷新按钮绑定事件
    $("#btn_refresh").on("click",function () {
        $('#reasonTable').bootstrapTable('refresh');
    });
});

function viewReason(){
    var selected = $("#reasonTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要查看的原因任务！');
        return;
    }
    if (selected_length > 1) {
        $MB.n_warning('一次只能查看一个原因任务！');
        return;
    }
    var reasonId = selected[0]["reasonId"];
    //进入查看界面 todo 更改为post方式
    window.location.href = "/page/reason/viewReason?reasonId=" + reasonId;
}

function deleteReason() {
    var selected = $("#reasonTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要删除的原因任务！');
        return;
    }

    var ids = "";
    for (var i = 0; i < selected_length; i++) {
        ids += selected[i].reasonId;
        if (i !== (selected_length - 1)) ids += ",";
    }

    //遮罩层打开
    lightyear.loading('show');
    $MB.confirm({
        title: '<i class="mdi mdi-alert-circle-outline"></i>提示：',
        content: '确认删除选中的原因任务？'
    }, function () {
        $.getJSON("/reason/deleteReasonById?reasonId="+ids,function (resp) {
            if (resp.code === 200){
                lightyear.loading('hide');
                //提示成功
                $MB.n_success('删除成功!');
                //刷新表格
                //todo 如果在后面某个页上删除数据后，刷新后还停在当前页
                $('#reasonTable').bootstrapTable('refresh');
            }
        })
    });
}

function updateReason() {
    var selected = $("#reasonTable").bootstrapTable('getSelections');
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要计算的原因任务！');
        return;
    }
    if (selected_length > 1) {
        $MB.n_warning('一次只能计算一个原因任务！');
        return;
    }
    // 计算状态的记录不可重复计算
    if (selected[0]["status"] === "F") {
        $MB.n_warning('该记录已计算完成！');
        return;
    }
    var reasonId = selected[0]["reasonId"];
    lightyear.loading('show');
    $.getJSON("/reason/updateProgressById?reasonId="+reasonId,function (resp) {
        if (resp.code === 200){
            lightyear.loading('hide');
            $MB.n_success('计算成功!');
            $('#reasonTable').bootstrapTable('refresh');
        }
    });
}

function searchReason() {
    $MB.refreshTable('reasonTable');
}

function resetReason() {
    $("input[name='reasonName']").val('');
    $MB.refreshTable('reasonTable');
}