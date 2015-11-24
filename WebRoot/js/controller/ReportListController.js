app.controller("ReportListController", function($scope, $http) {
	$http.get("http://localhost/report/reportList").success(function(response) {

		$scope.reportList = response.reportList;

	});
});