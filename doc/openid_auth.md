# OPENID CONNECT PHP 範例程式
讓我的網站可以使用教育部帳號登入
### 名詞說明
如果我的網站想用教育部的帳號 (本部帳號)登入，我的網站就是一個依賴方(Relying Party, RP)
而提供認證服務的人就叫作身分提供者(identity provider, IdP),
其中提供以OPENID方式來認證者，可以叫作OPENID提供者(OPENID PROVIDER, OP)
### 認證流程
##### 第一步
在我的網站中(RP)，需提供一個連結「使用教育部OPENID登入」，當使用者點擊後，會連到教育部的OPENID 認證平臺
所代參數(Request parameters)：
>
    clientid：需申請
    scope：openid, email, profile
    response_type：code, id_token, token 申請時設定
    redirect_uri：申請時設定
    state：必要，RP需由帶回來的值驗證該值
    nonce：非必要，RP可由OP帶回此值驗證指定的Session
認證方式是GET，範例如下
https://oidc.tanet.edu.tw/oidc/v1/azp?response_type=code&client_id=36d44985e22c44efe8820a804bf29347&redirect_uri=http%3A%2F%2F163.17.38.84%2Fopenidconnect%2Fcallback.php&scope=openid+email+profile&state=1:&nonce=
客戶點擊該連結

#### 第二步
導到OP的認證平台，由使用者輸入本部帳密進行認證

![認證畫面][img1]

認證成功後GET回傳一個code值，這是OP和RP之間的中介認證碼
http://163.17.38.84/openidconnect/callback.php?code=br2qjcGi5imfua29thT_0Qr9JAbLXNT0zOs_0DnwX0w&state=1%3A
會得到
>
    code：OP回傳的中介碼
    state：OP回傳原本帶入的state值
#### 第三步
RP驗證連線後，經由code值利用POST向OP取得 access token，回傳值為：
>string(774) "{"access_token":"tsyJyzdZcDbsny8gnXkj8FvcL2Hljrb9Ja66S-h2_ds","refresh_token":"_KWJmvsEz48GClSfghqBRpFoRrMBWSFOYu_ZW56RBiY","scope":"openid profile userinfo","id_token":"eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJjMWI2YjBiZS1iMGQzLTQ1MmEtOWRjMS03ODc5M2NjOTdlYWMiLCJhdWQiOiIzNmQ0NDk4NWUyMmM0NGVmZTg4MjBhODA0YmYyOTM0NyIsImlzcyI6Imh0dHBzOlwvXC9vaWRjLnRhbmV0LmVkdS50dyIsImV4cCI6MywiaWF0IjoxNDg5NDc2MzIzfQ.Zy7Ar-qhcvhopRCx9qDbwk2i1l-90My2Qs2aG2CCvwManifOT2lC1Y9Ij4C4j-9IYrIMFaEhCFQQVDunazRVrSeRIG0h1CM7QznPO9u0P3n6nc_H26wHp-h7iIJCfn9kUZWlYakNOTsP0O_tCNbAYBU6TYuIrewVp-vr7WMntDKCOR7tuPPcxpRR4d90UpcrHxowEwq3HPn-3Fvw69cxlIg3q3fKlyL9PEXZ1vy0HJBHAypoMOIWPqXHtj5h_C_uwyHh3wXqr4Naln_B3eci8zWf5A6TBYcfWOLLakHk5CYoT05i8hPf3eUWFBBR3j1qhGUzCosxoXiS5CdO2pG4gw","token_type":"Bearer","expires_in":3600}" 

access token的有效期為3600秒
#### 第四步
RP使用access token向OP取得user info或 user profile 等資料
>string(90) "{"sub":"c1b6b0be-b0d3-452a-9dc1-xxxxxxxxxx","name":"張００","email":"axeredu@edu.tw"}" 

[img1]: openid_login.png  "認證畫面"
