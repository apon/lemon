# 1.Lemon简介

Android Socket Client Library.

一个简单、轻量级的Android Socket框架。


# 2.使用

构建Lemon对象

```
SocketClient client = new SocketClient.Builder()
                .ip("120.78.175.94") //设置ip、端口
                .port(7272)
                .setKeepAlive(true) //设置socket选项
                .build();
//心跳包内容
String pingData = "{\"type\":\"ping-yaopeng\"}";
Lemon lemon = new Lemon.Builder()
        .client(client) //设置SocketClient
        .protocols(TextProtocols.create()) //设置协议，可自定义
        .pingInterval(10) //设置心跳间隔（秒）大于0打开心跳功能
        .pingData(pingData.getBytes()) //设置心跳包内容
        .debug(true) //开启调试模式
        .build();
```

连接服务器、发送数据、断开连接


```
lemon.connect();//连接服务器

String str = "hello apon！";
lemon.send(str);//发送数据

lemon.disconnect();//断开连接
```

添加连接回调，onConnect返回Disposable，可调用Disposable.dispose()方法移除当前回调。


```
Disposable disposable = lemon.onConnect(new ConnectHandler() {

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

```

添加接收数据的回调。onMessage返回Disposable，可调用Disposable.dispose()方法移除当前回调。

```
Disposable disposable = lemon.onMessage(new MessageHandler() {
    @Override
    public void receive(byte[] data) {
        Log.d(TAG,"====收到数据====: "+new String(data));
    }
});
```



# 3.通讯协议

Lemon支持text协议、frame协议。如果以上协议不满足你的业务需求，可以参考TextProtocols.java、FrameProtocols.java定制自己的协议。

## text协议
Lemon定义了一种叫做text的文本协议，协议格式为 `数据包+换行符`，即在每个数据包末尾加上一个换行符表示包的结束。

## frame协议

Lemon定义了一种叫做frame的协议，协议格式为 `总包长+包体`，其中包长为4字节网络字节序的整数，包体可以是普通文本或者二进制数据。


配置协议

```
Lemon lemon = new Lemon.Builder()
        .client(client) //设置SocketClient
        .protocols(TextProtocols.create()) //使用text协议
        //.protocols(FrameProtocols.create()) //使用frame协议
        .pingInterval(10) //设置心跳间隔（秒）大于0打开心跳功能
        .pingData(pingData.getBytes()) //设置心跳包内容
        .debug(true) //开启调试模式
        .build();
```

# 4.如何制定协议
实际上制定自己的协议是比较简单的事情。简单的协议一般包含两部分:

* 区分数据边界的标识
* 数据格式定义

一个例子

-------
协议定义

这里假设区分数据边界的标识为换行符"\n"（注意请求数据本身内部不能包含换行符），数据格式为Json，例如下面是一个符合这个规则的请求包


```
{"type":"message","content":"hello"}

```

实现

在Lemon中开发的协议类必须实现Protocols接口，实现pack,unpack方法。Lemon会调用这两个方法打包、分包。（具体参考TextProtocols.java）


```
/**
 * Created by yaopeng(aponone@gmail.com) on 2018/11/4.
 */
public class TextProtocols implements Protocols {

    private List<Byte> bytes;

    //区分数据边界的标识
    private byte[] tail = "\n".getBytes();

    public static TextProtocols create(){
        return new TextProtocols();
    }

    public TextProtocols() {
        this.bytes = new ArrayList<>();
    }
    
    //打包，将换行符添加到数据尾部，当向服务端发送数据的时候会自动调用
    @Override
    public byte[] pack(byte[] data) {
        ByteBuffer bb = ByteBuffer.allocate(tail.length + data.length);
        bb.put(data);
        bb.put(tail);
        return bb.array();
    }

    //根据数据边界标识分包
    @Override
    public byte[] unpack(InputStream inputStream) throws IOException {
        bytes.clear();
        int len;
        byte temp;
        byte[] result = null;

        while ((len = inputStream.read()) != -1) {
            temp = (byte) len;
            bytes.add(temp);
            Byte[] byteArray = bytes.toArray(new Byte[]{});
            if (endWith(byteArray,tail)){
                result = getRangeBytes(bytes,0,bytes.size());
                break;
            }
        }

        if (len == -1){
            throw new IOException();
        }

        return result;
    }

    private boolean endWith(Byte[] src, byte[] target) {
        if (src.length < target.length) {
            return false;
        }
        for (int i = 0; i < target.length; i++) {//逆序比较
            if (target[target.length - i - 1] != src[src.length - i - 1]) {
                return false;
            }
        }
        return true;
    }

    private byte[] getRangeBytes(List<Byte> list, int start, int end) {
        Byte[] temps = Arrays.copyOfRange(list.toArray(new Byte[0]), start, end);
        byte[] result = new byte[temps.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = temps[i];
        }
        return result;
    }
    
}

```

# 5.演示

直接编译运行本项目，使用text协议连接echo服务器（120.78.175.94:7272）。

# License



```
Copyright 2018 apon , https://apon.me

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```





