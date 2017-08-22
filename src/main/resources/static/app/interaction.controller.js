    'use strict';


    //InteractionController.$inject = ['$http'];

    app.controller("InteractionController",['$scope', '$http', function($scope, $http) {
        var url = "/isLiked/1";
        var imageLikePromise
            = $http.get(url);
        imageLikePromise.then(function(response){
            $scope.like = response.data;
        });

        $scope.likeCount = 5;


        $scope.likes = function (id) {


            var url = "/like2/1";
            var imageLikePromise = $http.get(url);
            imageLikePromise.then(function(response){
                $scope.like = response.data;
            });
            if ($scope.like == true){
                $scope.likeCount++
            }
            else{
                $scope.likeCount--;
            }
           

       /* function like(id){
            console.log(id);
            var url = "/like2/id";
            var imageLikePromise = $http.get(url);
            imageLikePromise.then(function(response){
                $scope.like = response.data;
            });

            url = "/likeCount/id";
            var imageLikePromise = $http.get(url);
            imageLikePromise.then(function(response){
                $scope.likeCount = response.data;
            });
        }*/

    }
    }]);
