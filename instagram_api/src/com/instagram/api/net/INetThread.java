package com.instagram.api.net;

import com.instagram.api.Constants;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class INetThread extends Thread {

    private static INetThread instance;

    private static final List<IQueueData> requestList = new ArrayList<>();

    public INetThread() {
        if (INetThread.instance != null)
            throw new RuntimeException("Network Thread is already running");

        INetThread.instance = new INetThread(new INetExec(), "IG_API.NetworkThread");
    }

    INetThread(Runnable task, String name) {
        super(task, name);
    }

    public void requestQueue(String endpoint, Map<String, String> headers, byte[] postData) {
        requestList.add(new IQueueData(endpoint, headers, postData));
    }

    static class INetExec implements Runnable {

        @Override
        public void run() {
            while (INetThread.instance.isAlive() && !INetThread.instance.isInterrupted()) {
                if (INetThread.requestList.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignore) {
                    }

                    continue;
                }

                for (int i = 0; i < INetThread.requestList.size(); i++) {
                    IQueueData queueData = INetThread.requestList.get(i);
                    if (queueData.isEndpointValid()) {
                        Logger.getGlobal().warning("Endpoint URL (\"" + queueData.endpoint() + "\") invalid; cannot pass HTTP/HTTPS Prefix");
                        INetThread.requestList.remove(queueData);
                        continue;
                    }

                    try {
                        // Network code goes here
                        URL url = new URI(Constants.URL_API + queueData.endpoint()).toURL();

                        Thread.sleep(2500);
                    } catch (IOException | URISyntaxException | InterruptedException ex) {
                        Logger.getGlobal().throwing("INetThread.java:INetExec", "run", ex);
                    }
                }
            }
        }

    }

}
