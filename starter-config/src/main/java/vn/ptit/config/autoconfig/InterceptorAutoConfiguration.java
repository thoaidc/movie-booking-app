package vn.ptit.config.autoconfig;

import vn.ptit.config.interceptor.BaseResponseFilter;
import vn.ptit.config.interceptor.DefaultBaseResponseFilter;
import vn.ptit.config.interceptor.DefaultInterceptorPatternsConfig;
import vn.ptit.config.interceptor.InterceptorPatternsConfig;
import vn.ptit.model.common.MessageTranslationUtils;
import vn.ptit.model.config.properties.InterceptorProps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(InterceptorProps.class)
public class InterceptorAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(InterceptorAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(BaseResponseFilter.class)
    public BaseResponseFilter defaultBaseResponseFilter(MessageTranslationUtils messageTranslationUtils) {
        log.debug("[RESPONSE_FILTER_AUTO_CONFIG] - Use default base response filter");
        return new DefaultBaseResponseFilter(messageTranslationUtils);
    }

    @Bean
    @ConditionalOnMissingBean(InterceptorPatternsConfig.class)
    public InterceptorPatternsConfig defaultInterceptorPatternsConfig(InterceptorProps interceptorProps) {
        log.debug("[INTERCEPTOR_PATTERN_AUTO_CONFIG] - Use default excluded interceptor patterns");
        return new DefaultInterceptorPatternsConfig(interceptorProps);
    }
}
