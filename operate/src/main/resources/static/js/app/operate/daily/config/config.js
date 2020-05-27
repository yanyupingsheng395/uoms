let step;
let current_group = {userValue: 'ULC_01', lifeCycle: '1', pathActive: 'UAC_01', tarType: 'H'};
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
                    colspan: 4
                },
                {
                    title: '个性化推送(消息)',
                    colspan: 4
                }
            ], [
                {
                    title: '购买商品与时间',
                    field: 'timeAndShop',
                    align: 'center',
                    valign: 'top',
                    formatter: function (value, row, index) {
                        return "<a href='/page/insight' target='_blank' style='color: #48b0f7;text-decoration: underline;'>系统配置</a>";
                    }
                },
                {
                    field: 'userValue',
                    title: '用户对类目的价值/沉默成本',
                    align: 'center',
                    formatter: function (value, row, index) {
                        var res = "";
                        switch (value) {
                            case "ULC_01":
                                res = "高价值低敏感";
                                break;
                            case "ULC_02":
                                res = "高价值高敏感";
                                break;
                            case "ULC_03":
                                res = "中价值高敏感";
                                break;
                            case "ULC_04":
                                res = "低价值低敏感";
                                break;
                            case "ULC_05":
                                res = "低价值高敏感";
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
                            return "新手期";
                        }
                        if (value == "0") {
                            return "成长期";
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
                }, {
                    title: '理解用户群组',
                    align: 'center',
                    formatter: function (value, row, index) {
                        return "<a style='color: #4c4c4c' onclick='openSelectedGroupModal()'><i class='fa fa-user'></i></a>";
                    }
                },
                {
                    title: '短信',
                    align: 'center',
                    field: 'smsCode',
                    formatter: function (value, row, index) {
                        if(value === null || value === undefined || value === '') {
                            return "<a style='color: #4c4c4c' onclick='openSmsTemplateModal(\""+row['groupId']+"\")'>" +
                                "<i class='fa fa-envelope-o'></i><span style='color: #f96868'>&nbsp;[未配置]</span>" +
                                "</a>";
                        }else {
                            return "<a style='color: #4c4c4c' onclick='openSmsTemplateModal(\""+row['groupId']+"\")'>" +
                                "<i class='fa fa-envelope-o'></i><span style='color: #52c41a'>&nbsp;[已配置]</span>" +
                                "</a>";
                        }

                    }
                },
                {
                    title: '企业微信',
                    align: 'center',
                    field: 'qywxId',
                    formatter: function (value, row, index) {
                        if(value === null || value === undefined || value === '') {
                            return "<a style='color: #52c41a' onclick='openWxMsgModal(\""+row['groupId']+"\")'>" +
                                "<i class='mdi mdi-wechat mdi-18px'></i><span style='color:#f96868;'>&nbsp;[未配置]</span>" +
                                "</a>";
                        }else {
                            return "<a style='color: #52c41a' onclick='openWxMsgModal(\""+row['groupId']+"\")'>" +
                                "<i class='mdi mdi-wechat mdi-18px'></i><span style='color: #52c41a'>&nbsp;[已配置]</span>" +
                                "</a>";
                        }

                    }
                },
                {
                    title: '公众号',
                    align: 'center',
                    formatter: function (value, row, index) {
                        if(value === null || value === undefined || value === '') {
                            return "<a style='color: #48b0f7' onclick='personalMsg(\""+row['groupId']+"\")'>" +
                                "<i class='mdi mdi-pig'></i><span style='color: #f96868'>&nbsp;[未配置]</span>" +
                                "</a>";
                        }else {
                            return "<a style='color: #48b0f7' onclick='personalMsg(\""+row['groupId']+"\")'>" +
                                "<i class='fa fa-users'></i><span style='color: #52c41a'>&nbsp;[已配置]</span>" +
                                "</a>";
                        }

                    }
                }
            ]
        ]
    };
    $( "#userGroupTable" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
    $.get( "/daily/userGroupList", {}, function (r) {
        var dataList = r.data;
        var total = dataList.length;
        var limit = total / 10;
        $( "#userGroupTable" ).bootstrapTable( 'load', dataList );
        $( "a[data-toggle='tooltip']" ).tooltip();
        // 合并单元格
        let data = $( '#userGroupTable' ).bootstrapTable( 'getData', true );
        mergeCells( data, "userValue", 1, $( '#userGroupTable' ) );
        for (let i = 0; i < 10; i++) {
            $( "#userGroupTable" ).bootstrapTable( 'mergeCells', {
                index: i * 3,
                field: "lifecycle",
                colspan: 1,
                rowspan: 3
            } );
        }
        $( "#userGroupTable" ).bootstrapTable( 'mergeCells', {
            index: 0,
            field: "timeAndShop",
            colspan: 1,
            rowspan: total
        } );
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
 * 微信消息窗口
 */
function openWxMsgModal(groupId) {
    $( "#currentGroupId" ).val(groupId);
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
        $( "#step3" ).attr( "style", "display:none;" );
    }
}

function nextStep(stepNum) {
    if (stepNum === 2) {
        step.setActive( 1 );
        couponTable();
        $( "#step1" ).attr( "style", "display:none;" );
        $( "#step2" ).attr( "style", "display:block;" );
        $( "#step3" ).attr( "style", "display:none;" );
    }
    if(stepNum === 3) {
        $.get("/daily/resetGroupCoupon", {}, function (r) {
            if(r.code === 200) {
                $MB.n_success("根据您的当前补贴信息，系统已自动配置补贴到用户群组上！");
                step.setActive( 2);
                $( "#step1" ).attr( "style", "display:none;" );
                $( "#step2" ).attr( "style", "display:none;" );
                $( "#step3" ).attr( "style", "display:block;" );
            }else {
                $MB.n_warning(r.msg);
            }
        });
    }
}

function smsCouponListTable(tableId, data) {
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
                },
                {
                    title: '用户特征',
                }
            ]
        };
    $( "#" + tableId ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
    $( "#" + tableId ).bootstrapTable( 'load', data );
}


// 用户群组按钮点击
function userGroupButton(key, value, dom, className) {
    $(dom).removeClass("btn-secondary").addClass(className);
    $(dom).siblings('button').removeClass(className).addClass("btn-secondary");
    current_group[key] = value;
    getCurrentGroupData();
}
getCurrentGroupData();
function getCurrentGroupData() {
    $.get("/daily/getCurrentGroupData",current_group, function (r) {
        console.log(r);
        var data = r.data;
        $("#duanxinContent").html('').append(((data['duanxin'] === undefined) || (data['duanxin'] === null)) ? '未配置短信文案':data['duanxin']);
        $("#weixinContent").html('').append(((data['weixin'] === undefined) || (data['weixin'] === null)) ? '未配置企业微信文案' : data['weixin']);

        smsCouponListTable("smsCouponListTable", data['couponList']);
        smsCouponListTable("weixinCouponListTable", data['couponList']);
    });
}

// 更新微信消息到用户群组
function updateWxMsg() {
    var selected = $("#msgListDataTable").bootstrapTable('getSelections');
    if(selected.length === 0) {
        $MB.n_warning("请选择一条记录！");
        return;
    }
    var qywxId = selected[0]['qywxId'];
    $.get("/daily/updateWxMsgId", {qywxId: qywxId, groupId: $("#currentGroupId").val()}, function (r) {
        if(r.code === 200) {
            $MB.n_success("更新成功！");
            $("#wxMsgListModal").modal('hide');
            getTableData();
        }
    });
}