package vn.ptit.model.constants;

@SuppressWarnings("unused")
public enum AuthenticationType {
    JWT_VERIFY,
    HEADER_FORWARDED;

    public final static String JWT_VERIFY_VALUE = "jwt_verify";
    public final static String HEADER_FORWARDED_VALUE = "header_forwarded";
}
