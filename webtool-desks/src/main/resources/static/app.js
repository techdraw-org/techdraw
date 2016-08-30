
angular
    .module('desksApp', ['ngDialog', 'ngStorage'])
    .service('ConfirmationService', function(ngDialog) {
        this.open = function(text) {
            return ngDialog.openConfirm({
                data: {
                    title: text.title,
                    message: text.message,
                    confirm: text.confirm
                },
                template: 'dialog/confirm.html',
            });
        };
    })
    .controller('DesksController', function($http, $window, $localStorage, $scope, ngDialog, ConfirmationService) {
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
            pageStyle : {
                marginTop : 18,
                marginBottom: 15,
                marginLeft : 12,
                marginRight : 12,
            },
        };
        this.formattedPreview = false;

        this.checkStructure = function() {
            for(var gi = 0; gi < this.model.groups.length; gi++) {
                var group = this.model.groups[gi];
                for(var di = 0; di < group.desks.length; di++) {
                    var desk = group.desks[di];
                    var e = desk.edges;
                    desk.edges = [e[0], e[1], e[2], e[3]];
                }
            }
        };
        this.generateSVG = function() {
            this.checkStructure();
            $http.post('/svg',this.model, {responseType:'arraybuffer'})
                .success(function (data) {
                    var blob = new Blob([data], {type: "image/xml+svg"});
                    $window.saveAs(blob, "sheet.svg");
                });
        };
        this.generatePDF = function() {
            this.checkStructure();
            $http.post('/pdf',this.model, {responseType:'arraybuffer'})
                .success(function (data) {
                    var blob = new Blob([data], {type: "application/pdf"});
                    $window.saveAs(blob, "sheets.pdf");
                });
        };
        this.removeGroup = function(index){
            var vm = this;
            ConfirmationService.open({
                title: 'Remove group',
                message: 'Are you sure, that you want remove this group?',
                confirm: 'Remove',
            }).then(function(){
                vm.model.groups.splice(index,1);
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
            var vm = this;
            if(!$localStorage.documents)
                $localStorage.documents = {};
            var save = function() {
                $localStorage.documents[id] = JSON.parse(JSON.stringify(vm.model));
            };

            // ask overwrite
            if($localStorage.documents[id]) {
                ConfirmationService.open({
                    title: 'Overwrite previous',
                    message: 'This will overwrite previously saved stuff..',
                    confirm: 'Overwrite',
                })
                .then(save);
            } else {
                save();
            }

        }
    });