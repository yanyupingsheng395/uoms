// var retention_option= {
//     tooltip: {
//         trigger: 'axis'
//     },
//     xAxis: {
//         type: 'category',
//         data: [],
//         name: '',
//         nameTextStyle: 'oblique',
//         boundaryGap: false,
//         nameRotate: 45
//     },
//     axisLabel:{
//         interval: 0,
//         rotate: 45
//     },
//     yAxis: {
//         type: 'value',
//         name: '',
//         max: 50
//     },
//     series: [{
//         data: [],
//         type: 'line',
//         smooth: true
//     }]
// };
//
// var radar_option = {
//     tooltip: {},
//     legend: {
//         data: ['组合1', '组合2','组合3']
//     },
//     radar: {
//         indicator: [
//             { name: '购买次数', max: 50},
//             { name: '客单价', max: 1000},
//             { name: '购买间隔', max: 365},
//             { name: '购买品类指数', max: 10}
//         ],
//         center: ['50%','50%'],
//         radius: 80
//     },
//     series: [{
//         type: 'radar',
//       //  itemStyle: {normal: {areaStyle: {type: 'default'}}},
//         data : [
//             {
//                 value : [30, 256, 25, 2],
//                 name : '组合1'
//             },
//             {
//                 value : [5, 999, 20, 1],
//                 name : '组合2'
//             },
//             {
//                 value : [40, 500, 45, 5],
//                 name : '组合3'
//             }
//         ]
//     }]
// };
//
// var area_option = {
//     tooltip : {
//         trigger: 'axis',
//         axisPointer: {
//             type: 'cross',
//             label: {
//                 backgroundColor: '#6a7985'
//             }
//         }
//     },
//     xAxis: {
//         type: 'category',
//         boundaryGap: false,
//         data: []
//     },
//     yAxis: {
//         type: 'value'
//     },
//     series: [{
//         data: [],
//         type: 'line',
//         areaStyle: {}
//     }]
// };
// $(function () {
//
//
//
//     retention_option.xAxis.name="品类";
//     retention_option.xAxis.data=['1','2','3','4','5','6','7','8','9','10','11+'];
//     retention_option.yAxis.name="留存率(%)";
//     retention_option.series[0].data=['3','5','5.4','6','12','14','17','17.5','17.6','17.7','17.8'];
//     var catChart = echarts.init(document.getElementById('retention_cat'), 'macarons');
//     catChart.setOption(retention_option);
//
//
//
// });