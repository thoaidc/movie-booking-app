package vn.ptit.userservice.resources;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ptit.model.dto.response.BaseResponseDTO;
import vn.ptit.userservice.dto.request.AuthRequestDTO;
import vn.ptit.userservice.service.AuthService;

@RestController
@RequestMapping("/api")
public class AuthenticationResource {

    private final AuthService authService;

    public AuthenticationResource(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/p/users/authenticate")
    public BaseResponseDTO login(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        return authService.authenticate(authRequestDTO);
    }
}
