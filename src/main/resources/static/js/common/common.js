$(document).ready(function () {
    menu_tree();
});
// 菜单点击效果
function menu_tree() {
    var urlStr = location.href;
    var status = false;
    $(".sidebar-main ul li a").each(function() {
        if ((urlStr + '/').indexOf($(this).attr('href')) > -1&&$(this).attr('href')!='') {
            $(this).parent("li").addClass("active");
            $(this).parent("li").parents("li").addClass("open").addClass("active");

            $(this).parent("li").siblings().removeClass("active");
            $(this).parent("li").parents("li").siblings().removeClass("open").removeClass("active");
            status = true;
            return;
        }
    });
    if(!status) {
        $(this).parent("li").addClass("active");
        $(this).parents("li").addClass("open").addClass("active");

        $(this).parent("li").siblings().removeClass("active");
        $(this).parent("li").parents("li").siblings().removeClass("open").removeClass("active");
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