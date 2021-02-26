var Single=false;//在更新时，判断当前活码是不是单人的。如果是单人的，就不能转成多人。

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
            $form.find( "input[name='picUrl']" ).val( d.picUrl );
            $form.find( "input[name='linkTitle']" ).val( d.linkTitle );
            $form.find( "input[name='linkDesc']" ).val( d.linkDesc );
            $form.find( "input[name='linkUrl']" ).val( d.linkUrl );
            $form.find( "input[name='linkPicurl']" ).val( d.linkPicurl );
            $form.find( "input[name='mpTitle']" ).val( d.mpTitle );
            $form.find( "input[name='mpUrl']" ).val( d.mpUrl );
            $form.find( "input[name='mediaId']" ).val( d.mediaId );
            $form.find( "textarea[name='chatText']" ).val( d.chatText );
            $(":radio[name='msgType'][value='" + d.msgType + "']").prop("checked", "checked");
            //选择消息类型
            selectType(d.msgType)
            $(":radio[name='relateChat'][value='" + d.relateChat + "']").prop("checked", "checked");
            var tagid=d.tagIds;
            if(d.relateChat =='N'){
                $("#groupCode").hide();
            }
            if(d.contactType==1){
                Single=true;
            }
            rebuildData(d.usersList,d.party);
            if(tagid!=null&&tagid!=""){
                tagIds=tagid.split(",");
            }
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
                wayChat.chatQrimgUrl=d[i].chatQrimgUrl;
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

/**
 * 更新渠道活码
 */
function upContactWay() {
    getDeptAndUserId();
    validQywxContact();
    var validator = $contactWayForm.validate();
    var flag = validator.form();
    if(Single){
        if(user_list.length>1||dept_list.length>0){
            $MB.n_danger( "该渠道活码类型是单人，不能添加多人或部门！");
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
 * 点击编辑按钮，将挑选的职员部门数据展示
 * @param usersList
 * @param deptLis
 */
function rebuildData(usersList, deptLis) {
    //此处为了防止，在点击更新时，userDate和deptData从后端请求数据还没请求过来。
    if(userData==null||userData==""||deptData==null||deptData==""){
        $.post( "/contactWay/getDept", {}, function (r) {
            deptData = r.data;
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
        } );
        $.post( "/contactWay/getUser", {}, function (r) {
            userData = r.data;
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
        } );
    }else{
        assemblyData(usersList, deptLis,userData,deptData);
    }
}

/**
 * 将选择的成员或部门添加到样式中
 * @param usersList
 * @param deptLis
 * @param userData
 * @param deptData
 */
function assemblyData(usersList, deptLis,userData,deptData) {
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