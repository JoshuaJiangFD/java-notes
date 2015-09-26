package joshua.java.nio.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 *利用 java nio 实现简单的多端口 Echo Server。
 *这个server可以监听多个端口的消息，并读取client端发送的数据转换成string再返回给client
 *
 *
 * @see <a href="http://www.ibm.com/developerworks/cn/education/java/j-nio/j-nio.html#ibm-pcon">blog address</a>
 * Created by joshua on 2015/8/7.
 */
public class MultiPortEcho {

    private int ports[];

    //
    private ByteBuffer echoBuffer=ByteBuffer.allocate(1024);

    public MultiPortEcho(int[] ports) throws IOException {
        this.ports = ports;
        start();
    }

    private void start() throws IOException {

        //create a new selector
        Selector selector=Selector.open();
        //open a listener on each port, and register each one with the selector
        for (int i=0; i<ports.length; ++i) {
            /*
                1. 为了接收连接，我们需要一个 ServerSocketChannel，对于要监听的每一个端口都需要有一个 ServerSocketChannel
                因此对于每一个端口，我们打开一个 ServerSocketChannel
             */
            ServerSocketChannel ssc = ServerSocketChannel.open();
            /*
                2. 将 ServerSocketChannel 设置为 非阻塞的
                我们必须对每一个要使用的套接字通道调用这个方法，否则异步 I/O 就不能工作
             */
            ssc.configureBlocking( false );
            /**
             *  3. ServerSocketChannel绑定到指定的端口
             *  首先拿到ServerSocket,再从端口建立InetSocketAddress,最后绑定两者
             */
            ServerSocket ss = ssc.socket();
            InetSocketAddress address = new InetSocketAddress( ports[i] );
            ss.bind( address );

            /**
             *  4. 将新打开的 ServerSocketChannels 注册到 Selector上
             *  a)  register() 的第一个参数总是这个 Selector；
             *  b)  OP_ACCEPT指定我们想要监听 accept 事件，也就是在新的连接建立时所发生的事件。这是适用于 ServerSocketChannel 的唯一事件类型
             *  c)  register() 的调用的返回值。 SelectionKey 代表这个通道在此 Selector 上的这个注册。
             *      当某个 Selector 通知您某个传入事件时，它是通过提供对应于该事件的 SelectionKey 来进行的。SelectionKey 还可以用于取消通道的注册。
             */
            SelectionKey key = ssc.register( selector, SelectionKey.OP_ACCEPT );

            System.out.println( "Going to listen on "+ports[i] );
        }

        /**
         * 主循环，使用 Selectors 的几乎每个程序都像下面这样使用内部循环
         */
        while(true){
            /**
             *
             */
            int num = selector.select();
            Set selectedKeys=selector.selectedKeys();
            Iterator it=selectedKeys.iterator();
            while(it.hasNext()){
                SelectionKey key=(SelectionKey)it.next();
                /**
                 * 检查key的事件类型， 是否为新连接事件
                 */
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {

                    /**
                     * 接受新的连接
                     * 因为我们知道这个服务器套接字上有一个传入连接在等待，所以可以安全地接受它；也就是说，不用担心 accept() 操作会阻塞
                     */
                    ServerSocketChannel ssc=(ServerSocketChannel)key.channel();
                    SocketChannel sc = ssc.accept();
                    //同样设置为非阻塞
                    sc.configureBlocking( false );
                    //同样注册该事件，监听socket上新的数据到达事件
                    SelectionKey newKey= ssc.register(selector,SelectionKey.OP_READ);
                    /**
                     * 删除已经处理的SelectionKey
                     * 如果我们没有删除处理过的键，那么它仍然会在主集合中以一个激活的键出现，这会导致我们尝试再次处理它
                     */
                    it.remove();
                    System.out.println("Got Connection from"+sc);
                /**
                 * 检查key的事件类型是否为OP_READ事件，该事件在来自一个socket的数据到达时触发
                 */
                }else if((key.readyOps() & SelectionKey.OP_READ)== SelectionKey.OP_READ){
                    //从key获取通道
                    SocketChannel sc = (SocketChannel)key.channel();
                    int bytesEchoed = 0;
                    /**
                     * 先将从通道读出数据到缓冲区，再flip缓冲区，最后再将数据从缓冲区写出到通道中
                     */
                    while (true) {
                        /**
                         * clear()重置缓冲区，使缓冲区可以接收新的数据
                         */
                        echoBuffer.clear();
                        //将数据从通道读到缓冲区
                        int r = sc.read( echoBuffer );

                        if (r<=0) {
                            break;
                        }
                        /**
                         * flip()使得可以从缓冲区读数据
                         */
                        echoBuffer.flip();

                        sc.write( echoBuffer );
                        bytesEchoed += r;
                    }

                    System.out.println( "Echoed "+bytesEchoed+" from "+sc );

                    it.remove();
                }
            }
        }
    }


    static public void main( String args[] ) throws Exception {
        if (args.length<=0) {
            System.err.println( "Usage: java MultiPortEcho port [port port ...]" );
            System.exit( 1 );
        }

        int ports[] = new int[args.length];

        for (int i=0; i<args.length; ++i) {
            ports[i] = Integer.parseInt( args[i] );
        }

        new MultiPortEcho( ports );
    }
}
