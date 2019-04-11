$(document).ready(function () {
    menu_tree();
});

// 菜单点击效果
function menu_tree() {
    var urlStr = location.href;
    var flag = false;
    $(".sidebar-main ul li a").each(function() {
        if ((urlStr + '/').indexOf($(this).attr('href')) > -1&&$(this).attr('href')!='') {
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
        urlStr = localStorage.getItem("menuUrl");
        $(".sidebar-main ul li a").each(function() {
            if ((urlStr + '/').indexOf($(this).attr('href')) > -1&&$(this).attr('href')!='') {
                flag = true;
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

toastr.options = {
    "closeButton": true,
    "progressBar": true,
    "positionClass": "toast-top-center",
    "preventDuplicates": true,
    "timeOut": 1500,
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};