'use strict';

var appControllers = angular.module('appControllers', []);

appControllers.controller('ReportListController', [ '$scope', '$http',
		function($scope, $http) {
			$http.get("/report/reportList").success(function(response) {
				$scope.reportList = response.reportList;
			});
		} ]);

appControllers.controller('ReportDetailController', [
		'$scope',
		'$routeParams',
		'$http',
		function($scope, $routeParams, $http) {
			$scope.report_id = $routeParams.report_id;

			$http.get("/report/query?report_id=" + $scope.report_id).success(
					function(response) {

						$scope.report = response;
						$scope.contents = response.contents;

					});

						$scope.submit = function() {
			
						};

		} ]);