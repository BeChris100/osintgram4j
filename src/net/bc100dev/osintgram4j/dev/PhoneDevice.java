package net.bc100dev.osintgram4j.dev;

import org.json.JSONObject;

public class PhoneDevice extends Device {

    /*
    {
        "manufacturer": "Google",
        "model": "Pixel 8 Pro",
        "android": true,
        "ios": false,
        "client_type": "App",
        "client_app": {
            "application_version": ""
        },
        "client_browser": {
            "browser": "",
            "browser_version": ""
        },
        "android_values": {
            "version": 14,
            "kernel_version": "5.15.131-android14-gd99d9fe08e0c-ab11209359",
            "build_number": "AP1A.240405.002",
            "serial": "35625PCEF6166R"
        },
        "ios_values": {}
    }
     */

    public static PhoneDevice fromJson(JSONObject json) {
        String manu = json.getString("manufacturer");
        String model = json.getString("model");
        DeviceClient appClient = DeviceClient.fromJson(DeviceClient.Platform.MOBILE, DeviceClient.AppType.APP, json.getJSONObject("client_app"));
        DeviceClient browserClient = DeviceClient.fromJson(DeviceClient.Platform.MOBILE, DeviceClient.AppType.BROWSER, json.getJSONObject("client_browser"));
        DeviceOSInfo devOsInfo = DeviceOSInfo.fromJson(json.getString("os").equalsIgnoreCase("android") ?
                json.getJSONObject("android_values") : (json.getString("os").equalsIgnoreCase("ios") ?
                json.getJSONObject("ios_values") : null));

        if (devOsInfo == null)
            throw new NullPointerException("Initiated a Null Pointer Reference while fetching Device Information");

        DeviceClient client = json.getString("client_type").equals("App") ? appClient : browserClient;

        return new PhoneDevice(manu, model, client, devOsInfo);
    }

    protected PhoneDevice(String manufacturer, String model, DeviceClient client, DeviceOSInfo devOsInfo) {
    }

    @Override
    public Type constructDeviceType() {
        return Type.PHONE;
    }

    @Override
    public String constructUserAgent() {
        return "";
    }
}
