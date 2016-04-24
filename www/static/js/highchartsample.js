/*

_.map(datamap, function(num, key){ console.log(num); });

*/

$(function () {
    var chart;
    $(document).ready(function() {
       chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container',
                zoomType: 'x',
                spacingRight: 20
            },
            credits:{
            enabled:false
            },
            title: {
                text: 'View Rate'
            },
            subtitle: {
                text: document.ontouchstart === undefined ?
                    'Click and drag in the plot area to zoom in' :
                    'Drag your finger over the plot to zoom in'
            },
            xAxis: {
                type: 'datetime',
                maxZoom:1 * 3600 * 100,// fourteen days
                title: {
                    text: null
                }
            },
            yAxis: {
                title: {
                    text: 'View Rate(%)'
                },
                showFirstLabel: false
            },
            tooltip: {
                shared: true
            },
            legend: {
               layout: 'vertical',
                           align: 'right',
                           verticalAlign: 'middle',
                           borderWidth: 0
            },

            series: [
                        {
                             type: 'spline',
                             name: 'USD to EUR',
                             pointInterval:1 * 3600 * 100,
                             // pointStart: Date.UTC(2006, 0, 01),

                            data: [ [Date.UTC(2016,03,21,01,30),27.28],[Date.UTC(2016,03,21,02,00),29.28],[Date.UTC(2016,03,21,02,30),29.28],[Date.UTC(2016,03,21,03,00),22.28],[Date.UTC(2016,03,21,03,30),22.28],[Date.UTC(2016,03,21,04,00),22.28],[Date.UTC(2016,03,21,04,30),20.28]
                                  ]
                       },
                        {
                            type: 'spline',
                            name: 'EUR to USD',
                            pointInterval:1 * 3600 * 100,
                            data: [ [Date.UTC(2016,03,21,01,30),40.28],[Date.UTC(2016,03,21,02,30),35.28],[Date.UTC(2016,03,21,03,00),45.28],[Date.UTC(2016,03,21,03,30),22.28],[Date.UTC(2016,03,21,04,00),22.28],[Date.UTC(2016,03,21,04,30),56.28]
                            ]
                        }
                    ]
            });
    });

});