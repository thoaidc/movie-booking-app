package vn.ptit.userservice.constants;

/**
 * Messages for exceptions with internationalization (I18n) here<p>
 * The constant content corresponds to the message key in the resources bundle files in directories such as:
 * <ul>
 *   <li><a href="">resources/i18n/messages</a></li>
 * </ul>
 *
 * @author thoaidc
 */
@SuppressWarnings("unused")
public interface ExceptionConstants {

    // Validate account info exception
    String REGISTER_FAILED = "exception.account.register.failed";
    String ACCOUNT_EXISTED = "exception.account.existed";
    String ACCOUNT_NOT_EXISTED = "exception.account.notExisted";
    String OLD_PASSWORD_INVALID = "exception.account.oldPasswordInvalid";
    String NEW_PASSWORD_DUPLICATED = "exception.account.newPasswordDuplicated";

    // Role
    String ROLE_EXISTED = "exception.role.existed";
    String ROLE_PERMISSIONS_NOT_EMPTY = "exception.role.permissions.notEmpty";
    String ROLE_PERMISSION_INVALID = "exception.role.permission.invalidList";

    // Form data request
    String DATA_INVALID = "exception.data.invalid";
    String DATA_NOT_FOUND = "exception.data.notFound";
    String DATA_EXISTED = "exception.data.existed";
    String DATA_NOT_EXISTED = "exception.data.notExisted";
    String ID_NOT_NULL = "exception.data.id.notNull";
    String ID_INVALID = "exception.data.id.invalid";
    String NAME_NOT_BLANK = "exception.data.name.notBlank";
    String NAME_MAX_LENGTH = "exception.data.name.maxLength";
    String CODE_NOT_BLANK = "exception.data.code.notBlank";
    String FULLNAME_NOT_BLANK = "exception.data.fullname.notBlank";
    String FULLNAME_MAX_LENGTH = "exception.data.fullname.maxLength";
    String USERNAME_NOT_BLANK = "exception.data.username.notBlank";
    String USERNAME_INVALID = "exception.data.username.invalid";
    String USERNAME_MIN_LENGTH = "exception.data.username.minLength";
    String USERNAME_MAX_LENGTH = "exception.data.username.maxLength";
    String PASSWORD_NOT_BLANK = "exception.data.password.notBlank";
    String PASSWORD_MIN_LENGTH = "exception.data.password.minLength";
    String PASSWORD_MAX_LENGTH = "exception.data.password.maxLength";
    String PASSWORD_INVALID = "exception.data.password.invalid";
    String EMAIL_NOT_BLANK = "exception.data.email.notBlank";
    String EMAIL_MIN_LENGTH = "exception.data.email.minLength";
    String EMAIL_MAX_LENGTH = "exception.data.email.maxLength";
    String EMAIL_INVALID = "exception.data.email.invalid";
    String PHONE_NOT_BLANK = "exception.data.phone.notBlank";
    String PHONE_MIN_LENGTH = "exception.data.phone.minLength";
    String PHONE_MAX_LENGTH = "exception.data.phone.maxLength";
    String PHONE_INVALID = "exception.data.phone.invalid";
    String DESCRIPTION_NOT_BLANK = "exception.data.description.notBlank";
    String DESCRIPTION_MAX_LENGTH = "exception.data.description.maxLength";
    String ADDRESS_NOT_BLANK = "exception.data.address.notBlank";
    String ADDRESS_MAX_LENGTH = "exception.data.address.maxLength";
    String STATUS_NOT_BLANK = "exception.data.status.notBlank";
    String STATUS_INVALID = "exception.data.status.invalid";
    String TITLE_NOT_BLANK = "exception.data.title.notBlank";
    String TITLE_MAX_LENGTH = "exception.data.title.maxLength";
    String CONTENT_NOT_BLANK = "exception.data.content.notBlank";
    String CONTENT_MAX_LENGTH = "exception.data.content.maxLength";
    String DEVICE_ID_NOT_BLANK = "exception.data.deviceId.notBlank";
}
