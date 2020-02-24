$(function () {
    // 初始化日期控件
    initDt();
    // 初始化上传商品的数据
    getProductInfo('preheat');

    validBasic();
});
function getProductInfo(stage) {
    var settings = {
        url: '/activity/getActivityProductPage',
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  ////页面大小
                pageNum: (params.offset / params.limit) + 1,
                param: {
                    headId: $( "#headId" ).val(),
                    productId: $( "#productId" ).val(),
                    productName: $( "#productName" ).val(),
                    productAttr: $( "#productAttr" ).val(),
                    stage: stage
                }
            };
        },
        columns: [
            {
                checkbox: true
            },
            {
                field: 'productId',
                title: '商品ID'
            }, {
                field: 'productName',
                title: '名称'
            }, {
                field: 'minPrice',
                title: '活动期间最低价（元/件）'
            }, {
                field: 'formalPrice',
                title: '非活动日常单价（元/件）'
            }, {
                field: 'activityIntensity',
                title: '活动力度（%）'
            }, {
                field: 'productAttr',
                title: '活动属性',
                formatter: function (value, row, index) {
                    let res = '-';
                    switch (value) {
                        case "0":
                            res = "主推";
                            break;
                        case "1":
                            res = "参活";
                            break;
                        case "2":
                            res = "正常";
                            break;
                    }
                    return res;
                }
            }, {
                field: 'productUrl',
                title: '商品短链',
                formatter: function (value, row, index) {
                    return "<a style='color: #409eff;cursor:pointer;text-decoration: underline;' href='"+value+"' target='_blank'>"+value+"</a>";
                }
            }]
    };
    $("#activityProductTable").bootstrapTable('destroy').bootstrapTable(settings);
}

// 跳转到
function createActivity(type) {
    $("#step1").attr("style", "display:none;");
    $("#step2").attr("style", "display:none;");
    $("#step3").attr("style", "display:block;");
    $("#step4").attr("style", "display:block;");
    if('preheat' === type) {
    }
    if('formal' === type) {
    }
}

// 初始化日期控件
function initDt() {
    init_date('preheatNotifyDt', 'yyyy-mm-dd', 0,2,0);
    init_date('formalNotifyDt', 'yyyy-mm-dd', 0,2,0);
    init_date_begin( 'preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd', 0, 2, 0 );
    init_date_end( 'preheatStartDt', 'preheatEndDt', 'yyyy-mm-dd', 0, 2, 0 );
    init_date_begin( 'formalStartDt', 'formalEndDt', 'yyyy-mm-dd', 0, 2, 0 );
    init_date_end( 'formalStartDt', 'formalEndDt', 'yyyy-mm-dd', 0, 2, 0 );

    var date = new Date();
    date.setDate(date.getDate() + 1);
    $("#preheatStartDt").datepicker('setStartDate', date);
    $("#preheatEndDt").datepicker('setStartDate', date);
    $("#formalStartDt").datepicker('setStartDate', date);
    $("#formalEndDt").datepicker('setStartDate', date);
    $("#preheatNotifyDt").datepicker('setStartDate', date);
    $("#formalNotifyDt").datepicker('setStartDate', date);
}

$("#btn_basic").click(function () {
    var validator = $basicAddForm.validate();
    var flag1 = validator.form();
    var flag2 = validBasicDt();
    if(flag1 && flag2) {
        $MB.confirm({
            title: '提示:',
            content: '确定保存信息？'
        }, function () {
            saveActivityHead();
        });
    }
});

var basic_validator;
var $basicAddForm = $("#basic-add-form");
function validBasic() {
    var icon = "<i class='fa fa-close'></i> ";
    basic_validator = $basicAddForm.validate({
        rules: {
            activityflag: {
                required: true
            },
            activityName: {
                required: true
            },
            hasPreheat: {
                required: true
            },
            preheatNotifyDt: {
                required: true
            },
            preheatStartDt: {
                required: true
            },
            preheatEndDt: {
                required: true
            },
            formalNotifyDt: {
                required: true
            },
            formalStartDt: {
                required: true
            },
            formalEndDt: {
                required: true
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
            activityflag: {
                required: icon + "请选择活动类型"
            },
            activityName: {
                required: icon + "请输入活动名称"
            },
            hasPreheat: {
                required: icon + "请选择是否预售"
            },
            preheatNotifyDt: {
                required: icon + "请输入预售提醒时间"
            },
            preheatStartDt: {
                required: icon + "请输入预售开始时间"
            },
            preheatEndDt: {
                required: icon + "请输入预售结束时间"
            },
            formalNotifyDt: {
                required: icon + "请输入正式提醒时间"
            },
            formalStartDt: {
                required: icon + "请输入正式开始时间"
            },
            formalEndDt: {
                required: icon + "请输入正式结束时间"
            }
        }
    });
}

// 验证基本信息的时间
function validBasicDt() {
    var flag = true;
    var hasPreheat = $("input[name='hasPreheat']:checked").val();
    let preheatStartDt = $( "#preheatStartDt" ).val();
    let preheatEndDt = $( "#preheatEndDt" ).val();
    let formalStartDt = $( "#formalStartDt" ).val();
    var preheatNotifyDt = $("#preheatNotifyDt").val();
    var formalNotifyDt = $("#formalNotifyDt").val();

    if(hasPreheat === '1') {
        var flag1 = new Date( String( formalStartDt ) ) > new Date( String( preheatEndDt ) );
        if(!flag1) {
            flag = false;
            $MB.n_warning("预热结束时间必须小于正式开始时间！");
        }
        var flag2 = new Date(String(preheatStartDt)) > new Date(String(preheatNotifyDt));
        if(!flag2) {
            flag = false;
            $MB.n_warning("预售提醒时间必须小于预售开始时间！");
        }
    }
    var flag3 = new Date(String(formalStartDt)) > new Date(String(formalNotifyDt));
    if(!flag3) {
        flag = false;
        $MB.n_warning("正式提醒时间必须小于正式开始时间！");
    }
    return flag;
}
$("input[name='hasPreheat']").click(function() {
    var hasPreheat = $(this).val();
    if(hasPreheat === '1') {
        $("#preheatDiv").attr("style", "display:block;");
    }else {
        $("#preheatDiv").attr("style", "display:none;");
    }
});

// 保存基本信息
function saveActivityHead() {
    $.post( "/activity/saveActivityHead", $basicAddForm.serialize(), function (r) {
        if (r.code === 200) {
            $MB.n_success( r.msg );
            $( "#headId" ).val( r.data );
        } else {
            $MB.n_danger( "有错误发生！" )
        }
    });
}