package vn.ptit.config.autoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import vn.ptit.config.interceptor.CircuitBreakerRestTemplateInterceptor;
import vn.ptit.model.config.properties.CircuitBreakerProps;
import vn.ptit.model.constants.BasePropertiesConstants;

import static vn.ptit.model.constants.ActivateStatus.ENABLED_VALUE;

/**
 * Helps the application use functions related to sending and receiving HTTP requests/responses, similar to a client
 * @author thoaidc
 */
@AutoConfiguration
public class HttpClientAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(HttpClientAutoConfiguration.class);
    private final ObjectMapper objectMapper;
    private final CircuitBreakerProps circuitBreakerProps;
    private final CircuitBreakerRestTemplateInterceptor circuitBreakerRestTemplateInterceptor;

    public HttpClientAutoConfiguration(ObjectMapper objectMapper,
                                       CircuitBreakerProps circuitBreakerProps,
                                       CircuitBreakerRestTemplateInterceptor circuitBreakerRestTemplateInterceptor) {
        this.objectMapper = objectMapper;
        this.circuitBreakerProps = circuitBreakerProps;
        this.circuitBreakerRestTemplateInterceptor = circuitBreakerRestTemplateInterceptor;
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(circuitBreakerProps.getConnectTimeout());
        factory.setReadTimeout(circuitBreakerProps.getReadTimeout());
        return factory;
    }

    /**
     * This configuration defines a RestTemplate bean in Spring <p>
     * Purpose: Create a tool that makes sending HTTP requests and handling responses
     */
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    @ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_REST_TEMPLATE, havingValue = ENABLED_VALUE)
    public RestTemplate defaultRestTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        log.debug("[REST_TEMPLATE_AUTO_CONFIG] - Use default RestTemplate for send HTTP request");
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        // Create an HTTP message converter, using JacksonConverter to convert between JSON and Java objects
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        restTemplate.getMessageConverters().add(converter);
        restTemplate.getInterceptors().add(circuitBreakerRestTemplateInterceptor);
        return restTemplate;
    }
}
