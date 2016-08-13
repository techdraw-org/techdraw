
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
        this.formattedPreview = false;
        this.generateSVG = function() {
            $http.post('/svg',this.desks, {responseType:'arraybuffer'})
                .success(function (data) {
                    var blob = new Blob([data], {type: "image/xml+svg"});
                    $window.saveAs(blob, "sheet.svg");
                });
        };
        this.generatePDF = function() {
            $http.post('/pdf',this.desks, {responseType:'arraybuffer'})
                .success(function (data) {
                    var blob = new Blob([data], {type: "application/pdf"});
                    $window.saveAs(blob, "sheets.pdf");
                });
        };
        this.addDesk = function() {
            this.desks.push({});
        };
        this.removeDesk = function(index) {
            this.desks.splice(index,1);
        };
    });