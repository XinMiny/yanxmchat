package com.yanxm.chat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class NIOTest {
    public static void main(String[] args) throws Exception {


        readFile();


    }


    /**
     * 原生零拷贝
     * @throws Exception
     */
    public static void readFile() throws Exception {
        File file = new File("C:\\Users\\yanxm\\Desktop\\vuedemo\\mysql.sql");
        long len = file.length();
        byte[] ds = new byte[(int) len];
        MappedByteBuffer mappedByteBuffer = new FileInputStream(file).getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
                len);
        for (int offset = 0; offset < len; offset++) {
            ds[offset] = mappedByteBuffer.get();

        }
        FileOutputStream outs = new FileOutputStream(new File("C:\\Users\\yanxm\\Desktop\\vuedemo\\test1.txt"));
        outs.write(ds);
    }


    /**
     * 零拷贝关键 transferTo方法
     * @throws Exception
     */
    private static void catFiles() throws Exception {

        //OutputStream out = new FileOutputStream(new File("C:\\Users\\yanxm\\Desktop\\vuedemo\\copy.txt" ));
        WritableByteChannel target = Channels.newChannel(System.out);
        FileInputStream fis = new FileInputStream("C:\\Users\\yanxm\\Desktop\\vuedemo\\mysql.sql");
        FileChannel channel = fis.getChannel();
        channel.transferTo(0, channel.size(), target);
        channel.close();
        fis.close();

    }

}
