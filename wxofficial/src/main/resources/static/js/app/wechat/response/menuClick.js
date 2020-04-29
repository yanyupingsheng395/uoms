var menu_data = [];
$( function () {
    getDataList();
    menuInit();
} );

function getDataList() {
    let settings = {
        url: "",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                pageSize: params.limit,  //页面大小
                pageNum: (params.offset / params.limit) + 1
            }
        },
        columns: [
            {
                checkbox: true,
            },
            {
                field: 'mediaId',
                title: 'ID',
                visible: false
            },
            {
                field: '',
                title: '回复消息类型'
            }, {
                field: '',
                title: '人群标签'
            }]
    };
    $MB.initTable( 'dataTable', settings );
}

function add_sub_menu_data() {
    var parentIndex = $( ".weixin-menu" ).find( '.menu_active' ).parent().parent().parent().index();
    var subButtons = menu_data[parentIndex]['sub_button'] === undefined ? [] : menu_data[parentIndex]['sub_button'];
    var tmp = {};
    tmp['name'] = '子菜单名称';
    tmp['type'] = 'click';
    subButtons.push( tmp );
    menu_data[parentIndex]['sub_button'] = subButtons;
}

// 菜单点击
function sub_menu_add(dom) {
    var len = $( dom ).parent().siblings().length;
    if (len <= 4) {
        $( dom ).parent().parent().prepend( "" +
            "<div class=\"subtitle menu_bottom\">\n" +
            "<div class=\"menu_subItem\" style=\"cursor:pointer;\" onclick=\"sub_menu(this)\">\n" +
            "子菜单名称\n" +
            "</div>\n" +
            "</div>" );
    }
    if (len == 4) {
        $( dom ).parent().remove();
    }
    add_sub_menu_data();
}

// 二级菜单新增的情况
function sub_menu(dom) {
    $( "#menuName" ).val( $( dom ).text().trim() );
    $( dom ).addClass( "menu_active" );
    $( dom ).parent().siblings().find( "div[class='menu_subItem menu_active']" ).removeClass( "menu_active" );
    $( ".menu_bottom" ).find( "div[class='menu_item menu_active']" ).removeClass( "menu_active" );

    $( "#menuContent" ).attr( "style", "display:block;" );
    $( "#sendMsg" ).attr( "style", "background-color: #fff;display: block;" );
}

// 一级菜单新增的情况
function menu_add(dom) {
    $( dom ).siblings().attr( "style", "display:block;" );
    $( dom ).addClass( "menu_active" );
    $( "#menuName" ).val( $( dom ).text().trim() );
    $( dom ).parent().siblings().find( ".submenu" ).attr( "style", "display:none;" );
    $( dom ).parent().siblings().find( "div[class='menu_item menu_active']" ).removeClass( "menu_active" );
    $( ".menu_bottom" ).find( "div[class='menu_subItem menu_active']" ).removeClass( "menu_active" );

    if ($( dom ).siblings().children().length == 1) {
        $( "#menuContent" ).attr( "style", "display:block;" );
        $( "#sendMsg" ).attr( "style", "background-color: #fff;display: block;" );
    } else {
        $( "#menuContent" ).attr( "style", "display:none;" );
        $( "#sendMsg" ).attr( "style", "background-color: #fff;display: none;" );
    }
}


function getMaterialDataList(type, tableId) {
    let settings = {
        url: "/material/getDataList",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        queryParams: function (params) {
            return {
                limit: params.limit,  //页面大小
                offset: (params.offset / params.limit) + 1,
                param: {type: type}
            }
        },
        columns: [
            {
                checkbox: true
            },
            {
                field: 'mediaId',
                title: 'ID',
                visible: false
            },
            {
                field: 'name',
                title: '名称',
                formatter: function (value, row, index) {
                    if (media_type === 'image') {
                        return '<a href="' + row['url'] + '" target="_blank" style="text-decoration: underline">' + value + '</a>';
                    } else {
                        return value;
                    }
                }
            },
            {
                field: 'updateTime',
                title: '创建时间',
                align: 'center',
                formatter: function (value, row, index) {
                    return resolvingDate( value );
                }
            }]
    };
    $( "#" + tableId ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

// 图文列表
function getImageTextTableData() {
    let settings = {
        url: "/imageText/getDataList",
        cache: false,
        pagination: true,
        singleSelect: true,
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        columns: [
            {
                checkbox: true
            },
            {
                field: 'title',
                title: '图文标题',
                formatter: function (value, row, index) {
                    return "<a onclick='window.open(\"" + row['url'] + "\", \"_target\")' style='text-decoration: underline;cursor: pointer;'>" + value + "</a>";
                }
            },
            {
                field: 'userGroup',
                title: '适用人群'
            }]
    };
    $( "#imageTextTable" ).bootstrapTable( 'destroy' ).bootstrapTable( settings );
}

// 日期格式化
function resolvingDate(date) {
    let d = new Date( date );
    let month = (d.getMonth() + 1) < 10 ? '0' + (d.getMonth() + 1) : (d.getMonth() + 1);
    let day = d.getDate() < 10 ? '0' + d.getDate() : d.getDate();
    let hours = d.getHours() < 10 ? '0' + d.getHours() : d.getHours();
    let min = d.getMinutes() < 10 ? '0' + d.getMinutes() : d.getMinutes();
    let sec = d.getSeconds() < 10 ? '0' + d.getSeconds() : d.getSeconds();
    let times = d.getFullYear() + '-' + month + '-' + day + ' ' + hours + ':' + min;
    return times
}

var media_type = 'text';
$( "#text-tab" ).on( "shown.bs.tab", function () {
    media_type = 'text';
} );

$( "#image-tab" ).on( "shown.bs.tab", function () {
    media_type = 'image';
    getMaterialDataList( 'image', 'imageTable' );
} );

$( "#audio-tab" ).on( "shown.bs.tab", function () {
    media_type = 'voice';
    getMaterialDataList( 'voice', 'voiceTable' );
} );

$( "#video-tab" ).on( "shown.bs.tab", function () {
    media_type = 'video';
    getMaterialDataList( 'video', 'videoTable' );
} );

// 图文消息tab切换
$( "#imageText-tab" ).on( "shown.bs.tab", function () {
    media_type = 'news';
    getImageTextTableData();
} );

// 菜单事件
function menu_event(idx) {
    if (idx == 1) {
        $( "#sendMsg" ).attr( "style", "background-color: #fff;display:inline-block;" );
        $( "#urlBreak" ).attr( "style", "display:none;" );
    }
    if (idx == 2) {
        $( "#sendMsg" ).attr( "style", "display:none;" );
        $( "#urlBreak" ).attr( "style", "background-color: #fff;display:inline-block;" );
    }
}

$( "#menuName" ).on( 'input', function () {
    $( ".weixin-menu" ).find( '.menu_active' ).html( $( this ).val() );
} );

function deleteMenu() {
    if ($( ".weixin-menu" ).find( '.menu_active' ).length == 0) {
        $MB.n_warning( "您没有选中任何菜单！" );
        return;
    }
    $MB.confirm( {
        title: '提示',
        content: '确认删除选中的菜单？'
    }, function () {
        var flag1 = $( ".weixin-menu" ).find( '.menu_active' ).parent().siblings().hasClass( "sub_plus" );
        var flag2 = $( ".weixin-menu" ).find( '.menu_active' ).hasClass( "menu_item" );
        if (!flag1 && !flag2) {
            $( ".weixin-menu" ).find( '.menu_active' ).parent().parent().append( "" +
                "<div class=\"subtitle menu_bottom sub_plus\">\n" +
                "                                                    <div class=\"menu_subItem\" style=\"cursor:pointer;\"\n" +
                "                                                         onclick=\"sub_menu_add(this)\">\n" +
                "                                                        <i class=\"mdi mdi-plus mdi-18px\"></i>\n" +
                "                                                    </div>\n" +
                "                                                </div>" +
                "" );
        }

        $( ".weixin-menu" ).find( '.menu_active' ).parent().remove();
        var menuItemLen = $( ".weixin-menu" ).find( 'div[class="menu_item"]' ).length;
        if (menuItemLen == 2) {
            $( ".weixin-menu" ).append( "<div class=\"menu_bottom\">\n" +
                "<div class=\"menu_item plus\" onclick=\"menu_add_plus(this)\">\n" +
                "<i class=\"mdi mdi-plus mdi-18px\"></i>" +
                "</div>" );
        }

    } );
}

function add_menu_data() {
    var tmp = {};
    tmp['name'] = '菜单名称';
    tmp['type'] = 'click';
    menu_data.push( tmp );
}

// 一级菜单点击+事件
function menu_add_plus(dom) {
    $( dom ).parent().remove();
    var oldMenuBottomLen = $( ".weixin-menu" ).find( 'div[class="menu_bottom"]' ).length;
    $( ".weixin-menu" ).append( "" +
        "                                        <div class=\"menu_bottom\">\n" +
        "                                            <div class=\"menu_item\" onclick=\"menu_add(this)\">\n" +
        "                                                菜单名称\n" +
        "                                            </div>\n" +
        "                                            <div class=\"submenu\" style='display:none;'>\n" +
        "                                                <!--增加子菜单-->\n" +
        "                                                <div class=\"subtitle menu_bottom sub_plus\">\n" +
        "                                                    <div class=\"menu_subItem\" style=\"cursor:pointer;\"\n" +
        "                                                         onclick=\"sub_menu_add(this)\">\n" +
        "                                                        <i class=\"mdi mdi-plus mdi-18px\"></i>\n" +
        "                                                    </div>\n" +
        "                                                </div>\n" +
        "                                            </div>\n" +
        "                                        </div>" +
        "" );

    var menuPlusLen = $( ".weixin-menu" ).find( 'div[class="menu_item plus"]' ).length;
    var menuBottomLen = $( ".weixin-menu" ).find( 'div[class="menu_bottom"]' ).length;
    if (menuBottomLen < 3 && menuPlusLen == 0) {
        $( ".weixin-menu" ).append( "<div class=\"menu_bottom\">\n" +
            "<div class=\"menu_item plus\" onclick=\"menu_add_plus(this)\">\n" +
            "<i class=\"mdi mdi-plus mdi-18px\"></i>" +
            "</div>" );
    }
    add_menu_data();
}

// 保存当前菜单
function saveCurrentMenu() {
    // 判断是否是一级菜单 true:一级菜单，false:二级菜单
    var flag = $( ".weixin-menu" ).find( '.menu_active' ).hasClass( "menu_item" );

    if ($( ".weixin-menu" ).find( '.menu_active' ).length == 0) {
        $MB.n_warning( "您没有选中任何菜单！" );
        return;
    }
    var index = $( ".weixin-menu" ).find( '.menu_active' ).parent().index();
    if (flag) {
        var data = menu_data[index];
        data = getMenuOtherInfo( data );
        data['name'] = $( "#menuName" ).val();
    } else {
        var parentIndex = $( ".weixin-menu" ).find( '.menu_active' ).parent().parent().parent().index();
        var subButtons = menu_data[parentIndex]['sub_button'];
        index = Math.abs( subButtons.length - index ) - 1;
        var tmp = subButtons[index];
        tmp['name'] = $( "#menuName" ).val();
        tmp['type'] = 'click';
        tmp = getMenuOtherInfo( tmp );
        subButtons[index] = tmp;
        menu_data[parentIndex]['sub_button'] = subButtons;
    }
    console.log( menu_data );
}

// 重新封装菜单数据
function getMenuOtherInfo(data) {
    var menuType = $( "input[name='menuType']:checked" ).val();
    if (menuType === '0') {// 发消息
        data['type'] = 'media_id';
        if (media_type === 'text') {

        }
        if (media_type === 'image') {
            var selected = $( "#imageTable" ).bootstrapTable( 'getSelections' );
            if (selected.length == 0) {
                $MB.n_warning( "至少选择一条记录！" );
                return;
            }
            data['media_id'] = selected[0]['mediaId'];
        }
        if (media_type === 'voice') {
            var selected = $( "#voiceTable" ).bootstrapTable( 'getSelections' );
            if (selected.length == 0) {
                $MB.n_warning( "至少选择一条记录！" );
                return;
            }
            data['media_id'] = selected[0]['mediaId'];
        }
        if (media_type === 'video') {
            var selected = $( "#videoTable" ).bootstrapTable( 'getSelections' );
            if (selected.length == 0) {
                $MB.n_warning( "至少选择一条记录！" );
                return;
            }
            data['media_id'] = selected[0]['mediaId'];
        }
        if (media_type === 'news') {
            var selected = $( "#imageTextTable" ).bootstrapTable( 'getSelections' );
            if (selected.length == 0) {
                $MB.n_warning( "至少选择一条记录！" );
                return;
            }
            data['type'] = "view_limited";
            data['media_id'] = selected[0]['mediaId'];
        }
    } else if (menuType === '1') {// 跳转网页
        data['type'] = 'view';
        data['url'] = $( "#url" ).val();
    } else if (menuType === '2') {// 跳转小程序

    }
    return data;
}

function saveAllMenu() {
    $MB.confirm( {
        title: '提示：',
        content: '确认提交当前所有设置吗？旧的数据将会被覆盖'
    }, function () {
        var data = {menu: {button: menu_data}};
        $.post( "/wxMenu/addMenu", {data: encodeURIComponent( JSON.stringify( data ) )}, function (r) {
            if (r.code === "200") {
                $MB.n_success( "菜单保存成功！" );
            }
        } );
    } );
}

// 初始化微信菜单数据
function menuInit() {
    $.get( "/wxMenu/getMenuList", {}, function (r) {
        if (r.code === "200") {
            var code = "";
            var data = r.msg['menu']['buttons'];
            data.forEach( (v, k) => {
                var subMenuCode = "<div class=\"submenu\">";
                var subMenuButtons = v['subButtons'];
                if (subMenuButtons.length > 0) {
                    subMenuButtons.forEach( (v1, k1) => {
                        subMenuCode += "" +
                            "<div class=\"subtitle menu_bottom\">\n" +
                            "<div class=\"menu_subItem\" style=\"cursor:pointer;\" onclick=\"sub_menu(this)\">\n" +
                            ""+ v1['name'] +"\n" +
                            "</div>\n" +
                            "</div>";
                    } );

                    if(subMenuButtons.length < 5) {
                        subMenuCode += "<div class=\"subtitle menu_bottom\">\n" +
                            "<div class=\"menu_subItem\" style=\"cursor:pointer;\"\n" +
                            "onclick=\"sub_menu_add(this)\">\n" +
                            "<i class=\"mdi mdi-plus mdi-18px\"></i>\n" +
                            "</div>\n" +
                            "</div>";
                    }
                } else {
                    subMenuCode += "<div class=\"subtitle menu_bottom\">\n" +
                        "<div class=\"menu_subItem\" style=\"cursor:pointer;\"\n" +
                        "onclick=\"sub_menu_add(this)\">\n" +
                        "<i class=\"mdi mdi-plus mdi-18px\"></i>\n" +
                        "</div>\n" +
                        "</div>";
                }
                subMenuCode += "</div>";
                code += "<div class=\"menu_bottom\">\n" +
                    "<div class=\"menu_item\" onclick=\"menu_add(this)\">\n" +
                    "" + v['name'] + "\n" +
                    "</div>\n" +
                    "" + subMenuCode + "\n" +
                    "</div>";
            } );
            if(data.length < 3) {
                code += "<div class=\"menu_bottom\">\n" +
                    "<div class=\"menu_item plus\" onclick=\"menu_add_plus(this)\">\n" +
                    "<i class=\"mdi mdi-plus mdi-18px\"></i>" +
                    "</div>";
            }
            $( "#weixinMenu" ).html( '' ).append( code );
            $("#weixinMenu").find(".submenu").attr("style", "display:none;");
        }
    } );
}

