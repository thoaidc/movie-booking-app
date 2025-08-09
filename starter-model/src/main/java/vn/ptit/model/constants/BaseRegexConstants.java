package vn.ptit.model.constants;

/**
 * Some patterns to validate data formats
 * @author thoaidc
 */
@SuppressWarnings("unused")
public interface BaseRegexConstants {

    String USERNAME_PATTERN = "^[a-zA-Z0-9]{2,100}$"; // Includes only numbers and letters

    // The length is between 8 and 20 characters
    // It contains only letters (lowercase/uppercase), numbers, and special characters from a specified list
    String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]{6,20}$";

    // The email address cannot have invalid characters, such as a dot at the beginning or end of the domain name
    String EMAIL_PATTERN = "^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

    // Check +84 or 0, 10 digits
    String PHONE_PATTERN = "^(\\+84|0)\\d{9}$";
}
