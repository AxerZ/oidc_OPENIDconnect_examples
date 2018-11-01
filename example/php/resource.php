<?php
session_start();
$accesstoken=$_SESSION['access_token'];
//請修改您的 openid provider URL
print 
$url= "https://tyc.sso.edu.tw/cncresource/api/v1/";
    $header= array( "Authorization: Bearer $accesstoken" );
    $options = array(
        'http' => array(
          'header'  => $header,
          'method'  => 'GET',
          'content' => ''
        ));
    $context = stream_context_create($options);
print "<h3>GUID scope guid</h3>";
print    $result = file_get_contents($url."guid", false, $context);
print "<h3>EDUINFO scope eduinfo</h3>";
print    $result = file_get_contents($url."eduinfo", false, $context);
print "<h3>PERSONID scope personid</h3>";
print    $result = file_get_contents($url."personid", false, $context);



