let appId, timestamp, nonceStr, signature, agentId, agentSignature;
let CURR_EXTERNAL_USER_ID;
let OPERATE_USER_ID;
$( function () {
    if(!isWeiXin())
    {
        //提示
        $.toptip('此功能仅能在企业微信APP/PC客户端内使用！',20000,'error');
        return;
    }
    $.get( "/qwClient/getJsapiInfo", {url: location.href.split( '#' )[0]}, function (r) {
        if (r.code == 200) {
            let data = r.data;
            appId = data['corpId'];
            timestamp = data['timestamp'];
            nonceStr = data['nonceStr'];
            signature = data['signature'];
            agentId = data['agentId'];
            agentSignature = data['agentSignature'];
            wx.config( {
                beta: true,// 必须这么写，否则wx.invoke调用形式的jsapi会有问题
                debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                appId: appId, // 必填，企业微信的corpID
                timestamp: timestamp, // 必填，生成签名的时间戳
                nonceStr: nonceStr, // 必填，生成签名的随机串
                signature: signature,// 必填，签名，见 附录-JS-SDK使用权限签名算法
                jsApiList: ['getCurExternalChat', 'sendChatMessage', 'getContext'] // 必填，需要使用的JS接口列表，凡是要调用的接口都需要传进来
            } );
            wx.ready( function () {
                // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
                wx.agentConfig( {
                    corpid: appId, // 必填，企业微信的corpid，必须与当前登录的企业一致
                    agentid: agentId, // 必填，企业微信的应用id （e.g. 1000247）
                    timestamp: timestamp, // 必填，生成签名的时间戳
                    nonceStr: nonceStr, // 必填，生成签名的随机串
                    signature: agentSignature,// 必填，签名，见附录-JS-SDK使用权限签名算法
                    jsApiList: ['sendChatMessage', 'getCurExternalContact', 'getContext'], //必填
                    success: function (res) {
                        wx.invoke( 'getContext', {}, function (res) {
                            if (res.err_msg == "getContext:ok") {
                                var entry = res.entry; //返回进入H5页面的入口类型，目前有normal、contact_profile、single_chat_tools、group_chat_tools
                                if ('single_chat_tools' === entry) {
                                    getExternalUserId();
                                } else if ('group_chat_tools' === entry) {
                                    document.write( "群聊助手正在开发中..." );
                                }
                            } else {
                                //错误处理
                            }
                        } );
                    },
                    fail: function (res) {
                        document.write( JSON.stringify( res ) );
                        if (res.errMsg.indexOf( 'function not exist' ) > -1) {
                            alert( '版本过低请升级' )
                        }
                    }
                } );
            } );
        }
        else
        {
            //获取jsapi错误
            document.write( "用户授权错误..." );
        }
    } );
} );

function getExternalUserId() {
    wx.invoke( 'getCurExternalContact', {}, function (res) {
        if (res.err_msg == "getCurExternalContact:ok") {
            CURR_EXTERNAL_USER_ID = res.userId; //返回当前外部联系人userId
            $.get( "/qwClient/getExternalUserInfo", {externalUserId: CURR_EXTERNAL_USER_ID}, function (r) {
                if (r.code === 200) {
                    let operateUserId=r.data['operateUserId'];
                    if(null!=operateUserId&&operateUserId!=-999){
                        OPERATE_USER_ID=operateUserId;
                        tabChange(operateUserId);
                        recProductList(operateUserId);
                    }else{
                        $( "#allcontent" ).html( "<div class=\"weui-loadmore weui-loadmore_line\">\n" +
                            "    <span class=\"weui-loadmore__tips\">当前客户尚未匹配到商城用户！</span>\n" +
                            "</div>" );

                      //  $("#allcontent").html("<div ><i class=\"weui-icon-warn  weui-icon_msg\"></i><h2 class=\"weui-msg__title\">当前客户尚未匹配到商城用户!</h2></div>" );

                    }
                    //企业微信外部客户的名称
                    let userName = r.data['name'];
                    CURRENT_USER = userName;
                    $( ".user_name" ).html( '' ).append( '&nbsp;' + userName + '&nbsp;' );
                }
            } );
        } else {
            alert( "企业微信授权失败！详情:"+res.err_msg );
        }
    } );
}

// 给客户发送消息
function sendMsgToUser(content) {
    $.toptip('配置管理员尚未开启发送配置！',20000,'error');
    // wx.invoke( 'sendChatMessage', {
    //     msgtype: "text",
    //     text: {
    //         content: content
    //     }
    // }, function (res) {
    // } );
}


/**
 *
 * @param spuId
 * @param userId
 */
function getUserInitData(spuId) {
    $.get( "/qwClient/getUserBuyHistory", {externalUserId: CURR_EXTERNAL_USER_ID, spuId: spuId}, function (r) {
        if (r.code === 200) {
            let data = r.data;
            // 获取购买历史数据
            let userBuyHistoryList = data.userBuyHistoryList;
            buildUserBuyHistory(userBuyHistoryList);
            let spuList = data.spuList;
            buildSpuList(spuList, spuId);
            let userStats = data.userStats;
            buildUserStats(userStats);
            let userDesc = r.userDesc;
            $("#userDesc").html('').append(userDesc);
        }
    } );
}

function buildUserStats(userStats) {
    if(userStats='{}')
    {
        $("#userStats").html('').append("<tr><td>无购买统计数据!</td></tr>");
    }else
    {
        var code = "<tr><td>"+userStats['kpi11']+"</td><td>"+userStats['kpi12']+"</td><td>"+userStats['kpi13']+"</td><td>"+userStats['kpi14']+"</td></tr>";
        code += "<tr><td>"+userStats['kpi21']+"</td><td>"+userStats['kpi22']+"</td><td>"+userStats['kpi23']+"</td><td>"+userStats['kpi24']+"</td></tr>";
        code += "<tr><td>"+userStats['kpi31']+"</td><td>"+userStats['kpi32']+"</td><td>"+userStats['kpi33']+"</td><td>"+userStats['kpi34']+"</td></tr>";
        $("#userStats").html('').append(code);
    }
}

//构造类目的页面显示
function buildSpuList(spuList, spuId) {
    var code = "<ul class=\"weui-payselect-ul\" style='margin-top: 5px;'>";
    if(spuId == -1) {
        code += "<li class=\"weui-payselect-li\">\n" +
            "<a onclick=\"getUserInitData(-1)\" class=\"weui-payselect-a weui-payselect-on\">整体类目</a>\n" +
            "</li>";
    }else {
        code += "<li class=\"weui-payselect-li\">\n" +
            "<a onclick=\"getUserInitData(-1)\" class=\"weui-payselect-a\">整体类目</a>\n" +
            "</li>";
    }

    $.each(spuList, function (k, v) {
        if(spuId == v['spuId']) {
            code += "<li class=\"weui-payselect-li\">\n" +
                "<a onclick=\"getUserInitData("+v['spuId']+")\" class=\"weui-payselect-a weui-payselect-on\">"+v['spuName']+"</a>\n" +
                "</li>";
        }else {
            code += "<li class=\"weui-payselect-li\">\n" +
                "<a onclick=\"getUserInitData("+v['spuId']+")\" class=\"weui-payselect-a\">"+v['spuName']+"</a>\n" +
                "</li>";
        }
    });
    code += "</ul>";
    $("#spuList").html('').append(code);

    $(".weui-payselect-li").on('click',function(){
        $(this).children().addClass("weui-payselect-on");
        $(this).siblings().children().removeClass("weui-payselect-on");
        return false;
    });
}

//获取购买历史数据
function buildUserBuyHistory(userBuyHistoryList) {
    var htmlCode = '';
    $.each(userBuyHistoryList, function (k, v) {
        if (v['intervalDays'] === null || v['intervalDays'] === 'null') {
            if (k == 0) {
                htmlCode += "<li class=\"timeline-item\">\n" +
                    "                    <div class=\"timeline-item-color timeline-item-head-first\"><i\n" +
                    "                            class=\"timeline-item-checked   weui-icon-success-no-circle\"></i>\n" +
                    "                    </div>\n" +
                    "                    <div class=\"timeline-item-tail\" ></div>\n" +
                    "                    <div class=\"timeline-item-content\">\n" +
                    "                        <ul class=\"weui-payrec\">\n" +
                    "                            <div class=\"weui-pay-m\">\n" +
                    "                                <li class=\"weui-pay-order\">\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">时间：</dt><dd class=\"weui-pay-e\">" + v['orderDt'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">类目：</dt><dd class=\"weui-pay-e\">" + v['spuName'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">商品：</dt><dd class=\"weui-pay-e\">" + v['productName'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">金额：</dt><dd class=\"weui-pay-e\">¥" + v['orderFee'] + "</dd></dl>\n" +
                    "                                </li>\n" +
                    "                            </div>\n" +
                    "                        </ul>\n" +
                    "                    </div>\n" +
                    "                </li>";
            } else {
                htmlCode += "<li class=\"timeline-item\">\n" +
                    "                    <div class=\"timeline-item-color timeline-item-head\"></div>\n" +
                    "                    <div class=\"timeline-item-tail\" ></div>\n" +
                    "                    <div class=\"timeline-item-content\">\n" +
                    "                        <ul class=\"weui-payrec\">\n" +
                    "                            <div class=\"weui-pay-m\">\n" +
                    "                                <li class=\"weui-pay-order\">\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">时间：</dt><dd class=\"weui-pay-e\">" + v['orderDt'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">类目：</dt><dd class=\"weui-pay-e\">" + v['spuName'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">商品：</dt><dd class=\"weui-pay-e\">" + v['productName'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">金额：</dt><dd class=\"weui-pay-e\">¥" + v['orderFee'] + "</dd></dl>\n" +
                    "                                </li>\n" +
                    "                            </div>\n" +
                    "                        </ul>\n" +
                    "                    </div>\n" +
                    "                </li>";
            }
        } else {
            if (k == 0) {
                htmlCode += "<li class=\"timeline-item\">\n" +
                    "                    <div class=\"timeline-item-color timeline-item-head-first\"><i\n" +
                    "                            class=\"timeline-item-checked   weui-icon-success-no-circle\"></i>\n" +
                    "                    </div>\n" +
                    "                    <div class=\"timeline-item-tail\" ></div>\n" +
                    "                    <div class=\"timeline-item-content\">\n" +
                    "                        <ul class=\"weui-payrec\">\n" +
                    "                            <div class=\"weui-pay-m\">\n" +
                    "                                <li class=\"weui-pay-order\">\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">时间：</dt><dd class=\"weui-pay-e\">" + v['orderDt'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">类目：</dt><dd class=\"weui-pay-e\">" + v['spuName'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">商品：</dt><dd class=\"weui-pay-e\">" + v['productName'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">金额：</dt><dd class=\"weui-pay-e\">¥" + v['orderFee'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">间隔：</dt><dd class=\"weui-pay-e\">" + v['intervalDays'] + "天</dd></dl>\n" +
                    "                                </li>\n" +
                    "                            </div>\n" +
                    "                        </ul>\n" +
                    "                    </div>\n" +
                    "                </li>";
            } else {
                htmlCode += "<li class=\"timeline-item\">\n" +
                    "                    <div class=\"timeline-item-color timeline-item-head\"></div>\n" +
                    "                    <div class=\"timeline-item-tail\" ></div>\n" +
                    "                    <div class=\"timeline-item-content\">\n" +
                    "                        <ul class=\"weui-payrec\">\n" +
                    "                            <div class=\"weui-pay-m\">\n" +
                    "                                <li class=\"weui-pay-order\">\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">时间：</dt><dd class=\"weui-pay-e\">" + v['orderDt'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">类目：</dt><dd class=\"weui-pay-e\">" + v['spuName'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">商品：</dt><dd class=\"weui-pay-e\">" + v['productName'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">金额：</dt><dd class=\"weui-pay-e\">¥" + v['orderFee'] + "</dd></dl>\n" +
                    "                                    <dl class=\"weui-pay-line\"><dt class=\"weui-pay-label\">间隔：</dt><dd class=\"weui-pay-e\">" + v['intervalDays'] + "天</dd></dl>\n" +
                    "                                </li>\n" +
                    "                            </div>\n" +
                    "                        </ul>\n" +
                    "                    </div>\n" +
                    "                </li>";
            }
        }
    } );
    $( "#buyHistory" ).html( htmlCode );
}

function tabChange(operateUserId) {
    $( '.weui-tab' ).tab( {
        defaultIndex: 0,
        activeClass: 'weui-bar__item_on',
        onToggle: function (index) {
            if (index == 0) {
                $( "#tab1" ).attr( "style", "display:block;" );
                $( "#tab2" ).attr( "style", "display:none;" );
            } else if (index == 1) {
                if (operateUserId === '') {
                    $( "#buyHistory" ).html( "<h1 class=\"weui-pay-title\">当前用户暂无购买记录！</h1>" );
                    $( "#tab1" ).attr( "style", "display:none;" );
                    $( "#tab2" ).attr( "style", "display:block;" );
                } else {
                    getUserInitData(-1);
                    $( "#tab1" ).attr( "style", "display:none;" );
                    $( "#tab2" ).attr( "style", "display:block;" );
                }
            } else {
                $( "#tab1" ).attr( "style", "display:none;" );
                $( "#tab2" ).attr( "style", "display:none;" );
            }
        }
    } );
}

// 选中某个商品ID动态获取更多的详细信息
function getUserGuideInfo(operateUserId, productId) {
    $.get( "/qwClient/getUserGuideInfo", {operateUserId: operateUserId, productId: productId}, function (r) {
        if (r.code === 200) {
            guidePurchTimes( r.userTodayStatus );
            guidePurchWeek( r.userTimeList );
            guideCoupon( r.couponList );
        }
    } );
}

/**
 * 引导用户购买商品的表
 * @param userId
 */
function recProductList(operateUserId) {
    if (operateUserId === '') {
        let settings = {
            columns: [
                {
                    field: 'ebp_product_name',
                    title: '推荐商品'
                }, {
                    field: 'price',
                    title: '价格（元）'
                }, {
                    field: 'purch_probability',
                    title: '购买概率（%）',
                    formatter: function (value, row, index) {
                        if (value !== '' && value !== null) {
                            return Math.round( value * 100 * 100 ) / 100;
                        }
                    }
                }
                // , {
                //     field: 'ebp_product_url',
                //     title: '操作',
                //     formatter: function (value, row, index) {
                //         return "<a onclick='sendMsgToUser(\"" + value + "\")' style='text-decoration: underline;'>发送</a>";
                //     }
                // }
            ]
        };
        $( "#recProductTable" ).bootstrapTable( settings ).bootstrapTable( 'load', [] );
        $( ".product_name1" ).html( '' ).append( '&nbsp;--&nbsp;' );
        guidePurchTimes( [] );
        guidePurchWeek( [] );
        guideCoupon( [] );
    } else {
        let settings = {
            url: "/qwClient/getRecProductList?operateUserId=" + operateUserId,
            pagination: false,
            columns: [
                {
                    field: 'ebp_product_name',
                    title: '推荐商品'
                }, {
                    field: 'price',
                    title: '价格（元）'
                }, {
                    field: 'purch_probability',
                    title: '购买概率（%）',
                    formatter: function (value, row, index) {
                        if (value !== '' && value !== null) {
                            return Math.round( value * 100 * 100 ) / 100;
                        }
                    }
                }
                // , {
                //     field: 'ebp_product_url',
                //     title: '操作',
                //     formatter: function (value, row, index) {
                //         return "<a onclick='sendMsgToUser(\"" + value + "\")' style='text-decoration: underline;'>发送</a>";
                //     }
                // }
            ],
            onClickRow: function (row, $element) {
                //判断是否已选中
                if ($( $element ).hasClass( "changeColor" )) {
                    //已选中则移除 当前行的class='changeColor'
                    // $($element).removeClass('changeColor');
                } else {
                    //未点击则，为当前行添加 class='changeColor'
                    $( $element ).addClass( 'changeColor' );
                    $( $element ).siblings().removeClass( 'changeColor' );
                }
                getUserGuideInfo( operateUserId, row['ebp_product_id'] );
                $( ".product_name1" ).html( '' ).append( '&nbsp;' + row['ebp_product_name'] + '&nbsp;' );
            },
            onLoadSuccess: function (data) {
                getUserGuideInfo( operateUserId, data.data[0]['ebp_product_id'] );
                $( ".product_name1" ).html( '' ).append( '&nbsp;' + data.data[0]['ebp_product_name'] + '&nbsp;' );
            }
        };
        $( "#recProductTable" ).bootstrapTable( settings );
    }
}

/**
 * 引导用户购买时机的表
 * @param data
 */
function guidePurchTimes(data) {
    if (data === undefined) {
        data = [];
    }
    let settings = {
        rowStyle: rowStyle,
        columns: [
            {
                field: 'rn',
                title: '购买商品次数（次）',
                visible: false
            }, {
                field: 'active_type',
                title: '用户今日的活跃状态'
            }, {
                title: '状态对引导购买的意义',
                formatter: function () {
                    return "最佳时间段";
                }
            }, {
                field: 'growth_dt',
                title: '状态截止时间'
            }, {
                field: 'next_activity_type',
                title: '下一个状态'
            }
        ],
        onLoadSuccess: function (data) {
            var rn = data.data[0]['rn'];
            if(rn === '' || rn === null || rn === undefined) {
                $(".rn1").html('-');
                $(".rn2").html('-');
            }else {
                $(".rn1").html(rn);
                $(".rn2").html(rn + 1);
            }
        }
    };
    $( "#guidePurchTimesTable" ).bootstrapTable( settings ).bootstrapTable( 'load', data );
    $( "#guidePurchTimesTable" ).find( "tr td:eq(0)" ).attr( "style", "background-color:#fff" );
    mergeCells( data, 'rn', 1, $( '#guidePurchTimesTable' ) );
}

function rowStyle(row, index) {
    if (row['now_flag']) {
        return {css: {'background-color': '#FFEFD5'}};
    }
    return {};
}

function mergeCells(data, fieldName, colspan, target) {
    //声明一个map计算相同属性值在data对象出现的次数和
    var sortMap = {};
    for (var i = 0; i < data.length; i++) {
        for (var prop in data[i]) {
            if (prop == fieldName) {
                var key = data[i][prop]     //fieldName的value
                if (sortMap.hasOwnProperty( key )) {
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
        $( target ).bootstrapTable( 'mergeCells', {
            index: index,
            field: fieldName,
            colspan: colspan,
            rowspan: count
        } );
        index += count;
    }
}

/**
 * 引导用户购买的时间点
 * @param data
 */
function guidePurchWeek(data) {
    if (data === undefined) {
        data = [];
    }
    let settings = {
        columns: [
            {
                field: 'week_day',
                title: '工作日',
                formatter: function (value, row, index) {
                    return "星期" + value;
                }
            }, {
                field: 'time_slot',
                title: '时间段',
                formatter: function (value, row, index) {
                    return value + ":00";
                }
            }
        ]
    };
    $( "#guidePurchWeek" ).bootstrapTable( settings ).bootstrapTable( 'load', data );
}

/**
 * 推荐的优惠券
 * @param data
 */
function guideCoupon(data) {
    if (data === undefined) {
        data = [];
    }
    let settings = {
        columns: [
            {
                field: 'ebp_product_name',
                title: '引导购买商品'
            }, {
                field: 'coupon_min',
                title: '门槛（元）'
            }, {
                field: 'coupon_deno',
                title: '面额（元）'
            }, {
                field: 'coupon_valid_end',
                title: '有效截止日期'
            }, {
                field: 'coupon_url',
                title: '操作',
                formatter: function (value, row, index) {
                    return "<a onclick='sendMsgToUser(\"" + value + "\")' style='text-decoration: underline;'>发送</a>";
                }
            }
        ]
    };
    $( "#guideCouponTable" ).bootstrapTable( settings ).bootstrapTable( 'load', data );
}

function isWeiXin(){
    var ua = window.navigator.userAgent.toLowerCase();
    if(ua.match(/MicroMessenger/i) == 'micromessenger'){
        return true;
    }else{
        return false;
    }
}