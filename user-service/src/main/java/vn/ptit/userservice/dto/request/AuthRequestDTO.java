package vn.ptit.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.MethodArgumentNotValidException;
import vn.ptit.config.exception.BaseExceptionHandler;
import vn.ptit.model.constants.BaseRegexConstants;
import vn.ptit.userservice.constants.ExceptionConstants;

/**
 * Used to map with authentication requests in a manual authenticate flow <p>
 * The @{@link Valid} annotation is used along with @{@link ResponseBody} to validate input data format <p>
 * Annotations like @{@link Pattern}, @{@link NotBlank} will be automatically handled by Spring <p>
 * {@link MethodArgumentNotValidException} will be thrown with the predefined message key
 * if any of the validated fields contain invalid data <p>
 * This exception is configured to be handled by {@link BaseExceptionHandler#handleBaseIllegalArgumentException}
 *
 * @author thoaidc
 */
@SuppressWarnings("unused")
public class AuthRequestDTO {

    @NotBlank(message = ExceptionConstants.USERNAME_NOT_BLANK)
    @Size(min = 2, message = ExceptionConstants.USERNAME_MIN_LENGTH)
    @Size(max = 50, message = ExceptionConstants.USERNAME_MAX_LENGTH)
    @Pattern(regexp = BaseRegexConstants.USERNAME_PATTERN, message = ExceptionConstants.USERNAME_INVALID)
    private String username;

    @NotBlank(message = ExceptionConstants.PASSWORD_NOT_BLANK)
    @Size(min = 6, message = ExceptionConstants.PASSWORD_MIN_LENGTH)
    @Size(max = 20, message = ExceptionConstants.PASSWORD_MAX_LENGTH)
    @Pattern(regexp = BaseRegexConstants.PASSWORD_PATTERN, message = ExceptionConstants.PASSWORD_INVALID)
    private String password;

    private boolean rememberMe = false;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
