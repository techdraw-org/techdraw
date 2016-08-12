
angular
    .module('desksApp', [])
    .controller('DesksController', function($http, $window) {
        this.desks = [
            { a: 500, b:100, width: 18, edges: [true,true,true,true] },
            { a: 300, b:200, width: 18, edges: [true,false,true,false] },
            { a: 300, b:200, width: 12, edges: [true,false,true,false] },
            { a: 300, b:200, width: 12, edges: [true,false,true,false] },
            { a: 300, b:200, width: 12, edges: [true,false,true,false] }
        ];
        this.submit = function() {
            $http.post('/svg',this.desks)
                .success(function (data) {
                    var blob = new Blob([data], {type: "image/xml+svg"});
                    $window.saveAs(blob, "sheet.svg");
                });
        };
    });