package me.apon.lemon.core;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/10/26.
 */
public class SocketClient implements Client {

    private final static String TAG = SocketClient.class.getSimpleName();

    private Socket mSocket;

    private InetSocketAddress mAddress;

    private Options mOptions;

    private volatile boolean isConnected = false;

    private volatile boolean isConnecting = false;

    private volatile boolean isDisconnected = true;

    public static final class Builder {

        private String ip;
        private int port;
        private Options options = new Options();

        public Builder ip(String ip){
            this.ip = ip;
            return this;
        }

        public Builder port(int port){
            this.port = port;
            return this;
        }

        public Builder setOOBInline(boolean ooBInline){
            options.SO_OOBInline = ooBInline;
            return this;
        }

        public Builder setReuseAddress(boolean reuseAddress){
            options.SO_ReuseAddress = reuseAddress;
            return this;
        }

        public Builder setKeepAlive(boolean keepAlive){
            options.SO_KeepAlive = keepAlive;
            return this;
        }

        public Builder setReceiveBufferSize(int receiveBufferSize){
            options.SO_ReceiveBufferSize = receiveBufferSize;
            return this;
        }

        public Builder setSendBufferSize(int sendBufferSize){
            options.SO_SendBufferSize = sendBufferSize;
            return this;
        }

        public Builder setTcpNoDelay(boolean tcpNoDelay){
            options.SO_TcpNoDelay = tcpNoDelay;
            return this;
        }

        public Builder setSoTimeout(int timeout){
            options.SO_Timeout = timeout;
            return this;
        }

        public SocketClient build() {
            SocketClient client = new SocketClient(this.ip,this.port,options);
            return client;
        }

    }

    public static class Options{
        boolean SO_OOBInline = false;
        boolean SO_ReuseAddress = false;
        boolean SO_KeepAlive = false;
        boolean SO_TcpNoDelay = false;
        int SO_ReceiveBufferSize = 8096;
        int SO_SendBufferSize = 8096;
        int SO_Timeout = 0;
    }

    public SocketClient(String ip, int port,Options options) {
        mAddress = new InetSocketAddress(ip,port);
        mOptions = options;
    }

    @Override
    public Socket getSocket() {
        return mSocket;
    }

    @Override
    public void connect() {
        try {
            this.mSocket = new Socket();

            //高可靠性和最小延迟传输
            this.mSocket.setTrafficClass(0x04 | 0x10);
            this.mSocket.setOOBInline(mOptions.SO_OOBInline);
            this.mSocket.setReuseAddress(mOptions.SO_ReuseAddress);
            this.mSocket.setKeepAlive(mOptions.SO_KeepAlive);
            this.mSocket.setSoTimeout(mOptions.SO_Timeout);
            this.mSocket.setReceiveBufferSize(mOptions.SO_ReceiveBufferSize);
            this.mSocket.setSendBufferSize(mOptions.SO_SendBufferSize);
            this.mSocket.setTcpNoDelay(mOptions.SO_TcpNoDelay);

            this.isConnecting = true;
            this.mSocket.connect(mAddress);
            this.isConnecting = false;

            this.isConnected = true;
            this.isDisconnected = false;
            LLog.d(TAG,"=========连接成功=========="+mAddress.getHostName()+":"+mAddress.getPort());
        } catch (IOException e) {
//            e.printStackTrace();
            this.isConnected = false;
            this.isDisconnected = true;
            LLog.d(TAG,"=========连接失败=========="+mAddress.getHostName()+":"+mAddress.getPort()+":"+e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            Socket socket = this.mSocket;
            if (socket != null && !socket.isClosed()) {

                try {
                    socket.shutdownInput();
                    socket.shutdownOutput();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
                socket.close();
            }
            this.mSocket = null;
            this.isConnected = false;
            this.isDisconnected = true;

            LLog.d(TAG,"=========断开连接=========="+mAddress.getHostName()+":"+mAddress.getPort());
        } catch (NullPointerException
                | IOException
                e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public boolean isDisconnected() {
        return isDisconnected;
    }

    @Override
    public boolean isConnecting() {
        return isConnecting;
    }
}
