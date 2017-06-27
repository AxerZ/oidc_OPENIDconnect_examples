package app.demo.demoapp;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

@WebServlet(name = "Welcome", urlPatterns = {"/welcome"})
public class Welcome extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(Callback.class);
    URI userinfoEndpointURL;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.net.URISyntaxException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, URISyntaxException, java.text.ParseException, BadJOSEException, BadJOSEException, JOSEException, JOSEException {
        HttpSession session = request.getSession();

        if (session.getAttribute("accessToken") == null) {

            request.setAttribute("msg", "Not Allowed");

            RequestDispatcher dispatcher = request
                    .getRequestDispatcher("forbidden.jsp");
            dispatcher.forward(request, response);

        } else {

            BearerAccessToken accessToken = new BearerAccessToken((session.getAttribute("accessToken").toString()));
            String idToken = session.getAttribute("idToken").toString();
//            logger.info("session idToken:" + idToken);
            //https://connect2id.com/products/nimbus-oauth-openid-connect-sdk/guides/java-cookbook-for-openid-connect-public-clients

            userinfoEndpointURL = new URI(session.getAttribute("userinfoEndpointURL").toString());

            // Append the access token to form actual request
            UserInfoRequest userInfoReq = new UserInfoRequest(userinfoEndpointURL, accessToken);
            HTTPResponse userInfoHTTPResponse = null;
            try {
                //觀察送出的header
                logger.info("userinfo request header:"+ userInfoReq.toHTTPRequest().getHeaders().toString());
                userInfoHTTPResponse = userInfoReq.toHTTPRequest().send();
            } catch (SerializeException | IOException e) {
                // TODO proper error handling
            }

            UserInfoResponse userInfoResponse = null;
            try {
                userInfoResponse = UserInfoResponse.parse(userInfoHTTPResponse);
            } catch (ParseException e) {
                // TODO proper error handling
            }

            if (userInfoResponse instanceof UserInfoErrorResponse) {
                ErrorObject error = ((UserInfoErrorResponse) userInfoResponse).getErrorObject();
                // TODO error handling
            }

            UserInfoSuccessResponse successUserInfoResponse = (UserInfoSuccessResponse) userInfoResponse;
            String msg = successUserInfoResponse.getUserInfo().toJSONObject().toString();
            logger.info(msg);
            request.setAttribute("msg", msg);

            // Set up a JWT processor to parse the tokens and then check their signature
            // and validity time window (bounded by the "iat", "nbf" and "exp" claims)
            ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();

            // The public RSA keys to validate the signatures will be sourced from the
// OAuth 2.0 server's JWK set, published at a well-known URL. The RemoteJWKSet
// object caches the retrieved keys to speed up subsequent look-ups and can
// also gracefully handle key-rollover
            String jwksURI = session.getAttribute("jwksURI").toString();
//            JWKSource keySource = new RemoteJWKSet(new URL("https://oidc.tanet.edu.tw/oidc/v1/jwksets"));
            JWKSource keySource = new RemoteJWKSet(new URL(jwksURI));

            // The expected JWS algorithm of the access tokens (agreed out-of-band)
            JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

            // Configure the JWT processor with a key selector to feed matching public
// RSA keys sourced from the JWK set URL
            JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
            jwtProcessor.setJWSKeySelector(keySelector);

// Process the token
            SecurityContext ctx = null; // optional context parameter, not required here
            JWTClaimsSet claimsSet = jwtProcessor.process(idToken, ctx);

            logger.info(claimsSet.toString());
            String idTokenParsed = claimsSet.toString();
            request.setAttribute("idTokenParsed", idTokenParsed);
            RequestDispatcher dispatcher = request
                    .getRequestDispatcher("welcome.jsp");
            dispatcher.forward(request, response);

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
        } catch (URISyntaxException ex) {
            java.util.logging.Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.text.ParseException ex) {
            java.util.logging.Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadJOSEException ex) {
            java.util.logging.Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JOSEException ex) {
            java.util.logging.Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (URISyntaxException ex) {
            java.util.logging.Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, null, ex);
        } catch (java.text.ParseException ex) {
            java.util.logging.Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadJOSEException ex) {
            java.util.logging.Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JOSEException ex) {
            java.util.logging.Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
