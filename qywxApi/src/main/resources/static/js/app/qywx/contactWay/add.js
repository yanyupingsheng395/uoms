var dept_list=[] ;//添加部门的集合
var user_list =[];//添加人员的集合
var tagCount=0;//标签组总数
$( function () {
    createUserTree();
    showAllTag();
} );

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
    $.get( "/qywxCustomer/getContractList?limit=100&offset=0", {}, function (r) {
        deptData = r.rows;
        var html="";
        if (r.code === 200) {
            for(var i=0;i<deptData.length;i++){
                html= html+"<option value='"+deptData[i].chatId+"'>"+deptData[i].groupName+"</option>";
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

var chatMap={};
function saveChatImg() {
    console.log(chatMap);
   var text= $("#chatList  option:selected").text();
   var val=$("#chatList  option:selected").val();
   var code=$( "input[name='image[]']" ).val();
    $MB.loadingDesc("show", "图片正在上传中，请稍候...");
    $.post( "/contactWay/uploadQrcode", {
        mediaType: 'image',
        base64Code: code
    }, function (r) {
        $MB.loadingDesc("hide");
        if (r.code === 200) {
            console.log(r.data);
            var html=" <tr data-index=\"0\">\n" +
                "                                                        <td style=\"text-align: center; \">\n" +
                "                                                            <img style=\"width:120px;height:120px\" src="+code+">\n" +
                "                                                        </td>\n" +
                "                                                        <td style=\"text-align: center; \">\n" +
                "                                                            <a href=\"http://\" target=\"_blank\">"+text+"</a>\n" +
                "                                                        </td>\n" +
                "                                                    </tr>";
            $("#addtr").append(html);
            $("#addGroupChat").modal('hide');
            chatMap[val]=r.data;
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




function hex(x) {
    return ("0" + parseInt(x).toString(16)).slice(-2);
}
var labelId = 0;

function submitDiyLabel() {
    var labelName = $("#newLabel").val();

    $("#newLabel").val("");
    var str = '<span id="' + String.fromCharCode((65 + labelId)) + '" class="diyLabelSpan labelS" style="border-color:#599bff;background-color:#599bff30">' + labelName + ' </span>';
    $("#diyLabelDiv").append(str);
    appendStr2 = '<input id="diyLabel' + String.fromCharCode((65 + labelId)) + '" type="hidden" name="labelName" class="form-control" value="' + labelName + '">';
    $("#diyLabelDiv").prepend(appendStr2);

    $(".diyLabelSpan").on("click", function () {
        var rgb = $(this).css("borderColor");
        rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
        rgb = "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]) + "30";

        if ($(this).css("backgroundColor") != null && $(this).css("backgroundColor") != "rgba(0, 0, 0, 0)") {

            $(this).css("backgroundColor", "rgba(0, 0, 0, 0)");

            var inputId = "diyLabel" + $(this).attr("id");

            $("#" + inputId).remove();

        } else {
            $(this).css("backgroundColor", rgb);
            appendStr2 = '<input id="diyLabel' + $(this).attr("id") + '" type="hidden" name="labelName" class="form-control" value="' + $(this).html() + '">';
            $("#diyLabelDiv").prepend(appendStr2);
        }
    });
    labelId++;
}
