init_date_begin('growthStartDt', 'growthEndDt', 'yyyy-mm', 1, 2, 1);
init_date_end('growthStartDt', 'growthEndDt', 'yyyy-mm', 1, 2, 1);

init_data();
function init_data() {
    var date = new Date();
    $("#growthEndDt").val(getDate('yyyy-MM', date));
    date.setMonth(date.getMonth() - 3);
    $("#growthStartDt").val(getDate('yyyy-MM', date));
    brandUserDataTable();
    getAllGrowthChartData();
}

function getDate(format, date) {
    if (format === undefined) format = 'yyyy-MM-dd hh:mm:ss';
    const o = {
        'M+': date.getMonth() + 1,
        'd+': date.getDate(),
        'h+': date.getHours(),
        'm+': date.getMinutes(),
        's+': date.getSeconds(),
        S: date.getMilliseconds()
    };

    if (/(y+)/.test(format)) {
        format = format.replace(
            RegExp.$1,
            (date.getFullYear() + '').substr(4 - RegExp.$1.length)
        );
    }

    for (let k in o) {
        if (new RegExp('(' + k + ')').test(format)) {
            format = format.replace(
                RegExp.$1,
                RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
            );
        }
    }
    return format;
}

function getAllGrowthChartData() {
    $.get("/insight/allGrowthData", {startDt: $("#growthStartDt").val(), endDt: $("#growthEndDt").val()}, function (r) {
        allGrowthChartData('allGRChart', r.data.growthV, '所有发生成长用户的成长速度监控图', '成长日期', '成长速度');
        allGrowthChartData('allGPChart', r.data.growthP, '所有推送的用户成长潜力监控图', '成长日期', '成长潜力');
    });
}

function allGrowthChartData(chartId, data, title, xName, yName) {
    var dom = document.getElementById(chartId);
    const columns = [...new Set(data.map(v => v.date_))].sort((a, b) => a - b);
    const dataSource = columns.map(date =>data.filter(item => item.date_ === date).map(item => item.value_));
    const tooltipData = echarts.dataTool.prepareBoxplotData(dataSource);
    const { boxData } = tooltipData;
    const myChart = echarts.init(dom);
    myChart.showLoading({
        text : '加载数据中...'
    });
    const option = {
        grid: {
            top: '17%',
            left: '10%',
            right: '17%',
            bottom: '19%'
        },
        title: {
            text: title,
            x: 'center',
            y: 'bottom',
            textStyle: {
                color: '#000',
                fontStyle: 'normal',
                fontWeight: 'normal',
                fontFamily: 'sans-serif',
                fontSize: 12
            }
        },
        tooltip: {
            formatter: function(param) {
                return [
                    columns[param.dataIndex] + ': ',
                    '最大: ' + boxData[param.dataIndex][4],
                    '2/3: ' + boxData[param.dataIndex][3],
                    '中间: ' + boxData[param.dataIndex][2],
                    '1/3: ' + boxData[param.dataIndex][1],
                    '最小: ' + boxData[param.dataIndex][0]
                ].join('<br/>');
            }
        },
        xAxis: {
            name: xName,
            type: 'category',
            data: columns,
            boundaryGap: true,
            nameGap: 30,
            splitArea: {
                show: false
            },
            splitLine: {
                show: false
            },
            axisLine: {
                show: true
            },
            axisTick: {
                show: false
            },
            boundaryGap: true
        },
        yAxis: {
            name: yName,
            z: 2,
            type: 'value',
            splitLine: {
                show: false
            },
            axisLine: {
                show: true
            },
            axisTick: {
                show: false
            },
            minInterval: 0.001,
            // boundaryGap: [1, 0.1]
        },
        series: [
            {
                barMinHeight: 10,
                type: 'custom',
                color: ['#B1D0FA'],
                name: 'violin plot',
                renderItem: (params, api) => {
                    const categoryIndex = api.value(0);

                    const min = Math.min(...dataSource[categoryIndex]);
                    console.log(min)
                    const max = Math.max(...dataSource[categoryIndex]);
                    const liner = d3
                        .scaleLinear()
                        .domain([min - 50, max + 50])
                        .ticks(20);
                    let density = kernelDensityEstimator(kernelEpanechnikov(7), liner)(
                        dataSource[categoryIndex]
                    );

                    const maxDens = Math.max(...density.map(v => v[1]));

                    const points = density.map(v => {
                        const [y, dens] = v;
                        const point = api.coord([categoryIndex, y]);
                        point[0] += (((api.size([0, 0])[0] / 2) * dens) / maxDens) * 0.85;
                        return point;
                    });

                    const points2 = density.map(v => {
                        const [y, dens] = v;
                        const point = api.coord([categoryIndex, y]);
                        point[0] -= (((api.size([0, 0])[0] / 2) * dens) / maxDens) * 0.85;
                        return point;
                    });

                    const lineGenerator = d3.line().curve(d3.curveBasis);
                    const pathData = lineGenerator(points);
                    const pathData2 = lineGenerator(points2);

                    return {
                        z: 2,
                        type: 'path',
                        shape: {
                            pathData: pathData + pathData2
                        },
                        style: api.style({
                            fill: api.visual('color'),
                            stroke: '#428EEE',
                            lineWidth: 1
                        }),
                        styleEmphasis: api.style({
                            fill: d3.color(api.visual('color')).darker(0.05),
                            stroke: d3.color('#428EEE').darker(0.05),
                            lineWidth: 2
                        })
                    };
                },
                encode: {
                    x: 0,
                    y: dataSource[
                        d3.scan(dataSource, function(a, b) {
                            return b.length - a.length;
                        })
                        ].map((v, i) => i + 1)
                },
                data: dataSource.map((v, i) => [i, ...v])
            }
        ]
    };
    myChart.setOption(option);
    myChart.hideLoading();
}

function kernelDensityEstimator(kernel, X) {
    return function(V) {
        return X.map(function(x) {
            return [
                x,
                d3.mean(V, function(v) {
                    return kernel(x - v);
                })
            ];
        });
    };
}

function kernelEpanechnikov(k) {
    return function(v) {
        return Math.abs((v /= k)) <= 1 ? (0.75 * (1 - v * v)) / k : 0;
    };
}

function searchGrowthData() {
    $("#singleDiv").attr("style", "display:none;");
    brandUserDataTable();
    getAllGrowthChartData();
}

function resetGrowthData() {
    $("#singleDiv").attr("style", "display:none;");
    var date = new Date();
    $("#growthEndDt").val(getDate('yyyy-MM', date));
    date.setMonth(date.getMonth() - 3);
    $("#growthStartDt").val(getDate('yyyy-MM', date));
    brandUserDataTable();
    getAllGrowthChartData();
}

$('#brandUserDataTable').on('click-row.bs.table', function (e, row, $element) {
    if ($($element).hasClass("changeColor")) {
        $($element).removeClass('changeColor');
        $("#singleDiv").attr("style", "display:none;");
    } else
    {
        $($element).addClass('changeColor');
        $($element).siblings().removeClass('changeColor');
        $("#singleDiv").attr("style", "display:block;");
        singleGrowthData(row['userId']);
    }
});