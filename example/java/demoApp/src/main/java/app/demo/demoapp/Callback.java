/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.demo.demoapp;

import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
//import com.nimbusds.openid.connect.sdk.OIDCAccessTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author igogo
 */
@WebServlet(name = "Callback", urlPatterns = {"/callback"})
public class Callback extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(Callback.class);
    ClientID clientID;
    Secret clientSecret;
    URI tokenEndpoint;
    AuthorizationCode code;
    URI callback;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws com.nimbusds.oauth2.sdk.SerializeException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SerializeException, URISyntaxException {
        HttpSession session = request.getSession();

        callback = new URI(session.getAttribute("redirectURI").toString());
        clientID = new ClientID(session.getAttribute("clientID").toString());
        clientSecret = new Secret(session.getAttribute("clientSecret").toString());
        tokenEndpoint = new URI(session.getAttribute("tokenEndpoint").toString());

        try {

            //authCode 如果還沒取得,表示要從Auth Server 發過來
            if (session.getAttribute("code") == null) {
                //logger.info(request.getQueryString());
                String queryString = request.getQueryString();
                String responseURL = "https:///path/?" + queryString;
                logger.info(responseURL);

                logger.info("session state:" + session.getAttribute("state"));
                String state = session.getAttribute("state").toString();

                AuthenticationResponse authResponse = AuthenticationResponseParser.parse(new URI(responseURL));
                AuthenticationSuccessResponse successResponse = (AuthenticationSuccessResponse) authResponse;
                // Retrieve the authorisation code
                code = successResponse.getAuthorizationCode();
                logger.info("3. auth code grant.");
                logger.info("code:" + code.getValue());
                session.setAttribute("code", code.getValue());

                logger.info("return state:" + successResponse.getState().toString());
                assert successResponse.getState().toString().equals(state);
                logger.info("the same state");
            } else {
                logger.info("auth code from session:" + session.getAttribute("code").toString());
                code = new AuthorizationCode(session.getAttribute("code").toString());
            }

            String getCodeOption = request.getParameter("getCodeOption");
            if (getCodeOption == null) {
                logger.info("get code option is null");

                RequestDispatcher dispatcher = request
                        .getRequestDispatcher("getCodeOption.jsp");
                dispatcher.forward(request, response);

            } else {
                if (getCodeOption.equals("no")) {

                    logger.info("after get code option: no");
                    logger.info(String.format("curl -d \"client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code&code=%s\" %s", clientID.getValue(), clientSecret.getValue(), callback.toString(), code.getValue(), tokenEndpoint.toString()));
                    RequestDispatcher dispatcher = request
                            .getRequestDispatcher("curlGetToken.jsp");
                    dispatcher.forward(request, response);

                } else {
                    logger.info("after get code option: yes");

//                    URI callback = new URI(redirectURI);
                    AuthorizationGrant codeGrant = new AuthorizationCodeGrant(code, callback);

//     
//                    clientID = new ClientID(session.getAttribute("clientID").toString());
//                    clientID = new ClientID(clientIDStr);
//                    clientSecret = new Secret(session.getAttribute("clientSecret").toString());
//                    clientSecret = new Secret(clientSecretStr);
                    ClientAuthentication clientAuth = new ClientSecretPost(clientID, clientSecret);

                    // Make the token request
                    TokenRequest tokenRequest
                            = new TokenRequest(tokenEndpoint, clientAuth, codeGrant);

//            logger.info("Token Authorization Header : " + httpRequest.getAuthorization());
                    logger.info("4. Access Token Request");
                    TokenResponse tokenResponse = OIDCTokenResponseParser.parse(tokenRequest.toHTTPRequest().send());
                    if (tokenResponse instanceof TokenErrorResponse) {

                        TokenErrorResponse errorResponse = (TokenErrorResponse) tokenResponse;
                        logger.info("error happened!!");
                        logger.info(errorResponse.getErrorObject().getCode());
                        logger.error(String.format("%d", errorResponse.getErrorObject().getHTTPStatusCode()));

                    } else {
                        OIDCTokenResponse accessTokenResponse = (OIDCTokenResponse) tokenResponse;
                        BearerAccessToken accessToken
                                = accessTokenResponse.getOIDCTokens().getBearerAccessToken();

                        SignedJWT idToken = (SignedJWT) accessTokenResponse.getOIDCTokens().getIDToken();
                        RefreshToken refreshToken = (RefreshToken) accessTokenResponse.getOIDCTokens().getRefreshToken();

                        logger.info("5 Access Token Grant.");
                        logger.info("access token value:" + accessToken.getValue());
                        logger.info("idToken value:" + idToken.getParsedString());
                        session.setAttribute("accessToken", accessToken.getValue());
                        session.setAttribute("idToken", idToken.getParsedString());
                        response.sendRedirect("welcome");
                    }
                }

            }

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SerializeException | URISyntaxException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SerializeException | URISyntaxException ex) {
            logger.debug(ex.getMessage());
        }
    }

}
