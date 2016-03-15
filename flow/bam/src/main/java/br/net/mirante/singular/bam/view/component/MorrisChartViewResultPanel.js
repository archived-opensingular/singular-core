MorrisChartViewResultPanel = (function () {
    function createChart(idChartDiv, definition, portletContext) {
        restprovider.callDelegate(portletContext, function (data) {
            definition.data = data;
            definition.element = idChartDiv;
            definition.dateFormat = function (x) {
                var months = ['JAN', 'FEV', 'MAR', 'ABR', 'MAI', 'JUN', 'JUL', 'AGO', 'SET', 'OUT', 'NOV', 'DEZ'];
                var value = new Date(x);
                return months[value.getMonth()] + '/' + value.getFullYear().toString().substring(2, 4);
            };
            var func = eval('Morris.' + definition.type);
            new func(definition);
        });
    }

    return {
        createChart: createChart
    }
})();