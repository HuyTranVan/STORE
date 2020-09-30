package com.lubsolution.store.libraries.googleauth;

import android.content.Intent;
import android.net.Uri;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.util.Preconditions;
import com.lubsolution.store.utils.Util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * OAuth 2.0 authorization code flow for an installed Java application that persists end-user
 * credentials.
 *
 * <p>
 * Implementation is thread-safe.
 * </p>
 *
 * @author Yaniv Inbar
 * @author Philipp Hanslovsky
 * @since 1.11
 */
public class AuthorizationCodeInstalledApp {


    /**
     * Helper interface to allow caller to browse.
     */
    public static interface Browser {
        /**
         * @param url url to browse
         * @throws IOException
         */
        public void browse(String url) throws IOException;
    }

    /**
     * Default browser that just delegates to
     * {@link AuthorizationCodeInstalledApp#browse(String)}.
     */
    public static class DefaultBrowser implements Browser {

        public void browse(String url) throws IOException {
//            AuthorizationCodeInstalledApp.browse(url);
            AuthorizationCodeInstalledApp.startOpenWebPage(url);
        }

    }

    /**
     * Authorization code flow.
     */
    private final AuthorizationCodeFlow flow;

    /**
     * Verification code receiver.
     */
    private final VerificationCodeReceiver receiver;

    private static final Logger LOGGER =
            Logger.getLogger(AuthorizationCodeInstalledApp.class.getName());

    private final Browser browser;

    /**
     * @param flow     authorization code flow
     * @param receiver verification code receiver
     */
    public AuthorizationCodeInstalledApp(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver) {
        this(flow, receiver, new DefaultBrowser());
    }

    /**
     * @param flow     authorization code flow
     * @param receiver verification code receiver
     */
    public AuthorizationCodeInstalledApp(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver, Browser browser) {
        this.flow = Preconditions.checkNotNull(flow);
        this.receiver = Preconditions.checkNotNull(receiver);
        this.browser = browser;
    }

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @param userId user ID or {@code null} if not using a persisted credential store
     * @return credential
     */
    public Credential authorize(String userId) throws IOException {
        try {
            Credential credential = flow.loadCredential(userId);
            if (credential != null
                    && (credential.getRefreshToken() != null ||
                    credential.getExpiresInSeconds() == null ||
                    credential.getExpiresInSeconds() > 60)) {
                return credential;
            }
            // open in browser
            String redirectUri = receiver.getRedirectUri();
            AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
            onAuthorization(authorizationUrl);
            // receive authorization code and exchange it for an access token
            String code = receiver.waitForCode();
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            // store credential and return it
            return flow.createAndStoreCredential(response, userId);

        } finally {
            receiver.stop();
        }
    }

    /**
     * Handles user authorization by redirecting to the OAuth 2.0 authorization server.
     *
     * <p>
     * Default implementation is to call {@code browse(authorizationUrl.build())}. Subclasses may
     * override to provide optional parameters such as the recommended state parameter. Sample
     * implementation:
     * </p>
     *
     * <pre>
     * &#64;Override
     * protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
     * authorizationUrl.setState("xyz");
     * super.onAuthorization(authorizationUrl);
     * }
     * </pre>
     *
     * @param authorizationUrl authorization URL
     * @throws IOException I/O exception
     */
    protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
        String url = authorizationUrl.build();
        Preconditions.checkNotNull(url);
        browser.browse(url);
    }

//    /**
////     * Open a browser at the given URL using {@link Desktop} if available, or alternatively output the
//     * URL to {@link System#out} for command-line applications.
//     *
//     * @param url URL to browse
//     */


    public static void browse(String url) {
        Preconditions.checkNotNull(url);
        // Ask user to open in their browser using copy-paste
        System.out.println("Please open the following address in your browser:");
        System.out.println("  " + url);
        // Attempt to open it in the browser
        try {
//            if (Desktop.isDesktopSupported()) {
//                Desktop desktop = Desktop.getDesktop();
//                if (desktop.isSupported(Desktop.Action.BROWSE)) {
//                    System.out.println("Attempting to open that address in the default browser now...");
//                    desktop.browse(URI.create(url));
//                }
//            }
//        } catch (IOException e) {
//            LOGGER.log(Level.WARNING, "Unable to open browser", e);
        } catch (InternalError e) {
            // A bug in a JRE can cause Desktop.isDesktopSupported() to throw an
            // InternalError rather than returning false. The error reads,
            // "Can't connect to X11 window server using ':0.0' as the value of the
            // DISPLAY variable." The exact error message may vary slightly.
            LOGGER.log(Level.WARNING, "Unable to open browser", e);
        }
    }

    /**
     * Returns the authorization code flow.
     */
    public final AuthorizationCodeFlow getFlow() {
        return flow;
    }

    /**
     * Returns the verification code receiver.
     */
    public final VerificationCodeReceiver getReceiver() {
        return receiver;
    }


    public static void startOpenWebPage(String url) {
        boolean result = false;
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        try {
            Util.getInstance().getCurrentActivity().startActivityForResult(intent, 1);
            result = true;
        } catch (Exception e) {
            if (url.startsWith("http://")) {
                startOpenWebPage(url.replace("http://", "https://"));
            }
        }
//        return result;
    }
}