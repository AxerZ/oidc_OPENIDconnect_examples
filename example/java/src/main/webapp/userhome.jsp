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
            <div class="jumbotron" id="app">
                <%= request.getAttribute("msg")%>
                <br/>
                <!--{{item}}-->
                <br/>  <br/>
                <table class="table">
                    <thead>
                        <tr>
                            <th>username</th>
                            <th>password</th>
                            <th>nickname</th>
                            <th>coins</th>
                            <th>mappingID</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>{{item.username}}</td>
                            <td>{{item.password}}</td>
                            <td>{{item.nickname}}</td>
                            <td>{{item.coins}}</td>
                            <td>{{item.mappingID}}</td>

                        </tr>
                        <tr>
                            <td>
                                <button v-on:click="reset" class="btn btn-danger"> 
                                    資料重置
                                </button>
                            </td>
                        </tr>

                    </tbody>
                </table>
            </div>  
        </div>

    </div> <!-- /container -->



    <script>
Vue.prototype.$http = axios;
var app = new Vue({
    el: '#app',
    data: {
        msg: 'hello',
        item: {}
    },
    created: function () {
        console.log('make request for user information.');
        var vm = this;
        let username = '<%= request.getAttribute("username")%>';
        url = '/demoApp/api/users/' + username;
        this.$http.get(url, username
                ).then((response) => {
            // success callback
            console.log(response.data);
            vm.item = response.data;
        }, (response) => {
            // error callback
        });
    },
    methods: {
        reset: function () {
            var vm = this;
            this.$http.delete(url, {
                'act': 'reset'
            }
            ).then((response) => {
                // success callback
                console.log(response.data);
                vm.item = response.data;


            }, (response) => {
                // error callback
            });
        }
    }
})

    </script>

</body>
</html>
