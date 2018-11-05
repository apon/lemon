package me.apon.lemonnet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;



import me.apon.lemon.core.ConnectHandler;
import me.apon.lemon.core.Disposable;
import me.apon.lemon.Lemon;
import me.apon.lemon.core.MessageHandler;
import me.apon.lemon.core.SocketClient;
import me.apon.lemon.protocols.FrameProtocols;
import me.apon.lemon.protocols.TextProtocols;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private Lemon lemon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SocketClient client = new SocketClient.Builder()
                .ip("120.78.175.94")
                .port(7272)
                .setKeepAlive(true)
                .build();
        String pingData = "{\"type\":\"ping-yaopeng\"}";
        lemon = new Lemon.Builder()
                .client(client)
                .protocols(TextProtocols.create())
                .pingInterval(10)
                .pingData(pingData.getBytes())
                .debug(true)
                .build();

        lemon.onConnect(new ConnectHandler() {

            @Override
            public void connectSuccess() {
                Log.d(TAG,"===连接成功===");
                lemon.send("连接成功-进入100房间-");
                Toast.makeText(MainActivity.this, "连接成功!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectFail() {
                Log.d(TAG,"===连接失败===");
            }

            @Override
            public void disconnect() {
                Log.d(TAG,"===连接断开===");
            }

        });
        final Disposable disposable = lemon.onMessage(new MessageHandler() {
            @Override
            public void receive(byte[] data) {
                Log.d(TAG,"====1.数据监听====: "+new String(data));
            }
        });
        lemon.onMessage(new MessageHandler() {
            @Override
            public void receive(byte[] data) {
                Log.d(TAG,"====2.数据监听====: "+new String(data));
            }
        });
        findViewById(R.id.start_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lemon.connect();
            }
        });
        findViewById(R.id.stop_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lemon.disconnect();
            }
        });

        findViewById(R.id.send_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = "hello apon！";

                lemon.send(str);
            }
        });

        findViewById(R.id.disposable_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disposable.dispose();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lemon.disconnect();
    }
}
