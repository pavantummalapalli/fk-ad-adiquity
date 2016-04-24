(function(){

angular.module("brandTemplates", []).run(["$templateCache", function($templateCache) {$templateCache.put("/main/templates/graph.html",'<div class="customborder"><div> <md-toolbar class="demo-toolbar md-primary"> <div class="md-toolbar-tools"> <h3 class="ng-binding">{{graphCtrl.title}}</h3> <span flex="" class="flex"></span> <md-menu> <md-button ng-click="$mdOpenMenu()"> Refresh Every.. </md-button> <md-menu-content> <md-menu-item> <md-button ng-click="every30()"> every 30 minutes </md-button> </md-menu-item> <md-menu-item> <md-button ng-click="every45()"> every 45 minutes </md-button> </md-menu-item> <md-menu-item> <md-button ng-click="every1hr()"> every 1 hour </md-button> </md-menu-item> <md-menu-item> <md-button ng-click="every2hrs()"> every 2 hours </md-button> </md-menu-item> </md-menu-content> </md-menu> <button class="md-icon-button md-button md-ink-ripple active" type="button" aria-label="View Source" ng-class="{active: showSource}" ng-click="graphCtrl.showSource=!graphCtrl.showSource"> <md-icon md-svg-src="/static/img/icons/ic_code_24px.svg" class="ng-scope ng-isolate-scope" aria-hidden="true"><svg xmlns="http://www.w3.org/2000/svg" width="100%" height="100%" viewBox="0 0 24 24" fit="" preserveAspectRatio="xMidYMid meet" focusable="false"> <path fill="none" d="M0 0h24v24H0V0z"></path> <path d="M9.4 16.6L4.8 12l4.6-4.6L8 6l-6 6 6 6 1.4-1.4zm5.2 0l4.6-4.6-4.6-4.6L16 6l6 6-6 6-1.4-1.4z"></path> </svg></md-icon> <div class="md-ripple-container"></div></button> </div></md-toolbar> <div layout="row" class="demo-source-tabs" layout-padding layout-align="end start" ng-show="graphCtrl.showSource" aria-hidden="false"> <div layout="column" layout-padding> <md-button class="md-primary" aria-label="3hours" ng-click="last3hours()">Last 3 Hours</md-button> <md-button class="md-primary" aria-label="6hours" ng-click="last6hours()">Last 6 Hours</md-button> <md-button class="md-primary" aria-label="12hours" ng-click="last12hours()">Last 12 Hours</md-button> <md-button class="md-primary" aria-label="24hours" ng-click="last24hours()">Last 24 Hours</md-button> </div><div layout="column" layout-padding> <md-button class="md-primary" aria-label="1day" ng-click="last1day()">Last 1 Day</md-button> <md-button class="md-primary" aria-label="2days" ng-click="last2days()">last 2 Days</md-button> <md-button class="md-primary" aria-label="4days" ng-click="last3days()">last 4 Days</md-button> <md-button class="md-primary" aria-label="5days" ng-click="last4days()">last 5 Days</md-button> <md-button class="md-primary" aria-label="1week" ng-click="last1week()">last 1 Week</md-button> </div><div layout="row" layout-padding> <form name="form" ng-submit="form.$valid && submit()" novalidate> <md-input-container md-is-error="form.starttime.$invalid && (form.$submitted || form.starttime.$dirty)"> <label>StartDate</label> <input mdc-datetime-picker date="true" time="true" type="text" id="starttime" short-time="true" placeholder="starttime" min-date="minDate" format="YYYY-MM-DD HH:mm:ss" ng-model="starttime" name="starttime"> <div class="errors" ng-messages="form.starttime.$error" ng-if="form.$submitted || form.starttime.$dirty|| form.starttime.$touched"> <div ng-message="required">Required</div></div></md-input-container> <md-input-container > <label>EndDate</label> <input mdc-datetime-picker date="true" time="true" type="text" id="endtime" short-time="true" placeholder="endtime" min-date="minDate" format="YYYY-MM-DD HH:mm:ss" ng-model="endtime" name="endtime"> </md-input-container> <md-input-container> <md-button type="reset" class="md-primary" aria-label="Save Project">clear</md-button> </md-input-container> <md-input-container> <md-button type="submit" class="md-primary" aria-label="Save Project">search</md-button> </md-input-container> </form> </div></div></div><div> <div id="render-part" style="height: 400px"></div></div></div>');
$templateCache.put("/main/templates/metrictemplate.html",'<div flex="40" layout-padding class="card"> <div layout="row" layout-align="start center"> <div flex="30"><span style="color:#3F51B5;"><b>{{metricCtrl.title}}</b></span></div><div layout="row" flex="70" layout-align="end end" > <div ng-repeat="metric in metrics"><ng-md-icon icon="{{metric.icon}}" style="fill:{{metric.color}}" size="20"></ng-md-icon></div></div></div><div layout="row" layout-align="space-between start" > <div layout="column" ng-repeat="metric in metrics" flex="50"> <div layout-wrap layout-margin style="border:1px solid;border-color:{{metric.color}};color:{{metric.color}};"> <div layout="row" layout-align="space-between start"> <div><i style="font-size:30px;">Total</i></div><div><i style="font-size:30px;">{{metric.totalRes}}</i></div></div><div layout="row" layout-align="space-between start"> <div><i style="font-size:30px;">Last 24hrs</i></div><div><i style="font-size:30px;">{{metric.l24hrpromise}}</i></div></div><div layout="row" layout-align="space-between start"> <div><i style="font-size:30px;">Last 7 Days</i></div><div><i style="font-size:30px;">{{metric.l7daysPromise}}</i></div></div></div></div></div></div>')}]);




var brandAdsApp = angular.module('brandAdsApp', ['brandTemplates','ngMdIcons','ngRoute', 'ngMaterial','ngMessages','md.data.table', 'ngMaterialDatePicker']);


brandAdsApp.config(['$routeProvider','$locationProvider',function($routeProvider,$locationProvider) {

    $locationProvider.html5Mode(true);



    $routeProvider
    .when('/', {
      templateUrl:'partials/metrics.html'
    })
    .when('/metric', {
          templateUrl:'partials/metrics.html'
        })
    .when('/brand', {
              templateUrl: 'partials/brandads.html'
    })
    .otherwise({
        templateUrl: 'partials/404.html'
    });

}]);


brandAdsApp.service("brandAdsConstants",getConstants);
brandAdsApp.service("getNameToUrlMappingDetails",["brandAdsConstants",getNameToUrlMappingDetails]);
brandAdsApp.service("Chart",["$http","$q","brandAdsConstants","getNameToUrlMappingDetails",HighGraphs]);
brandAdsApp.service("HttpResource",["$http",HttpResource]);

brandAdsApp.directive('graph', ["$q","$interval","Chart","getNameToUrlMappingDetails",graphDirective]);
brandAdsApp.directive('metric', ["$q","$interval","HttpResource","getNameToUrlMappingDetails",cardDirective]);

brandAdsApp.run(function($rootScope, $templateCache) {
                $rootScope.$on('$routeChangeStart', function(event, next, current) {
                    if (typeof(current) !== 'undefined'){
                        $templateCache.remove(current.templateUrl);
                    }
                });
            });


brandAdsApp.filter('numberformat',typeFormatter);

Date.prototype.yyyymmddHHMMSS = function() {
   var yyyy = this.getFullYear().toString();
   var mm = (this.getMonth()+1).toString(); // getMonth() is zero-based
   var dd  = this.getDate().toString();
   var HH = this.getHours().toString();
   var MM = this.getMinutes().toString();
   var SS = this.getSeconds().toString();
   return yyyy+"-"+(mm[1]?mm:"0"+mm[0])+"-"+(dd[1]?dd:"0"+dd[0])+" "+(HH[1]?HH:"0"+HH[0])+":"+(MM[1]?MM:"0"+MM[0])+":"+(SS[1]?SS:"0"+SS[0]); // padding
  };


brandAdsApp.controller('listController',listController);
brandAdsApp.controller('brandController',brandController);
brandAdsApp.controller('createController',createController);
brandAdsApp.controller('metricController',metricController);


}());