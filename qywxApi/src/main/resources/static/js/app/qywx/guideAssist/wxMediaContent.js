let appId, timestamp, nonceStr, signature, agentId, agentSignature;

let CURR_EXTERNAL_USER_ID;
let CURRENT_USER;
$( function () {
    if(!isWeiXin())
    {
        //提示
        $.toptip('此功能仅能在企业微信APP/PC客户端内使用！',20000,'error');
        return;
    }
} );
/**
 * 选择类型
 */
$("#choosetype").select({
    title: "请选择类型",
    items: ['商品','优惠券'],
    onChange: function(d) {
        $("#choosetype").val(d.values);
    },
    onClose: function() {
    },
    onOpen: function() {
    },
});

/**
 * 通过类型和id,获取mediaid；
 */
function getMediaId(){
    var identityId=$("#identityId").val();
    var type=$("#choosetype").val();
    if(identityId==null||type==null||identityId==""||type==""){
        $.toast("<font style='font-size: 14px;'><span class='icon icon-73'></span>&nbsp;请填写信息！</font>", "text");
        return;
    }
    var identityType="PRODUCT";
    if(type=="优惠券"){
        identityType="COUPON";
    }

    $.get( "/wxMedia/getMediaId", {identityId: identityId, identityType: identityType}, function (r) {
        if (r.code === 200) {
            console.log(r);
            if(r.data!=null){
                sendImg(r.data);
            }else{
                $.toast("<font style='font-size: 14px;'><span class='icon icon-73'></span>&nbsp;未获取到图片ID！</font>", "text");
                return;
            }
        }
    } );
}


function sendImg(mediaid){
    $.get( "/jsapi/getJsapiInfo", {url: location.href.split( '#' )[0]}, function (r) {
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
                        wx.invoke('sendChatMessage', {
                            msgtype:"image", //消息类型，必填
                            image:
                                {
                                    mediaid: mediaid, //图片的素材id
                                }
                        }, function(res) {
                            if (res.err_msg == 'sendChatMessage:ok') {
                                $.toast("<font style='font-size: 14px;'><span class='icon icon-71'></span>&nbsp;发送成功！</font>", "text");
                            }
                        })
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
}

function isWeiXin(){
    let ua = window.navigator.userAgent.toLowerCase();
    if(ua.match(/MicroMessenger/i) == 'micromessenger'){
        return true;
    }else{
        return false;
    }
}