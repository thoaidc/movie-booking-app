package vn.ptit.model.exception;

@SuppressWarnings("unused")
public class BaseAuthenticationException extends BaseException {

    public BaseAuthenticationException(String entityName, String errorKey) {
        super(entityName, errorKey, null, null, null);
    }

    private BaseAuthenticationException(String entityName, String errorKey, Object[] args, Throwable error, String message) {
        super(entityName, errorKey, args, error, message);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String entityName;
        private String errorKey;
        private Object[] args;
        private Throwable error;
        private String originalMessage;

        public Builder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        public Builder errorKey(String errorKey) {
            this.errorKey = errorKey;
            return this;
        }

        public Builder args(Object[] args) {
            this.args = args;
            return this;
        }

        public Builder error(Throwable error) {
            this.error = error;
            return this;
        }

        public Builder originalMessage(String originalMessage) {
            this.originalMessage = originalMessage;
            return this;
        }

        public BaseAuthenticationException build() {
            return new BaseAuthenticationException(entityName, errorKey, args, error, originalMessage);
        }
    }
}
