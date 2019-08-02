$(function () {
    getTargetType();
    getUrgency();
    getGroupDataList();
    getTipInfo();
});

// 目标分类图
function getTargetType() {
    $.get("/daily/getTargetType", {headId: headId}, function (r) {
        var chart = r.data;
        var option = getBarOption2(chart['legendData'], chart['xAxisData'], chart['xAxisName'], chart['yAxisName'], chart['seriesData']);
        option.grid = {right:'13%'};
        option.title = {
            text: '目标分类对比图',
            x:'center',
            y: 'bottom',
            textStyle:{
                color:'#000',
                fontStyle:'normal',
                fontWeight:'normal',
                fontFamily:'sans-serif',
                fontSize:12
            }
        };
        var echart = echarts.init(document.getElementById("chart1"), 'macarons');
        echart.setOption(option);
    });
}

// 紧迫度
function getUrgency() {
    $.get("/daily/getUrgency", {headId: headId}, function (r) {
        var chart = r.data;
        var option = getBarOption2(chart['legendData'], chart['xAxisData'], chart['xAxisName'], chart['yAxisName'], chart['seriesData']);
        option.grid = {right:'13%'};
        option.title = {
            text: '紧迫度分级对比图',
            x:'center',
            y: 'bottom',
            textStyle:{
                color:'#000',
                fontStyle:'normal',
                fontWeight:'normal',
                fontFamily:'sans-serif',
                fontSize:12
            }
        };
        var echart = echarts.init(document.getElementById("chart2"), 'macarons');
        echart.setOption(option);
    });
}

function getGroupDataList() {
    var settings = {
        url: '/daily/getGroupDataList',
        pagination: false,
        queryParams: function (params) {
            return {headId: headId};
        },
        columns: [[
            {
                checkbox: true,
                rowspan: 2,
                valign:"middle"
            },{
                field: 'isCheck',
                visible: false,
                rowspan: 2,
                valign:"middle"
            },{
                field: 'groupId',
                title: 'ID',
                rowspan: 2,
                visible: false,
                valign:"middle"
            },{
                field: 'groupName',
                rowspan: 2,
                title: '群组优先级',
                valign:"middle"
            }, {
                field: 'operate',
                title: '用户目标与策略明细',
                rowspan: 2,
                valign:"middle",
                formatter: function (value, row, index) {
                    return "<a style='text-align:center;text-decoration: underline;color: #000;cursor: pointer;' onclick='userDetail(" + row.groupId + ")'><font color='#1e90ff' style='font-size: 18px;'><i class='fa fa-id-card'></i></font></a>";
                },
            },{
                field: 'userCount',
                rowspan: 2,
                title: '用户数量',
                valign:"middle"
            }, {
                title: '当日成长群组描述',
                colspan: 4
            }],[{
            field: 'userValue',
            title: '用户价值',
            formatter: function (value,row,index) {
                switch (value) {
                    case "ULC_01":
                        res = "重要";
                        break;
                    case "ULC_02":
                        res = "主要";
                        break;
                    case "ULC_03":
                        res = "普通";
                        break;
                    case "ULC_04":
                        res = "长尾";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        }, {
            field: 'pathActive',
            title: '活跃度',
            formatter: function (value, row, index) {
                var res = "";
                switch (value) {
                    case "UAC_01":
                        res = "高度活跃";
                        break;
                    case "UAC_02":
                        res = "中度活跃";
                        break;
                    case "UAC_03":
                        res = "流失预警";
                        break;
                    case "UAC_04":
                        res = "弱流失";
                        break;
                    case "UAC_05":
                        res = "强流失";
                        break;
                    case "UAC_06":
                        res = "沉睡";
                        break;
                    default:
                        res = "-";
                }
                return res;
            }
        },{
            field: 'isNew',
            title: '周期阶段',
            formatter:function (value, row, index) {
                if(value == "1") {
                    return "非新手期";
                }else {
                    return "新手期";
                }
            }
        }, {
            field: 'pathMulti',
            title: '成长SPU数量',
            formatter:function (value, row, index) {
                if(value == '1') {
                    return "大于1";
                }else {
                    return "1";
                }
            }
        }]],onLoadSuccess: function () {
            $("#groupTable").find("tr").find("td").addClass("text-center");
            $("a[data-toggle='tooltip']").tooltip();
            var allData = $("#groupTable").bootstrapTable('getData');
            allData.forEach(function(value, index, arr) {
                if(value.isCheck == '1') {
                    $("#groupTable").bootstrapTable("check", index);
                }
            });
            tableCheck();
        },onClickRow: function (row, $element, field) {
            // 设置点击查看列表该记录选中状态不变
            var id = row['groupId'];
            if(field == 'operate') {
                if(row[0]) {
                    $("#groupTable").bootstrapTable("uncheckBy", {field:"groupId", values:[id]})
                }else {
                    $("#groupTable").bootstrapTable("checkBy", {field:"groupId", values:[id]})
                }
            }
        }
    };
    $MB.initTable('groupTable', settings);

}

// 注册tableCheck事件
function tableCheck() {
    $('#groupTable').on('check.bs.table', function ($element, row) {
        var groupId = row['groupId'];
        var isCheck = "1";
        var groupData = new Array();
        groupData.push({groupId: groupId, isCheck: isCheck});
        updateIfCheck(groupData);
    });

    $('#groupTable').on('uncheck.bs.table', function ($element, row) {
        var groupId = row['groupId'];
        var isCheck = "0";
        var groupData = new Array();
        groupData.push({groupId: groupId, isCheck: isCheck});
        updateIfCheck(groupData);
    });

    $('#groupTable').on('check-all.bs.table', function ($element, row) {
        var allData = $("#groupTable").bootstrapTable('getData');
        var groupData = new Array();
        allData.forEach(function(value, index, arr) {
            var id = value.groupId;
            groupData.push({groupId: id, isCheck: '1'});
        });
        updateIfCheck(groupData);
    });

    $('#groupTable').on('uncheck-all.bs.table', function ($element, row) {
        var allData = $("#groupTable").bootstrapTable('getData');
        var groupData = new Array();
        allData.forEach(function(value, index, arr) {
            var id = value.groupId;
            groupData.push({groupId: id, isCheck: '0'});
        });
        updateIfCheck(groupData);
    });
}


/**
 * 当选中或取消一条记录时，更改数据，并刷行当前页面图表信息
 * @param groupId
 * @param isCheck
 */
function updateIfCheck(groupData) {
    $.get('/daily/updateGroupCheck', {groups:JSON.stringify(groupData), headId: headId}, function (r) {
        if(r.code === 200) {
            getTargetType();
            getUrgency();
            getTipInfo();
        }else {
            $MB.n_danger("发生未知异常！");
        }
    });
}

var GROUP_ID = "";
function userDetail(groupId) {
    GROUP_ID = groupId;
    $("#user_modal").modal('show');
}

$('#user_modal').on('shown.bs.modal', function () {
    if(GROUP_ID != "") {
        getDetailDataList(GROUP_ID);
    }
});

function getDetailDataList(groupId) {
    var settings = {
        url: '/daily/getDetailPageList',
        pagination: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  //页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {headId: headId, groupId: groupId}
            };
        },
        columns: [[{
            field: 'userId',
            title: '用户ID',
            rowspan: 2,
            valign:"middle"
        },{
            title: '当日成长目标',
            colspan: 4
        },{
            title: '当日用户状态',
            colspan: 5
        },{
            title: '当日成长策略',
            colspan: 7
        }],[
            {
                field: 'purchProductName',
                title: '购买商品'
            },{
                field: 'purchTimes',
                title: '购买次数'
            },{
                field: 'tarType',
                title: '目标分类',
                formatter: function (value, row, index) {
                    var res;
                    switch (value) {
                        case "target01":
                            res = "提升";
                            break;
                        case "target02":
                            res = "留存";
                            break;
                        case "target03":
                            res = "挽回";
                            break;
                        default:
                            res = "-";
                            break;
                    }
                    return res;
                }
            },{
                field: 'piecePrice',
                title: '件单价'
            },{
                field: 'spuName',
                title: '成长SPU'
            },{
                field: 'completePurch',
                title: '完成购买(次)'
            },{
                field: 'pathActiv',
                title: '活跃度',
                formatter: function (value, row, index) {
                    var res = "";
                    switch (value) {
                        case "UAC_01":
                            res = "高度活跃";
                            break;
                        case "UAC_02":
                            res = "中度活跃";
                            break;
                        case "UAC_03":
                            res = "流失预警";
                            break;
                        case "UAC_04":
                            res = "弱流失";
                            break;
                        case "UAC_05":
                            res = "强流失";
                            break;
                        case "UAC_06":
                            res = "沉睡";
                            break;
                        default:
                            res = "-";
                    }
                    return res;
                }
            },{
                field: 'userValue',
                title: '价值',
                formatter: function (value, row, index) {
                    var res = "";
                    switch (value) {
                        case "ULC_01":
                            res = "重要";
                            break;
                        case "ULC_02":
                            res = "主要";
                            break;
                        case "ULC_03":
                            res = "普通";
                            break;
                        case "ULC_04":
                            res = "长尾";
                            break;
                        default:
                            res = "-";
                    }
                    return res;
                }
            },{
                field: 'urgencyLevel',
                title: '紧迫度'
            },{
                field: 'recRetentionName',
                title: '留存推荐'
            },{
                field: 'recUpName',
                title: '向上推荐'
            },{
                field: 'recCrossName',
                title: '交叉推荐'
            },{
                field: 'recType',
                title: '推荐类型',
                formatter: function (value, row, index) {
                    var res = "";
                    switch (value) {
                        case "1":
                            res = "留存推荐";
                            break;
                        case "2":
                            res = "向上推荐";
                            break;
                        case "3":
                            res = "交叉推荐";
                            break;
                        default:
                            res = "-";
                            break;
                    }
                    return res;
                }
            },
            {
                field: 'discountLevel',
                title: '优惠力度'
            },{
                field: 'couponDenom',
                title: '优惠面额'
            },{
                field: 'orderPeriod',
                title: '触达时段'
            }
            ]]
    };
    $('#userListTable').bootstrapTable('destroy');
    $MB.initTable('userListTable', settings);
}

function getTipInfo() {
    $.get("/daily/getTipInfo", {headId: headId}, function (r) {
        var data = r.data;
        if(data != null) {
            var actual = data['ACTUAL'] == null ? data['TOTAL'] : data['ACTUAL'];
            var code = "<i class=\"mdi mdi-alert-circle-outline\"></i>"+data['TOUCHDT']+":有 " + data['TOTAL'] + " 个用户需要完成成长目标，您实际选择" + actual + "个用户进行成长管理；";
            $("#tipContent").html("").append(code);
        }
    });
}

/**
 * 提交数据，生成名单
 */
function submitData() {
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定提交当前数据?"
    }, function () {
        $.get("/daily/submitData", {headId: headId}, function (r) {
            if(r.code === 200) {
                $MB.n_success("提交成功！");
            }else {
                $MB.n_danger("未知错误！");
            }
            setTimeout(function () {
                window.location.href = "/page/daily";
            }, 1000);
        });
    });
}

/**
 * 还原数据选中状态
 */
function resetDataCheck() {
    $.get("/daily/getOriginalGroupCheck", {}, function (r) {
        var data = r.data;
        var allData = $("#groupTable").bootstrapTable('getData');
        var selectedIds = new Array();
        allData.forEach(function(value, index, arr) {
            selectedIds.push(value.groupId);
        });
        if(data != null) {
            $.each(data, function (k,v) {
                var id = v['ID'];
                if(selectedIds.indexOf(id) > -1) {
                    if(v['CHECKED'] == '1') {
                        $("#groupTable").bootstrapTable("checkBy", {field:"groupId", values:[id]})
                    }else if(v['CHECKED'] == '0') {
                        $("#groupTable").bootstrapTable("uncheckBy", {field:"groupId", values:[id]})
                    }
                }
            });
        }
    });
}

// 选中或取消所有
function checkAllData() {
    var selectedData = $("#groupTable").bootstrapTable('getSelections');
    var allData = $("#groupTable").bootstrapTable('getData');
    var groupData = new Array();
    var selectedIds = new Array();
    selectedData.forEach(function(value, index, arr) {
        selectedIds.push(value.groupId);
    });
    allData.forEach(function(value, index, arr) {
        var id = value.groupId;
        if(selectedIds.indexOf(id) > -1) {
            groupData.push({groupId: id, isCheck: '1'});
        }else {
            groupData.push({groupId: id, isCheck: '0'});
        }
    });
    $.get("/daily/submitData", {headId: headId, groups: JSON.stringify(groupData)}, function (r) {
        if(r.code === 200) {
            $MB.n_success("提交成功！");
        }else {
            $MB.n_danger("未知错误！");
        }
        setTimeout(function () {
            window.location.href = "/page/daily";
        }, 1000);
    });
}