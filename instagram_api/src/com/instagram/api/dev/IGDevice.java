package com.instagram.api.dev;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class IGDevice implements Serializable {

    @Serial
    private static final long serialVersionUID = -823447845648L;
    private String userAgent;
    private String capabilities;
    private Map<String, Object> deviceMap;

}