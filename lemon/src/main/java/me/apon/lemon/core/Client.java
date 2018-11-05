package me.apon.lemon.core;

import java.net.Socket;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/10/26.
 */
public interface Client {


    Socket getSocket();

    void connect();

    void disconnect();

    boolean isConnected();

    boolean isDisconnected();

    boolean isConnecting();
}
