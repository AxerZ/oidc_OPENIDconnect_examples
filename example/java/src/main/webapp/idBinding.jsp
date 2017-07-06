<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Start Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <script src="https://unpkg.com/vue"></script>
        <script src="https://unpkg.com/axios/dist/axios.min.js"></script>

    </head>
    <body>
        <div id="app">
            <div class="container">
                <!-- Static navbar -->
                <nav class = "navbar navbar-default" role = "navigation">
                    <div class="container-fluid">
                        <div class="navbar-header">
                            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                                <span class="sr-only">Toggle navigation</span>
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                            </button>
                            <a class="navbar-brand">demoApp</a>
                        </div>
                        <div id="navbar" class="navbar-collapse collapse">
                            <ul class="nav navbar-nav">
                                <li><a href="logout">登出</a></li>



                        </div>
                    </div><!--/.container-fluid -->
                </nav>

                <!-- Main component for a primary marketing message or call to action -->
                <div class="jumbotron">
                    <!--                    <form action="/demoApp/demoAppLogin" method="post" id="loginform" class="form-horizontal" role="form">
                    
                                            <div style="margin-bottom: 25px" class="input-group">
                                                <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                                                <input id="login-username" type="text" class="form-control" required="required" name="username" value="" placeholder="username">                                        
                                            </div>
                    
                                            <div style="margin-bottom: 25px" class="input-group">
                                                <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
                                                <input id="login-password" type="password" class="form-control" required="required" name="password" placeholder="password">
                                            </div>
                    
                                            <div style="margin-top:10px" class="form-group">
                    
                    
                                                <div class="col-sm-12 controls">
                                                    <input type="submit" class="btn btn-default" value="進行帳號綁定">
                                                </div>
                                            </div>
                    
                                        </form>   -->
                    <div class="form-group row">
                        <label for="example-text-input" class="col-xs-2 col-form-label">帳號:</label>
                        <div class="col-xs-10">
                            <input class="form-control" type="text" v-model="item.username">
                            <br/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label for="example-text-input" class="col-xs-2 col-form-label"> 密碼：</label>
                        <div class="col-xs-10">
                            <input class="form-control" type="password" v-model="item.password">
                            <br/>
                        </div>
                    </div>
                    <button class="btn btn-default" v-on:click="say">帳號綁定</button>
                    <br/>
                    <h3 style="color:red;"> {{msg}}</h3>

                </div>  

                <div class="jumbotron">
                    我沒有會員帳號<br/>
                    <!--<a href="#" class="btn btn-info" role="button">申請新帳號</a>-->
                    <button class="btn btn-default" v-on:click="register">申請新帳號</button>
                    {{registerMsg}}
                </div>
            </div>

        </div> <!-- /container -->
        <script>
Vue.prototype.$http = axios;
var app = new Vue({
    el: '#app',
    data: {
        msg: '',
        registerMsg:'',
        item: {
            username: '',
            password: '',
            act: 'update'
        }
    },
    methods: {
        register: function () {
            console.log('register member.');
            this.registerMsg = "請填寫帳號申請單"
        },
        say: function () {
            url = '/demoApp/api/users/' + this.item.username;
            var vm = this;
            console.log('make request');
            console.log(vm.item.username);
            if (vm.item.username.length == 0 || vm.item.password.length == 0) {
                vm.msg = '欄位必填';
            } else {
                this.$http.put(url, vm.item
                        ).then((response) => {
                    // success callback
                    console.log(response.data.result);

                    if (response.data.result === 'ok') {
                        console.log('redirect page');
                        window.location.href = 'userhome';
                    }
                    vm.msg = decodeURI(response.data.result);
                }, (response) => {
                    // error callback
                });
            }


        }
    }
})

        </script>

    </body>
</html>
