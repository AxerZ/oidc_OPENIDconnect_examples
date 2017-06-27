<%-- 
    Document   : curl
    Created on : 2017/6/11, 上午 12:01:50
    Author     : igogo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Welcome Page</title>
      
    </head>
    <body>
     
        curl -d "client_id=CLIENTID&client_secret=CLIENTSECRET&redirect_uri=https://coding.teliclab.info/demoApp/callback&grant_type=authorization_code&code=AUTHZCODE" https://oidc.tanet.edu.tw/oidc/v1/token
  


    </body>
</html>
