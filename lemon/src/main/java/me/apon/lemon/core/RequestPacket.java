package me.apon.lemon.core;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/11/1.
 */
public class RequestPacket {

    private byte[] data;

    public RequestPacket(byte[] data) {
        this.data = data;
    }

    byte[] getData(){
        return this.data;
    }

}
