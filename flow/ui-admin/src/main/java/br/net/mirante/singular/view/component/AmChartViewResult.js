AmChartViewResult = (function () {

    function createChart(idChartDiv, definition, portletContext) {

        var chart = AmCharts.makeChart(idChartDiv, definition);

        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            url: portletContext.restEndpoint,
            data: JSON.stringify(portletContext),
            type: "POST",
            success: function (data) {
                chart.dataProvider = data;
                chart.validateData();
            }
        });
    }

    return {
        createChart: createChart
    }
})();