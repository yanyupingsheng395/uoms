
let active_arr = [{
    id:'UAC_01',
    text: '高度活跃'
},{
    id:'UAC_02',
    text: '中度活跃'
},{
    id:'UAC_03',
    text: '流失预警'
}];

let growth_arr = [{
    id: '1',
    text: '成长性1级'
},{
    id: '2',
    text: '成长性2级'
},{
    id: '3',
    text: '成长性3级'
},{
    id: '4',
    text: '成长性4级'
},{
    id: '5',
    text: '成长性5级'
},{
    id: '6',
    text: '成长性6级'
},{
    id: '7',
    text: '成长性7级'
},{
    id: '8',
    text: '成长性8级'
},{
    id: '9',
    text: '成长性9级'
},{
    id: '10',
    text: '成长性10级'
},{
    id: '11',
    text: '成长性11级'
},{
    id: '12',
    text: '成长性12级'
},{
    id: '13',
    text: '成长性13级'
},{
    id: '14',
    text: '成长性14级'
},{
    id: '15',
    text: '成长性15级'
},{
    id: '16',
    text: '成长性16级'
}];

createJsTree();
function createJsTree() {
    $('#tree_active').jstree({
        "core": {
            'data': [{
                text: '活跃度',
                children: active_arr
            }],
            'themes': {
                icons: false
            },
        },
        "state": {
            "disabled": true
        },
        "checkbox": {
            "three_state": true
        },
        "plugins": ["wholerow", "checkbox"]
    }).bind('click.jstree', function(event) {
        selectGroup();
    }).bind('loaded.jstree ', function (event) {
        getDefaultActive();
    });

    $('#tree_urgency').jstree({
        "core": {
            'data':  [{
                text: '成长性',
                children: growth_arr
            }],
            'themes': {
                icons: false
            }
        },
        "state": {
            "disabled": true
        },
        "checkbox": {
            "three_state": true
        },
        "plugins": ["wholerow", "checkbox"]
    }).bind('click.jstree', function(event) {
        selectGroup();
    }).bind('loaded.jstree ', function (event) {
        getDefaultGrowth();
    });
}

// 根据树获取对应的群组和名单
function selectGroup() {
    let selected_active_ids;
    let selected_growth_ids;

    let ref_active = $('#tree_active').jstree(true);
    let activeIds = ref_active.get_selected(false);
    if(selected_active_ids == undefined) {
        selected_active_ids = new Array();
        activeIds.forEach(v=>{
            selected_active_ids.push(v);
        });
    }else {
        activeIds.forEach(v=>{
            if(selected_active_ids.indexOf(v) == -1) {
                selected_active_ids.push(v);
            }
        });
    }

    let ref_urgency = $('#tree_urgency').jstree(true);
    let growthIds = ref_urgency.get_selected(false);

    if(selected_growth_ids == undefined) {
        selected_growth_ids = new Array();
        growthIds.forEach(v=>{
            selected_growth_ids.push(v);
        });
    }else {
        growthIds.forEach(v=>{
            if(selected_growth_ids.indexOf(v) == -1) {
                selected_growth_ids.push(v);
            }
        });
    }

    let code = "";

    let activeMap = new Map();
    active_arr.forEach(v=>{
        activeMap.set(v['id'], v['text']);
    });

    let growthMap = new Map();
    growth_arr.forEach(v=>{
        growthMap.set(v['id'], v['text']);
    });

    selected_active_ids.forEach(v=>{
        let v1 = activeMap.get(v);
        if(v1 != undefined) {
            code += "<tr><td>" + v1 + "</td><td class='text-right'><i class='mdi mdi-close' style='cursor: pointer;' onclick='removeTr(this, \"" + v + "\", \"active\")'></i></td></tr>";
        }else {
            activeIds.remove(v);
        }
    });
    selected_growth_ids.forEach(x=>{
        let v2 = growthMap.get(x);
        if(v2 != undefined) {
            code += "<tr><td>" + v2 + "</td><td class='text-right'><i class='mdi mdi-close' style='cursor: pointer;' onclick='removeTr(this, \"" + x + "\", \"growth\")'></i></td></tr>";
        }else {
            growthIds.remove(x);
        }
    });
    $("#selected_condition").html('').append(code);

    getSelectedGroup(activeIds.join(","), growthIds.join(","));
}

function removeTr(dom, id, type) {
    if(type == "active") {
        $("#tree_active").jstree('deselect_node', id, true);
    }else if(type == "growth") {
        $("#tree_urgency").jstree('deselect_node', id, true);
    }
    $(dom).parent().parent().remove();
    selectGroup();
}
getSelectedGroup();
function getSelectedGroup(activeIds, growthIds) {
    let code = "";
    $.get("/daily/getSelectedGroup", {headId: headId, activeIds:activeIds, growthIds:growthIds}, function (r) {
        let data = r.data;
        let groupIds = new Array();
        data.forEach(v=>{
            code += "<tr><td>" + v['NAME'] + "</td><td class='text-right'><i class='mdi mdi-close' style='cursor: pointer;' onclick='removeGroup(this, \"" + v['ID'] + "\")'></i></td></tr>";
            groupIds.push(v['ID']);
        });
        $("#selected_group").html('').append(code);
        getUserDataList(groupIds.join(","));
        $("#selectedGroupIds").val(groupIds.join(","));
    });
}

// 获取已选群组的用户名单
function getUserDataList(groupIds) {
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
                param: {headId: headId, groupIds: groupIds}
            };
        },
        columns: [[{
            field: 'userId',
            title: '用户ID',
            rowspan: 2,
            valign:"middle"
        },{
            title: '当日用户目标',
            colspan: 3
        },{
            title: '当日用户状态',
            colspan: 3
        },{
            title: '当日成长策略',
            colspan: 6
        }],[
            {
                field: 'spuName',
                title: '成长SPU'
            },{
                field: 'purchTimes',
                title: '应该买第几次'
            },{
                field: 'piecePrice',
                title: '件单价（元/件）'
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
                field: 'groupName',
                title: '所在群组'
            },{
                field: 'recRetentionName',
                title: '留存推荐商品'
            },{
                field: 'recUpName',
                title: '向上推荐商品'
            },{
                field: 'recCrossName',
                title: '交叉推荐商品'
            },{
                field: 'discountLevel',
                title: '建议折扣力度'
            },{
                field: 'couponDenom',
                title: '建议优惠面额'
            },{
                field: 'orderPeriod',
                title: '建议触达时段'
            }
        ]], onLoadSuccess: function (data) {
            $("#optNum").html('').append($("#userListTable").bootstrapTable("getOptions").totalRows);
        }
    };
    $('#userListTable').bootstrapTable('destroy');
    $MB.initTable('userListTable', settings);
}

// 获取活跃度和成长性的推荐情况
function getDefaultActive() {
    $.get("/daily/getDefaultActive", {headId: headId}, function (r) {
        var activeIds = r.data;
        activeIds.forEach(v=>{
            $("#tree_active").jstree('select_node', v, true);
        });
        selectGroup();
    });
}

function getDefaultGrowth() {
    $.get("/daily/getDefaultGrowth", {headId: headId}, function (r) {
        var growthIds = r.data;
        growthIds.forEach(v=>{
            $("#tree_urgency").jstree('select_node', v, true);
        });
        selectGroup();
    });
}


/**
 * 移除已选群组
 * @param dom 当前元素
 * @param id 群组ID
 */
function removeGroup(dom, id) {
    var tmp = $("#selectedGroupIds").val().split(",");
    $(dom).parent().parent().remove();
    tmp.remove(id);
    $("#selectedGroupIds").val(tmp.join(','));
    getUserDataList(tmp.join(","));
}

// 获取用户最后选择的群组并更改group表的状态为1
function setGroupCheck() {
    var groupIds = $("#selectedGroupIds").val();
    if(groupIds == "") {
        $MB.n_warning("用户群组不能为空！");
    }else {
        $.get("/daily/setGroupCheck", {headId:headId, groupIds: groupIds}, function (r) {
            if(r.code != 200) {
                $MB.n_danger("未知异常发生！");
            }else {
                step2();
            }
        });
    }

}

/**
 * 下一步
 */
$("#btn_next").click(function () {
    setGroupCheck();
});

function step2() {
    step.setActive(1);
    $("#step1").attr("style", "display: none;");
    $("#step2").attr("style", "display: block;");
    // 获取群组信息
    getGroupDataList();
}