
angular
    .module('desksApp', [])
    .controller('DesksController', function() {
        this.desks = [
            { a: 500, b:100 },
            { a: 300, b:200 }
        ];
    });