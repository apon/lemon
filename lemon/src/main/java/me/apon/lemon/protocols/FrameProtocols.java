package me.apon.lemon.protocols;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import me.apon.lemon.core.LLog;
import me.apon.lemon.core.Protocols;


/**
 * Created by yaopeng(aponone@gmail.com) on 2018/10/26.
 */
public class FrameProtocols implements Protocols {

    private final static String TAG = FrameProtocols.class.getSimpleName();

    public static FrameProtocols create(){
        return  new FrameProtocols();
    }


    private int getDataLen(InputStream in) throws IOException{

        int headLen = 4;
        int dataLen;
        byte[] pheadBuf = new byte[headLen];
        for (int i=0;i<headLen;i++){
            int b = in.read();
            pheadBuf[i] = (byte) b;
        }
        dataLen = ByteBuffer.wrap(pheadBuf).getInt()-headLen;

        return dataLen;
    }

    @Override
    public byte[] unpack(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = null;
        int dataLen = getDataLen(inputStream);
        LLog.d(TAG,Thread.currentThread().getName()+": =======解析收到的数据=======:数据大小:"+dataLen);
        dataInputStream = new DataInputStream(inputStream);
        byte[] buf = null;

        if (dataLen > 0) {
            if (dataLen > 10 * 1024 * 1024) {
                return buf;
            }
            buf = new byte[dataLen];
            dataInputStream.readFully(buf);

        }else {
            throw new IOException();//抛出异常断开连接
        }

        return buf;
    }

    @Override
    public byte[] pack(byte[] data) {
        ByteBuffer bb = ByteBuffer.allocate(4 + data.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(data.length+4);
        bb.put(data);
        return bb.array();
    }

//    @Override
//    public byte[] decode(byte[] data) {
//        byte[] dest = new byte[data.length-4];
//        System.arraycopy(data, 4, dest, 0, data.length-4);
//        return dest;
//    }
}
