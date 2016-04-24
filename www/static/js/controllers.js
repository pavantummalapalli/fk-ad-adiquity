function listController($scope,$location,brandAdsConstants){

console.log("app fillrate"+brandAdsConstants.appFillRate);

$scope.object = {};


$scope.object.view = "";

       //  $scope.view ="";
         $scope.viewFilters = {
                            'active': ["running","paused","draft"],
                            'inactive':["completed","aborted"]
                        };

          $scope.submit = function() {
               console.log("i am submitting");
              };

          $scope.$watch('object.view', function(newval, oldval) {
            if( newval ) {
            console.log("valis "+oldval);
              $scope.filters = $scope.viewFilters[newval];
            }
            else {
              $scope.filters = [];
            }

            // delete the dependent selection, if the master changes
            if( newval !== oldval ) {
              $scope.object.filterval = null;
            }
          });
}


function createController($scope,$rootScope){

}


function brandController($scope,$templateCache){




}

function metricController($scope,$templateCache){

}


