$(document).ready(function () {
    menu_tree();

    //消息提示组件
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "newestOnTop": true,
        "progressBar": true,
        "rtl": false,
        "positionClass": "toast-top-center",
        "preventDuplicates": true,
        "onclick": null,
        "showDuration": 300,
        "hideDuration": 1000,
        "timeOut": 5000,
        "extendedTimeOut": 1000,
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    }
});

// 菜单点击效果
function menu_tree() {
    var urlStr = location.href;
    var flag = false;
    $(".sidebar-main:eq(1) ul li a").each(function() {
        if ((urlStr + '/').indexOf($(this).attr('href')) > -1&&$(this).attr('href')!='') {
            $("#collapseTwo").addClass("in");
            $("#collapseOne").removeClass("in");

            flag = true;
            localStorage.setItem("menuUrl", urlStr);
            $(this).parent("li").addClass("active");
            $(this).parent("li").parents("li").addClass("open").addClass("active");

            $(this).parent("li").siblings().removeClass("active");
            $(this).parent("li").parents("li").siblings().removeClass("open").removeClass("active");
            return;
        }
    });

    // 非url菜单
    if(!flag) {
        $(".sidebar-main:eq(0) ul li a").each(function() {
            if ((urlStr + '/').indexOf($(this).attr('href')) > -1&&$(this).attr('href')!='') {
                $("#collapseOne").addClass("in");
                $("#collapseTwo").removeClass("in");

                flag = true;
                localStorage.setItem("menuUrl", urlStr);
                $(this).parent("li").addClass("active");
                $(this).parent("li").parents("li").addClass("open").addClass("active");

                $(this).parent("li").siblings().removeClass("active");
                $(this).parent("li").parents("li").siblings().removeClass("open").removeClass("active");
                return;
            }
        });
        if(!flag) {
            urlStr = localStorage.getItem("menuUrl");
            $(".sidebar-main:eq(0) ul li a").each(function() {
                if ((urlStr + '/').indexOf($(this).attr('href')) > -1&&$(this).attr('href')!='') {
                    flag = true;
                    $("#collapseOne").addClass("in");
                    $("#collapseTwo").removeClass("in");
                    // menuUrl = urlStr;
                    localStorage.setItem("menuUrl", urlStr);
                    $(this).parent("li").addClass("active");
                    $(this).parent("li").parents("li").addClass("open").addClass("active");

                    $(this).parent("li").siblings().removeClass("active");
                    $(this).parent("li").parents("li").siblings().removeClass("open").removeClass("active");
                    return;
                }
            });
        }
    }
}

/**
 * 初始化起止日期插件：开始
 * @param beginId
 * @param endId
 * @param format
 * @param startView
 * @param maxViewMode
 * @param minViewMode
 */
function init_date_begin(beginId, endId, format, startView, maxViewMode, minViewMode) {
    var dataDt = new Date();
    dataDt.setDate(dataDt.getDate()-1);
    $('#' + beginId).datepicker({
        format: format,
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true,
        startView: startView,
        maxViewMode: maxViewMode,
        minViewMode: minViewMode,
        endDate: dataDt
    }).on("changeDate",function(ev){  //值改变事件
        //选择的日期不能大于第二个日期控件的日期
        if(ev.date){
            $("#" + endId).datepicker('setStartDate', new Date(ev.date.valueOf()));
        }else{
            $("#" + endId).datepicker('setStartDate',null);
        }
    });
}

/**
 * 初始化起止日期插件：结束
 * @param beginId
 * @param endId
 * @param format
 * @param startView
 * @param maxViewMode
 * @param minViewMode
 */
function init_date_end(beginId, endId, format, startView, maxViewMode, minViewMode) {
    var dataDt = new Date();
    dataDt.setDate(dataDt.getDate()-1);
    $('#' + endId).datepicker({
        format: format,
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true,
        startView: startView,
        maxViewMode: maxViewMode,
        minViewMode: minViewMode,
        endDate: dataDt
    }).on("changeDate",function(ev){  //值改变事件
        //选择的日期不能大于第二个日期控件的日期
        console.log(ev);
        if(ev.date){
            $("#" + beginId).datepicker('setEndDate', new Date(ev.date.valueOf()));
        }else{
            $("#" + beginId).datepicker('setEndDate',null);
        }
    });
}

/**
 * 初始化日期插件
 * @param id 控件ID名称
 * @param format 时间格式
 * @param startView 起始选择范围 0为日，1为月，2为年
 * @param maxViewMode 最大选择范围 0为日，1为月，2为年
 * @param minViewMode 最小选择范围 0为日，1为月，2为年
 *
 */
function init_date(id, format, startView, maxViewMode, minViewMode) {
    $('#' + id).datepicker({
        format: format,
        language: "zh-CN",
        todayHighlight: true,
        autoclose: true,
        startView: startView,
        maxViewMode: maxViewMode,
        minViewMode: minViewMode
    });
}