init_date('endDate', 'yyyy-mm-dd', 0, 2, 0);
var $qywxManualForm = $( "#welcomeFormData" );
var $qywxManualForm_validator;

// 获取图片表单数据
function getTabFormData() {
    var tabFormData = '';
    var id = $("#myTabs").find("li.active a").attr('id');
    if(id === 'text-tab') {

    }else if(id === 'image-tab') {
        var selected = $("#imageTable1").bootstrapTable('getSelections');
        if(selected.length > 0) {
            tabFormData = "&picId=" + selected[0]['imgId'] + "&picUrl=" + selected[0]['imgUrl'];
        }
    }else if(id === 'web-tab') {
        var selected = $("#imageTable2").bootstrapTable('getSelections');
        if(selected.length > 0) {
            tabFormData = "&linkPicurl=" + selected[0]['imgUrl'];
        }
    }else if(id === 'miniprogram-tab') {

    }
    return tabFormData;
}

function saveDataWel(dom) {
    var operate = $(dom).attr('name');
    var validator = $qywxManualForm.validate();
    if(operate === 'save') {
        if(validator.form()) {
            $MB.confirm({
                title: "<i class='mdi mdi-alert-outline'></i>提示：",
                content: "确定保存当前记录?"
            }, function () {
                $.post("/welcome/saveData", $("#welcomeFormData").serialize() + getTabFormData(), function (r) {
                    if(r.code == 200) {
                        $MB.n_success("保存成功！");
                        $("#qywxWelcomeId").val(r.data);
                    }else {
                        $MB.n_danger("保存失败！");
                    }
                });
            });
        }
    }

    if(operate === 'update') {
        if(validator.form()) {
            $MB.confirm({
                title: "<i class='mdi mdi-alert-outline'></i>提示：",
                content: "确定更新当前记录?"
            }, function () {
                $.post("/welcome/updateData", $("#welcomeFormData").serialize() + getTabFormData(), function (r) {
                    if(r.code == 200) {
                        $MB.n_success("更新成功！");
                    }else {
                        $MB.n_danger("更新失败！");
                    }
                });
            });
        }
    }
}

// wxPreview
 $("#textContent1").bind('input', function () {
    var content = $(this).val();
    content=content.replace(/\r\n/g,'<br/>').replace(/\n/g,'<br/>').replace(/\s/g,' ');
    if(content.length >= 155) {
        content = content.substr(0, 154) + "&nbsp;...";
    }
    $('#wxPreview').html('').append(content==='' ? '请输入欢迎语内容':content);

    var height = document.getElementById('preview').offsetHeight;
    height = height + 60;
    $("#chatSend").attr('style', 'position: relative;margin-top:' + height + 'px;');
});


$("#myTabs").on('shown.bs.tab', function (e) {
    var height = document.getElementById('preview').offsetHeight;
    height = height + 60;
    // chatDiv
    if(e.target.id === 'image-tab') {
        $("#wxChat").attr("style", "height: auto;width: 100%;background-color: rgb(235,235,235);border-bottom-left-radius: 10px;border-bottom-right-radius: 10px;padding-bottom: 15px");
        $("#chatDiv").html('<div style="overflow:hidden;">\n' +
            '<div id="chatSend" class="send" style="position: relative;margin-top: ' + height + 'px;">\n' +
            '<div class="arrow"></div>\n' +
            '<div style=" width: 230px;height: 170px;background-image: url(https://goss.veer.com/creative/vcg/veer/1600water/veer-158345394.jpg);-webkit-background-size:cover;-moz-background-size: cover;-o-background-size: cover;background-size: cover;"></div>\n' +
            '</div>\n' +
            '</div>');
    }else if(e.target.id === 'web-tab'){
        $("#wxChat").attr("style", "height: auto;width: 100%;background-color: rgb(235,235,235);border-bottom-left-radius: 10px;border-bottom-right-radius: 10px;padding-bottom: 15px");
        $("#chatDiv").html('<div style="overflow:hidden;">\n' +
            '<div id="chatSend" class="send" style="position: relative;margin-top: ' + height + 'px;">\n' +
            '<div class="arrow"></div>\n' +
            '<div style=" width: 230px;height: 170px;background-image: url(https://goss.veer.com/creative/vcg/veer/1600water/veer-141727262.jpg);-webkit-background-size:cover;-moz-background-size: cover;-o-background-size: cover;background-size: cover;"></div>\n' +
            '</div>\n' +
            '</div>');
    }else if(e.target.id === 'miniprogram-tab') {
        $("#wxChat").attr("style", "height: auto;width: 100%;background-color: rgb(235,235,235);border-bottom-left-radius: 10px;border-bottom-right-radius: 10px;padding-bottom: 15px");
        $("#chatDiv").html('<div style="overflow:hidden;">\n' +
            '<div id="chatSend" class="send" style="position: relative;margin-top: ' + height + 'px;">\n' +
            '<div class="arrow"></div>\n' +
            '<p class="h5">送你一张五元券</p>\n' +
            '<div style=" width: 230px;height: 170px;background-image: url(https://goss.veer.com/creative/vcg/veer/800water/veer-307142650.jpg);-webkit-background-size:cover;-moz-background-size: cover;-o-background-size: cover;background-size: cover;"></div>\n' +
            '<hr style="background: #b0b0b0;margin-top: 6px;margin-bottom: 6px;"/>\n' +
            '<p style="margin-bottom: 0px;color: #838383"><i class="fa fa-skyatlas" style="color: #1296db"></i>&nbsp;小程序</p>\n' +
            '</div>\n' +
            '</div>');
    }
});

function selectType(type) {
    var icon = "<i class='fa fa-close'></i> ";
    $qywxManualForm_validator = $qywxManualForm.validate( {
        rules: {
            welcomeName:{
                required: true
            },
            content:{
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
            welcomeName:{
                required: icon + "请输入欢迎语名称"
            },
            content:{
                required: icon + "请输入欢迎语内容"
            }
        }
    } );


    if(type=="image"){
        $("#image").show();
        $("#web").hide();
        $("#miniprogram").hide();
        $('#configBtn').attr('style', 'display:none;');
        //添加对图片的校验
        $("#picUrl").rules("add",{required:true,messages:{required:"请选择图片地址！"}});

        //清除对小程序和web的校验
        $("#miniprogramTitle").rules("remove");
        $("#miniprogramPage").rules("remove");
        $("#miniprogramImageName").rules("remove");

        $("#linkTitle").rules("remove");
        $("#linkUrl").rules("remove");

    }else if(type=="web"){
        $("#image").hide();
        $("#web").show();
        $("#miniprogram").hide();
        $('#configBtn').attr('style', 'display:none;');
        $("#linkTitle").rules("add",{required:true,messages:{required:"请填写网页标题！"}});
        $("#linkUrl").rules("add",{required:true,messages:{required:"请填写网页地址！"}});

        $("#miniprogramTitle").rules("remove");
        $("#miniprogramPage").rules("remove");
        $("#miniprogramImageName").rules("remove");

        $("#picUrl").rules("remove");
    }else if(type=="applets"){
        $("#image").hide();
        $("#web").hide();
        $("#miniprogram").show();
        $('#configBtn').attr('style', 'display:inline-block;');
        //添加对小程序的校验
        $("#miniprogramTitle").rules("add",{required:true,messages:{required:"请输入小程序标题！"}});
        $("#miniprogramPage").rules("add",{required:true,messages:{required:"请输入小程序地址！"}});
        $("#miniprogramImageName").rules("add",{required:true,messages:{required:"请上传小程序封面图片！"}});

        $("#linkTitle").rules("remove");
        $("#linkUrl").rules("remove");
        $("#picUrl").rules("remove");
    }
    $qywxManualForm_validator.resetForm();

}

var upload;
image();

function image() {
    upload = new Cupload( {
        ele: '#cupload-create',
        num: 1
    } );
}

function uploadMediaImg() {
    $("#mediaModal").modal('show');
}

function saveModelImg() {
    var code=$( "input[name='image[]']" ).val();

    if(code === ''||null==code) {
        $MB.n_warning("图片不能为空！");
        return false;
    }
    var filename=document.getElementsByClassName("cupload-image-delete")[0].title;
    if(filename.substr(filename.lastIndexOf(".")+1)!="png"&&filename.substr(filename.lastIndexOf(".")+1)!="PNG"){
        $MB.n_warning("图片格式不正确，请上传png格式图片！");
        return false;
    }
    $MB.loadingDesc("show", "图片正在上传中，请稍候...");
    $.post( "/welcome/saveModelImg", {
        mediaType: 'image',
        base64Code: code,
        filename:filename.substr(0,filename.lastIndexOf("."))
    }, function (r) {
        $MB.loadingDesc("hide");
        if (r.code === 200) {
            $MB.n_success( "保存成功！" );
            $("#miniprogramImageName").val(filename);
            $("#miniprogramImagePath").val(r.data);
            $("#mediaModal").modal('hide');
            clearImg();
        }
    } );
}


function closeModelImg() {
    $("#mediaModal").modal('hide');
    clearImg();
}
/**
 * 清除上传内容
 */
function clearImg() {
    //清除所选图片
    $("#cupload-create").html("");
    image();
}