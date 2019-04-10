// $(function () {
//     var settings = {
//         url: "/op/opDayList",
//         method: 'post',
//         cache: false,
//         pagination: true,
//      //   striped: true,
//         sidePagination: "server",
//         pageNumber: 1,            //初始化加载第一页，默认第一页
//         pageSize: 10,            //每页的记录行数（*）
//         pageList: [10, 25, 50, 100],
//         queryParams: function (params) {
//             return {
//                 pageSize: params.limit,  ////页面大小
//                 pageNum: (params.offset / params.limit )+ 1  //页码
//             };
//         },
//         columns: [{
//             field: 'REASON_NAME',
//             title: '编号'
//         }, {
//             field: 'PROGRESS',
//             title: '进度',
//             formatter: function (value, row, index) {
//                 return value == null ? "" : value + "%"
//                // return "<div class='progress_middle' style='height: 19px;'><div class='progress-bar progress_middle' role='progressbar' aria-valuenow='10' aria-valuemin='0' aria-valuemax='100' style='min-width: 2em; width:"+val+"%'>"+val+"%</div></div>";
//             }
//         }, {
//             // A表示草稿 R表示计算中 F表示计算完成
//             field: 'STATUS',
//             title: '状态',
//             formatter: function (value, row, index) {
//                 if (value == "A") {
//                     return "<span class='label label-info'>草稿</span>";
//                 }else if (value == "R") {
//                     return "<span class='label label-primary'>计算中</span>";
//                 }else if (value == "F") {
//                     return "<span class='label label-warning'>完成</span>";
//                 }
//             }
//         },  {
//             field: 'KPI_NAME',
//             title: '指标'
//         }, {
//             field: 'CREATE_DT',
//             title: '创建时间'
//         }, {
//             filed: 'button',
//             title: '操作',
//             formatter: function (value, row, index) {
//                 var reasonId=row.REASON_ID;
//                 if (row.STATUS == "A" || row.STATUS=='F') {
//                     return "<div class='btn btn-success' onclick='view("+reasonId+")'>查看</div>&nbsp;<div class='btn btn-danger' onclick='del("+reasonId+")'>删除</div>";
//                 }else if (row.STATUS == "R") {
//                     return "<div class='btn btn-success' onclick='view("+reasonId+")'>查看</div>&nbsp;<div class='btn btn-cyan' onclick='updatedata("+reasonId+")'>更新</div>";
//                 }
//             }
//         }]
//     };
//     $('#opdayTable').bootstrapTable(settings);
//
//     //为刷新按钮绑定事件
//     $("#btn_refresh").on("click",function () {
//
//     });
// });
//
//
