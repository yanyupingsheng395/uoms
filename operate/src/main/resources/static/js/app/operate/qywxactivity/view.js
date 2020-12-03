let create_step;

$( function () {
    create_step = steps({
        el: "#addStep",
        data: [
            {title: "活动信息", description: ""},
            {title: "商品信息", description: ""},
            {title: "配置消息", description: ""}
        ],
        center: true,
        dataOrder: ["title", "line", "description"]
    });
    $("#activitySource").css('pointer-events', 'none');
    $("#activityflag").css('pointer-events', 'none');
});

// 根据索引跳转到相应的页面
function stepBreak(index) {
    if(index == 0) {
        create_step.setActive(0);
        $("#step1").attr("style", "display:block;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:none;");
    }
    if(index == 1) {
        create_step.setActive(1);
        getProductInfo('NOTIFY', 'activityProductTable1');
        getProductInfo('DURING', 'activityProductTable');
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:block;");
        $("#step3").attr("style", "display:none;");
    }
    if(index == 2) {
        create_step.setActive(2);
        $("#step1").attr("style", "display:none;");
        $("#step2").attr("style", "display:none;");
        $("#step3").attr("style", "display:block;");
        createActivity();
    }
}

// step2:商品信息表
function getProductInfo(type, tableId) {
    var title = '活动通知';
    if(type === 'NOTIFY') {
        title = '活动通知';
    }
    if(type === 'DURING') {
        title = '活动期间';
    }
    var settings = {
        url: '/qywxActivity/getActivityProductPage',
        pagination: true,
        singleSelect: false,
        sidePagination: "server",
        clickToSelect: true,
        pageSize: 5,
        pageList: [5, 15, 50, 100],
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset,
                headId: $( "#headId" ).val(),
                productId: function() {
                    if(tableId === 'activityProductTable1') {
                        return $( "#productId1" ).val();
                    }else if(tableId === 'activityProductTable') {
                        return $( "#productId" ).val();
                    }
                },
                productName:function() {
                    if(tableId === 'activityProductTable1') {
                        return $( "#productName1" ).val();
                    }else if(tableId === 'activityProductTable') {
                        return $( "#productName" ).val();
                    }
                },
                groupId: function() {
                    if(tableId === 'activityProductTable1') {
                        return $("#groupId1").find("option:selected").val();
                    }else if(tableId === 'activityProductTable') {
                        return $("#groupId").find("option:selected").val();
                    }
                },
                activityType: type
            };
        },
        columns: [
            {
                checkbox: true
            },
            {
                field: 'productId',
                title: '商品ID',
                valign: 'middle',
                align: 'center'
            }, {
                field: 'productName',
                title: '名称',
                valign: 'middle',
                align: 'center'
            },{
                field: 'groupId',
                title: '店铺活动机制',
                valign: 'middle',
                align: 'center',
                formatter: function (value, row, index) {
                    var res = "-";
                    if(value === '9') {
                        res =  "满件打折";
                    }
                    if(value === '14') {
                        res =  "立减特价";
                    }
                    if(value === '13') {
                        res = '仅店铺券'
                    }
                    return res;
                }
            },{
                field: 'activityProfit',
                title: '活动商品体现利益点',
                valign: 'middle',
                align: 'center',
                formatter: function (value, row, index) {
                    if(row['groupId'] == '9') {
                        return value +'折';
                    }else {
                        return value +'元';
                    }
                }
            }, {
                field: 'checkFlag',
                title: '校验结果',
                valign: 'middle',
                align: 'center',
                formatter: function (value, row, index) {
                    if(value === 'Y') {
                        return "<span class=\"badge bg-success\">通过</span>";
                    }
                    if(value === 'N') {
                        return "<span class=\"badge bg-danger\">未通过</span>";
                    }
                    return "-";
                }
            }, {
                field: 'checkComments',
                title: '失败原因',
                valign: 'middle',
                align: 'center'
            }]
    };
    $( "#" + tableId  ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}
// step2:下载商品数据
$( "#btn_download" ).click( function () {
    window.location.href = "/qywxActivity/downloadFile";
} );

// step3:初始化
function createActivity() {
    getGroupList( 'NOTIFY', 'table1');
    getGroupList('DURING', 'table5');
}

// step3:获取群组表
function getGroupList(type, tableId) {
    var headId = $( "#headId" ).val();
    var settings = {
        url: '/qywxActivity/getGroupList',
        pagination: false,
        singleSelect: false,
        queryParams: function () {
            return {
                headId: headId,
                type: type
            };
        },
        columns: [
            {
                field: 'groupName',
                title: '店铺活动机制'
            },
            {
                field: 'groupInfo',
                title: '理解用户群组'
            }, {
                field: 'smsTemplateContent',
                title: '消息内容',
                //td宽度及内容超过宽度隐藏
                cellStyle : function(value, row, index, field){
                    return {
                        css: {"min-width": '300px',
                            "max-width":'300px'
                        }
                    }},
                formatter: function (value, row, index) {
                    if(value === null || value.length==0)
                    {
                        return '';
                    }else if(value.length <20) {
                        return "<a style='color: #48b0f7;' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-placement=\"bottom\" data-original-title=\""+value+"\" data-trigger=\"hover\">\n" +
                            value+ "</a>&nbsp;&nbsp;<a class='btn-xs' style='cursor:pointer' onclick='copyToClipboard(this)'>复制</a>";
                    }else
                    {
                        return "<a style='color: #48b0f7;' data-toggle=\"tooltip\" data-html=\"true\" title=\"\" data-placement=\"bottom\" data-original-title=\""+value+"\" data-trigger=\"hover\">\n" +
                            value.substring(0, 20) + "...</a>&nbsp;&nbsp;<a class='btn-xs' style='cursor:pointer' onclick='copyToClipboard(this)'>复制</a>";
                    }
                }
            }, {
                field: 'checkFlag',
                title: '校验结果',
                formatter: function (value, row, index) {
                    if(value === 'Y') {
                        return "<span class=\"badge bg-success\">通过</span>";
                    }
                    if(value === 'N') {
                        return "<span class=\"badge bg-danger\">未通过</span>";
                    }
                    return "-";
                }
            },{
                field: 'checkComments',
                title: '失败原因'
            }],
        onLoadSuccess: function () {
            $("a[data-toggle='tooltip']").tooltip();
        }
    };
    $( "#" + tableId ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

// 查询商品信息
function searchActivityProduct1() {
    $MB.refreshTable( 'activityProductTable1' );
}

function searchActivityProduct() {
    $MB.refreshTable( 'activityProductTable' );
}

// 重置查询条件
function resetActivityProduct1() {
    $( "#productId1" ).val( "" );
    $( "#productName1" ).val( "" );
    $( "#groupId1" ).find( "option:selected" ).removeAttr( "selected" );
    $MB.refreshTable( 'activityProductTable1' );
}

function resetActivityProduct() {
    $( "#productId" ).val( "" );
    $( "#productName" ).val( "" );
    $( "#groupId" ).find( "option:selected" ).removeAttr( "selected" );
    $MB.refreshTable( 'activityProductTable' );
}

// 活动商品数据下载
$("#btn_download_data1").click(function () {
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定下载商品数据?"
    }, function () {
        $("#btn_download_data1").text("下载中...").attr("disabled", true);
        $.post("/qywxActivity/downloadExcel", {headId: $("#headId").val(), activityType: 'NOTIFY'}, function (r) {
            if (r.code === 200) {
                window.location.href = "/common/download?fileName=" + r.msg + "&delete=" + true;
            } else {
                $MB.n_warning(r.msg);
            }
            $("#btn_download_data1").html("").append("<i class=\"fa fa-download\"></i> 下载数据").removeAttr("disabled");
        });
    });
});

$("#btn_download_data").click(function () {
    $MB.confirm({
        title: "<i class='mdi mdi-alert-outline'></i>提示：",
        content: "确定下载商品数据?"
    }, function () {
        $("#btn_download_data").text("下载中...").attr("disabled", true);
        $.post("/qywxActivity/downloadExcel", {headId: $("#headId").val(), activityType: 'DURING'}, function (r) {
            if (r.code === 200) {
                window.location.href = "/common/download?fileName=" + r.msg + "&delete=" + true;
            } else {
                $MB.n_warning(r.msg);
            }
            $("#btn_download_data").html("").append("<i class=\"fa fa-download\"></i> 下载数据").removeAttr("disabled");
        });
    });
});