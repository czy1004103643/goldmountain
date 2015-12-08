'use strict';

/* App Module */

var app = angular.module('app', [ 'ngRoute', 'appControllers' ]);

app.config([ '$routeProvider', function($routeProvider) {
	// $routeProvider.when('/phones', {
	// templateUrl : 'partials/phone-list.html',
	// controller : 'PhoneListCtrl'
	// }).when('/phones/:phoneId', {
	// templateUrl : 'partials/phone-detail.html',
	// controller : 'PhoneDetailCtrl'
	// }).otherwise({
	// redirectTo : '/phones'
	// });

	$routeProvider.when('/reports', {
		templateUrl : 'html/reportList.html',
		controller : 'ReportListController'
	}).when('/reports/:report_id', {
		templateUrl : 'html/reportDetail.html',
		controller : 'ReportDetailController'
	}).otherwise({
		redirectTo : '/reports'
	});
} ]);