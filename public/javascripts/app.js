var app = angular.module('app', []);

app.config(['$logProvider', function ($logProvider) {
    $logProvider.debugEnabled(true);
}]);

app.controller('MainController', ['$http', '$scope', function ($http, $scope) {
	$scope.request = {
		uri: '',
		busy: false
	};

	$scope.sendRequest = function () {
	    $scope.request.busy = true;

		$http.post('set-config?uri=' + $scope.request.uri, $scope.request).then(function (res) {
			$scope.request.busy = false;
			$scope.response = res;
		});
	};
}]);