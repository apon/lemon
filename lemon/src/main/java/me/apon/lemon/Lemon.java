package me.apon.lemon;

import java.nio.charset.Charset;

import me.apon.lemon.core.Client;
import me.apon.lemon.core.ConnectHandler;
import me.apon.lemon.core.ConnectHandlerWrap;
import me.apon.lemon.core.Disposable;
import me.apon.lemon.core.LLog;
import me.apon.lemon.core.MessageHandler;
import me.apon.lemon.core.MessageHandlerWrap;
import me.apon.lemon.core.NetworkExecutor;
import me.apon.lemon.core.Protocols;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/10/26.
 */
public class Lemon {

    public static final class Builder {


        private Client mClient;

        private Protocols mProtocols;

        private byte[] mPingData;

        private int mPingInterval = 0;

        public Builder client(Client client){
            this.mClient = client;
            return this;
        }

        public Builder protocols(Protocols protocols){
            this.mProtocols = protocols;
            return this;
        }

        public Builder pingData(byte[] data){
            this.mPingData = data;
            return this;
        }

        public Builder pingInterval(int pingInterval){
            this.mPingInterval = pingInterval;
            return this;
        }

        public Builder debug(boolean debug){
            LLog.debug(debug);
            return this;
        }

        public Lemon build() {
            return new Lemon(this.mClient,this.mProtocols,this.mPingInterval,this.mPingData);
        }
    }


    private NetworkExecutor mNetworkExecutor;

    public Lemon(Client client,Protocols protocols,int pingInterval,byte[] pingData) {
        this.mNetworkExecutor = new NetworkExecutor(client,protocols,pingInterval,pingData);
    }

    public Disposable onMessage(MessageHandler handler){
        MessageHandlerWrap packet = new MessageHandlerWrap(handler);
        packet.addTo(mNetworkExecutor.getResponseHandlerList());
        return packet;
    }

    public Disposable onConnect(ConnectHandler connectHandler){
        ConnectHandlerWrap connectHandlerWrap = new ConnectHandlerWrap(connectHandler);
        connectHandlerWrap.addTo(mNetworkExecutor.getConnectHandlerList());
        return connectHandlerWrap;
    }

    public void send(String msg){
        send(msg.getBytes(Charset.defaultCharset()));
    }

    public void send(byte[] data){

        mNetworkExecutor.send(data);

    }

    public void connect(){
        mNetworkExecutor.connect();
    }

    public void disconnect(){
        mNetworkExecutor.disconnect();
    }
}
