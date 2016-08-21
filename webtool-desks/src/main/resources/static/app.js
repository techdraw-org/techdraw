
angular
    .module('desksApp', ['ngDialog', 'ngStorage'])
    .controller('DesksController', function($http, $window, $localStorage, ngDialog) {
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
        this.removeDesk = function(group,index) {
            group.desks.splice(index,1);
        };
        this.newDeskPartGroup = function() {
            this.model.groups.push({
                desks: [{}]
            });
        };
        this.open = function() {
            var vm = this;
            ngDialog.openConfirm({
                template: 'dialog/open.html',
                className: 'ngdialog-theme-default'
            }).then(function(){
                vm.loadDocument('doc'); // TODO: use id
            });
        };
        this.save = function() {
            if(!this.documentId) {
                var vm = this;
                ngDialog.openConfirm({
                    template: 'dialog/save.html',
                    className: 'ngdialog-theme-default',
                }).then(function(){
                    vm.saveDocument('doc'); // TODO: use id
                });
            }
        };
        this.loadDocument = function(id) {
            this.model = JSON.parse(JSON.stringify($localStorage.documents[id]));
        };
        this.saveDocument = function(id) {
            if(!$localStorage.documents)
                $localStorage.documents = {};
            $localStorage.documents[id] = JSON.parse(JSON.stringify(this.model));
        }
    });