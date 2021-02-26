var dept_list=[] ;//添加部门的集合
var user_list =[];//添加人员的集合
var tagCount=0;//标签组总数
var userData;
var deptData;
var $contactWayForm = $( "#contactWay_edit" );
var userSelect = $contactWayForm.find( "select[name='userSelect']" );
var $usersList = $contactWayForm.find( "input[name='usersList']" );
var chatMap=[];//选择群聊对象集合
var checkChatID=[];//校验是否重复选择群聊
var tagIds=[];//选择标签集合
var $qywxManualForm_validator;
$( function () {
    createUserTree();
    showAllTag();
    if(contactWayId!=null&&contactWayId!=""){
        $("#creatPreheatEdit").show();
        $("#creatPreheat").hide();
        editContact(contactWayId);
        editChat(contactWayId);
    }
});

/**
 * 修改渠道活码，将选中的标签展示
 */
function rebulidTag() {
    for (var i=0;i<tagIds.length;i++){
        addCss( document.getElementById(tagIds[i]));
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
            html+="    <label class=\"col-md-2 control-label\" style=\"text-align:left;text-align: right\">"+groupName+":</label>\n";
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
        rebulidTag();
    } );
}

/**
 * 选择一个标签，给这个标签增加样式
 * @param dom
 */
function addCss(dom) {
    var rgb = $(dom).css("borderColor");
    rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
    rgb = "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]) + "30";
    if ($(dom).css("backgroundColor") != null && $(dom).css("backgroundColor") != "rgba(0, 0, 0, 0)") {
        $(dom).css("backgroundColor", "rgba(0, 0, 0, 0)");
        var inputId = "label" + $(dom).attr("id");
        $("#" + inputId).remove();
    } else {
        $(dom).css("backgroundColor", rgb);
    }
}

/**
 * 改变选中标签的透明度
 * @param x
 * @returns {string}
 */
function hex(x) {
    return ("0" + parseInt(x).toString(16)).slice(-2);
}

/**
 * 选择标签，增加样式
 * @param dom
 */
function chooseThis(dom) {
    var id=$(dom).attr("id");
    if ($(dom).css("backgroundColor") != null && $(dom).css("backgroundColor") != "rgba(0, 0, 0, 0)") {
        for (var i = 0; i < tagIds.length; i++) {
            if (tagIds[i] == id) {
                tagIds.splice(i, 1);
            }
        }
    } else {
        tagIds.push(id);
    }
    addCss(dom);
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
        ele: '#cupload-create_er',
        num: 1
    } );
}

/**
 * 选择群聊中的上传图片
 */
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
            $("#cupload-create_er").html("");
            image();
        }
    } );


}

/**
 * 移除选择的成员或部门
 * @param dom
 * @param id
 * @param selid
 */
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
 * 将选择的部门，成员标签数据格式话。
 */
function getDeptAndUserId() {
    $( "input[name='usersList']" ).val( user_list.join( "," ) );
    $( "input[name='deptList']" ).val( dept_list.join( "," ) );
    $( "input[name='validUser']" ).val( user_list.join( "," ) + dept_list.join( "," ));
    $( "input[name='watChatList']" ).val(JSON.stringify(chatMap));
    $( "input[name='tagIds']" ).val(tagIds.toString());
}

/**
 * 新增渠道活码
 */
function addContactWay() {
    getDeptAndUserId();
    validQywxContact();
    var validator = $contactWayForm.validate();
    var flag = false;
    if(validator.form()==true){
        flag=true;
    }
    if(Single){
        if(user_list.length>1){
            $MB.n_danger( "该渠道活码类型是单人，不能添加多人！");
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

/**
 * 是否关联群聊，显示欢迎语、小程序等下面的内容
 * @param flag
 */
function relatedVal(flag) {
    if(flag=="Y"){
        $("#groupCode").show();
    }else{
        $("#groupCode").hide();
    }
}

/**
 * 表单验证规则
 */
function validQywxContact() {
    var icon = "<i class='fa fa-close'></i> ";
    var msgType = $('input[name="msgType"]:checked').val();
    var relateChat=$('input[name="relateChat"]:checked').val();
    //relateChat(是否关联群聊),如果等于Y，那么就需要验证渠道，成员部门，小程序，图片，网址，如果等于N就只需要验证渠道。成员部门是否录入
    if(relateChat=='Y'){
        if(msgType=="applets"){
            validator = $contactWayForm.validate( {
                rules: {
                    mpTitle: {
                        required: true
                    },
                    mpUrl: {
                        required: true
                    },
                    mediaId: {
                        required: true
                    },
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
                    mpTitle: {
                        required: icon + "请输入小程序标题"
                    },
                    mpUrl: {
                        required: icon + "请输入小程序连接"
                    },
                    mediaId: {
                        required: icon + "请输入小程序封面ID"
                    },
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
        }else if(msgType=="image"){
            validator = $contactWayForm.validate( {
                rules: {
                    picUrl: {
                        required: true
                    },
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
                    picUrl: {
                        required: icon + "请选择图片地址！"
                    },
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
        }else if(msgType=="web"){
            validator = $contactWayForm.validate( {
                rules: {
                    linkTitle: {
                        required: true
                    },
                    linkUrl: {
                        required: true
                    },
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
                    linkTitle: {
                        required: icon + "请填写网页标题！"
                    }, linkUrl: {
                        required: icon + "请填写网页地址！"
                    },
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
    }else{
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
}

/**
 * 选择小程序，图片，网页后，清除其他两项中填充的数据
 * @param type
 */
function selectType(type) {
    if(type=="image"){
        $("#image").show();
        $("#web").hide();
        $("#applets").hide();

        $("#mpTitle").val("");
        $("#mpUrl").val("");
        $("#mediaId").val("");
        $("#linkTitle").val("");
        $("#linkDesc").val("");
        $("#linkUrl").val("");
        $("#linkPicurl").val("");
    }else if(type=="web"){
        $("#image").hide();
        $("#web").show();
        $("#applets").hide();
        $("#mpTitle").val("");
        $("#mpUrl").val("");
        $("#mediaId").val("");
        $("#picUrl").val("");
    }else if(type=="applets"){
        $("#image").hide();
        $("#web").hide();
        $("#applets").show();
        $("#picUrl").val("");
        $("#linkTitle").val("");
        $("#linkDesc").val("");
        $("#linkUrl").val("");
        $("#linkPicurl").val("");
    }
}




