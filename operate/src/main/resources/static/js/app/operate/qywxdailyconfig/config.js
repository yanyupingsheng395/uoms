let step;
let current_group = {userValue: 'ULC_01', lifeCycle: '1', pathActive: 'UAC_01', tarType: 'H'};
$( function () {
    //加载群组列表
    getTableData();
    step = steps( {
        el: "#configSteps",
        data: [
            {title: "配置消息", description: ""},
            {title: "配置补贴", description: ""},
            {title: "配置完成", description: ""}
        ],
        center: true,
        dataOrder: ["title", "line", "description"]
    } );
} );

/**
 * 获取用户群组数据
 */
function getTableData() {
    let settings = {
        pagination: false,
        singleSelect: false,
        columns: [
            [
                {
                    title: '个性化文案用户群组',
                    colspan: 3
                },
                {
                    title: '为群组配置个性化消息',
                    colspan: 2
                }
            ], [{
                    field: 'lifecycle',
                    title: '在成长类目所处生命周期阶段',
                    align: 'center',
                    valign: 'middle',
                    formatter: function (value, row, index) {
                        if (value == "1") {
                            return "新手期";
                        }
                        if (value == "0") {
                            return "成长期；成熟期；衰退期";
                        }
                        return "";
                    }
                },
                {
                    field: 'pathActive',
                    align: 'center',
                    title: '完成下一次购买前的活跃度状态',
                    formatter: function (value, row, index) {
                        var res = "";
                        switch (value) {
                            case "UAC_01":
                                res = "活跃状态";
                                break;
                            case "UAC_02,UAC_03":
                                res = "留存状态；流失状态";
                                break;
                            default:
                                res = "-";
                        }
                        return res;
                    }
                }, {
                    title: '理解用户群组',
                    align: 'center',
                    valign: 'middle',
                    formatter: function (value, row, index) {
                        return "<a style='color: #4c4c4c' onclick='openSelectedGroupModal()'><i class='mdi mdi-account mdi-18px'></i></a>";
                    }
                },
                {
                    title: '企业微信',
                    align: 'center',
                    field: 'qywxId',
                    valign: 'middle',
                    formatter: function (value, row, index) {
                        if(value === null || value === undefined || value === '') {
                            return "<a  style='color: #4c4c4c' onclick='openWxMsgModal("+value+", \""+row['lifecycle']+"\", \""+row['pathActive']+"\")'>" +
                                "<i class='mdi mdi-wechat mdi-18px'></i><span style='color:#f96868;'>&nbsp;[未配置]</span>" +
                                "</a>";
                        }else {
                            return "<a  style='color: #4c4c4c' onclick='openWxMsgModal("+value+", \""+row['lifecycle']+"\", \""+row['pathActive']+"\")'>" +
                                "<i class='mdi mdi-wechat mdi-18px'></i><span style='color: #52c41a'>&nbsp;[已配置]</span>" +
                                "</a>";
                        }
                    }
                },
                {
                    title: '最近一次配置时间',
                    align: 'center',
                    field: 'qywxOpDt',
                    valign: 'middle'
                }
            ]
        ]
    };
    $( "#userGroupTable" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
    $.get( "/qywxDailyConfig/userGroupList", {}, function (r) {
        var dataList = r.data;
        $( "#userGroupTable" ).bootstrapTable( 'load', dataList );
        $( "a[data-toggle='tooltip']" ).tooltip();
        $( '#userGroupTable' ).bootstrapTable( 'getData', true );
        for (let i = 0; i < 2; i++) {
            $( "#userGroupTable" ).bootstrapTable( 'mergeCells', {
                index: i * 2,
                field: "lifecycle",
                colspan: 1,
                rowspan: 2
            } );
        }
    } );
}

// 合并单元格
function mergeCells(data, fieldName, colspan, target) {
    //声明一个map计算相同属性值在data对象出现的次数和
    var sortMap = {};
    for (var i = 0; i < data.length; i++) {
        for (var prop in data[i]) {
            if (prop == fieldName) {
                var key = data[i][prop]
                if (sortMap.hasOwnProperty(key)) {
                    sortMap[key] = sortMap[key] * 1 + 1;
                } else {
                    sortMap[key] = 1;
                }
                break;
            }
        }
    }
    var index = 0;
    for (var prop in sortMap) {
        var count = sortMap[prop] * 1;
        $(target).bootstrapTable('mergeCells', {index: index, field: fieldName, colspan: colspan, rowspan: count});
        index += count;
    }
}

/**
 * 获取 群组的描述信息 并渲染到对应的表格上去 (公用方法)
 * @param userValue
 * @param lifecycle
 * @param pathActive
 * @param tableId
 */
function getGroupDescription(tableId) {
    let settings = {
        url: '/qywxDailyConfig/getGroupDescription',
        pagination: false,
        singleSelect: false,
        columns: [
            {
                title: '群组特征',
                field: 'colName'
            },
            {
                title: '特征值',
                field: 'colValue'
            },
            {
                title: '特征值业务理解',
                field: 'colDesc'
            },{
                title: '推送配置建议',
                field: 'colAdvice'
            }
        ],
        onLoadSuccess: function () {
            for (let i = 0; i < 2; i++) {
                $( "#" + tableId).bootstrapTable( 'mergeCells', {
                    index: i * 3,
                    field: "colName",
                    colspan: 1,
                    rowspan: 3
                } );
            }
        }
    };
    $( "#" + tableId).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

function getQywxGroupDescription(tableId) {
    let settings = {
        pagination: false,
        singleSelect: false,
        columns: [
            {
                title: '群组特征',
                field: 'colName'
            },
            {
                title: '特征值',
                field: 'colValue'
            },
            {
                title: '特征值业务理解',
                field: 'colDesc'
            },{
                title: '推送配置建议',
                field: 'colAdvice'
            }
        ]
    };
    $( "#" + tableId).bootstrapTable( 'destroy' ).bootstrapTable( settings );
    var dataList = [
        {colName: '在成长类目所处生命周期阶段', colValue:'新手期', colDesc:'', colAdvice:''},
        {colName: '在成长类目所处生命周期阶段', colValue:'成长期；成熟期', colDesc:'', colAdvice:''},
        {colName: '完成下一次购买前的活跃度状态', colValue:'活跃状态', colDesc:'', colAdvice:''},
        {colName: '完成下一次购买前的活跃度状态', colValue:'留存状态', colDesc:'', colAdvice:''},
    ];
    $( "#" + tableId).bootstrapTable( 'load', dataList );
    for (let i = 0; i < 2; i++) {
        $( "#" + tableId).bootstrapTable( 'mergeCells', {
            index: i * 2,
            field: "colName",
            colspan: 1,
            rowspan: 2
        } );
    }
}

/**
 * 微信消息窗口
 */
var current_lifeCycle;
var current_pathActive;
function openWxMsgModal(qywxId, lifeCycle, pathActive) {
    getWxMsgTableData(qywxId);
    getQywxGroupDescription('selectedGroupInfo2');
    $( '#wxMsgListModal' ).modal( 'show' );
    current_lifeCycle = lifeCycle;
    current_pathActive = pathActive;
}

/**
 * 企微新增窗口
 */
function openWxMsgAddModal() {
    $( "#wxMsgAddModal" ).modal( 'show' );
    $( "#wxMsgListModal" ).modal( 'hide' );
}

/**
 * 打开理解群组的modal页
 * @param userValue
 * @param lifecycle
 * @param pathActive
 */
function openSelectedGroupModal() {
    getGroupDescription('userGroupDescTable');
    $( "#selectedGroupModal" ).modal( 'show' );
}

/**
 * 打开公众号的消息配置面板 暂时用不到
 */
function personalMsg() {
    $( "#msgListModal" ).modal( 'show' );
}

function contentInput() {
    var content = $( "#smsContent" ).val();
    if (content === '') {
        content = "请输入消息内容";
    }
    $( '#preview' ).html( '<div class=\'arrow\'></div>' + content )
}


function prevStep(stepNum) {
    if (stepNum === 1) {
        step.setActive( 0 );
        getTableData();
        $( "#step1" ).attr( "style", "display:block;" );
        $( "#step2" ).attr( "style", "display:none;" );
        $( "#step3" ).attr( "style", "display:none;" );
    }
}

function nextStep(stepNum) {
    if (stepNum === 2) {
        step.setActive( 1 );
        //将文案的修改标记进行恢复
        $.get("/qywxDailyConfig/resetOpFlag", {}, function (r) {
            couponTable();
            $( "#step1" ).attr( "style", "display:none;" );
            $( "#step2" ).attr( "style", "display:block;" );
            $( "#step3" ).attr( "style", "display:none;" );
        });
    }
    if(stepNum === 3) {
        $.get("/qywxDailyConfig/autoSetGroupCoupon", {}, function (r) {
            if(r.code === 200) {
                $MB.n_success("根据您的当前补贴信息，系统已自动配置补贴到用户群组上！");
                step.setActive( 2);
                $( "#step1" ).attr( "style", "display:none;" );
                $( "#step2" ).attr( "style", "display:none;" );
                $( "#step3" ).attr( "style", "display:block;" );
                getConfigInfoByGroup();
            }else {
                $MB.n_warning(r.msg);
            }
        });
    }
}

/**
 * 优惠券列表的表格 （短信和小程序券共用）
 * @param tableId
 * @param data
 */
function couponListTable(tableId, data) {
    let settings = {
        pagination: false,
        singleSelect: false,
        columns: [
                {
                    title: '门槛(元)',
                    field: 'couponThreshold',
                    align: 'center'
                },
                {
                    title: '面额(元)',
                    field: 'couponDenom',
                    align: 'center'
                }
            ]
        };
    $( "#" + tableId ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
    $( "#" + tableId ).bootstrapTable( 'load', data );
}


/**
 * 第三步 预览群组页面 点击左侧标签后的响应事件
 * @param key 标点当前点了那组属性 可能的值有userValue、pathActive、lifecycle、tarType
 * @param value 在第一个key对应的属性上面的值
 * @param dom 当前属性所在的html元素
 * @param className 点击事件后修改的样式类名
 */
function userGroupButton(key, value, dom, className) {
    $(dom).removeClass("btn-secondary").addClass(className);
    $(dom).siblings('button').removeClass(className).addClass("btn-secondary");
    current_group[key] = value;
    getConfigInfoByGroup();
}

/**
 * 刷新右侧的预览数据
 */
function getConfigInfoByGroup() {
    $.get("/dailyConfig/getConfigInfoByGroup",current_group, function (r) {
        let data = r.data;
        $("#duanxinContent").html('').append(((data['duanxin'] === undefined) || (data['duanxin'] === null)) ? '未配置短信文案':data['duanxin']);
        $("#weixinContent").html('').append(((data['weixin'] === undefined) || (data['weixin'] === null)) ? '未配置企业微信文案' : data['weixin']);
        //加载优惠券列表
        couponListTable("couponPreviewTable", data['couponInfos']);
    });
}

/**
 * 更新微信消息到用户群组上
 */
function updateWxMsg() {
    var selected = $("#msgListDataTable").bootstrapTable('getSelections');
    if(selected.length === 0) {
        $MB.n_warning("请选择一条记录！");
        return;
    }
    var qywxId = selected[0]['qywxId'];
    $.get("/qywxDailyConfig/updateWxMsgId", {qywxId: qywxId, lifeCycle: current_lifeCycle, pathActive: current_pathActive}, function (r) {
        if(r.code === 200) {
            $MB.n_success("更新成功！");
            $("#wxMsgListModal").modal('hide');
            getTableData();
        }
    });
}