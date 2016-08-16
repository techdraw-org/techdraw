
angular
    .module('desksApp', [])
    .controller('DesksController', function($http, $window) {
        this.model = {
            groups : [
                {
                    material : { decor: 'Svetly buk', width : 18},
                    desks: [
                        {key: 'a01', a: 500, b: 100, edges: [true, true, true, true]},
                        {key: 'a02', a: 300, b: 200, edges: [true, false, true, false]},
                    ]
                },
                {
                    material : { decor: 'Svetly buk', width : 12},
                    desks: [
                        {key: 'b01', a: 300, b: 200, edges: [true, false, true, false]},
                        {key: 'c01', a: 300, b: 200, edges: [true, false, true, false]},
                        {key: 'd01', a: 300, b: 200, edges: [true, false, true, false]}
                    ]
                }
            ],
        };
        this.formattedPreview = false;
        this.generateSVG = function() {
            $http.post('/svg',this.model, {responseType:'arraybuffer'})
                .success(function (data) {
                    var blob = new Blob([data], {type: "image/xml+svg"});
                    $window.saveAs(blob, "sheet.svg");
                });
        };
        this.generatePDF = function() {
            $http.post('/pdf',this.model, {responseType:'arraybuffer'})
                .success(function (data) {
                    var blob = new Blob([data], {type: "application/pdf"});
                    $window.saveAs(blob, "sheets.pdf");
                });
        };
        this.addDesk = function(group) {
            group.desks.push({});
        };
        this.removeDesk = function(group) {
            group.splice(index,1);
        };
        this.newDeskPartGroup = function() {
            this.model.groups.push({
                desks: [{}]
            });
        };
    });