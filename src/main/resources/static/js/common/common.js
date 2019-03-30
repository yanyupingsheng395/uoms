$(document).ready(function () {
    menu_tree();
});
// 菜单点击效果
function menu_tree() {
    var urlstr = location.href;
    var status = false;
    $(".sidebar-main ul li a").each(function() {
        if ((urlstr + '/').indexOf($(this).attr('href')) > -1&&$(this).attr('href')!='') {
            $(this).parent("li").addClass("active");
            $(this).parent("li").parents("li").addClass("open").addClass("active");
            console.log($(this).parent("li").parents("li").attr("class"));
            status = true;
            return;
        }
    });
    if(!status) {
        $(this).parent("li").addClass("active");
        $(this).parents("li").addClass("open").addClass("active");
    }
}