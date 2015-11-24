app.controller("ReportListController", function($scope, $http) {
	$http.get("http://localhost/summaryPlan/reportList").success(
			function(response) {
				if (response.status == "SUCCESS") {
					
					$scope.reportList = response.data;
					
				}
				else{
//					$http.get("http://localhost/summaryPlan/reportlist");
				}
			});
});