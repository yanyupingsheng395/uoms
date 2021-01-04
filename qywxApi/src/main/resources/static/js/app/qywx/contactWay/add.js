var dept_list=[] ;//添加部门的集合
var user_list =[];//添加人员的集合
var tagCount=0;//标签组总数
var userData;
var deptData;
var Single=false;//在更新时，判断当前活吗是不是单人的。如果是单人的，就不能转成多人。
var $contactWayForm = $( "#contactWay_edit" );
var userSelect = $contactWayForm.find( "select[name='userSelect']" );
var $usersList = $contactWayForm.find( "input[name='usersList']" );
var chatMap=[];//选择群聊对象集合
var checkChatID=[];//校验是否重复选择群聊
$( function () {
    createUserTree();
    showAllTag();
    validateRule();
    if(contactWayId!=null&&contactWayId!=""){
        $("#creatPreheatEdit").show();
        $("#creatPreheat").hide();
        editContact(contactWayId);
        editChat(contactWayId);
    }
} );

/**
 * 点击更新按钮，填充页面数据
 */
function editContact(contactWayId) {
    //获取数据 并进行填充
    $.post( "/contactWay/getContactWayById", {"contactWayId": contactWayId}, function (r) {
        if (r.code === 200) {
            var $form = $( '#contactWay_edit' );
            var d = r.data;
            $( "#myLargeModalLabel" ).html( '修改渠道活码' );
            $form.find( "input[name='contactWayId']" ).val( d.contactWayId );
            $form.find( "input[name='configId']" ).val( d.configId );
            $form.find( "input[name='state']" ).val( d.state ).attr( "readOnly", "readOnly" );
            $form.find( "input[name='usersList']" ).val(d.usersList);
            $form.find( "input[name='deptList']" ).val(d.party);
            $form.find( "input[name='shortUrl']" ).val( d.shortUrl );
            $form.find( "input[name='contactName']" ).val( d.contactName );
            $form.find( "textarea[name='chatText']" ).val( d.chatText );
            $(":radio[name='relateChat'][value='" + d.relateChat + "']").prop("checked", "checked");
            if(d.relateChat =='N'){
                $("#groupCode").hide();
            }
            if(d.contactType==1){
                Single=true;
            }
            rebuildData(d.usersList,d.party);
        } else {
            $MB.n_danger( r['msg'] );
        }
    } );
}

/**
 * 填充群聊
 * @param contactWayId
 */
function editChat(contactWayId) {
    $.post( "/contactWay/getContactChatById", {"contactWayId": contactWayId}, function (r) {
        if(r.code==200){
            var d=r.data;
            var html="";
            for (var i=0;i<d.length;i++){
                var chatID=d[i].chatId;
                var chatName=d[i].chatName;
                var chatQrimgUrl=host+"/contactWay/getImage?filePath="+d[i].chatQrimgUrl;
                var wayChat = new Object();
                wayChat.chatId=chatID;
                wayChat.chatName=chatName;
                wayChat.chatQrimgUrl=chatQrimgUrl;
                chatMap.push(wayChat);
                //校验是否重复选择群聊
                checkChatID.push(chatID);

                 html+=" <tr data-index=\"0\">\n" +
                    "                                                        <td style=\"text-align: center; \">\n" +
                    "                                                            <a href=\"http://\" target=\"_blank\">"+chatName+"</a>\n" +
                    "                                                        </td>\n" +
                    "                                                        <td style=\"text-align: center; \">\n" +
                    "                                                            <img style=\"width:120px;height:120px\" src="+chatQrimgUrl+">\n" +
                    "                                                        </td>\n" +
                    "                                                    </tr>";
            }
            $("#addtr").append(html);
        }
    })
}


function upContactWay() {
    getDeptAndUserId();
    var validator = $contactWayForm.validate();
    var flag = validator.form();
    if(Single){
        if(user_list.length>1||dept_list.length>0){
            $MB.n_danger( "该渠道活吗初始类型是单人，不能添加多人或部门！");
            return;
        }
    }
    if (flag) {
        //打开遮罩层
        $MB.loadingDesc('show', '保存中，请稍候...');
        $.post("/contactWay/update",$("#contactWay_edit").serialize(), function (r) {
            if (r.code === 200) {
                $MB.closeAndRestModal( "add_modal" );
                $MB.n_success(r.msg);
                $MB.refreshTable("contactWayTable");
                clearData();
                window.location.href="/page/contactWay";
            } else {
                $MB.n_danger(r.msg);
            };
            $MB.loadingDesc('hide');
        });
    }
}



/**
 * 添加渠道和职员选项
 * @param selid
 */
function addRegion(selid) {
    var id = $( "#" + selid ).find( "option:selected" ).val();
    var name = $( "#" + selid ).find( "option:selected" ).text();
    if(id!=""&&id!=null){
        if(selid=="region1"){
            id=parseInt(id);
            if(dept_list.indexOf(id)==-1){
                dept_list.push(id);
                $( "#alllist" ).append( "<span class=\"tag\"><span>" + name + "&nbsp;&nbsp;</span><a style=\"color: #fff;cursor: pointer;\" onclick=\"regionRemove(this, \'" + id + "\', \'" + selid + "\')\">x</a></span>" );
            }
        }else if(selid=="region2"){
            if(user_list.indexOf(id)==-1){
                user_list.push(id);
                $( "#alllist" ).append( "<span class=\"tag\"><span>" + name + "&nbsp;&nbsp;</span><a style=\"color: #fff;cursor: pointer;\" onclick=\"regionRemove(this, \'" + id + "\', \'" + selid + "\')\">x</a></span>" );
            }
        }
    }
}

/**
 * 选择职员和公司列表
 */
function createUserTree() {
    $.post( "/contactWay/getDept", {}, function (r) {
        deptData = r.data;
        var html="";
        if (r.code === 200) {
            for(var i=0;i<deptData.length;i++){
                html= html+"<option value='"+deptData[i].id+"'>"+deptData[i].name+"</option>";
            }
            $("#region1").html(html);
        } else {
            $MB.n_warning( r.msg );
        }
    } );

    $.post( "/contactWay/getUser", {}, function (r) {
        userData = r.data;
        var html="";
        if (r.code === 200) {
            for(var i=0;i<userData.length;i++){
                html= html+"<option value='"+userData[i].user_id+"'>"+userData[i].name+"</option>";
            }
            $("#region2").html(html);
        } else {
            $MB.n_warning( r.msg );
        }
    } );
    $( '#add_modal' ).modal( 'show' );
}

/**
 * 显示所有标签
 */
function showAllTag() {
    $.post( "/qywxtag/getTagList", {}, function (r) {
           var data=r.data;
        tagCount=data.length;
        var html="";
        for(var i=0;i<data.length;i++){
            var groupId=data[i].groupId;
            var groupName=data[i].groupName;
            var tagList=data[i].tagList;
            html+= " <div class=\"col-sm-12\">\n";
            html+="    <label class=\"col-md-2 control-label\" style=\"text-align:left\">"+groupName+":</label>\n";
            html+= "    <div id=\"SelectLabelDiv"+i+"\" class=\"col-md-10\">" ;
                for(var j=0;j<tagList.length;j++){
                    var tagId=tagList[j].tagId;
                    var tagName=tagList[j].tagName;
                    html+= "<span id="+tagId+" onclick=\"chooseThis(this)\" class=\"labelSpan labelS\" style=\"border-color:#70f3ff\">"+tagName+" </span>";
                }
            html+= "<span class=\"labelSpan labelS\" onclick=\"addTag("+i+",'"+groupId+"')\"><i class=\"glyphicon glyphicon-edit\"></i>新增标签 </span></div>\n" ;
            html+= "   </div>";
        }
        $("#allTag").html(html);
    } );
}

/**
 * 选择标签，增加样式
 * @param dom
 */
function chooseThis(dom) {
    var rgb = $(dom).css("borderColor");
    rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
    rgb = "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]) + "30";
    if ($(dom).css("backgroundColor") != null && $(dom).css("backgroundColor") != "rgba(0, 0, 0, 0)") {
        $(dom).css("backgroundColor", "rgba(0, 0, 0, 0)");
        var inputId = "label" + $(dom).attr("id");
        $("#" + inputId).remove();
    } else {
        $(dom).css("backgroundColor", rgb);
        appendStr2 = '<input id="label' + $(dom).attr("id") + '" name="labelID" type="hidden" class="form-control" value="' + $(dom).attr("id") + '">';
        $("#SelectLabelDiv").prepend(appendStr2);
    }
}

/**
 * 新增标签组弹窗
 */
function addTagList() {
    $( '#add_tag_group' ).modal( 'show' );
}

/**
 * 新增标签组
 */
$("#tag_group_save").click(function () {
    var groupName=$("#groupName").val();
    var group_tag_name=$("#group_tag_name").val();
    $.get( "/qywxtag/addTagGroup", {groupName:groupName,groupTagName:group_tag_name}, function (r) {
        if (r.code === 200) {
            //给出提示
            $MB.n_success( "新建标签组成功!" );
            showAllTag();
            $( '#add_tag_group' ).modal( 'hide' );
            clearTagGroup();
        } else {
            $MB.n_danger( r.msg );
        }
    } );
});

/**
 * 清除新增标签组内容
 */
function clearTagGroup() {
    $("#groupName").val("");
    $("#group_tag_name").val("");
}

/**
 * 添加标签
 * @param flag
 */
function addTag(flag,groupid) {
    $( "#myLargeModalLabel3" ).html( '添加标签' );
    $( '#add_tag' ).modal( 'show' );
    $("#tagflag").val(flag);
    $("#tagGroupid").val(groupid);

}

/**
 * 新增标签
 */
$("#tag_save").click(function () {
    var flag= $("#tagflag").val();
    var tagName=$("#tagName").val();
    var groupid=$("#tagGroupid").val();
    if(tagName==null||tagName==""){
        $MB.n_warning( "请填写标签名称！" );
        return;
    }
    $.get( "/qywxtag/addTag", {tagName:tagName,groupid:groupid}, function (r) {
        if (r.code === 200) {
            $MB.n_success( "新建标签成功!" );
            showAllTag();
            $( '#add_tag' ).modal( 'hide' );
            clearTag();
        } else {
            $MB.n_danger( r.msg );
        }
    } );
});
/**
 * 关闭弹框，并清除数据
 */
$("#tag_close").click(function () {
    clearTag();
    $( '#add_tag' ).modal( 'hide' );
});

/**
 * 清除新增标签数据
 */
function clearTag() {
    $("#tagflag").val("");
    $("#tagName").val("");
    $("#tagGroupid").val("");
}

/**
 * 弹出群聊列表界面，并查询群聊
 */
function addGroupChat() {
    var params={param: {owner:'', status: ''}};
    $.get( "/qywxCustomer/getContractList?limit=100&offset=0", params, function (r) {
        deptData = r.rows;
        var html="";
        if (r.code === 200) {
            for(var i=0;i<deptData.length;i++){
                if(deptData[i].groupName==""||deptData[i].groupName==null){
                    html= html+"<option value='"+deptData[i].chatId+"'>群聊</option>";
                }else{
                    html= html+"<option value='"+deptData[i].chatId+"'>"+deptData[i].groupName+"</option>";
                }
            }
            $("#chatList").html(html);
        } else {
            $MB.n_warning( r.msg );
        }
    } );
    $("#addGroupChat").modal('show');
}

/**
 * 初始化图片上传
 */
var upload;
image();
function image() {
    upload = new Cupload( {
        ele: '#cupload-create',
        num: 1
    } );
}

function saveChatImg() {
   var text= $("#chatList  option:selected").text();
   var val=$("#chatList  option:selected").val();
   var code=$( "input[name='image[]']" ).val();
   if(checkChatID.indexOf(val)>-1){
       $MB.n_warning( "请勿重复选择群聊！" );
       return;
   }
    $MB.loadingDesc("show", "图片正在上传中，请稍候...");
    $.post( "/contactWay/uploadQrcode", {
        mediaType: 'image',
        base64Code: code
    }, function (r) {
        $MB.loadingDesc("hide");
        if (r.code === 200) {
            var imgurl=host+"/contactWay/getImage?filePath="+r.data;
            var html=" <tr data-index=\"0\">\n" +
                "                                                        <td style=\"text-align: center; \">\n" +
                "                                                            <a href=\"http://\" target=\"_blank\">"+text+"</a>\n" +
                "                                                        </td>\n" +
                "                                                        <td style=\"text-align: center; \">\n" +
                "                                                            <img style=\"width:120px;height:120px\" src="+imgurl+">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>";
            $("#addtr").append(html);
            $("#addGroupChat").modal('hide');
            var wayChat = new Object();
            wayChat.chatId=val;
            wayChat.chatName=text;
            wayChat.chatQrimgUrl=r.data;
            chatMap.push(wayChat);
            //校验是否重复选择群聊
            checkChatID.push(val);
            //清除所选图片
            $("#cupload-create").html("");
            image();
        }
    } );


}

// 移除region数据
function regionRemove(dom, id, selid) {
    $( dom ).parent().remove();
    if(selid=="region1"){
        id=parseInt(id);
        dept_list.splice( dept_list.indexOf( id ), 1 );
    }else if(selid=="region2"){
        user_list.splice( user_list.indexOf( id ), 1 );
    }
}

/**
 * 点击编辑按钮，将挑选的职员部门数据展示
 * @param usersList
 * @param deptLis
 */
function rebuildData(usersList, deptLis) {
    if(usersList!=""&&usersList!=null){
        usersList=usersList.split(',');
        var selid2="region2";
        for(var i=0;i<userData.length;i++){
            for (var j=0;j<usersList.length;j++){
                if(userData[i].user_id==usersList[j]){
                    user_list.push(userData[i].user_id);
                    $( "#alllist" ).append( "<span class=\"tag\"><span>" + userData[i].name + "&nbsp;&nbsp;</span><a style=\"color: #fff;cursor: pointer;\" onclick=\"regionRemove(this, \'" + userData[i].user_id + "\', \'" + selid2 + "\')\">x</a></span>" );
                }
            }
        }
    }
    if(deptLis!=""&&deptLis!=null){
        deptLis=deptLis.split(',');
        var selid="region1";
        for(var i=0;i<deptData.length;i++){
            for (var j=0;j<deptLis.length;j++){
                if(deptData[i].id==deptLis[j]){
                    dept_list.push(deptData[i].id);
                    $( "#alllist" ).append( "<span class=\"tag\"><span>" + deptData[i].name + "&nbsp;&nbsp;</span><a style=\"color: #fff;cursor: pointer;\" onclick=\"regionRemove(this, \'" + deptData[i].id + "\', \'" + selid + "\')\">x</a></span>" );
                }
            }
        }

    }
}

function getDeptAndUserId() {
    $( "input[name='usersList']" ).val( user_list.join( "," ) );
    $( "input[name='deptList']" ).val( dept_list.join( "," ) );
    $( "input[name='validUser']" ).val( user_list.join( "," ) + dept_list.join( "," ));
    $( "input[name='watChatList']" ).val(JSON.stringify(chatMap));
}

/**
 * 新增渠道活吗
 */
function addContactWay() {
    getDeptAndUserId();
    var validator = $contactWayForm.validate();
    var flag = validator.form();
    if(Single){
        if(user_list.length>1){
            $MB.n_danger( "该渠道活吗初始类型是单人，不能添加多人！");
            return;
        }
    }
    if (flag) {
        //打开遮罩层
        $MB.loadingDesc('show', '保存中，请稍候...');
            $.post("/contactWay/save",$("#contactWay_edit").serialize(), function (r) {
                if (r.code === 200) {
                    $MB.closeAndRestModal( "add_modal" );
                    $MB.n_success(r.msg);
                    $MB.refreshTable("contactWayTable");
                    clearData();
                    window.location.href="/page/contactWay";
                } else {
                    $MB.n_danger(r.msg);
                };
                $MB.loadingDesc('hide');
            });
    }
}

/**
 * 清空数据
 */
function clearData(){
    userData=null;
    deptData=null;
    Single=false;
    $("#alllist").html("");
    $( "#btn_save" ).attr( "name", "save" );
    dept_list=[];
    user_list=[];
    $contactWayForm.validate().resetForm();
    $contactWayForm.find( "input[name='contactWayId']" ).val( "" );
    $contactWayForm.find( "input[name='configId']" ).val( "" );
    $contactWayForm.find( "input[name='state']" ).val( "" );
    $contactWayForm.find( "input[name='usersList']" ).val( "" );
    $contactWayForm.find( "input[name='deptList']" ).val( "" );
    $contactWayForm.find( "input[name='validUser']" ).val( "" );
}

function relatedVal(flag) {
    if(flag=="Y"){
        $("#groupCode").show();
    }else{
        $("#groupCode").hide();
    }
}

// 表单验证规则
function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $contactWayForm.validate( {
        rules: {
            state: {
                required: true,
                maxlength: 30
            },
            validUser: {
                required: true
            },
            contactName: {
                required: true
            }
        },
        errorPlacement: function (error, element) {
            if (element.is( ":checkbox" ) || element.is( ":radio" )) {
                error.appendTo( element.parent().parent() );
            } else {
                error.insertAfter( element );
            }
        },
        messages: {
            state: {
                required: icon + "请输入渠道",
                maxlength: icon + "最大长度不能超过30个字符"
            },
            validUser: {
                required: icon + "请选择可联系成员"
            },
            contactName: {
                required: icon + "请输入名称"
            }
        }
    } );
}




function hex(x) {
    return ("0" + parseInt(x).toString(16)).slice(-2);
}

