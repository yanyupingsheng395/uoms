var $qywxsetting = $( "#qywx-setting-form" );
$(function () {
    getQywxParam();
});

//获取企业微信应用设置中的公司ID和应用秘钥。
function getQywxParam(){
    $.get("/qywx/getQywxParam",function (r) {
        if(r.code === 200) {
            if(r.msg.msg=='Y'){
                $("#corpId").val(r.msg.corpId);
                $("#applicationSecret").val(r.msg.secret);
            }else{
                $MB.n_danger("未获取到微信的引用配置！");
            }
        }else {
            $MB.n_danger("获取数据异常！");
        }
    });
}

function updateWxSetting() {
    var corpId=$("#corpId").val().trim();
    var secret=$("#applicationSecret").val().trim();
    if(corpId==null||secret==null||corpId==''||secret==''||corpId=='null'||secret=='null'){
        $MB.n_danger("数据填写不完整！");
        return;
    }
    var data ="corpId="+corpId+"&secret="+secret;
    $.post( "/qywx/updateCorpInfo", data, function (r) {
        if (r.code === 200) {
            $MB.n_success("更新成功！");
        } else {
            $MB.n_danger( "更新失败" );
        }
    } );

}