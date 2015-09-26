package joshua.java.nio.local;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * 通道和缓冲区实现以block的方式 读写本地文件
 *
 * Created by joshua on 2015/8/8.
 */
public class CopyFile {


    /**
     *通过通道和缓冲区 以block的方式 copy文件
     *
     * @param fromFile
     * @param toFile
     * @throws Exception
     */
    public void copyFile(String fromFile, String toFile) throws Exception {
        FileInputStream fin = new FileInputStream(fromFile);
        FileOutputStream fout = new FileOutputStream(toFile);
        /*
            1.获得FileChannel
         */
        FileChannel fcin = fin.getChannel();
        FileChannel fcout = fout.getChannel();

        /*
            2.创建缓冲区
         */
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        /*
            3. 通过缓冲区，将数据从输入通道写入到输出通道
            从源文件中将数据读到这个缓冲区中，然后将缓冲区写入目标文件。这个程序不断重复 ― 读、写、读、写 ― 直到源文件结束
         */
        while (true) {
            //从输入通道读入缓冲区之前，我们调用 clear() 方法
            buffer.clear();

            //从输入通道读入缓冲区,r表示本次读入的字节数，如果为0或-1,表示本次没有读入，fcin到达结束位
            int r = fcin.read(buffer);

            if (r == -1) {
                break;
            }
            //flip() 方法让缓冲区可以将新读入的数据写入另一个通道
            buffer.flip();
            //从缓冲区写入输出通道，缓冲区内部跟踪它包含多少数据，以及还有多少数据要写入
            fcout.write(buffer);
        }
    }

    /**
     * 通过通道和直接缓冲区的方式实现fast copy 文件
     *
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    public void fastCopyFile(String fromFile, String toFile) throws IOException {
        FileInputStream fin = new FileInputStream(fromFile);
        FileOutputStream fout = new FileOutputStream(toFile);

        FileChannel fcin = fin.getChannel();
        FileChannel fcout = fout.getChannel();

        /**
         * 直接缓冲区: 是为加快 I/O 速度，而以一种特殊的方式分配其内存的缓冲区
         *
         * 描述直接缓冲区:
         * 给定一个直接字节缓冲区，Java 虚拟机将尽最大努力直接对它执行本机 I/O 操作。
         * 也就是说，它会在每一次调用底层操作系统的本机 I/O 操作之前(或之后)，尝试避免将缓冲区的内容拷贝到一个中间缓冲区中(或者从一个中间缓冲区中拷贝数据)。
         *
         */
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 );

        while (true) {
            buffer.clear();

            int r = fcin.read(buffer);

            if (r == -1) {
                break;
            }
            buffer.flip();
            fcout.write(buffer);
        }
    }
}
