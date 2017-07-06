<%-- 
    Document   : welcome
    Created on : 2017/6/11, 上午 12:01:50
    Author     : igogo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Welcome Page</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>


    </head>
    <body>
        <div class="container">
            <nav class="navbar navbar-fixed-top">

                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand">demoApp</a>
                </div>
                <div id="navbar" class="collapse navbar-collapse">
                    <ul class="nav navbar-nav">
                        <li class="active"><a href="logout">Logout</a></li>

                    </ul>
                </div><!--/.nav-collapse -->

            </nav>
        </div>
        <div class="container">
            <div class="jumbotron">
                <h3> Using Access Token example</h3>
                <br/>
                <br/>
                <br/>
                <%= request.getAttribute("msg")%>
                <br/>
                <br/>
                <br/>
                <%= request.getAttribute("idTokenParsed")%>
                <br/>
                <br/>
                <br/>
                sub: <%= request.getAttribute("sub")%>
                <br/>
                openid2_id: <%= request.getAttribute("open2_id")%>
            </div>

            下一步, 進行帳號綁定
            <br/>
            <a href="idBinding" class="btn btn-info" role="button">帳號綁定</a>
        </div>




    </body>
</html>
