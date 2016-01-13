AmChartViewResult = (function () {

    function createChart(idChartDiv, definition, portletContext) {
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: portletContext.restEndpoint,
            data: JSON.stringify(portletContext),
            type: "POST",
            success: function (data) {
                definition.dataProvider = data;
                AmCharts.makeChart(idChartDiv, definition);
            }
        });
    }

    return {
        createChart: createChart
    }
})();