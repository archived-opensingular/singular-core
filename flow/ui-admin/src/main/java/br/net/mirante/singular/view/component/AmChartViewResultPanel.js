AmChartViewResultPanel = (function () {
    function createChart(idChartDiv, definition, portletContext) {
        restprovider.callDelegate(portletContext, function (data) {
            definition.dataProvider = data;
            AmCharts.makeChart(idChartDiv, definition);
        });
    }
    return {
        createChart: createChart
    }
})();