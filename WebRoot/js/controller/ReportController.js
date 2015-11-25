app.controller("ReportController", function($scope, $http, $rootScope) {
	
	var report_id = $rootScope.root_report_id;
	
	alert(report_id);
	
//	$http.get("/report/query?report_id=" + 1)
//			.success(function(response) {
//				$scope.report = response;
//			});
});