package com.instagram.api.dev;

import net.bc100dev.commons.ApplicationRuntimeException;

public class IGAndroidDevice extends IGDevice {

    private final String manufacturer, model, brand, cpuAbi, androidVersion;
    private final int androidSdk;

    public IGAndroidDevice(String manufacturer, String model, String brand, String cpuAbi, int androidSdk) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.brand = brand;
        this.cpuAbi = cpuAbi;

        if (androidSdk < 1)
            throw new IndexOutOfBoundsException("Android SDK Version cannot be 0 or ");
        if (androidSdk > 35) // Android 15 coming up
            throw new IndexOutOfBoundsException("No such Android version (max supported Android SDK is 35, received " + androidSdk + ")\n" +
                    "See https://developer.android.com/reference/android/os/Build.VERSION_CODES");

        this.androidSdk = androidSdk;

        if (androidSdk < 23)
            throw new ApplicationRuntimeException("Unsupported Android device; the Android device must be running Android 6 ( or later");

        // latest Instagram versions require Android SDK 28 (Android 9)
        switch (androidSdk) {
            case 23 -> this.androidVersion = "Android 6 (Marshmallow)";
            case 24 -> this.androidVersion = "Android 7 (Nougat)";
            case 25 -> this.androidVersion = "Android 7.1 (Nougat; MR1)";
            case 26 -> this.androidVersion = "Android 8 (Oreo)";
            case 27 -> this.androidVersion = "Android 8.1 (Oreo; MR1)";
            case 28 -> this.androidVersion = "Android 9 (Pie)";
            case 29 -> this.androidVersion = "Android 10 (Q)";
            case 30 -> this.androidVersion = "Android 11 (R)";
            case 31 -> this.androidVersion = "Android 12 (S)";
            case 32 -> this.androidVersion = "Android 12 (S; S_V2)";
            case 33 -> this.androidVersion = "Android 13 (Tiramisu)";
            case 34 -> this.androidVersion = "Android 14 (Upside Down Cake)";
            case 35 -> this.androidVersion = "Android 15 (V)";
            default -> throw new IndexOutOfBoundsException("Unsupported version, got " + androidSdk);
        }
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getBrand() {
        return brand;
    }

    public String getCpuAbi() {
        return cpuAbi;
    }

    public int getAndroidSdk() {
        return androidSdk;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    @Override
    public void passUserAgent(String userAgent) {
        super.passUserAgent(userAgent);
    }
}
