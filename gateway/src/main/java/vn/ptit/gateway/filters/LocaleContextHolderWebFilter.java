package vn.ptit.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Optional;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LocaleContextHolderWebFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(LocaleContextHolderWebFilter.class);

    /**
     * Set locale by 'Accept-Language' header from request to use i18n translation
     * @param exchange the current server exchange
     * @param chain provides a way to delegate to the next filter
     * @author thoaidc
     */
    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Locale locale = exchange.getLocaleContext().getLocale();
        log.debug("[LOCALE_FILTER] - Locale detected: {}", locale);
        LocaleContextHolder.setLocale(Optional.ofNullable(locale).orElse(Locale.ENGLISH));
        return chain.filter(exchange).doFinally(signalType -> LocaleContextHolder.resetLocaleContext());
    }
}
