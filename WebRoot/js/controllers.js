'use strict';

var appControllers = angular.module('appControllers', []);

appControllers.controller('ReportListController', [ '$scope', '$http',
		function($scope, $http) {
			$http.get("/report/reportList").success(function(response) {
				$scope.reportList = response.reportList;
			});
		} ]);

appControllers.controller('ReportDetailController', [ '$scope', '$routeParams',
		'$http', function($scope, $routeParams, $http) {
			$scope.report_id = $routeParams.report_id;

			var url = "";

			if ($scope.report_id == "add") {
				url = "/report/add";

			} else {
				url += "/report/query?report_id=" + $scope.report_id;
			}

			$http.get(url).success(function(response) {

				$scope.report = response;
				$scope.contents = response.contents;

			});

			$scope.remove = function(report_id) {
				if (confirm("确定删除？")) {
					$http.get("/report/delete?report_id=" + $scope.report_id);
				}
			};

		} ]);