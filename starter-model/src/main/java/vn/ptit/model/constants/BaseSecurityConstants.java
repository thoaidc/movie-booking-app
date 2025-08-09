package vn.ptit.model.constants;

import org.springframework.http.HttpMethod;

@SuppressWarnings("unused")
public interface BaseSecurityConstants {

    // The encryption complexity in PasswordEncoder's algorithm (between 4 and 31)
    // Higher values mean the password is harder to attack, but too high will reduce performance
    int BCRYPT_COST_FACTOR = 12;

    interface JWT {
        long DEFAULT_TOKEN_VALIDITY = 3 * 60 * 60 * 1000L; // 3 hours
        long DEFAULT_TOKEN_VALIDITY_FOR_REMEMBER = 7 * 24 * 60 * 60 * 1000L; // 7 days
    }

    /**
     * The corresponding keys to store information in the payload of a JWT token <p>
     */
    interface TOKEN_PAYLOAD {
        String BOT_ID = "botId";
        String USER_ID = "userId";
        String USERNAME = "username";
        String AUTHORITIES = "authorities";
    }

    interface HEADER {
        // The request header storing the JWT token, used in cases where the token is not found in the HTTP-only cookies
        String AUTHORIZATION_HEADER = "Authorization";
        String AUTHORIZATION_GATEWAY_HEADER = "Authorization-Gateway";
        String TOKEN_TYPE = "Bearer "; // JWT token type
        String BOT_ID = "X-Bot-ID";
        String USER_ID = "X-User-ID";
        String USER_NAME = "X-User-Name";
        String USER_AUTHORITIES = "X-User-Authorities";
    }

    /**
     * The paths for security configuration <p>
     * Requests matching the patterns below will have their own specific security rules applied <p>
     * Requests not listed will require authentication by default
     */
    interface REQUEST_MATCHERS {
        String[] DEFAULT_PUBLIC_API_PATTERNS = {
            "/",
            "/**.html",
            "/**.css",
            "/**.js",
            "/**.ico",
            "/i18n/**",
            "/register",
            "/p/**",
            "/api/p/**",
            "/login/**"
        };
    }

    /**
     * The configurations applied in the CORS filter
     */
    interface CORS {
        String DEFAULT_APPLY_FOR = "/**"; // CORS filter is applied to all requests
        String[] DEFAULT_ALLOWED_HEADERS = {
            "Content-Type",     // Content format
            "Authorization",    // Authentication token
            "Accept",           // Client-expected content
            "Origin",           // Origin of the request
            "X-CSRF-Token",     // Anti-CSRF token
            "X-Requested-With", // Ajax request markup
            "Access-Control-Allow-Origin", // Server response header
            "X-App-Version",    // Application version (optional)
            "X-Device-ID"       // Device ID (optional)
        };

        String[] DEFAULT_ALLOWED_REQUEST_METHODS = {
            HttpMethod.GET.name(),
            HttpMethod.PUT.name(),
            HttpMethod.POST.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name()
        };

        String[] DEFAULT_ALLOWED_ORIGIN_PATTERNS = {"*"}; // The list of domains allowed to access the resources. * means all
        boolean DEFAULT_ALLOW_CREDENTIALS = true; // Allow sending cookies or authentication information
    }
}
