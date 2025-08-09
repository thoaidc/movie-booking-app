package vn.ptit.model.dto.response;

import vn.ptit.model.constants.BaseHttpStatusConstants;
import vn.ptit.model.constants.BaseResultConstants;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * The format helps standardize the response for the client <p>
 * Other response types can inherit from it and extend it as needed for specific cases <p>
 * Normally only need this class for responses
 *
 * @author thoaidc
 */
@SuppressWarnings("unused")
public class BaseResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Integer code; // Http status
    // The status indicating successful processing of the request (in the case of valid input data and no system errors)
    private boolean status = false;
    private String message; // The response content follows the i18n standard
    private Object result; // The data after processing the request, is not required and can be null
    private Long total; // Total records if query with pageable, is not required and can be null

    public BaseResponseDTO() {}

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public static Builder builder() {
        return new Builder();
    }

    // The builder allows for faster response creation
    public static class Builder {
        private final BaseResponseDTO instance = new BaseResponseDTO();

        public Builder code(int code) {
            instance.code = code;
            return this;
        }

        public Builder success(boolean status) {
            instance.status = status;
            return this;
        }

        public Builder message(String message) {
            instance.message = message;
            return this;
        }

        public Builder result(Object result) {
            instance.result = result;
            return this;
        }

        public Builder total(Long totalRecords) {
            instance.total = totalRecords;
            return this;
        }

        public BaseResponseDTO ok() {
            instance.code = BaseHttpStatusConstants.OK;
            instance.status = Boolean.TRUE;
            instance.message = instance.message != null ? instance.message : BaseResultConstants.SUCCESS;
            return instance;
        }

        public BaseResponseDTO ok(Object result) {
            instance.result = result;
            return ok();
        }

        public BaseResponseDTO build() {
            if (Objects.isNull(instance.code))
                return ok();

            return instance;
        }
    }
}
