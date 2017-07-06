package app.demo.demoapp;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "Login", urlPatterns = {"/OIDCLogin"})
public class OIDCLogin extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(OIDCLogin.class);
    ClientID clientID;
    Secret clientSecret;
    URI tokenEndpoint;
    URI authEndpoint;
    String jwksURI;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SerializeException {
        HttpSession session = request.getSession();
        String login = request.getParameter("login");
        if (login == null) {
            login = "moe";  //預設教育部帳號服務登入
        }
        URI userinfoEndpointURL;
        String redirectURI;
//        logger.info(login);
        try {

            if (login.equals("google")) {
//                google clientid
                clientID = new ClientID("432402061677-ivsu1a14dtah90f4on0p5tsirfktfj8j.apps.googleusercontent.com");
                clientSecret = new Secret("I9Jgk7y9RdxdfptniK51mQxg");
                authEndpoint = new URI("https://accounts.google.com/o/oauth2/auth");
                tokenEndpoint = new URI("https://www.googleapis.com/oauth2/v4/token");
                userinfoEndpointURL = new URI("https://www.googleapis.com/oauth2/v3/userinfo");
                jwksURI = "https://www.googleapis.com/oauth2/v3/certs";
//                redirectURI = "http://localhost:8080/demoApp/callback";
                redirectURI = "https://coding.teliclab.info/demoApp/callback";

            } else {
                //moe clientid
                clientID = new ClientID("12841a641b912c7c7e50f7337805e5bd");
                clientSecret = new Secret("0167d1115f2560a111c2b8db6d28e47e33ef7dd6f408fffa8ab484516ae87145");
                authEndpoint = new URI("https://oidc.tanet.edu.tw/oidc/v1/azp");
                tokenEndpoint = new URI("https://oidc.tanet.edu.tw/oidc/v1/token");
                userinfoEndpointURL = new URI("https://oidc.tanet.edu.tw/oidc/v1/userinfo");
                jwksURI = "https://oidc.tanet.edu.tw/oidc/v1/jwksets";
                redirectURI = "https://coding.teliclab.info/demoApp/callback";

            }

            session.setAttribute("clientID", clientID.getValue());
//            logger.info("clientID:" + clientID.getValue());
            session.setAttribute("clientSecret", clientSecret.getValue());
//            logger.info("client Secret:" + clientSecret.getValue());
            session.setAttribute("tokenEndpoint", tokenEndpoint.toString());
//            logger.info("token endpoint"+ tokenEndpoint.toString());
            session.setAttribute("userinfoEndpointURL", userinfoEndpointURL.toString());
            session.setAttribute("jwksURI", jwksURI);
            // The client callback URI, typically pre-registered with the server
//            URI callback = new URI("https://coding.teliclab.info/demoApp/callback");

            URI callback = new URI(redirectURI);
            session.setAttribute("redirectURI", redirectURI);

            // Generate random state string for pairing the response to the request
            State state = new State();
            session.setAttribute("state", state.toString());

            // Generate nonce
            Nonce nonce = new Nonce();

            // Compose the request (in code flow)
            AuthenticationRequest authzReq = new AuthenticationRequest(
                    authEndpoint,
                    new ResponseType("code"),
                    Scope.parse("openid profile email openid2"),
                    clientID,
                    callback,
                    state,
                    nonce);

            logger.info("1.User authorization request");

            logger.info(authzReq.getEndpointURI().toString() + "?" + authzReq.toQueryString());
            response.sendRedirect(authzReq.getEndpointURI().toString() + "?" + authzReq.toQueryString());

        } catch (URISyntaxException ex) {
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
        } catch (SerializeException ex) {
            java.util.logging.Logger.getLogger(OIDCLogin.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (SerializeException ex) {
            java.util.logging.Logger.getLogger(OIDCLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
