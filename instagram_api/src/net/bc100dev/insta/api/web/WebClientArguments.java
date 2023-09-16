package net.bc100dev.insta.api.web;

import java.util.Map;

public class WebClientArguments {

    // The name itself of the argument
    private final String label;
    private Object value;
    private final ArgumentType type;

    public WebClientArguments(String label) {
        this.label = label;
        this.type = getTypeForArg(label);
    }

    public String getLabel() {
        return label;
    }

    public ArgumentType getType() {
        return type;
    }

    public void setString(String str) {
        this.value = str;
    }

    public void setBoolean(boolean bln) {
        this.value = bln;
    }

    public void setInteger(int i) {
        this.value = i;
    }

    public void setFloat(float fl) {
        this.value = fl;
    }

    public void setLong(long ln) {
        this.value = ln;
    }

    public void setDouble(double dbl) {
        this.value = dbl;
    }

    public void setMap(Map<String, String> mp) {
        this.value = mp;
    }

    public void setArgumentCallback(ArgumentCallback callback) {
        this.value = callback;
    }

    public void setOther(Object vl) {
        this.value = vl;
    }

    public Object getValue() {
        return this.value;
    }

    private ArgumentType getTypeForArg(String label) {
        return switch (label) {
            case ArgumentLabel.AUTO_PATCH, ArgumentLabel.DROP_INCOMPAT_KEYS,
                    ArgumentLabel.AUTHENTICATE -> ArgumentType.BOOLEAN;
            case ArgumentLabel.USER_AGENT, ArgumentLabel.USERNAME,
                    ArgumentLabel.PASSWORD, ArgumentLabel.MOBILE_USER_AGENT,
                    ArgumentLabel.RHX_GIS, ArgumentLabel.COOKIE,
                    ArgumentLabel.PROXY -> ArgumentType.STRING;
            case ArgumentLabel.USER_SETTINGS -> ArgumentType.MAP;
            case ArgumentLabel.TIMEOUT -> ArgumentType.INTEGER;
            case ArgumentLabel.ON_LOGIN -> ArgumentType.INTERFACE;
            case ArgumentLabel.PROXY_HANDLER, ArgumentLabel.CUSTOM_SSL_CONTEXT -> ArgumentType.OTHER;
            default -> throw new IllegalStateException("Unexpected/Unknown value: " + label);
        };
    }

    public interface ArgumentCallback {

        void onArgumentCallback(Object[] objArgs);

    }

    public enum ArgumentType {

        STRING,
        BOOLEAN,
        INTEGER,
        LONG,
        FLOAT,
        DOUBLE,
        MAP,
        INTERFACE,
        OTHER

    }

    public static class ArgumentLabel {

        public static final String AUTO_PATCH = "auto_patch";
        public static final String DROP_INCOMPAT_KEYS = "drop_incompat_keys";
        public static final String TIMEOUT = "timeout";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String AUTHENTICATE = "authenticate";
        public static final String ON_LOGIN = "on_login";
        public static final String USER_SETTINGS = "user_settings";
        public static final String USER_AGENT = "user_agent";
        public static final String MOBILE_USER_AGENT = "mobile_user_agent";
        public static final String RHX_GIS = "rhx_gis";
        public static final String COOKIE = "cookie";
        public static final String PROXY_HANDLER = "proxy_handler";
        public static final String PROXY = "proxy";
        public static final String CUSTOM_SSL_CONTEXT = "custom_ssl_context";

    }

}
