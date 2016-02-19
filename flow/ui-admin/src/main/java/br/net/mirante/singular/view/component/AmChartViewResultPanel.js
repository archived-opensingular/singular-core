AmChartViewResultPanel = (function () {
    var portletsToAnimate = 4;

    function createChart(idChartDiv, definition, portletContext) {
        restprovider.callDelegate(portletContext, function (data) {
            definition.dataProvider = data;
            if (portletContext.portletIndex && portletContext.portletIndex >= portletsToAnimate) {
                definition.startDuration = 0
            }
            AmCharts.makeChart(idChartDiv, definition);
            $('a').each(function(){
                if( $(this).attr("href") === "http://www.amcharts.com/javascript-charts/"){
                    $(this).hide();
                }
            });
        });
    }

    return {
        createChart: createChart
    }
})();