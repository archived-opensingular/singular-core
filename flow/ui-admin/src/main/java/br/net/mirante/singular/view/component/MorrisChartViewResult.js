MorrisChartViewResult = (function () {

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
                definition.data = data;
                definition.element = idChartDiv;
                definition.dateFormat = function (x) {
                    var months = ['JAN', 'FEV', 'MAR', 'ABR', 'MAI', 'JUN', 'JUL', 'AGO', 'SET', 'OUT', 'NOV', 'DEZ'];
                    var value = new Date(x);
                    return months[value.getMonth()] + '/' + value.getFullYear().toString().substring(2, 4);
                };
                var func = eval('Morris.' + definition.type);
                new func(definition);
            }
        });

    }

    return {
        createChart: createChart
    }
})();