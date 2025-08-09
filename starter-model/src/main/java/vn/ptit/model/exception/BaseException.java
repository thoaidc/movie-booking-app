package vn.ptit.model.exception;

/**
 * Custom class for proactive exceptions in the application <p>
 *
 * {@link #entityName}: Name of the class that throws the exception <p>
 * {@link #errorKey}: The i18n message key used for language internationalization <p>
 * {@link #args}: Parameters accompanying the message of the `errorKey` if required <p>
 * {@link #error}: The original cause of the exception is wrapped by this custom exception <p>
 * {@link #originalMessage}: Custom messages without using I18n automatically by response filters
 *
 * @author thoaidc
 */
@SuppressWarnings("unused")
public abstract class BaseException extends RuntimeException {

    private final String entityName;
    private final String errorKey;
    private final Object[] args;
    private final Throwable error;
    private final String originalMessage;

    protected BaseException(String entityName, String errorKey, Object[] args, Throwable error, String originalMessage) {
        super(entityName + '-' + errorKey, error);
        this.entityName = entityName;
        this.errorKey = errorKey;
        this.args = args;
        this.error = error;
        this.originalMessage = originalMessage;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public Object[] getArgs() {
        return args;
    }

    public Throwable getError() {
        return error;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }
}
