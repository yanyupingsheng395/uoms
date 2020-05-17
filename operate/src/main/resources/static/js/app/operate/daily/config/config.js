let step;
$( function () {
    getTableData();
    step = steps( {
        el: "#configSteps",
        data: [
            {title: "创建消息", description: ""},
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
                    title: '成长目标',
                    colspan: 1
                },
                {
                    title: '用户差异',
                    colspan: 3
                },
                {
                    title: '个性化推送(消息)',
                    colspan: 4
                }
            ], [
                {
                    title: '购买商品与时间',
                    field: 'groupInfo',
                    align: 'center',
                    formatter: function (value, row, index) {
                        return "<a style='color: #333;cursor:pointer;' onclick='userInsight(\"" + row['userValue'] + "\",\"" + row['pathActive'] + "\",\"" + row['lifecycle'] + "\")'><i class='mdi mdi-account mdi-14px'></i></a>";
                    }
                },
                {
                    field: 'userValue',
                    title: '用户对类目的价值/沉默成本',
                    align: 'center',
                    valign: "middle",
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
                },
                {
                    field: 'lifecycle',
                    title: '用户对类目的生命周期阶段',
                    align: 'center',
                    valign: "middle",
                    formatter: function (value, row, index) {
                        if (value == "1") {
                            return "新用户";
                        }
                        if (value == "0") {
                            return "复购用户";
                        }
                        return "";
                    }
                },
                {
                    field: 'pathActive',
                    align: 'center',
                    title: '用户下一次转化的活跃度节点',
                    formatter: function (value, row, index) {
                        var res = "";
                        switch (value) {
                            case "UAC_01":
                                res = "促活节点";
                                break;
                            case "UAC_02":
                                res = "留存节点";
                                break;
                            case "UAC_03":
                                res = "弱流失预警";
                                break;
                            case "UAC_04":
                                res = "强流失预警";
                                break;
                            case "UAC_05":
                                res = "沉睡预警";
                                break;
                            default:
                                res = "-";
                        }
                        return res;
                    }
                },
                {
                    title: '理解用户群组',
                    align: 'center',
                    formatter: function (value, row, index) {
                        return "<a style='color: #4c4c4c' onclick='openSelectedGroupModal()'><i class='fa fa-user'></i></a>";
                    }
                },
                {
                    title: '短信',
                    align: 'center',
                    formatter: function (value, row, index) {
                        return "<a style='color: #4c4c4c' onclick='openSmsTemplateModal(\""+row['groupId']+"\")'><i class='fa fa-envelope-o'></i></a>";
                    }
                },
                {
                    title: '企业微信',
                    align: 'center',
                    formatter: function (value, row, index) {
                        return "<a style='color: #4c4c4c' onclick='openWxMsgModal()'><i style='color: #52c41a' class='fa fa-wechat'></i></a>";
                    }
                },
                {
                    title: '公众号',
                    align: 'center',
                    formatter: function (value, row, index) {
                        return "<a style='color: #4c4c4c' onclick='personalMsg()'><i style='color: #409eff' class='fa fa-users'></i></a>";
                    }
                }
            ]
        ]
    };
    $( "#userGroupTable" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
    $.get( "/daily/userGroupList", {}, function (r) {
        var dataList = r.data;
        $( "#userGroupTable" ).bootstrapTable( 'load', dataList );
    } );
}

/**
 * 微信消息窗口
 */
function openWxMsgModal() {
    getWxMsgTableData();
    $( '#wxMsgListModal' ).modal( 'show' );
}

/**
 * 企微新增窗口
 */
function openWxMsgAddModal() {
    $( "#wxMsgAddModal" ).modal( 'show' );
    $( "#wxMsgListModal" ).modal( 'hide' );
}

function openSelectedGroupModal() {
    $( "#selectedGroupModal" ).modal( 'show' );
}

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
    }
}

function nextStep(stepNum) {
    if (stepNum === 2) {
        step.setActive( 1 );
        couponTable();
        $( "#step1" ).attr( "style", "display:none;" );
        $( "#step2" ).attr( "style", "display:block;" );
    }
}