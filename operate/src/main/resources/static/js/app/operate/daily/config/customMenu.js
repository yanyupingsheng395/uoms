let obj;
let mId;
let tempObj;
let button;
let objp;
let objs;
let ix;
let menu = '<div class="custom-menu-view__menu"><div class="text-ellipsis"></div></div>';
let customBtns = $( '.custom-menu-view__footer__right' );
let setMenuText;
let colIndex;
let radios;
let delClick;
let customEl = '<div class="custom-menu-view__menu"><div class="text-ellipsis">新建菜单</div></div>'
let customUl = '<ul class="custom-menu-view__menu__sub"><li class="custom-menu-view__menu__sub__add"><div class="text-ellipsis"><i class="glyphicon glyphicon-plus text-info"></i></div></li></ul>';
let customLi = '<li class="custom-menu-view__menu__sub__add"><div class="text-ellipsis">新建子菜单</div></li>';

function parents(param) {
    this.name = param;
    this.sub_button = [];
}

function subs(param) {
    this.name = param;
}

function showMenu() {
    if (button.length == 1) {
        appendMenu( button.length );
        showBtn();
        $( '.custom-menu-view__menu' ).css( {width: '50%',} );
    }
    if (button.length == 2) {
        appendMenu( button.length );
        showBtn();
        $( '.custom-menu-view__menu' ).css( {width: '33.3333%',} );
    }
    if (button.length == 3) {
        appendMenu( button.length );
        showBtn();
        $( '.custom-menu-view__menu' ).css( {width: '33.3333%',} );
    }
    for (var b = 0; b < button.length; b++) {
        $( '.custom-menu-view__menu' )[b].setAttribute( 'alt', b );
    }
}

function showBtn() {
    for (var i = 0; i < button.length; i++) {
        var text = button[i].name;
        var list = document.createElement( 'ul' );
        list.className = "custom-menu-view__menu__sub";
        $( '.custom-menu-view__menu' )[i].childNodes[0].innerHTML = text;
        $( '.custom-menu-view__menu' )[i].appendChild( list );
        for (var j = 0; j < button[i].sub_button.length; j++) {
            var text = button[i].sub_button[j].name;
            var li = document.createElement( "li" );
            var tt = document.createTextNode( text );
            var div = document.createElement( 'div' );
            li.className = 'custom-menu-view__menu__sub__add';
            li.id = 'sub_' + i + '_' + j;
            div.className = "text-ellipsis";
            div.appendChild( tt );
            li.appendChild( div );
            $( '.custom-menu-view__menu__sub' )[i].appendChild( li );
        }
        var ulBtnL = button[i].sub_button.length;
        var iLi = document.createElement( "li" );
        var ii = document.createElement( 'i' );
        var iDiv = document.createElement( "div" );
        ii.className = "glyphicon glyphicon-plus text-info";
        iDiv.className = "text-ellipsis";
        iLi.className = 'custom-menu-view__menu__sub__add';
        iDiv.appendChild( ii );
        iLi.appendChild( iDiv );
        if (ulBtnL < 5) {
            $( '.custom-menu-view__menu__sub' )[i].appendChild( iLi );
        }
    }
}

function appendMenu(num) {
    var menuDiv = document.createElement( 'div' );
    var mDiv = document.createElement( 'div' );
    var mi = document.createElement( 'i' );
    mi.className = 'glyphicon glyphicon-plus text-info iBtn';
    mDiv.className = 'text-ellipsis';
    menuDiv.className = 'custom-menu-view__menu';
    mDiv.appendChild( mi );
    menuDiv.appendChild( mDiv );
    switch (num) {
        case 1:
            customBtns.append( menu );
            customBtns.append( menuDiv );
            break;
        case 2:
            customBtns.append( menu );
            customBtns.append( menu );
            customBtns.append( menuDiv );
            break;
        case 3:
            customBtns.append( menu );
            customBtns.append( menu );
            customBtns.append( menu );
            break;
    }
}

function addMenu() {
    var menuI = '<div class="custom-menu-view__menu"><div class="text-ellipsis"><i class="glyphicon glyphicon-plus text-info iBtn"></i></div></div>';
    var sortIndex = true;
    customBtns.append( menuI );
    var customFirstBtns = $( '.custom-menu-view__menu' );
    var firstBtnsLength = customFirstBtns.length;
    if (firstBtnsLength <= 1) {
        customFirstBtns.css( {width: '100%',} )
    }
}

function setSubText() {
    var actived = $( '.custom-menu-view__menu__sub__add' ).hasClass( 'subbutton__actived' );
    var activedTxt = $( '.subbutton__actived' ).text();
    if (actived) {
        setInput( activedTxt );
        updateTit( activedTxt );
        radios[0].checked = true;
        $( '#editMsg' ).show();
        $( '#editPage' ).hide();
        $( '.msg-context__item' ).show();
        $( '.msg-template' ).hide();
    }
}

function setLiId() {
    var prev = $( '.custom-menu-view__menu' )[colIndex].getElementsByTagName( 'i' )[0].parentNode.parentNode.previousSibling;
    var divText = prev.childNodes[0].innerHTML;
    if (typeof (button[colIndex].sub_button) == "undefined") {
        var sub_button = {"sub_button": []};
        button[colIndex].append( sub_button );
    }
    button[colIndex].sub_button.push( new subs( divText ) );
    var id = button[colIndex].sub_button.length - 1;
    prev.setAttribute( 'id', 'sub_' + colIndex + '_' + id );
    $( '.custom-menu-view__footer__right' ).find( '.subbutton__actived' ).removeClass( 'subbutton__actived' );
    $( '.custom-menu-view__menu' ).eq( colIndex ).find( 'i' ).parent().parent().prev().addClass( 'subbutton__actived' );
}

function setInput(val) {
    $( 'input[name="custom_input_title"]' ).val( val );
}

function updateTit(text) {
    $( '#cm-tit' ).html( text );
}

function sortable(m, sortIndex) {
    if (sortIndex) {
        Sortable.create( document.getElementById( 'menuStage_2_' + m ), {animation: 300, disabled: false,} );
    } else {
        var el = document.getElementById( 'menuStage_2_' + m );
        var sortable = Sortable.create( el, {disabled: true,} );
        sortable.destroy();
    }
}

function delElement() {
    var msgTemp = $( '.msg-template' );
    var delEl = '<span class="msg-panel__del del-tuwen">删除</span>';
    msgTemp.append( delEl );
    if (msgTemp.find( 'span' ).length == 0) {
        msgTemp.append( delEl );
    }
};

function saveAjax() {
    layer.msg( "创建成功！" );
}

function deleteMenu() {
    layer.msg( "删除成功！" );
    obj = {"menu": {button: []}};
    $( '.custom-menu-view__menu' ).remove();
    menuCreate( obj );
    $( ".msg-tab_item" ).removeClass( 'on' );
    $( "#imgtextLi" ).addClass( "on" );
}

function menuCreate(obj) {
    $( '.iBtn' ).parent().unbind( "click" );
    $( '.reminder' ).unbind( "click" );
    $( '.text-ellipsis' ).unbind( "click" );
    $( 'li>.text-ellipsis>i' ).unbind( "click" );
    $( 'input[name="url"]' ).unbind( "keyup" );
    $( 'input[name="custom_input_title"]' ).unbind( "keyup" );
    $( '.msg-panel__tab>li' ).unbind( "click" );
    $( '#selectModal .modal-body .panel' ).unbind( "click" );
    $( '#selectModal .ensure' ).unbind( "click" );
    $( '#delMenu' ).unbind( "click" );
    $( '#saveBtns' ).unbind( "click" );
    customBtns.unbind( "click" );
    mId = null;
    tempObj = {};
    if (typeof (obj.menu) != "undefined") {
        button = obj.menu.button;
    } else {
        button = [];
    }
    if (obj.errcode) {
        $( '#abnormalModal' ).modal( 'show' );
    }
    objp = new parents();
    objs = new subs();
    if (typeof (button) != "undefined") {
        ix = button.length;
    } else {
        ix = 0;
    }
    if (typeof (button) != "undefined" && button.length > 0) {
        showMenu();
        $( '.cm-edit-after' ).hide();
    } else {
        addMenu();
        $( '.cm-edit-before' ).siblings().hide();
    }
    $( '.custom-menu-view__footer__right' ).off( 'click' ).on( 'click', ".iBtn", function () {
        var dom = $( this ).parent( ".text-ellipsis" );
        if ($( dom ).siblings( "ul" ).length == 0) {
            ix = $( '.custom-menu-view__menu[alt]' ).size();
            var num = $( '.custom-menu-view__footer__right' ).find( '.custom-menu-view__menu' ).length;
            var ulNum = $( dom ).parents( '.custom-menu-view__menu' ).prev().find( '.custom-menu-view__menu__sub' ).length;
            ix++;
            if (ix < 4) {
                $( dom ).parent().before( customEl );
                $( dom ).parent().prev().append( customUl );
                $( '.custom-menu-view__footer__right' ).find( '.subbutton__actived' ).removeClass( 'subbutton__actived' );
                $( dom ).parent().prev().addClass( 'subbutton__actived' );
                var buttonIndex = $( dom ).parents( '.custom-menu-view__menu' ).index() - 1;
                $( '.custom-menu-view__menu' ).eq( buttonIndex ).on( 'click', (function (buttonIndex) {
                    var txt = $( '.custom-menu-view__menu' ).eq( buttonIndex ).text();
                    setMenuText( txt );
                })( buttonIndex ) );
                if (ix == 1) {
                    $( '.custom-menu-view__menu' ).css( {width: '50%'} );
                    $( '.custom-menu-view__menu' )[ix - 1].setAttribute( 'alt', ix - 1 );
                }
                if (ix == 2) {
                    $( '.custom-menu-view__menu' ).css( {width: '33.3333%'} );
                    $( '.custom-menu-view__menu' )[ix - 1].setAttribute( 'alt', ix - 1 );
                }
                var divText = $( dom ).parent().prev().find( '.text-ellipsis' ).text();
                button.push( new parents( divText ) );
            }
            if (ix == 3) {
                $( dom ).parents( '.custom-menu-view__menu' ).remove();
                $( dom ).parent().append( customUl );
                var index = ix - 1
                if ($( ".custom-menu-view__menu" ).eq( ix - 1 ).children( ".text-ellipsis" ).children( ".iBtn" ).length == 0) {
                    $( '.custom-menu-view__menu' )[ix - 1].setAttribute( 'alt', ix - 1 );
                }
            }
            $( '.cm-edit-after' ).show().siblings().hide();
        }
    } );
    setMenuText = function (value) {
        setInput( value );
        updateTit( value );
        radios[0].checked = true;
        $( '#editMsg' ).show();
        $( '#editPage' ).hide();
        $( '.msg-context__item' ).show();
        $( '.msg-template' ).hide();
    }
    customBtns.on( 'click', 'li>.text-ellipsis>i', function () {
        $( '.msg-panel__del' ).on( 'click', delClick );
        colIndex = $( this ).parents( '.custom-menu-view__menu' ).prevAll().length;
        var liNum = $( this ).parents( '.custom-menu-view__menu' ).find( 'li' ).length;
        if (liNum <= 1) {
            $( '#reminderModal' ).modal( 'show' );
        } else {
            if (liNum < 6) {
                $( this ).parent().parent().before( customLi );
                setLiId();
            }
            if (liNum == 5) {
                $( this ).parents( 'li' ).remove();
            }
        }
        $( '#radioGroup' ).show();
        setSubText()
    } );
    $( '.reminder' ).click( function () {
        var ul = $( '.custom-menu-view__menu' )[colIndex].getElementsByTagName( 'ul' )[0];
        var li = document.createElement( 'li' );
        var div = document.createElement( 'div' );
        var Text = document.createTextNode( '新建子菜单' );
        li.className = "custom-menu-view__menu__sub__add";
        div.className = "text-ellipsis";
        div.appendChild( Text );
        li.appendChild( div );
        ul.insertBefore( li, ul.childNodes[0] );
        setLiId();
        delete button[colIndex].type;
        delete button[colIndex].media_id;
        delete button[colIndex].url;
        $( '#reminderModal' ).modal( 'hide' );
        setSubText();
    } );
    imageText();
    customBtns.on( 'click', '.text-ellipsis', function () {
        $( '.cm-edit-after' ).show().siblings().hide();
        if ($( this ).parent().attr( 'id' ) || $( this ).parent().attr( 'alt' )) {
            $( this ).parents( '.custom-menu-view__footer__right' ).find( '.subbutton__actived' ).removeClass( 'subbutton__actived' );
            $( this ).parent().addClass( 'subbutton__actived' );
        }
        var buttonIndex = $( this ).parents( '.custom-menu-view__menu' ).prevAll().length;
        if ($( '.msg-context__item' ).is( ':hidden' )) {
            $( '.msg-template' ).show();
        } else if ($( '.msg-context__item' ).is( ':visible' )) {
            $( '.msg-template' ).hide();
        }
        if ($( this ).parent().attr( 'alt' )) {
            if ($( '.custom-menu-view__menu' ).hasClass( 'subbutton__actived' )) {
                var current = $( '.subbutton__actived' );
                var alt = current.attr( 'alt' );
                var lis = current.find( 'ul>li' );
                setInput( button[buttonIndex].name );
                updateTit( button[buttonIndex].name );
                if (lis.length > 1) {
                    $( '#editMsg' ).hide();
                    $( '#editPage' ).hide();
                    $( '#radioGroup' ).hide();
                } else {
                    if (button[buttonIndex].type == 'media_id') {
                        radios[0].checked = true;
                        switch (button[buttonIndex].ctype) {
                            case 'image':
                                $( "#imgLi" ).trigger( 'click' );
                                break;
                            case 'voice':
                                $( "#voice" ).trigger( 'click' );
                                break;
                            case 'video':
                                $( "#video" ).trigger( 'click' );
                                break;
                            default:
                                $( "#imgtextLi" ).trigger( "click" );
                        }
                        $( '#editMsg' ).show();
                        $( '#editPage' ).hide();
                        $( '#radioGroup' ).show();
                        subKey = button[buttonIndex].media_id;
                        $( '.msg-template' ).html( $( '#' + subKey ).html() );
                        delElement();
                        $( '.msg-panel__del' ).on( 'click', delClick );
                        $( '.msg-template' ).html( tempObj[button[buttonIndex].media_id] );
                    } else if (button[buttonIndex].type == 'view') {
                        $( 'input[name="url"]' ).val( button[buttonIndex].url );
                        radios[1].checked = true;
                        $( '#editMsg' ).hide();
                        $( '#editPage' ).show();
                        $( '#radioGroup' ).show();
                    } else if (!button[buttonIndex].type) {
                        radios[0].checked = true;
                        $( '#editMsg' ).show();
                        $( '#editPage' ).hide();
                        $( '#radioGroup' ).show();
                    }
                    if (button[buttonIndex].media_id) {
                        $( '.msg-context__item' ).hide();
                        $( '.msg-template' ).show();
                    } else {
                        $( '.msg-context__item' ).show();
                        $( '.msg-template' ).hide();
                    }
                }
            }
        }
        if ($( this ).parent().attr( 'id' )) {
            var subIndex = $( this ).parent( "li" ).prevAll().length;
            var subText = button[buttonIndex].sub_button[subIndex].name;
            var subUrl = button[buttonIndex].sub_button[subIndex].url;
            var subType = button[buttonIndex].sub_button[subIndex].type;
            var subKey = button[buttonIndex].sub_button[subIndex].media_id;
            if ($( '.custom-menu-view__menu__sub__add' ).hasClass( 'subbutton__actived' )) {
                setInput( subText );
                updateTit( subText );
                $( '#radioGroup' ).show();
                if (subType == 'media_id') {
                    radios[0].checked = true;
                    switch (button[buttonIndex].sub_button[subIndex].ctype) {
                        case 'image':
                            $( "#imgLi" ).trigger( 'click' );
                            break;
                        case 'voice':
                            $( "#voice" ).trigger( 'click' );
                            break;
                        case 'video':
                            $( "#video" ).trigger( 'click' );
                            break;
                        default:
                            $( "#imgtextLi" ).trigger( "click" );
                    }
                    $( '#editMsg' ).show();
                    $( '#editPage' ).hide();
                    $( '.msg-template' ).html( $( '#' + subKey ).html() );
                    delElement();
                    $( '.msg-panel__del' ).on( 'click', delClick );
                    $( '.msg-template' ).html( tempObj[subKey] );
                } else if (subType == 'view') {
                    radios[1].checked = true;
                    $( '#editMsg' ).hide();
                    $( '#editPage' ).show();
                    $( 'input[name="url"]' ).val( subUrl );
                } else if (!subType) {
                    radios[0].checked = true;
                    $( '#editMsg' ).show();
                    $( '#editPage' ).hide();
                    $( 'input[name="url"]' ).val( '' );
                }
                if (subKey) {
                    $( '.msg-context__item' ).hide();
                    $( '.msg-template' ).show();
                } else {
                    $( '.msg-context__item' ).show();
                    $( '.msg-template' ).hide();
                }
            }
        }
        $( '.msg-panel__del' ).on( 'click', delClick );
    } );
    $( 'input[name="custom_input_title"]' ).keyup( function () {
        var val = $( this ).val();
        var current = $( '.subbutton__actived' );
        if ($( '.custom-menu-view__menu__sub__add' ).hasClass( 'subbutton__actived' )) {
            var sub_row = $( ".subbutton__actived" ).parents( '.custom-menu-view__menu' ).prevAll().length;
            var sub_col = $( '.subbutton__actived' ).prevAll().length;
            button[sub_row].sub_button[sub_col].name = val;
            current.find( '.text-ellipsis' ).text( val );
            updateTit( val );
        } else if ($( '.custom-menu-view__menu' ).hasClass( 'subbutton__actived' )) {
            var alt = current.attr( 'alt' );
            button[alt].name = val;
            current.children( '.text-ellipsis' ).text( val );
            updateTit( val )
        }
    } );
    $( 'input[name="url"]' ).keyup( function () {
        var val = $( this ).val();
        var current = $( '.subbutton__actived' );
        if ($( '.custom-menu-view__menu__sub__add' ).hasClass( 'subbutton__actived' )) {
            var sub_row = $( ".subbutton__actived" ).parents( '.custom-menu-view__menu' ).prevAll().length;
            var sub_col = $( '.subbutton__actived' ).prevAll().length;
            button[sub_row].sub_button[sub_col].url = val;
            button[sub_row].sub_button[sub_col].type = 'view';
            if (button[sub_row].sub_button[sub_col].url == '') {
                delete button[sub_row].sub_button[sub_col].url;
            }
        } else if ($( '.custom-menu-view__menu' ).hasClass( 'subbutton__actived' )) {
            var alt = current.attr( 'alt' );
            button[alt].url = val;
            button[alt].type = 'view';
            if (button[alt].url == '') {
                delete button[alt].url;
            }
        }
    } );
    $( '.msg-panel__tab>li' ).click( function () {
        $( '.msg-panel__tab>li' ).eq( $( this ).index() ).addClass( 'on' ).siblings().removeClass( 'on' );
        $( '.msg-panel__context' ).eq( $( this ).index() ).removeClass( 'hide' ).siblings().addClass( 'hide' )
    } );
    radios = document.getElementsByName( "radioBtn" );
    for (var n = 0; n < radios.length; n++) {
        radios[n].index = n;
        radios[n].onchange = function () {
            if (radios[this.index].checked == true) {
                if (radios[this.index].value == 'link') {
                    $( '#editMsg' ).hide();
                    $( '#editPage' ).show();
                } else {
                    $( '#editMsg' ).show();
                    $( '#editPage' ).hide();
                }
            }
        };
    }
    delClick = function () {
        $( '.msg-template' ).empty();
        $( '.msg-context__item' ).show();
        $( '.mask-bg' ).hide();
        var current = $( '.subbutton__actived' );
        if ($( '.custom-menu-view__menu__sub__add' ).hasClass( 'subbutton__actived' )) {
            var sub_col = $( ".subbutton__actived" ).prevAll().length;
            var sub_row = $( ".subbutton__actived" ).parents( '.custom-menu-view__menu' ).prevAll().length;
            delete button[sub_row].sub_button[sub_col].media_id;
            delete button[sub_row].sub_button[sub_col].type;
        } else if ($( '.custom-menu-view__menu' ).hasClass( 'subbutton__actived' )) {
            var alt = $( ".subbutton__actived" ).prevAll().length;
            delete button[alt].media_id;
            delete button[alt].type;
        }
    };
    $( '#delMenu' ).click( function () {
        var is_Actived = $( '.custom-menu-view__menu' ).hasClass( 'subbutton__actived' );
        var is_actived = $( '.custom-menu-view__menu__sub__add' ).hasClass( 'subbutton__actived' );
        var rowIndex = 0;
        var colIndex = 0;
        var liLength = $( ".subbutton__actived" ).parents( '.custom-menu-view__menu__sub' );
        if (is_Actived) {
            rowIndex = $( ".subbutton__actived" ).prevAll().length;
            $( '.subbutton__actived' ).remove();
            button.splice( rowIndex, 1 );
            let buttonLength = $( ".custom-menu-view__menu:last[alt]" ).length;
            let isTrue = $( ".custom-menu-view__menu .text-ellipsis .iBtn" );
            if (buttonLength == 1) {
                $( ".custom-menu-view__menu" ).css( "width", '33.3333%' );
            } else if (buttonLength == 0) {
                $( ".custom-menu-view__menu" ).css( "width", '50%' );
            }
            if ($( ".custom-menu-view__footer__right" ).children().length == 1) {
                $( ".custom-menu-view__menu" ).css( "width", '100%' );
            }
            let divHtml = '<div class="custom-menu-view__menu"><div class="text-ellipsis"><i class="glyphicon glyphicon-plus text-info iBtn"></i></div></div>';
            if (!isTrue.length && isTrue.length == 0) {
                $( ".custom-menu-view__footer__right" ).last().append( divHtml );
            }
        } else if (is_actived) {
            rowIndex = $( ".subbutton__actived" ).parents( '.custom-menu-view__menu' ).prevAll().length;
            colIndex = $( '.subbutton__actived' ).prevAll().length;
            $( '.subbutton__actived' ).remove();
            button[rowIndex].sub_button.splice( colIndex, 1 );
            var l = $( liLength ).find( 'li' ).length;
            var add_button = $( liLength ).find( "i" );
            if (l < 5) {
                if (!add_button.length) {
                    let liHtml = '<li class="custom-menu-view__menu__sub__add"><div class="text-ellipsis"><i class="glyphicon glyphicon-plus text-info"></i></div></li>';
                    $( liLength ).find( "li:last" ).after( liHtml );
                }
            }
        }
        $( '.cm-edit-before' ).show().siblings().hide();
        updateTit( '' );
        setInput( '' );
        $( 'input[name="url"]' ).val( '' );
        $( '.msg-template' ).children().remove();
        $( '.msg-context__item' ).show();
    } )
    $( '#saveBtns' ).click( function () {
        let url = null;
        let strRegex = '(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]';
        let re = new RegExp( strRegex );
        let flag;
        for (let i = 0; i < button.length; i++) {
            if (button[i].sub_button.length) {
                for (let j = 0; j < button[i].sub_button.length; j++) {
                    if (button[i].sub_button[j].hasOwnProperty( 'url' )) {
                        url = button[i].sub_button[j].url;
                        if (!re.test( url )) {
                            layer.msg( "请输入正确的url地址！" );
                            flag = false;
                        }
                    } else if (button[i].sub_button[j].hasOwnProperty( 'media_id' )) {
                        flag = true;
                    } else {
                        flag = false;
                        layer.msg( "菜单内容不能为空！" );
                    }
                }
            } else {
                if (button[i].hasOwnProperty( 'url' )) {
                    url = button[i].url;
                    if (!re.test( url )) {
                        layer.msg( "请输入正确的url地址！" );
                        flag = false;
                    }
                } else if (button[i].hasOwnProperty( 'media_id' )) {
                    flag = true;
                } else {
                    flag = false;
                    layer.msg( "菜单内容不能为空！" );
                }
            }
        }
        if (flag) {
            saveAjax();
        }
    } );
}

function imageText() {
    let responseStr = {
        "code": 0,
        "list": [{
            "groupId": "20190603172051wxc2c420fe6bf447fe",
            "groupList": [{
                "thumb_media_id": "5iDtROEQOkUtgnfeqz4pYNCVVCs3RPcU2KcfkO97lao",
                "create_time": "2019-06-03 17:21:56",
                "author": "阿萨德发",
                "content_source_url": "www.baidu.com",
                "title": "去玩儿",
                "news_id": 123,
                "content": "<p>发的</p>",
                "url": "img/t.png",
                "group_id": "20190603172051wxc2c420fe6bf447fe",
                "digest": "服务器而",
                "show_cover_pic": 1,
                "media_id": "5iDtROEQOkUtgnfeqz4pYGihEeD9ZbJ4TIWJYNPHiRk",
                "app_id": "wxc2c420fe6bf447fe",
                "article_idx": 0,
                "status": 1
            }]
        }, {
            "groupId": "20190611161414wxc2c420fe6bf447fe",
            "groupList": [{
                "thumb_media_id": "5iDtROEQOkUtgnfeqz4pYNI0i2m9i7niS4oXAbh878k",
                "create_time": "2019-06-11 16:18:32",
                "author": "燿一",
                "content_source_url": "www.baidu.com",
                "title": "阿斯顿",
                "news_id": 127,
                "content": "<p>而且我我如果</p>",
                "url": "img/t.png",
                "group_id": "20190611161414wxc2c420fe6bf447fe",
                "digest": "大额群若",
                "show_cover_pic": 1,
                "media_id": "5iDtROEQOkUtgnfeqz4pYOQMRUYSaDtfimimCGYYQVg",
                "app_id": "wxc2c420fe6bf447fe",
                "article_idx": 0,
                "status": 1
            }, {
                "thumb_media_id": "5iDtROEQOkUtgnfeqz4pYNeYXGRAE3-cN92Zv56lzi4",
                "create_time": "2019-06-11 16:19:12",
                "author": "清任务二",
                "content_source_url": "www.baidu.com",
                "title": "梅赛德斯奔驰",
                "news_id": 128,
                "content": "<p>小胡</p>",
                "url": "img/t.png",
                "group_id": "20190611161414wxc2c420fe6bf447fe",
                "digest": "得起",
                "show_cover_pic": 1,
                "media_id": "5iDtROEQOkUtgnfeqz4pYOQMRUYSaDtfimimCGYYQVg",
                "app_id": "wxc2c420fe6bf447fe",
                "article_idx": 1,
                "status": 1
            }, {
                "thumb_media_id": "5iDtROEQOkUtgnfeqz4pYPSc3ObOPTZSTix_VcWVXOY",
                "create_time": "2019-06-11 16:19:53",
                "author": "战马.",
                "content_source_url": "www.baidu.com",
                "title": "战马",
                "news_id": 129,
                "content": "<p>不在线</p>",
                "url": "img/t.png",
                "group_id": "20190611161414wxc2c420fe6bf447fe",
                "digest": "安定区无",
                "show_cover_pic": 1,
                "media_id": "5iDtROEQOkUtgnfeqz4pYOQMRUYSaDtfimimCGYYQVg",
                "app_id": "wxc2c420fe6bf447fe",
                "article_idx": 2,
                "status": 1
            }]
        }]
    };
    $( "#imgTextAdd" ).empty();
    let html = '';
    for (let i = 0; i < responseStr.list.length; i++) {
        let divHtml1 = '';
        let divHtml2 = '';
        if (i % 3 == 0) {
            html += '<div style="display: flex;">';
        }
        let imgUrl = responseStr.list[i].groupList[0].url;
        html += '<div id=' + JSON.stringify( responseStr.list[i].groupList[0].media_id ) + ' class="col-xs-4">' +
            '<div class="panel panel-default">' +
            '<div class=" newmodul">' +
            '<div class="panel-heading msg-date">' +
            '<div class=" weui-panel weui-panel_access">' +
            '<div class="weui-panel__hd" style="height: 200px;background-image:url(' + imgUrl + ');background-size: 100% 100%;">' +
            '<span class="newtitle">' + responseStr.list[i].groupList[0].title + '</span>' +
            '</div>';
        let circle = '';
        if (responseStr.list[i].groupList.length > 1) {
            html += '<div class="weui-panel__bd">';
            for (let j = 1; j < responseStr.list[i].groupList.length; j++) {
                circle = circle +
                    '<a href="javascript:void(0);" class="weui-media-box weui-media-box_appmsg">' +
                    '<div class="weui-media-box__bd">' +
                    '<h4 class="weui-media-box__title">' + responseStr.list[i].groupList[j].title + '</h4>' +
                    '</div>' +
                    '<div class="weui-media-box__hd">' +
                    '<img class="weui-media-box__thumb" src="' + responseStr.list[i].groupList[j].url + '" alt="">' +
                    '</div>' +
                    '</a>'
            }
            html += circle + '</div>';
        }
        html += '</div></div></div><div class="mask-bg"><div class="mask-icon"><i class="icon-ok"></i></div></div></div></div>';
        if (i % 3 == 2) {
            html += '</div>'
        }
    }
    $( "#imgTextAdd" ).append( html );
    modalAddClick();
    let delHtml = '<span class="msg-panel__del del-tuwen">删除</span>';
    if ($( ".subbutton__actived" ).attr( 'alt' )) {
        let row = $( ".subbutton__actived" ).prevAll().length;
        if (button[row].ctype != null) {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
            $( '.mask-bg' ).hide();
        } else if (typeof (button[row].ctype) == 'undefined') {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
        } else {
            var subKey = button[row].media_id;
            if (typeof (subKey) == 'undefined') {
                $( '.msg-template' ).empty();
                $( '.msg-context__item' ).css( "display", "block" );
                $( '.mask-bg' ).hide();
            } else {
                $( '.msg-template' ).html( $( '#' + subKey ).html() );
                $( '.msg-template' ).append( delHtml );
                $( '.msg-panel__del' ).on( 'click', delClick );
                $( '.msg-context__item' ).css( "display", "none" );
            }
        }
    } else if ($( ".subbutton__actived" ).attr( 'id' )) {
        let row = $( ".subbutton__actived" ).parents( '.custom-menu-view__menu' ).prevAll().length;
        let clo = $( ".subbutton__actived" ).prevAll().length;
        if (typeof (button[row].sub_button[clo].ctype) == 'undefined') {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
        } else if (button[row].sub_button[clo].ctype != null) {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
            $( '.mask-bg' ).hide();
        } else {
            var subKey = button[row].sub_button[clo].media_id;
            if (typeof (subKey) == 'undefined') {
                $( '.msg-template' ).empty();
                $( '.msg-context__item' ).css( "display", "block" );
                $( '.mask-bg' ).hide();
            } else {
                $( '.msg-template' ).html( $( '#' + subKey ).html() );
                $( '.msg-template' ).append( delHtml );
                $( '.msg-panel__del' ).on( 'click', delClick );
                $( '.msg-context__item' ).css( "display", "none" );
            }
        }
    }
}

function picture() {
    let responseStr = [{
        "materialId": null,
        "mediaId": "5iDtROEQOkUtgnfeqz4pYHZDtiZlnGS-54z-4H8ykCw",
        "url": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589199406386&di=d20249f56d7a9f8ff9bf5be905fe7399&imgtype=0&src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201408%2F05%2F222353wu5y5mzv6mprxvhn.jpg",
        "type": null,
        "urlExpireTime": null,
        "createTime": null,
        "modifyTime": null,
        "createUserId": null,
        "modifyUserId": null,
        "description": null,
        "appId": null,
        "expireType": null,
        "voiceType": null,
        "title": null,
        "duration": null
    }, {
        "materialId": null,
        "mediaId": "5iDtROEQOkUtgnfeqz4pYFpNwEd0c0Fh-qf0t_nhuJI",
        "url": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589199511437&di=763b3fe377470d9a7c5e7f99b1af8e44&imgtype=0&src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201611%2F19%2F171810hiiuslmdoffunmfi.jpg",
        "type": null,
        "urlExpireTime": null,
        "createTime": null,
        "modifyTime": null,
        "createUserId": null,
        "modifyUserId": null,
        "description": null,
        "appId": null,
        "expireType": null,
        "voiceType": null,
        "title": null,
        "duration": null
    }, {
        "materialId": null,
        "mediaId": "5iDtROEQOkUtgnfeqz4pYH730XMtOP0GDkHZK14u06Q",
        "url": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589199439414&di=d91c30073c35ab108c64599b272196ad&imgtype=0&src=http%3A%2F%2Fimg8.zol.com.cn%2Fbbs%2Fupload%2F13897%2F13896378.jpg",
        "type": null,
        "urlExpireTime": null,
        "createTime": null,
        "modifyTime": null,
        "createUserId": null,
        "modifyUserId": null,
        "description": null,
        "appId": null,
        "expireType": null,
        "voiceType": null,
        "title": null,
        "duration": null
    }, {
        "materialId": null,
        "mediaId": "5iDtROEQOkUtgnfeqz4pYNCVVCs3RPcU2KcfkO97lao",
        "url": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589199439407&di=c5dcb71f27493444d69644574ab28167&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fwallpaper%2F1205%2F22%2Fc0%2F11704482_1337657341929.jpg",
        "type": null,
        "urlExpireTime": null,
        "createTime": null,
        "modifyTime": null,
        "createUserId": null,
        "modifyUserId": null,
        "description": null,
        "appId": null,
        "expireType": null,
        "voiceType": null,
        "title": null,
        "duration": null
    }, {
        "materialId": null,
        "mediaId": "5iDtROEQOkUtgnfeqz4pYNI0i2m9i7niS4oXAbh878k",
        "url": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589199439413&di=85c0835a66f8ba826cb5b44c712e0be3&imgtype=0&src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201402%2F20%2F20140220220726_YvyRR.thumb.700_0.jpeg",
        "type": null,
        "urlExpireTime": null,
        "createTime": null,
        "modifyTime": null,
        "createUserId": null,
        "modifyUserId": null,
        "description": null,
        "appId": null,
        "expireType": null,
        "voiceType": null,
        "title": null,
        "duration": null
    }, {
        "materialId": null,
        "mediaId": "5iDtROEQOkUtgnfeqz4pYNeYXGRAE3-cN92Zv56lzi4",
        "url": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589199439411&di=fb3888be1e8f5b266b44cddd390b0fb5&imgtype=0&src=http%3A%2F%2Fimg.xshuma.com%2F201510%2F212517151005505139.jpg",
        "type": null,
        "urlExpireTime": null,
        "createTime": null,
        "modifyTime": null,
        "createUserId": null,
        "modifyUserId": null,
        "description": null,
        "appId": null,
        "expireType": null,
        "voiceType": null,
        "title": null,
        "duration": null
    }, {
        "materialId": null,
        "mediaId": "5iDtROEQOkUtgnfeqz4pYPSc3ObOPTZSTix_VcWVXOY",
        "url": "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589199495590&di=01829a84af128f4833aa00105243bb92&imgtype=0&src=http%3A%2F%2Fimg4.imgtn.bdimg.com%2Fit%2Fu%3D3004831859%2C2611273449%26fm%3D214%26gp%3D0.jpg",
        "type": null,
        "urlExpireTime": null,
        "createTime": null,
        "modifyTime": null,
        "createUserId": null,
        "modifyUserId": null,
        "description": null,
        "appId": null,
        "expireType": null,
        "voiceType": null,
        "title": null,
        "duration": null
    }];
    $( "#imgTextAdd" ).empty();
    var imgHtml = '';
    for (let i = 0; i < responseStr.length; i++) {
        if (i % 3 == 0) {
            imgHtml += '<div style="display: flex;">'
        }
        imgHtml += '<div id=' + JSON.stringify( responseStr[i].mediaId ) + ' class="col-xs-4">'
            + '<div class="panel panel-default">'
            + '<div class="panel-body">'
            + '<div class="msg-img"><img src=' + JSON.stringify( responseStr[i].url ) + ' alt=""></div>'
            + '</div>'
            + '<div class="mask-bg"><div class="mask-icon"><i class="icon-ok"></i></div></div>'
            + '</div>'
            + '</div>';
        if (i % 3 == 2) {
            imgHtml += '</div>'
        }
    }
    ;$( "#imgTextAdd" ).append( imgHtml );
    modalAddClick();
    let delHtml = '<span class="msg-panel__del del-tuwen">删除</span>';
    if ($( ".subbutton__actived" ).attr( 'alt' )) {
        let row = $( ".subbutton__actived" ).prevAll().length;
        if (button[row].ctype != 'image') {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
            $( '.mask-bg' ).hide();
        } else {
            var subKey = button[row].media_id;
            if (typeof (subKey) == 'undefined') {
                $( '.msg-template' ).empty();
                $( '.msg-context__item' ).css( "display", "block" );
                $( '.mask-bg' ).hide();
            } else {
                $( '.msg-template' ).html( $( '#' + subKey ).html() );
                $( '.msg-template' ).append( delHtml );
                $( '.msg-panel__del' ).on( 'click', delClick );
                $( '.msg-context__item' ).css( "display", "none" );
            }
        }
    } else if ($( ".subbutton__actived" ).attr( 'id' )) {
        let row = $( ".subbutton__actived" ).parents( '.custom-menu-view__menu' ).prevAll().length;
        let clo = $( ".subbutton__actived" ).prevAll().length;
        if (typeof (button[row].sub_button[clo].ctype) == 'undefined') {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
        } else if (button[row].sub_button[clo].ctype != 'image') {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
            $( '.mask-bg' ).hide();
        } else {
            var subKey = button[row].sub_button[clo].media_id;
            if (typeof (subKey) == 'undefined') {
                $( '.msg-template' ).empty();
                $( '.msg-context__item' ).css( "display", "block" );
                $( '.mask-bg' ).hide();
            } else {
                $( '.msg-template' ).html( $( '#' + subKey ).html() );
                $( '.msg-template' ).append( delHtml );
                $( '.msg-panel__del' ).on( 'click', delClick );
                $( '.msg-context__item' ).css( "display", "none" );
            }
        }
    }
}

function voice() {
    let responseStr = [{
        "materialId": null,
        "mediaId": "5iDtROEQOkUtgnfeqz4pYGQrFMcl2U3gezrZ6fR4hV4",
        "url": "#",
        "type": null,
        "urlExpireTime": null,
        "createTime": "2019-06-14 09:46:40",
        "modifyTime": null,
        "createUserId": null,
        "modifyUserId": null,
        "description": null,
        "appId": null,
        "expireType": null,
        "voiceType": null,
        "title": "music",
        "duration": 36
    }];
    $( "#imgTextAdd" ).empty();
    for (var i = 0; i < responseStr.length; i++) {
        let voice = '<div id=' + JSON.stringify( responseStr[i].mediaId ) + ' class="col-xs-4">' +
            '   <div class="panel panel-default">' +
            '   <div class="panel-body"><div class="wx-audio-content jg" >' +
            '    <audio muted class="wx-audio-content"  src=\"' + responseStr[i].url + '\" loop="true"></audio>' +
            '    <div class="wx-audio-left">\n' +
            '    <svg t="1589339039470" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2161" width="40" height="40"><path d="M511.3 109.8c111.1 0 201.1 90.1 201.1 201.1v180.7c0 111.1-90.1 201.1-201.1 201.1s-201.1-90.1-201.1-201.1V310.9c0-111.1 90-201.1 201.1-201.1zM360.5 388.1v103.4c0 83.3 67.5 150.9 150.9 150.9s150.9-67.5 150.9-150.9V388.1H360.5z m0-50.2h301.7v-27c0-7.2-0.5-14.3-1.5-21.3H536.5c-13.9 0-25.1-11.3-25.1-25.1s11.3-25.1 25.1-25.1h107.7c-25.5-47.2-75.4-79.3-132.8-79.3-83.3 0-150.9 67.5-150.9 150.9v26.9z m176 453.6v72.6h150.9c13.9 0 25.1 11.3 25.1 25.1 0 13.9-11.3 25.1-25.1 25.1h-352c-13.9 0-25.1-11.3-25.1-25.1 0-13.9 11.3-25.1 25.1-25.1h150.9v-72.6c-155-12.8-276.7-142.5-276.7-300.7v-50.3c0-13.9 11.3-25.1 25.1-25.1s25.1 11.3 25.1 25.1v50.3c0 138.9 112.6 251.4 251.4 251.4s251.4-112.6 251.4-251.4v-50.3c0-13.9 11.3-25.1 25.1-25.1 13.9 0 25.1 11.3 25.1 25.1v50.3c0.3 158.2-121.5 287.9-276.3 300.7z" p-id="2162"></path></svg>' +
            '    </div>' +
            '    <div class="wx-audio-right">' +
            '     <div><p class="wx-audio-title">' + responseStr[i].title + '</p>' +
            '     <p class="wx-audio-disc">' + responseStr[i].createTime + '</p>' +
            '     <p class="wx-audio-disc">00:' + responseStr[i].duration + '</p></div>' +
            '    </div></div>' +
            '   </div><div class="mask-bg"><div class="mask-icon"><i class="icon-ok"></i></div></div></div>';
        $( "#imgTextAdd" ).append( voice );
    }
    modalAddClick();
    let delHtml = '<span class="msg-panel__del del-tuwen">删除</span>';
    if ($( ".subbutton__actived" ).attr( 'alt' )) {
        let row = $( ".subbutton__actived" ).prevAll().length;
        if (button[row].ctype != 'voice') {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
            $( '.mask-bg' ).hide();
        } else {
            var subKey = button[row].media_id;
            if (typeof (subKey) == 'undefined') {
                $( '.msg-template' ).empty();
                $( '.msg-context__item' ).css( "display", "block" );
                $( '.mask-bg' ).hide();
            } else {
                $( '.msg-template' ).html( $( '#' + subKey ).html() );
                $( '.msg-template' ).append( delHtml );
                $( '.msg-panel__del' ).on( 'click', delClick );
                $( '.msg-context__item' ).css( "display", "none" );
            }
        }
    } else if ($( ".subbutton__actived" ).attr( 'id' )) {
        let row = $( ".subbutton__actived" ).parents( '.custom-menu-view__menu' ).prevAll().length;
        let clo = $( ".subbutton__actived" ).prevAll().length;
        if (typeof (button[row].sub_button[clo].ctype) == 'undefined') {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
        } else if (button[row].sub_button[clo].ctype != 'voice') {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
            $( '.mask-bg' ).hide();
        } else {
            var subKey = button[row].sub_button[clo].media_id;
            if (typeof (subKey) == 'undefined') {
                $( '.msg-template' ).empty();
                $( '.msg-context__item' ).css( "display", "block" );
                $( '.mask-bg' ).hide();
            } else {
                $( '.msg-template' ).html( $( '#' + subKey ).html() );
                $( '.msg-template' ).append( delHtml );
                $( '.msg-panel__del' ).on( 'click', delClick );
                $( '.msg-context__item' ).css( "display", "none" );
            }
        }
    }
}

function video1() {
    let responseStr = [{
        "materialId": null,
        "mediaId": "5iDtROEQOkUtgnfeqz4pYGQrFMcl2U3gezrZ6fR4hV4",
        "url": "#",
        "type": null,
        "urlExpireTime": null,
        "createTime": "2019-06-14 09:46:40",
        "modifyTime": null,
        "createUserId": null,
        "modifyUserId": null,
        "description": null,
        "appId": null,
        "expireType": null,
        "voiceType": null,
        "title": "music",
        "duration": 36
    }];
    $( "#imgTextAdd" ).empty();
    for (var i = 0; i < responseStr.length; i++) {
        let voice = '<div id=' + JSON.stringify( responseStr[i].mediaId ) + ' class="col-xs-4">' +
            '   <div class="panel panel-default">' +
            '   <div class="panel-body"><div class="wx-audio-content jg" >' +
            '    <audio muted class="wx-audio-content"  src=\"' + responseStr[i].url + '\" loop="true"></audio>' +
            '    <div class="wx-audio-left">\n' +
            '     <svg t="1589190984776" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1989" width="40" height="40"><path d="M793.6 972.8l-563.2 0c-113.1008 0-204.8-91.6992-204.8-204.8L25.6 256c0-113.1008 91.6992-204.8 204.8-204.8l563.2 0c113.1008 0 204.8 91.6992 204.8 204.8l0 512C998.4 881.1008 906.7008 972.8 793.6 972.8zM947.2 286.72c0-101.7856-82.5344-184.32-184.32-184.32L261.12 102.4C159.3344 102.4 76.8 184.9344 76.8 286.72l0 450.56c0 101.7856 82.5344 184.32 184.32 184.32l501.76 0c101.7856 0 184.32-82.5344 184.32-184.32L947.2 286.72zM384 308.224l307.2 203.776-307.2 203.776L384 308.224z" p-id="1990"></path></svg>\n' +
            '    </div>' +
            '    <div class="wx-audio-right">' +
            '     <div><p class="wx-audio-title">' + responseStr[i].title + '</p>' +
            '     <p class="wx-audio-disc">' + responseStr[i].createTime + '</p>' +
            '     <p class="wx-audio-disc">00:' + responseStr[i].duration + '</p></div>' +
            '    </div></div>' +
            '   </div><div class="mask-bg"><div class="mask-icon"><i class="icon-ok"></i></div></div></div>';
        $( "#imgTextAdd" ).append( voice );
    }


    var appid = $( "#appIdcode" ).val();
    let delHtml = '<span class="msg-panel__del del-tuwen">删除</span>';
    if ($( ".subbutton__actived" ).attr( 'alt' )) {
        let row = $( ".subbutton__actived" ).prevAll().length;
        if (button[row].ctype != 'video') {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
            $( '.mask-bg' ).hide();
        } else {
            var subKey = button[row].media_id;
            if (typeof (subKey) == 'undefined') {
                $( '.msg-template' ).empty();
                $( '.msg-context__item' ).css( "display", "block" );
                $( '.mask-bg' ).hide();
            } else {
                $( '.msg-template' ).html( $( '#' + subKey ).html() );
                $( '.msg-template' ).append( delHtml );
                $( '.msg-panel__del' ).on( 'click', delClick );
                $( '.msg-context__item' ).css( "display", "none" );
            }
        }
    } else if ($( ".subbutton__actived" ).attr( 'id' )) {
        let row = $( ".subbutton__actived" ).parents( '.custom-menu-view__menu' ).prevAll().length;
        let clo = $( ".subbutton__actived" ).prevAll().length;
        if (typeof (button[row].sub_button[clo].ctype) == 'undefined') {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
        } else if (button[row].sub_button[clo].ctype != 'video') {
            $( '.msg-template' ).empty();
            $( '.msg-context__item' ).css( "display", "block" );
            $( '.mask-bg' ).hide();
        } else {
            var subKey = button[row].sub_button[clo].media_id;
            if (typeof (subKey) == 'undefined') {
                $( '.msg-template' ).empty();
                $( '.msg-context__item' ).css( "display", "block" );
                $( '.mask-bg' ).hide();
            } else {
                $( '.msg-template' ).html( $( '#' + subKey ).html() );
                $( '.msg-template' ).append( delHtml );
                $( '.msg-panel__del' ).on( 'click', delClick );
                $( '.msg-context__item' ).css( "display", "none" );
            }
        }
    }
}

function modalAddClick() {
    $( ".msg-panel_select" ).on( 'click', function () {
        if ($( "li" ).hasClass( "msg-tab_item on" )) {
            let dom = $( "#clickUl" ).find( ".on" ).attr( 'id' );
            switch (dom) {
                case 'imgtextLi':
                    $( ".modal-title1" ).text( '选择图文消息' );
                    break;
                case 'imgLi':
                    $( ".modal-title1" ).text( '选择图片消息' );
                    break;
                case 'voice':
                    $( ".modal-title1" ).text( '选择语音消息' );
                    break;
                case 'video':
                    $( ".modal-title1" ).text( '选择视频消息' );
                    break;
            }
        }
    } );
    $( '#selectModal .modal-body .panel' ).click( function () {
        $( this ).find( '.mask-bg' ).show();
        $( this ).parent().siblings().find( '.mask-bg' ).hide();
        mId = $( this ).parent().attr( 'id' );
    } );
    $( '#selectModal .ensure' ).on( 'click', function () {
        var msgTemp = $( '.msg-template' );
        var delEl = '<span class="msg-panel__del del-tuwen">删除</span>';
        if (mId != null) {
            msgTemp.html( $( '#' + mId ).html() );
            delElement();
            msgTemp.find( '.mask-bg' ).hide();
            msgTemp.siblings().hide();
            msgTemp.show();
            tempObj[mId] = msgTemp.html();
            $( '.msg-panel__del' ).on( 'click', delClick );
            var current = $( '.subbutton__actived' ).prevAll().length;
            var input_name = $( 'input[name="custom_input_title"]' );
            if ($( '.custom-menu-view__menu__sub__add' ).hasClass( 'subbutton__actived' )) {
                var sub_col = $( ".subbutton__actived" ).prevAll().length;
                var sub_row = $( ".subbutton__actived" ).parents( '.custom-menu-view__menu' ).prevAll().length;
                button[sub_row].sub_button[sub_col].name = input_name.val();
                button[sub_row].sub_button[sub_col].media_id = mId;
                button[sub_row].sub_button[sub_col].type = 'media_id';
                if ($( "li" ).hasClass( "msg-tab_item on" )) {
                    let dom = $( "#clickUl" ).find( ".on" ).attr( 'id' );
                    switch (dom) {
                        case 'imgtextLi':
                            button[sub_row].sub_button[sub_col].ctype = null;
                            break;
                        case 'imgLi':
                            button[sub_row].sub_button[sub_col].ctype = 'image';
                            break;
                        case 'voice':
                            button[sub_row].sub_button[sub_col].ctype = 'voice';
                            break;
                        case 'video':
                            button[sub_row].sub_button[sub_col].ctype = 'video';
                            break;
                    }
                }
            } else if ($( '.custom-menu-view__menu' ).hasClass( 'subbutton__actived' )) {
                button[current].name = input_name.val();
                button[current].media_id = mId;
                button[current].type = 'media_id';
                if ($( "li" ).hasClass( "msg-tab_item on" )) {
                    let dom = $( "#clickUl" ).find( ".on" ).attr( 'id' );
                    switch (dom) {
                        case 'imgtextLi':
                            button[current].ctype = null;
                            break;
                        case 'imgLi':
                            button[current].ctype = 'image';
                            break;
                        case 'voice':
                            button[current].ctype = 'voice';
                            break;
                        case 'video':
                            button[current].ctype = 'video';
                            break;
                    }
                }
            }
        }
        $( '#selectModal' ).modal( 'hide' );
    } );
}

function appIdChange() {
    var app_id = $( "#appIdcode" ).val();
    obj = {"menu": {button: []}};
    $( '.custom-menu-view__menu' ).remove();
    $( '.cm-edit-before' ).show().siblings().hide();
    menuCreate( obj );
}

$( function () {
    let data = {
        "list": [{"id": "wx6d464222e2572d67", "text": "GYVW"}, {
            "id": "wxa40fd733fd63e57b",
            "text": "kx"
        }, {"id": "wxc2c420fe6bf447fe", "text": "半个柠檬c"}, {"id": "wxd2a2b89fae362185", "text": "fans"}]
    }
    var str = "";
    for (var i = 0; i < data.list.length; i++) {
        str += "<option value='" + data.list[i].id + "'>" + data.list[i].text + "</option>";
    }
    if (data.list.length >= 0) {
        $( ".custom-menu-view__title" ).text( data.list[0].text );
    }
    $( '#appIdcode' ).html( str );
    appIdChange();
    $( "#appIdcode" ).on( "change", function (e) {
        appIdChange();
        imageText();
        $( ".custom-menu-view__title" ).text( ' ' );
        $( ".custom-menu-view__title" ).text( $( "#appIdcode" ).find( "option:selected" ).text() );
    } )
} )
$( '#synchroBtns' ).click( function () {
    layer.msg( "同步成功！" );
} );
$( function () {
    $( "#showPhone" ).on( 'click', function () {
        $( "#mobileDiv" ).css( 'display', "block" );
        $( ".mask" ).css( 'display', "block" );
        $( ".nickname" ).text( ' ' );
        $( ".nickname" ).text( $( "#appIdcode" ).find( "option:selected" ).text() );
        $( "#viewList" ).empty();
        $( "#viewShow" ).empty();
        $( ".cm-edit-after" ).css( 'display', 'none' );
        $( ".cm-edit-before" ).css( 'display', 'block' );
        $( ".subbutton__actived" ).removeClass( 'subbutton__actived' );
        let html
        for (let i = 0; i < obj.menu.button.length; i++) {
            html = '<li class="pre_menu_item grid_item size1of' + obj.menu.button.length + ' jsViewLi " id="menu_' + i + '">' +
                '<a href="javascript:void(0);" class="jsView pre_menu_link" title="' + obj.menu.button[i].name + '" draggable="false" ' +
                'media_id="' + obj.menu.button[i].media_id + '" ctype="' + obj.menu.button[i].ctype + '" type="' + obj.menu.button[i].type + '">';
            if (obj.menu.button[i].sub_button.length) {
                html += '<i class="icon_menu_dot"></i>';
            }
            html += obj.menu.button[i].name + '</a>';
            let htmlDiv = '';
            if (obj.menu.button[i].sub_button.length) {
                htmlDiv += '<div class="sub_pre_menu_box jsSubViewDiv" style="display:none">' +
                    '<ul class="sub_pre_menu_list">';
                for (let j = 0; j < obj.menu.button[i].sub_button.length; j++) {
                    htmlDiv += '<li id="subMenu_menu_0_' + j + '">' +
                        '<a href="javascript:void(0);" class="jsSubView" title="' + obj.menu.button[i].sub_button[j].name + '" draggable="false" ' +
                        'media_id="' + obj.menu.button[i].sub_button[j].media_id + '" ctype="' + obj.menu.button[i].sub_button[j].ctype + '" type="' + obj.menu.button[i].sub_button[j].type + '">' +
                        obj.menu.button[i].sub_button[j].name +
                        '</a>' +
                        '</li>';
                }
                htmlDiv += '</ul>' +
                    '<i class="arrow arrow_out"></i>' +
                    '<i class="arrow arrow_in"></i>' +
                    '</div>';
            }
            html += htmlDiv + '</li>';
            $( "#viewList" ).append( html );
        }
        $( ".jsViewLi" ).off( 'click' ).on( 'click', function () {
            switch ($( this ).find( '.jsSubViewDiv' ).css( 'display' )) {
                case 'none':
                    $( '.jsSubViewDiv' ).css( 'display', 'none' );
                    $( this ).find( '.jsSubViewDiv' ).css( 'display', 'block' );
                    break;
                case 'block':
                    $( '.jsSubViewDiv' ).css( 'display', 'none' );
                    $( this ).find( '.jsSubViewDiv' ).css( 'display', 'none' );
                    break;
            }
        } )
        $( "#viewList" ).off( 'click' ).on( 'click', 'li', function () {
            switch ($( this ).children( 'a' ).attr( 'type' )) {
                case 'media_id':
                    let media_id = $( this ).children( 'a' ).attr( 'media_id' );
                    switch ($( this ).children( 'a' ).attr( 'ctype' )) {
                        case 'null':
                            imageText();
                            $( "#viewShow" ).append( '<li class="show_item">' + $( '#' + media_id ).html() + '</li>' );
                            $( '.mobile_preview_bd' ).scrollTop( $( '#viewShow' )[0].scrollHeight );
                            break;
                        case 'image':
                            picture();
                            $( "#viewShow" ).append( '<li class="show_item">' + $( '#' + media_id ).html() + '</li>' );
                            $( '.mobile_preview_bd' ).scrollTop( $( '#viewShow' )[0].scrollHeight );
                            break;
                        case 'voice':
                            voice();
                            $( "#viewShow" ).append( '<li class="show_item">' + $( '#' + media_id ).html() + '</li>' );
                            $( '.mobile_preview_bd' ).scrollTop( $( '#viewShow' )[0].scrollHeight );
                            break;
                        case 'video':
                            video1();
                            $( "#viewShow" ).append( '<li class="show_item">' + $( '#' + media_id ).html() + '</li>' );
                            $( '.mobile_preview_bd' ).scrollTop( $( '#viewShow' )[0].scrollHeight );
                            break;
                    }
                    break;
                case 'view':
                    if ($( this ).parent( 'ul' ).hasClass( 'sub_pre_menu_list' )) {
                        let row = $( this ).parents( '.pre_menu_item' ).prevAll().length;
                        let col = $( this ).prevAll().length;
                        $( this ).children( 'a' ).attr( 'href', button[row].sub_button[col].url );
                        $( this ).children( 'a' ).attr( 'target', '_blank' );
                    } else if ($( this ).parent( 'ul' ).hasClass( 'pre_menu_list' )) {
                        let row = $( this ).prevAll().length;
                        $( this ).children( 'a' ).attr( 'href', button[row].url );
                        $( this ).children( 'a' ).attr( 'target', '_blank' );
                    }
                    break;
            }
        } );
    } )
    $( ".mobile_preview_closed" ).on( 'click', function () {
        $( "#mobileDiv" ).css( 'display', "none" );
        $( ".mask" ).css( 'display', "none" );
    } )
} )