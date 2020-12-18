

$(function(){
	var get = new Date();
	var calendar = $('#calendar').fullCalendar({
		header: {													//设置日历头部信息，false，则不显示头部信息。包括left，center,right左中右三个位置
			left: 'prev,next, today',								//上一个、下一个、今天
			center: 'title',										//标题
			// right: 'month,agendaWeek,agendaDay,listMonth'			//月、周、日、日程列表
			right: 'month'			//月、周、日、日程列表
		},
		locale: 'zh-cn',											//?没用？
        timeFormat: 'HH:mm',										//日程事件的时间格式
		buttonText: {    											//各按钮的显示文本信息
            today: '今天',    
            month: '月',    
            agendaWeek: '周',
            agendaDay: '日',
            listMonth: '日程',
        },
		monthNames: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],    	//月份全称
        monthNamesShort: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"], //月份简写
        dayNames: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],    	//周全称
        dayNamesShort: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],  //周简写
        noEventsMessage : "当月无数据",   							//listview视图下，无数据提示
		allDayText: "全天",											//自定义全天视图的名称
        allDaySlot: false,  										//是否在周日历上方显示全天
//      allDayDefault: false,										//是否为全天日程事件，显示这一天中所做的事情
		slotDuration : "00:30:00",      							//一格时间槽代表多长时间，默认00:30:00（30分钟）
        slotLabelFormat : "H(:mm)a",    							//日期视图左边那一列显示的每一格日期时间格式
        slotLabelInterval : "01:00:00", 							//日期视图左边那一列多长间隔显示一条日期文字(默认跟着slotDuration走的，可自定义)
        snapDuration : "01:00:00",      							//其实就是动态创建一个日程时，默认创建多长的时间块
		firstDay: 1,   												//一周中显示的第一天是哪天，周日是0，周一是1，类推
        hiddenDays: [],												//隐藏一周中的某一天或某几天，数组形式，如隐藏周二和周五：[2,5]，默认不隐藏，除非weekends设置为false。
        weekends: true,												//是否显示周六和周日，设为false则不显示周六和周日。默认值为true
        weekMode: 'fixed',											//月视图里显示周的模式，因每月周数不同月视图高度不定。fixed：固定显示6周高，日历高度保持不变liquid：不固定周数，高度随周数变化variable：不固定周数，但高度固定
		weekNumbers: false,											//是否在日历中显示周次(一年中的第几周)，如果设置为true，则会在月视图的左侧、周视图和日视图的左上角显示周数。
        weekNumberCalculation: 'iso',								//周次的显示格式。
		height: 800,												//设置日历的高度，包括header日历头部，默认未设置，高度根据aspectRatio值自适应。
//      contentHeight: 600,											//设置日历主体内容的高度，不包括header部分，默认未设置，高度根据aspectRatio值自适应。
		handleWindowResize: true,									//是否随浏览器窗口大小变化而自动变化。
		defaultView: 'month',										//初始化时默认视图，默认是月month，agendaWeek是周，agendaDay是当天
//      slotEventOverlap: false,									//事件是否可以重叠覆盖
		defaultDate: get,											//默认显示那一天的日期视图getDates(true)2020-05-10
		nowIndicator: true,            								//周/日视图中显示今天当前时间点（以红线标记），默认false不显示
		eventLimit: false,       									//数据条数太多时，限制显示条数（多余的以“+2more”格式显示），默认false不限制,支持输入数字设定固定的显示条数
		eventLimitText: "更多",       								//当一块区域内容太多以"+2 more"格式显示时，这个more的名称自定义（应该与eventLimit: true一并用）
        dayPopoverFormat : "YYYY年M月d日", 							//点开"+2 more"弹出的小窗口标题，与eventLimitClick可以结合用
		render:function(view){										//method,绑定日历到id上。$('#id').fullCalendar('render');
	        console.log('render',view)
	    },

		events: [{
			"title": "标题1234567890",
			"start": "2020-12-2",
			"end": "2020-12-2",
		},
        	{
				"title": "标题1234567890",
				"start": "2020-12-16",
				"end": "2020-12-17",
			},
      	],
      	dayClick: function(date, allDay, jsEvent, view) {						//空白的日期区点击
		},
		eventClick: function(event, jsEvent) {
			layer.confirm('是否删除该日历数据?', function(index){
				calendar.fullCalendar('removeEvents',event._id);
				//执行删除事件！！！！！！！！！！
				layer.close(index);
			});

		},
		eventMouseover: function(){},						//鼠标划过和离开的事件，用法和参数同上
		eventMouseout: function(){},
		
		selectable: true,									//是否允许通过单击或拖动选择日历中的对象，包括天和时间
        selectHelper: true,   								//当点击或拖动选择时间时，是否预先画出“日程区块”的样式显示默认加载的提示信息，该属性只在周/天视图里可用
      	selectMirror: true,									//镜像
      	selectOverlap : false,       						//是否允许选择被事件占用的时间段，默认true可占用时间段
      	selectAllow : function(selectInfo){ 				//精确的控制可以选择的地方，返回true则表示可选择，false表示不可选择
            console.log("start:"+selectInfo.start.format()+"|end:"+selectInfo.end.format()+"|resourceId:"+selectInfo.resourceId);
            return true;
        },select: function(start, end, allDay) {
			var d = new Date(start._d);
			selectdate = d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate();
			layer.confirm('添加推送内容',{
				area: ['800px', '600px'],
				content: OrganizeData(),//组织弹出界面数据
				yes: function(index, layero){
					var title=$("#contentTitle").val();
					calendar.fullCalendar('renderEvent',{		//一旦日历重新取得日程源，则原有日程将消失，当指定stick为true时，日程将永久的保存到日历上
						title: title,
						start: start,
						end: end,
						allDay: allDay,
						block: true,
						content: title,
					},true);
					layer.close(index);
				}
			});
			layui.use('laydate', function(){
				var laydate = layui.laydate;
				//日期时间选择器
				laydate.render({
					elem: '#test5'
					,type: 'datetime'
				});
			});
			layui.use('form', function(){
				var form = layui.form;
				//监听提交
				form.on('submit(formDemo)', function(data){
					layer.msg(JSON.stringify(data.field));
					return false;
				});
			});
		},
		unselect : function(view, jsEvent){						//选择取消时触发
            console.log("");
            console.log("view:"+view);
        },
        lazyFetching : true,        							//是否启用懒加载技术--即只取当前条件下的视图数据，其它数据在切换时触发，默认true只取当前视图的，false是取全视图的
		defaultTimedEventDuration : "02:00:00",     			//在Event Object中如果没有end参数时使用，如start=7:00pm，则该日程对象时间范围就是7:00~9:00
        defaultAllDayEventDuration : { days: 1 },  				//默认1天是多长，（有的是采用工作时间模式，所以支持自定义）
      	editable: true,                 						//支持日程拖动修改，默认false
		dragOpacity:0.2,                						//拖拽时不透明度，0.0~1.0之间，数字越小越透明
        dragScroll : false,              						//是否在拖拽时自动移动容器，默认true
        eventOverlap : true,            						//拖拽时是否重叠
      	eventConstraint : {     								//限制拖拽拖放的位置（即限制有些地方拖不进去）t
            dow: [ 1, 2, 3, 4, 5, 6, 0 ] 						//0是周日，1是周一，以此类推
       	},
      	longPressDelay : 1000,  								//移动设备，长按多少毫秒即可拖动,默认1000毫秒（1S）
      	eventDragStart : function(event, jsEvent, ui, view){    //日程开始拖拽时触发
        },
        eventDragStop : function(event, jsEvent, ui, view){     //日程拖拽停止时触发
        },
        eventDrop : function(event, dayDelta, delta, revertFunc, jsEvent, ui, view){  //日程拖拽停止，并且时间改变时触发，ayDelta日程前后移动了多少天
            console.log('eventDrop被执行，Event的title是：', event.title);
            if (dayDelta._days != 0) {
                console.log('eventDrop被执行，Event的start和end时间改变了：', dayDelta._days + '天！');
            } else if (dayDelta._milliseconds != 0) {
                console.log('eventDrop被执行，Event的start和end时间改变了：', dayDelta._milliseconds / 1000 + '秒！');
            } else {
                console.log('eventDrop被执行，Event的start和end时间没有改变！');
            }
        },
        eventResizeStart : function( event, jsEvent, ui, view ) { //日程大小调整开始时触发
        },
        eventResizeStop : function(event, jsEvent, ui, view){     //日程大小调整停止时触发
        },
        eventResize : function(event, delta, revertFunc, jsEvent, ui, view){    //日程大小调整完成并已经执行更新时触发
        },
      	
    });
})


OrganizeData=function () {
	var html='<form class="layui-form" id="formData">\n' +
		'  <div class="layui-form-item">\n' +
		'    <div class="layui-col-xs2">内容名称</div>\n' +
		'    <div class="layui-col-xs6">\n' +
		'      <input type="text" name="title" required  id="contentTitle" lay-verify="required" placeholder="请输入内容" autocomplete="off" class="layui-input">\n' +
		'    </div>\n' +
		'  </div>\n' +
		'  <div class="layui-form-item">\n' +
		'      <label class="layui-col-xs2">设置发送时间</label>\n' +
		'      <div class="layui-input-inline layui-col-xs6">\n' +
		'        <input type="text" class="layui-input" id="test5" >\n' +
		'      </div>\n' +
		'      <div class="layui-form-mid layui-word-aux">提醒群主发送</div>' +
		'  </div>\n' +
		'  <div class="layui-form-item">\n' +
		'    <label class="layui-col-xs2">设置发送内容</label>\n' +
		' <div class="col-md-12">\n' +
		'   <ul id="myTabs" class="nav nav-tabs" role="tablist">\n' +
		'      <li class="active"><a href="#image" id="image-tab" role="tab" data-toggle="tab"><i class="fa fa-image"></i>&nbsp;图片</a>\n' +
		'      </li>\n' +
		'      <li><a href="#web" id="web-tab" role="tab" data-toggle="tab"><i class="fa fa-link"></i>&nbsp;网页</a>\n' +
		'      </li>\n' +
		'      <li><a href="#miniprogram" id="miniprogram-tab" role="tab" data-toggle="tab"><i class="fa fa-skyatlas"></i>&nbsp;小程序</a>\n' +
		'      </li>\n' +
		'    </ul>\n' +
		'    <div id="myTabContent" class="tab-content"style="height: auto;">\n' +
		'       <!--图片-->\n' +
		'       <div class="tab-pane fade  active in" id="image">\n' +
		'			<div class="row">\n' +
		'				<div class="col-md-12">\n' +
		'					<div class="form-horizontal">\n' +
		'						<div class="form-group" style="margin-bottom: 2px;">\n' +
		' 							<label class="col-md-3 control-label">图片地址：</label>\n' +
		'							<div class="col-md-7">\n' +
		'								<input class="form-control" name="picUrl"/>\n' +
		'							</div>\n' +
		'						</div>\n' +
		' 					</div>\n' +
		'				</div>\n' +
		'       	</div>\n' +
		'		</div>\n' +
		'		<div class="tab-pane fade" id="web">\n' +
		'			<div class="form-horizontal">\n' +
		'				<div class="form-group">\n' +
		'					<label class="col-md-3 control-label">消息标题：</label>\n' +
		'					<div class="col-md-7">\n' +
		'						<input class="form-control" name="linkTitle">\n' +
		'					</div>\n' +
		'				</div>\n' +
		'				<div class="form-group">\n' +
		'					<label class="col-md-3 control-label">消息描述：</label>\n' +
		'					<div class="col-md-7">\n' +
		'						<input class="form-control" name="linkDesc">\n' +
		'					</div>\n' +
		'				</div>\n' +
		'				<div class="form-group">\n' +
		'					<label class="col-md-3 control-label">消息地址：</label>\n' +
		'					<div class="col-md-7">\n' +
		'						<input class="form-control" name="linkUrl">\n' +
		'					</div>\n' +
		'				</div>\n' +
		'				<div class="form-group" style="margin-bottom: 2px;">\n' +
		'					<label class="col-md-3 control-label">封面地址：</label>\n' +
		'					<div class="col-md-7">\n' +
		'						<input class="form-control" name="linkPicurl">\n' +
		'					</div>\n' +
		'				</div>\n' +
		'			</div>\n' +
		'		</div>\n' +
		'		<div class="tab-pane fade" id="miniprogram">\n' +
		'			<div class="form-horizontal">\n' +
		'				<div class="form-group">\n' +
		'					<label class="col-md-3 control-label">小程序标题：</label>\n' +
		'					<div class="col-md-7">\n' +
		'						<input class="form-control" name="miniprogramTitle" id="minititle">\n' +
		'					</div>\n' +
		'				</div>\n' +
		'				<div class="form-group" >\n' +
		'					<label class="col-md-3 control-label">小程序地址：</label>\n' +
		'					<div class="col-md-7">\n' +
		'						<input class="form-control" name="miniprogramPage" id="miniaddress">\n' +
		'					</div>\n' +
		'				</div>\n' +
		'				<div class="form-group" >\n' +
		'					<label class="col-md-3 control-label">小程序MediaID：</label>\n' +
		'					<div class="col-md-7">\n' +
		'						<input class="form-control" name="mediaId" id="mediaId" readonly="readonly">\n' +
		'					</div>\n' +
		'					<a id=\'addShopDiscountBtn\' style="cursor:pointer;color: #409eff !important;" onclick="chooseMedia()"><i class="fa fa-plus-circle"></i>添加</a>\n' +
		'				</div>\n' +
		'			</div>\n' +
		'		</div>\n' +
		'	</div>\n' +
		'	</div>' +
		'  </div>'+
		'</form>';
	return html;
}
	