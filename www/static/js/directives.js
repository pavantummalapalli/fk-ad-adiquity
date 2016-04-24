function graphDirective($q,$interval,Chart,getNameToUrlMappingDetails){


        return {
            restrict:'E',
            scope:{
                name: "@name",
                title:"@title",

            },
            controller: function($scope,$templateCache,Chart,getNameToUrlMappingDetails){
                             $scope.defaultInterval=500000;


                        },
            controllerAs: "graphCtrl",
            bindToController:true,
            replace: true,
            link :function ($scope, element, attrs,ctrl){
                    var options =  {
                                            chart: {
                                                renderTo:'xyz',
                                                zoomType: 'x',
                                                spacingRight: 20,
                                                type:"spline"
                                            },
                                            credits:{
                                                enabled:false
                                            },
                                            title: {
                                                text: ''
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
                                                    text: ''
                                                },
                                                showFirstLabel: false
                                            },
                                            tooltip: {
                                                shared: true,
                                                formatter: function() {
                                                            var s = [];
                                                            var dateFormat = Highcharts.dateFormat('%b %Y %a %d  %H:%M:%S',this.x)+"<br>";
                                                            $.each(this.points, function(i, point) {
                                                                s.push(point.series.name +' : '+point.y);
                                                            });
                                                            return dateFormat+s.join('<br>');
                                                        }
                                            },
                                            legend: {
                                               layout: 'vertical',
                                                           align: 'right',
                                                           verticalAlign: 'middle',
                                                           borderWidth: 0
                                            },

                                            series: [
                                                        {
                                                             type: 'spline'
                                                       },
                                                        {   name:"\t",
                                                            type: 'spline'
                                                        }
                                                    ]
                                            };

                    options.chart.renderTo= element.find("#render-part")[0];
                    options.title.text=ctrl.title;
                    options.yAxis.title.text=ctrl.title;
                    $scope.chart = new Chart(options);
                    var responsePromises=[];
                    $scope.metricGraph = {
                            showLoading:function(){
                                $scope.chart.showLoading();
                            },
                            hideLoading:function(){
                                $scope.chart.hideLoading();
                            },
                            getMetircDetails:function($scope,ctrl,startTime,endTime){
                                                var mrgiraph=this;
                                                mrgiraph.showLoading();
                                                var responsePromises=[];
                                                var typeUrls=getNameToUrlMappingDetails[ctrl.name];
                                                console.log(typeUrls);

                                                mrgiraph.type=[];

                                                for (var i = 0; i < typeUrls.length; i++) {
                                                    var nameURLObject = typeUrls[i];
                                                    mrgiraph.type.push(nameURLObject.type);
                                                    var responsePromise = $scope.chart.getHttp(nameURLObject.intervalURL,startTime,endTime);


                                                    responsePromises.push(responsePromise);
                                                }
                                                $q.defer();
                                                $q.all(responsePromises)
                                                    .then(function(results) {
                                                    mrgiraph.hideLoading();
                                                    for(var i=0;i<results.length;i++){
                                                        var res=results[i];
                                                        var arrresponse=_.map(res.data, function(num, key){return [new Date(key).getTime(),num]});
                                                           mrgiraph.redraw(arrresponse,i,mrgiraph.type[i]);
                                                        }
                                                });

                                              },
                            redraw:function redraw(resp,i,type){
                                        $scope.chart.series[i].setData(resp,true);
                                        console.log(i);

                                        $scope.chart.series[i].update({'pointStart':new Date(resp[0][0]).getTime(),name:type},true);
                                    }
                    };


                    currentDate=new Date();
                    $scope.endDefaultTime=currentDate.yyyymmddHHMMSS();
                    $scope.startDefaultTime=new Date(currentDate.setDate(currentDate.getDate()-1)).yyyymmddHHMMSS();
                    $scope.metricGraph.getMetircDetails($scope,ctrl,$scope.startDefaultTime,$scope.endDefaultTime);
                    var stopTime=$interval(function(){$scope.metricGraph.getMetircDetails($scope,ctrl,$scope.startDefaultTime,$scope.endDefaultTime)},$scope.defaultInterval);

                     element.on('$destroy', function() {
                                $interval.cancel(stopTime);
                              });


                                  $scope.last3hours= function(){
                                                              var start = new Date();
                                                              var endTime=start.yyyymmddHHMMSS();
                                                              var end = start.setHours(start.getHours()-3);
                                                              var startTime=new Date(end).yyyymmddHHMMSS();
                                                              $scope.metricGraph.getMetircDetails($scope,ctrl,startTime,endTime);
                                                             };
                                  $scope.last6hours= function(){
                                                               var start = new Date();
                                                               var endTime=start.yyyymmddHHMMSS();
                                                               var end = start.setHours(start.getHours()-6);
                                                               var startTime=new Date(end).yyyymmddHHMMSS();
                                                               $scope.metricGraph.getMetircDetails($scope,ctrl,startTime,endTime);
                                                             };
                                  $scope.last12hours= function(){
                                                               var start = new Date();
                                                               var endTime=start.yyyymmddHHMMSS();
                                                               var end = start.setHours(start.getHours()-12);
                                                               var startTime=new Date(end).yyyymmddHHMMSS();
                                                               $scope.metricGraph.getMetircDetails($scope,ctrl,startTime,endTime);
                                                             };
                                  $scope.last24hours= function(){
                                                                var start = new Date();
                                                                var endTime=start.yyyymmddHHMMSS();
                                                                var end = start.setHours(start.getHours()-24);
                                                                var startTime=new Date(end).yyyymmddHHMMSS();
                                                                $scope.metricGraph.getMetircDetails($scope,ctrl,startTime,endTime);
                                                             };
                                  $scope.last1day= function(){
                                                              var start = new Date();
                                                              var endTime=start.yyyymmddHHMMSS();
                                                              var end = start.setDate(start.getDate()-1);
                                                              var startTime=new Date(end).yyyymmddHHMMSS();
                                                              $scope.metricGraph.getMetircDetails($scope,ctrl,startTime,endTime);

                                                             };
                                  $scope.last2days= function(){
                                                               var start = new Date();
                                                               var endTime=start.yyyymmddHHMMSS();
                                                               var end = start.setDate(start.getDate()-2);
                                                               var startTime=new Date(end).yyyymmddHHMMSS();
                                                               $scope.metricGraph.getMetircDetails($scope,ctrl,startTime,endTime);
                                                             };
                                  $scope.last3days= function(){
                                                               var start = new Date();
                                                               var endTime=start.yyyymmddHHMMSS();
                                                               var end = start.setDate(start.getDate()-3);
                                                               var startTime=new Date(end).yyyymmddHHMMSS();
                                                               $scope.metricGraph.getMetircDetails($scope,ctrl,startTime,endTime);
                                                             };
                                  $scope.last4days= function(){
                                                               var start = new Date();
                                                               var endTime=start.yyyymmddHHMMSS();
                                                               var end = start.setDate(start.getDate()-4);
                                                               var startTime=new Date(end).yyyymmddHHMMSS();
                                                               $scope.metricGraph.getMetircDetails($scope,ctrl,startTime,endTime);
                                                             };
                                  $scope.last1week= function(){
                                                              var start = new Date();
                                                              var endTime=start.yyyymmddHHMMSS();
                                                              var end = start.setDate(start.getDate()-5);
                                                              var startTime=new Date(end).yyyymmddHHMMSS();
                                                              $scope.metricGraph.getMetircDetails($scope,ctrl,startTime,endTime);
                                                             };

                                  $scope.every30= function(){
                                                               $scope.defaultInterval=1800;
                                                             };
                                  $scope.every45= function(){
                                                                $scope.defaultInterval=2700;
                                                             };
                                  $scope.every1hr= function(){
                                                               $scope.defaultInterval=3600;
                                                             };
                                 $scope.every2hrs= function(){
                                                               $scope.defaultInterval=7200;
                                                             };
                                 $scope.submit=function(){
                                            var fromTime=$scope.starttime.yyyymmddHHMMSS();
                                            if($scope.endtime == undefined ){
                                                   var curDate = new Date();
                                                   var toTime=curDate.yyyymmddHHMMSS();
                                            }else{
                                                var toTime=$scope.endtime.yyyymmddHHMMSS();
                                            }

                                            $scope.metricGraph.getMetircDetails($scope,ctrl,fromTime,toTime);
                                 }

                        },

            templateUrl: '/partials/graph.html'

             }
 }



 function cardDirective($q,$interval,HttpResource,getNameToUrlMappingDetails){
            return {
                            restrict:'E',
                            scope:{
                                name: "@name",
                                title:"@title",
                                id:"@id"
                            },
                            controller: function($scope,Chart,getNameToUrlMappingDetails){

                                             $scope.typeicons={app:{color:'green',icon:'phone_android'},desktop:{color:'purple',icon:'computer'}};
                                             $scope.defaultInterval=500000;
                                        },
                            controllerAs: "metricCtrl",
                            bindToController:true,
                            replace: true,
                            templateUrl: '/partials/metrictemplate.html',
                            link :function ($scope, element, attrs,ctrl){

                                                   var functions = {

                                                               getFormattedValue:function(number,formatter){
                                                                                if(formatter=="percent"){
                                                                                     return  this.percentFunction(number);
                                                                                }

                                                                                if(formatter=="number"){
                                                                                       return this.numberFunction(number,2);
                                                                                }

                                                               },

                                                               numberFunction:function nFormatter(num, digits) {
                                                                                 var si = [
                                                                                   { value: 1E18, symbol: "E" },
                                                                                   { value: 1E15, symbol: "P" },
                                                                                   { value: 1E12, symbol: "T" },
                                                                                   { value: 1E9,  symbol: "B" },
                                                                                   { value: 1E6,  symbol: "M" },
                                                                                   { value: 1E3,  symbol: "k" }
                                                                                 ], i;
                                                                                 for (i = 0; i < si.length; i++) {
                                                                                   if (num >= si[i].value) {
                                                                                     return (num / si[i].value).toFixed(digits).replace(/\.0+$|(\.[0-9]*[1-9])0+$/, "$1") + si[i].symbol;
                                                                                   }
                                                                                 }
                                                                                 return num.toString();
                                                                               },
                                                               percentFunction:function percentFormatter(number){
                                                                           return (Math.round(number * 100) / 100).toString()+"%";

                                                               }
                                                          }


                                                   $scope.metricFunction = {

                                                     showLoading:function(){
                                                     $scope.loading=true;
                                                   },
                                                     hideLoading:function(){
                                                        $scope.loading=false;
                                                              },
                                                     getMetricDetails:function($scope,ctrl){

                                                            $scope.metrics=[];
                                                            var mrmetric=this;
                                                            mrmetric.showLoading();
                                                            var typeUrls=getNameToUrlMappingDetails[ctrl.name];
                                                            mrmetric.type=[];
                                                            mrmetric.format=[];
                                                            var responsePromises={};

                                                            for (var i = 0; i < typeUrls.length; i++) {
                                                                var nameURLObject = typeUrls[i];
                                                                console.log(nameURLObject);

                                                                mrmetric.type.push(nameURLObject.type);
                                                                mrmetric.format.push(nameURLObject.format)

                                                                var start = new Date();
                                                                var endTime=start.yyyymmddHHMMSS();
                                                                var end = start.setDate(start.getDate()-1);
                                                                var startTime=new Date(end).yyyymmddHHMMSS();

                                                                var end = start.setDate(start.getDate()-7);
                                                                var startTime2=new Date(end).yyyymmddHHMMSS();

                                                                var totalPromise = HttpResource.getHttp(nameURLObject.totalURL,startTime,endTime);
                                                                var l24hrPromise = HttpResource.getHttp(nameURLObject.sumIntervalURL,startTime,endTime);
                                                                var l7daysPromise = HttpResource.getHttp(nameURLObject.sumIntervalURL,startTime2,endTime);
                                                                responsePromises.total=totalPromise;
                                                                responsePromises.l24hr=l24hrPromise;
                                                                responsePromises.l7days=l7daysPromise;


                                                                $q.defer();
                                                                $q.all(responsePromises)
                                                                    .then(function(results) {
                                                                mrmetric.hideLoading();

                                                                    var metricHash ={};
                                                                    metricHash.type=mrmetric.type.shift();
                                                                    metricHash.format=mrmetric.format.shift();
                                                                    metricHash.totalRes=functions.getFormattedValue(results.total.data.metric,metricHash.format);
                                                                    metricHash.l24hrpromise=functions.getFormattedValue(results.l24hr.data.metric,metricHash.format);
                                                                    metricHash.l7daysPromise=functions.getFormattedValue(results.l7days.data.metric,metricHash.format);
                                                                    metricHash.color=$scope.typeicons[metricHash.type].color;
                                                                    metricHash.icon=$scope.typeicons[metricHash.type].icon;

                                                                    $scope.metrics.push(metricHash);


                                                            });

                                                            }
                                                     }
                                };


                                currentDate=new Date();
                                $scope.endDefaultTime=currentDate.yyyymmddHHMMSS();
                                $scope.startDefaultTime=new Date(currentDate.setDate(currentDate.getDate()-1)).yyyymmddHHMMSS();
                                $scope.metricFunction.getMetricDetails($scope,ctrl);
                                var stopTime=$interval(function(){$scope.metricFunction.getMetricDetails($scope,ctrl)},$scope.defaultInterval);

            }
}


 }


 function typeFormatter(number,type){

       var functions = {

            numberFunction:function nFormatter(num, digits) {
                              var si = [
                                { value: 1E18, symbol: "E" },
                                { value: 1E15, symbol: "P" },
                                { value: 1E12, symbol: "T" },
                                { value: 1E9,  symbol: "G" },
                                { value: 1E6,  symbol: "M" },
                                { value: 1E3,  symbol: "k" }
                              ], i;
                              for (i = 0; i < si.length; i++) {
                                if (num >= si[i].value) {
                                  return (num / si[i].value).toFixed(digits).replace(/\.0+$|(\.[0-9]*[1-9])0+$/, "$1") + si[i].symbol;
                                }
                              }
                              return num.toString();
                            },
            percentFunction:function percentFormatter(number){
                        return number.toFixed(2).toString()+"%";

            }
       }

      if(type =="number"){
            return functions.numberFunction(number,2);
      }
      if(type =="percent"){
            return functions.percentFunction(number);

      }

 }



