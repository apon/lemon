package me.apon.lemon.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/10/26.
 */
public interface Protocols {

    /**
     * 分包
     * @param inputStream
     * @return
     * @throws IOException
     */
    byte[] unpack(InputStream inputStream) throws IOException;
    /**
     * 打包 如：加包头
     * @param data
     * @return
     */
    byte[] pack(byte[] data);


}
