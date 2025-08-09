package vn.ptit.config.interceptor;

import vn.ptit.model.config.properties.InterceptorProps;
import vn.ptit.model.constants.BaseCommonConstants;

import java.util.Optional;

public class DefaultInterceptorPatternsConfig implements InterceptorPatternsConfig {

    private final InterceptorProps interceptorProps;

    public DefaultInterceptorPatternsConfig(InterceptorProps interceptorProps) {
        this.interceptorProps = Optional.ofNullable(interceptorProps).orElse(new InterceptorProps());
    }

    @Override
    public String[] excludedPaths() {
        return Optional.ofNullable(interceptorProps.getExcludedPatterns())
                .orElse(BaseCommonConstants.DEFAULT_INTERCEPTOR_EXCLUDED_PATTERNS);
    }
}
