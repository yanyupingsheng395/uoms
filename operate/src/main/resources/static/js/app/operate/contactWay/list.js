var validator;
var $contactWayForm = $("#contactWay_edit");
var modifyUrlValidator;
var userSelect = $contactWayForm.find("select[name='userSelect']");
var $usersList = $contactWayForm.find("input[name='usersList']");
$(function () {
    validateRule();
    modifyUrlValidateRule();
    initTable();
    initFollowUser();
});

function initTable() {
    let settings = {
        url: '/contactWay/getList',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        sortable: true,
        sortOrder: "asc",
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: { state: $("#state").val(),
                        remark: $("#remark").val()}
            };
        },
        columns: [{
            checkbox: true
        },  {
            field: 'qrCode',
            title: '二维码样式',
            align: 'center',
            formatter: function (value, row, indx)
            {
                return "<img style='width:120px;height:120px' src='"+value+"'></img>";
            }
        }, {
            field: 'shortUrl',
            title: '短链接',
            align: 'center',
            formatter: function (value, row, indx) {
               return "<a href=http://'"+value+"' target='_blank'>"+value+"</a>";
            }
        },  {
            field: 'usersList',
            title: '可联系成员',
            align: 'center',
            formatter: function (value,row,indx) {
                let arr=value.split(',');
                let result='';
                if(null!=arr&&arr.length>0)
                {
                    for(let i=0;i<arr.length;i++)
                    {
                        if(i>=1)
                        {
                            result+= "&nbsp;&nbsp;<span><i class='mdi mdi-account' style='color:#33cabb'></i>"+arr[i]+"</span>";
                        }else
                        {
                            result+= "<span><i class='mdi mdi-account' style='color:#33cabb'></i>"+arr[i]+"</span>";
                        }


                    }
                }else
                {
                    result+= "<span><i class='mdi mdi-account' style='color:#33cabb'></i>"+value+"</span>";
                }
                return result;
            }
        },  {
            field: 'type',
            title: '二维码类型',
            align: 'center',
            formatter: function (value,row,indx) {
               if(value==='1')
               {
                   return "<span class=\"badge bg-info\">单人</span>";;
               }else if(value==='2')
               {
                  return "<span class=\"badge bg-warning\">多人</span>";;
               }
            }
        },  {
            field: 'state',
            title: '渠道',
            align: 'center'
        },  {
            field: 'remark',
            title: '备注',
            align: 'center'
        },  {
            field: 'externalUserNum',
            title: '添加客户人数',
            align: 'center'
        },  {
            field: 'createDt',
            title: '创建时间',
            align: 'center'
        },  {
            field: 'contactWayId',
            title: 'contactWayId',
            visible: false
        }]
    };
    $MB.initTable('contactWayTable', settings);
}

function resetQuery() {
    $("#state").val("");
    $("#remark").val("");
    $MB.refreshTable("contactWayTable");
}

$("#btn_query").click(function () {
    $MB.refreshTable("contactWayTable");
});

/**
 * 编辑按钮
 */
$("#btn_edit").click(function () {
    $("#btn_save").attr("name", "update");
    let selected = $("#contactWayTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要编辑的渠道活码！');
        return;
    }
    let contactWayId = selected[0].contactWayId;
    //获取数据 并进行填充
    $.post("/contactWay/getContactWayById", {"contactWayId": contactWayId}, function (r) {
        if (r.code === 200) {
            var $form = $('#contactWay_edit');
            $("#add_modal").modal('show');
            let d = r.data;
            $("#myLargeModalLabel").html('修改渠道活码');
            $form.find("input[name='contactWayId']").val(d.contactWayId);
            $form.find("input[name='state']").val(d.state);
            $form.find("input[name='remark']").val(d.remark);
            $form.find("select[name='userSelect']").selectpicker('val', d.usersList.split(','));
            $form.find("input[name='usersList']").val($form.find("select[name='userSelect']").selectpicker('val'));
        } else {
            $MB.n_danger(r['msg']);
        }
    });

});

/**
 * 新增按钮
 */
$("#btn_add").click(function () {
    $('#add_modal').modal('show');
});

$("#btn_delete").click(function () {
    $MB.n_warning('演示环境暂不支持删除数据!');
});

$("#add_modal").on('hidden.bs.modal', function () {
    //执行一些清空操作
    $MB.closeAndRestModal("add_modal");
    $("#btn_save").attr("name", "save");
    $("#contactWay_edit").validate().resetForm();
});

$("#btn_save").click(function () {
    var name = $(this).attr("name");
    var validator = $contactWayForm.validate();
    var flag = validator.form();
    if (flag) {
        if (name === "save") {
            $.post("/contactWay/save", $("#contactWay_edit").serialize(), function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success(r.msg);
                    $MB.refreshTable("contactWayTable");
                } else $MB.n_danger(r.msg);
            });
        }
        if (name === "update") {
            $.post("/contactWay/update", $("#contactWay_edit").serialize(), function (r) {
                if (r.code === 200) {
                    closeModal();
                    $MB.n_success(r.msg);
                    $MB.refreshTable("contactWayTable");
                } else $MB.n_danger(r.msg);
            });
        }
    }
});

// 表单验证规则
function validateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    validator = $contactWayForm.validate({
        rules: {
            state: {
                required: true,
                maxlength: 30
            },
            usersList: {
                required: true
            },
            remark: {
                maxlength: 30
            }
        },
        errorPlacement: function (error, element) {
            if (element.is(":checkbox") || element.is(":radio")) {
                error.appendTo(element.parent().parent());
            } else {
                error.insertAfter(element);
            }
        },
        messages: {
            state: {
                required: icon + "请输入渠道",
                maxlength: icon + "最大长度不能超过30个字符"
            },
            usersList: {
                required: icon + "请选择使用成员"
            },
            remark: {
                maxlength: icon + "最大长度不能超过30个字符"
            }
        }
    });
}

// 表单验证规则
function modifyUrlValidateRule() {
    var icon = "<i class='zmdi zmdi-close-circle zmdi-hc-fw'></i> ";
    modifyUrlValidator = $("#modifyurlForm").validate({
        rules: {
            shortUrl: {
                required: true,
                maxlength: 15
            }
        },
        errorPlacement: function (error, element) {
            if (element.is(":checkbox") || element.is(":radio")) {
                error.appendTo(element.parent().parent());
            } else {
                error.insertAfter(element);
            }
        },
        messages: {
            shortUrl: {
                required: icon + "短链不能为空",
                maxlength: icon + "最大长度不能超过15个字符"
            }
        }
    });
}

function initFollowUser() {
    let data=new Array();
    data.push("HuangKun");
    data.push("CaoHuiXue");
    data.push("wake");
    data.push("brandonz");
    let option = "";

    $.each(data,function(index,value){
        option += "<option value='" + value + "'>" + value + "</option>"
    })

    userSelect.html("").append(option);
    userSelect.selectpicker('refresh');
}

userSelect.on('changed.bs.select',function(){
    $usersList.val(userSelect.selectpicker('val'));
});

function closeModal() {
    $MB.closeAndRestModal("add_modal");
}

$("#btn_url").click(function () {
    let selected = $("#contactWayTable").bootstrapTable('getSelections');
    let selected_length = selected.length;
    if (!selected_length) {
        $MB.n_warning('请选择需要修改短链接的渠道活码！');
        return;
    }
    let contactWayId = selected[0].contactWayId;
    //获取数据 并进行填充
    $.post("/contactWay/getContactWayById", {"contactWayId": contactWayId}, function (r) {
        if (r.code === 200) {
            var $form = $('#modifyurlForm');
            $("#modifyurl_modal").modal('show');
            let d = r.data;
            $("#myLargeModalLabel4").html('修改渠道活码');
            $form.find("input[name='modifyurl_contactWayId']").val(d.contactWayId);
            $form.find("input[name='state2']").val(d.state);
            $form.find("input[name='remark2']").val(d.remark);
        } else {
            $MB.n_danger(r['msg']);
        }
    });
});

/**
 * 链接保存
 */
$("#btn_saveUrl").click(function () {
    var validator = $("#modifyurlForm").validate();
    var flag = validator.form();
    if (flag) {
        let contactWayId=$('#modifyurl_contactWayId').val();
        let shortUrl=$('#shortUrl').val();
       //对短链进行保存
        $.post("/contactWay/updateShortUrl", {"contactWayId": contactWayId,"shortUrl":shortUrl}, function (r) {
            if (r.code === 200) {
                $MB.closeAndRestModal("modifyurl_modal");
                $MB.n_success(r.msg);
                $MB.refreshTable("contactWayTable");
            } else {
                $MB.n_danger(r['msg']);
            }
        });
    }
});

/**
 * 获取短链
 */
function getShortUrl() {
    var url = $("#longUrl").val();
    if(url.trim() == "") {
        $MB.n_warning("长链不能为空！");
        return;
    }

    var Expression=/http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/;
    var objExp=new RegExp(Expression);
    if(objExp.test(url) != true)
    {
        $MB.n_warning("长链格式错误！");
        return;
    }
    $.get("/coupon/getShortUrl", {url: url}, function(r) {
        if(r.code === 200) {
            $("#shortUrl").val(r.data);
            //给出提示
            $MB.n_success("生成短链成功!");
        }else {
            $MB.n_danger(r['msg']);
        }
    });
}