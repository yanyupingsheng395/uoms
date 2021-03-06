getTableData();
function getTableData() {
    let settings = {
        url: "/msg/getMsgPageList",
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
                param: {msgLevel: $("#msgLevel").val(), readFlag: $("#readFlag").val()}
            };
        },
        columns: [
            {
                field: 'msgTitle',
                title: '标题',
                align: "center",
            },
            {
                field: 'msgContent',
                title: '内容',
                align: "left",
            },{
                field: 'typeName',
                title: '类型',
                align: "center",
            },
            {
                field: 'msgLevelDesc',
                title: '消息等级',
                align: "center",
                formatter: function (value, row, index) {
                    if(value === '一般') {
                        return "<span class='badge btn-info'>"+value+"</span>";
                    }
                    if(value === '警告') {
                        return "<span class='badge btn-warning'>"+value+"</span>";
                    }else if(value === '严重') {
                        return "<span class='badge btn-danger'>"+value+"</span>";
                    }else
                    {
                        return '';
                    }
                }
            },{
                field: 'readFlag',
                title: '是否已读',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value === '0') {
                        return "未读";
                    }
                    if(value === '1') {
                        return "已读";
                    }
                    return '-';
                }
            },{
                field: 'createDt',
                title: '创建时间',
                align: 'center'
            }
        ]
    };
    $("#allMsgTable").bootstrapTable('destroy').bootstrapTable(settings);
}

function searchMsg() {
    getTableData();
}

function resetMsg() {
    $("#msgLevel").find("option[value='']").attr("selected", "selected");
    $("#readFlag").find("option[value='']").attr("selected", "selected");
    getTableData();
}