package vn.ptit.model.common;

import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class SecurityUtils {

    public static MvcRequestMatcher[] convertToMvcMatchers(MvcRequestMatcher.Builder mvc, String[] patterns) {
        return Stream.of(patterns)
            .map(mvc::pattern)
            .toList()
            .toArray(new MvcRequestMatcher[0]);
    }

    public static boolean checkIfAuthenticationRequired(String requestUri, String[] publicPatterns) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        return Arrays.stream(publicPatterns).noneMatch(pattern -> antPathMatcher.match(pattern, requestUri));
    }
}
