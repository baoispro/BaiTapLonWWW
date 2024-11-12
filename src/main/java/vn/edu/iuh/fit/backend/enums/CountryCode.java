package vn.edu.iuh.fit.backend.enums;

public enum CountryCode {
    VN("84");
    private final String code;
    CountryCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

}
