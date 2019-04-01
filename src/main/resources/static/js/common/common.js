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

myChart.showLoading();


var data = [];
myChart.hideLoading();

echarts.util.each(data.children, function (datum, index) {
    index % 2 === 0 && (datum.collapsed = true);
});

myChart.setOption(option = {
    tooltip: {
        trigger: 'item',
        triggerOn: 'mousemove'
    },
    series: [
        {
            type: 'tree',

            data: [data],

            top: '1%',
            left: '7%',
            bottom: '1%',
            right: '20%',

            symbolSize: 7,

            label: {
                normal: {
                    position: 'left',
                    verticalAlign: 'middle',
                    align: 'right',
                    fontSize: 9
                }
            },

            leaves: {
                label: {
                    normal: {
                        position: 'right',
                        verticalAlign: 'middle',
                        align: 'left'
                    }
                }
            },

            expandAndCollapse: true,
            animationDuration: 550,
            animationDurationUpdate: 750
        }
    ]
});