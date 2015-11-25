app.controller("ReportListController", function($scope, $http, $rootScope) {
	$http.get("/report/reportList").success(function(response) {

		$scope.reportList = response.reportList;

	});

	$scope.goToReport = function(report_id) {

		alert(report_id);

		$rootScope.root_report_id = report_id;

	}

});