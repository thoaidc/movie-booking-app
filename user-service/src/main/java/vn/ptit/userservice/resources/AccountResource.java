package vn.ptit.userservice.resources;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import vn.ptit.model.constants.BaseSecurityConstants;
import vn.ptit.model.dto.response.BaseResponseDTO;

@RestController
@RequestMapping("/api")
public class AccountResource {

    private final RestTemplate restTemplate;

    public AccountResource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/users/info")
    public BaseResponseDTO getAccountInfo(HttpServletRequest request) {
        String userId = request.getHeader(BaseSecurityConstants.HEADER.USER_ID);
        System.out.println("UserID: " + userId);
        return BaseResponseDTO.builder().ok(userId);
    }

    // Test CircuitBreaker
    @GetMapping("/p/users/fail")
    public BaseResponseDTO alwaysFail() throws InterruptedException {
        System.out.println("UserID (Fail API)");
        Thread.sleep(3000);
        throw new RuntimeException("Simulated permanent failure");
    }

    // Test CircuitBreaker call internal API
    @GetMapping("/p/users/success")
    public BaseResponseDTO alwaysSuccess() {
        return restTemplate.getForObject("http://localhost:8008/test", BaseResponseDTO.class);
    }
}
