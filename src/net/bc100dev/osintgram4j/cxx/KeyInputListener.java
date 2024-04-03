package net.bc100dev.osintgram4j.cxx;

public interface KeyInputListener {

    /**
     * Invoked, when a key is released
     * @param keyCode The key character itself
     * @param device The device that the Native Shell is using
     */
    void onKeyUp(int keyCode, KbdDevice device);

    /**
     * Invoked, when a key is pressed
     * @param keyCode The key character itself
     * @param device The device that the Native Shell is using
     */
    void onKeyDown(int keyCode, KbdDevice device);

}
