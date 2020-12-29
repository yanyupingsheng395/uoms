$("#btn_lable").click(function () {
    var selected = $( "#baseTable" ).bootstrapTable( 'getSelections' );
    var selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning( '请选择需要打标签的群！' );
        return;
    }
    $("#lableModal").modal('show');
    showAllTag();
});

function saveMaterial() {
    $("#lableModal").modal('hide');
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
            showParent();
        } else {
            $MB.n_danger( r.msg );
        }
    } );
});

$("#tag_group_close").click(function () {
    $( '#add_tag_group' ).modal( 'hide' );
    showParent();
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
    hideParent();
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
            //显示父类界面
            showParent();
        } else {
            $MB.n_danger( r.msg );
        }
    } );
});

/**
 * 清除新增标签数据
 */
function clearTag() {
    $("#tagflag").val("");
    $("#tagName").val("");
    $("#tagGroupid").val("");
}

function hex(x) {
    return ("0" + parseInt(x).toString(16)).slice(-2);
}

/**
 * 关闭弹框，并清除数据
 */
$("#tag_close").click(function () {
    clearTag();
    $( '#add_tag' ).modal( 'hide' );
    //显示父界面
    showParent();
});

/**
 * 新增标签组弹窗
 */
function addTagList() {
    $( '#add_tag_group' ).modal( 'show' );
    //隐藏父界面
    hideParent();
}