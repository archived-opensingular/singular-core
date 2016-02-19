AmChartViewResultPanel = (function () {
    var portletsToAnimate = 4;

    function createChart(idChartDiv, definition, portletContext) {
        restprovider.callDelegate(portletContext, function (data) {
            definition.dataProvider = data;
            if (portletContext.portletIndex && portletContext.portletIndex >= portletsToAnimate) {
                definition.startDuration = 0
            }
            var chart = AmCharts.makeChart(idChartDiv, definition);
            var rmc = function () {
                $('a').each(function () {
                    if ($(this).attr("href") === "http://www.amcharts.com/javascript-charts/") {
                        $(this).hide();
                    }
                })
            };
            chart.addListener('drawn', rmc);
            rm();
        });
    }

    return {
        createChart: createChart
    }
})();