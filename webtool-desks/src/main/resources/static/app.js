
angular
    .module('desksApp', ['ngDialog', 'ngStorage'])
    .service('ToolbarService', function($rootScope){
        $rootScope.ToolbarService = this;
        this.groups = {};
        this.addGroup = function(location, group) {
            var groupLocation = this.groups[location];
            if(!groupLocation)
                groupLocation = this.groups[location] = [];
            groupLocation.push(group);
        };
        this.removeGroup = function(location, group) {
            var groupLocation = this.groups[location];
            if(groupLocation) {
                groupLocation.splice(groupLocation.indexOf(group), 1);
            }
        };
    })
    .component('desksEditor', {
        templateUrl: "desks/desksEditorComponent.html",
        bindings: { model: '=' },
        controller : function(ConfirmationService,ToolbarService) {
            this.computeMaterialName = function(material) {
                return material.decor + " " + material.width + "mm";
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
            this.addDesk = function() {
                this.model.desks.push({
                    edges: [true, true, true, true]
                });
            };
            this.removeDesk = function(group,index) {
                group.desks.splice(index,1);
            };
            this.newDeskPartGroup = function() {
                this.model.groups.push({
                    material : { decor: 'Svetly buk', width : 1},
                    desks: [{}]
                });
            };
            this.$onInit = function() {
                this.toolbarItems = [
                    {
                        title : 'New group',
                        description : 'Add new desk group',
                        callback : this.newDeskPartGroup.bind(this)
                    }
                ];
                ToolbarService.addGroup('editing', this.toolbarItems);
            };
        }
    })
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
            materials : [
                { decor: 'Svetly buk', width : 18},
                { decor: 'Svetly buk', width : 12}
            ],
            desks: [
                {key: 'a01', a: 500, b: 100, edges: [true, true, true, true], material: "0"},
                {key: 'a02', a: 300, b: 200, edges: [true, false, true, false], material: "0"},
                {key: 'b01', a: 300, b: 200, edges: [true, false, true, false], material: "1"},
                {key: 'c01', a: 300, b: 200, edges: [true, false, true, false], material: "1"},
                {key: 'd01', a: 300, b: 200, edges: [true, false, true, false], material: "1"}
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
            for(var di = 0; di < this.model.desks.length; di++) {
                var desk = this.model.desks[di];
                var e = desk.edges;
                if(e)
                    desk.edges = [e[0], e[1], e[2], e[3]];
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