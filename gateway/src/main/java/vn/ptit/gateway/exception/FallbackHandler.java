package vn.ptit.gateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import vn.ptit.model.dto.response.BaseResponseDTO;

@RestController
@RequestMapping("/fallback")
public class FallbackHandler {

    private final Logger log = LoggerFactory.getLogger(FallbackHandler.class);

    @GetMapping(value = "/services/unavailable", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BaseResponseDTO>> serviceUnavailableJson() {
        log.error("[SERVICE_UNAVAILABLE] - Service unavailable");
        BaseResponseDTO responseDTO = BaseResponseDTO.builder()
                .code(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Service temporarily unavailable. Please try again later")
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(responseDTO));
    }
}
