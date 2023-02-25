package com.example.nakama.Services;

import android.app.Service;
import android.content.Intent;

import java.security.Provider;

public class BroadcastSender {

    Service service;

    public BroadcastSender(Service service) {
        this.service = service;
    }

    public void sendBroadcast(String action, String broadcastName, int value) {
        Intent sendLevel = new Intent();
        sendLevel.setAction(action);
        sendLevel.putExtra(broadcastName, value);
        service.sendBroadcast(sendLevel);
    }

    public void sendBroadcast(String action, String broadcastName, long value) {
        Intent sendLevel = new Intent();
        sendLevel.setAction(action);
        sendLevel.putExtra(broadcastName, value);
        service.sendBroadcast(sendLevel);
    }

    public void sendBroadcast(String action, String broadcastName, String value) {
        Intent sendLevel = new Intent();
        sendLevel.setAction(action);
        sendLevel.putExtra(broadcastName, value);
        service.sendBroadcast(sendLevel);
    }

    public void sendBroadcast(String action, String broadcastName, boolean value) {
        Intent sendLevel = new Intent();
        sendLevel.setAction(action);
        sendLevel.putExtra(broadcastName, value);
        service.sendBroadcast(sendLevel);
    }

}
