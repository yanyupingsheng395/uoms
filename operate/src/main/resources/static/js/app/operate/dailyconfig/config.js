let step;
let current_group = {userValue: 'ULC_01', lifeCycle: '1', pathActive: 'UAC_01', tarType: 'H'};
$( function () {
    //加载群组列表
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
                    valign: 'middle',
                    formatter: function (value, row, index) {
                        return "<a href='/page/insight' target='_blank' style='color: #48b0f7;text-decoration: underline;'>系统配置</a>";
                    }
                },
                {
                    field: 'userValue',
                    title: '用户对类目的<br/>价值/沉默成本',
                    align: 'center',
                    formatter: function (value, row, index) {
                        var res = "";
                        switch (value) {
                            case "ULC_01":
                                res = "高价值低敏感";
                                break;
                            case "ULC_02":
                                res = "高价值较敏感";
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
                    title: '用户对类目的<br/>生命周期阶段',
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
                    title: '用户下一次转化<br/>的活跃度节点',
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
                    valign: 'middle',
                    formatter: function (value, row, index) {
                        return "<a style='color: #4c4c4c' onclick='openSelectedGroupModal(\""+row['userValue']+"\", \""+row['lifecycle']+"\", \""+row['pathActive']+"\")'><i class='mdi mdi-account mdi-18px'></i></a>";
                    }
                },
                {
                    title: '短信',
                    align: 'center',
                    field: 'smsCode',
                    valign: 'middle',
                    formatter: function (value, row, index) {
                        if(value === null || value === undefined || value === '') {
                            return "<a style='color: #4c4c4c' onclick='openSmsTemplateModal(\""+row['groupId']+"\", null, \""+row['userValue']+"\", \""+row['lifecycle']+"\", \""+row['pathActive']+"\")'>" +
                                "<i class='mdi mdi-email-variant mdi-18px'></i><span style='color: #f96868'>&nbsp;[未配置]</span>" +
                                "</a>";
                        }else {
                            return "<a style='color: #4c4c4c' onclick='openSmsTemplateModal(\""+row['groupId']+"\", \""+value+"\", \""+row['userValue']+"\", \""+row['lifecycle']+"\", \""+row['pathActive']+"\")'>" +
                                "<i class='mdi mdi-email-variant mdi-18px'></i><span style='color: #52c41a'>&nbsp;[已配置]</span>" +
                                "</a>";

                        }

                    }
                },
                {
                    title: '短信最后配置时间',
                    align: 'center',
                    field: 'smsOpDt',
                    valign: 'middle'
                },
                {
                    title: '企业微信',
                    align: 'center',
                    field: 'qywxId',
                    valign: 'middle',
                    formatter: function (value, row, index) {
                        if(value === null || value === undefined || value === '') {
                            return "<a  style='color: #4c4c4c' onclick='openWxMsgModal(\""+row['groupId']+"\", null, \""+row['userValue']+"\", \""+row['lifecycle']+"\", \""+row['pathActive']+"\")'>" +
                                "<i class='mdi mdi-wechat mdi-18px'></i><span style='color:#f96868;'>&nbsp;[未配置]</span>" +
                                "</a>";
                        }else {
                            return "<a  style='color: #4c4c4c' onclick='openWxMsgModal(\""+row['groupId']+"\", \""+value+"\", \""+row['userValue']+"\", \""+row['lifecycle']+"\", \""+row['pathActive']+"\")'>" +
                                "<i class='mdi mdi-wechat mdi-18px'></i><span style='color: #52c41a'>&nbsp;[已配置]</span>" +
                                "</a>";
                        }

                    }
                },
                {
                    title: '企业微信最后配置时间',
                    align: 'center',
                    field: 'qywxOpDt',
                    valign: 'middle'
                }
                // {
                //     title: '公众号',
                //     align: 'center',
                //     valign: 'middle',
                //     formatter: function (value, row, index) {
                //         if(value === null || value === undefined || value === '') {
                //             return "<a style='color: #48b0f7' onclick='personalMsg(\""+row['groupId']+"\")'>" +
                //                 "<i class='mdi mdi-account-multiple mdi-18px'></i><span style='color: #f96868'>&nbsp;[未配置]</span>" +
                //                 "</a>";
                //         }else {
                //             return "<a style='color: #48b0f7' onclick='personalMsg(\""+row['groupId']+"\")'>" +
                //                 "<i class='mdi mdi-account-multiple mdi-18px'></i><span style='color: #52c41a'>&nbsp;[已配置]</span>" +
                //                 "</a>";
                //         }
                //
                //     }
                // }
            ]
        ]
    };
    $( "#userGroupTable" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
    $.get( "/dailyConfig/userGroupList", {}, function (r) {
        var dataList = r.data;
        var total = dataList.length;
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
        $("#userGroupTable").find("tr[data-index=0]").find("td").first().attr('style', 'text-align: center; vertical-align: top; display: table-cell;');
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
function getGroupDescription(userValue, lifecycle, pathActive, tableId) {
    let settings = {
        url: '/dailyConfig/getGroupDescription',
        pagination: false,
        singleSelect: false,
        queryParams: function() {
            return {
                userValue: userValue,
                lifecycle: lifecycle,
                pathActive: pathActive
            }
        },
        columns: [
            {
                title: '差异特征维度',
                field: 'colName'
            },
            {
                title: '用户群组特征值',
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
}

/**
 * 微信消息窗口
 */
function openWxMsgModal(groupId, qywxId, userValue, lifecycle, pathActive) {
    $( "#currentGroupId").val(groupId);
    getWxMsgTableData(qywxId);
    getGroupDescription(userValue, lifecycle, pathActive, 'selectedGroupInfo2');
    $( '#wxMsgListModal' ).modal( 'show' );
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
function openSelectedGroupModal(userValue, lifecycle, pathActive) {
    getGroupDescription(userValue, lifecycle, pathActive, 'userGroupDescTable');
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
        $.get("/dailyConfig/resetOpFlag", {}, function (r) {
            couponTable();
            $( "#step1" ).attr( "style", "display:none;" );
            $( "#step2" ).attr( "style", "display:block;" );
            $( "#step3" ).attr( "style", "display:none;" );
        });
    }
    if(stepNum === 3) {
        $.get("/dailyConfig/autoSetGroupCoupon", {}, function (r) {
            if(r.code === 200) {
                $MB.n_success("根据您的当前补贴信息，系统已自动配置补贴到用户群组上！");
                step.setActive( 2);
                $( "#step1" ).attr( "style", "display:none;" );
                $( "#step2" ).attr( "style", "display:none;" );
                $( "#step3" ).attr( "style", "display:block;" );
                getConfigInfoByGroup();
            }else if(r.code === 400){
                $MB.n_warning(r.msg);
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
                },
                {
                    title: '用户特征'
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
        couponListTable("smsCouponListTable", data['duanxinCouponInfos']);
        couponListTable("weixinCouponListTable", data['weixinCouponInfos']);
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
    $.get("/dailyConfig/updateWxMsgId", {qywxId: qywxId, groupId: $("#currentGroupId").val()}, function (r) {
        if(r.code === 200) {
            $MB.n_success("更新成功！");
            $("#wxMsgListModal").modal('hide');
            getTableData();
        }
    });
}