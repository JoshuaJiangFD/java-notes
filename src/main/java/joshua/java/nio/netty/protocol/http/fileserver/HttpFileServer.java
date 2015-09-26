package joshua.java.nio.netty.protocol.http.fileserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * 利用 Netty提供的HTTP协议栈实现一个FileServer
 * <p/>
 * Created by joshua on 2015/9/12.
 */
public class HttpFileServer {

    private static final String DEFAULT_URL = "/src/main/java";

    public void run(final int port, final String url) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            /*Inbound*/
                            /**
                             * HttpRequestDecoder： 在每个HTTP消息中会生成多个消息对象: HttpRequest,HttpContent, LastHttpContent
                             */
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            /**
                             * HttpObjectAggregator: 将Decoder生成的多个消息对象转换为单一的FullHttpRequest或者FullHttpResponse
                             */
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            /*Outbound*/
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            /**
                             *ChunkedWriteHandler: 异步发送大的码流，而不占用过多的内存(Transfer-Encoding:chunked)
                             */
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));
                        }
                    });
            ChannelFuture future = b.bind("localhost", port).sync();
            System.out.println("HTTP文件目录服务器启动，网址是 : " + "http://localhost:"
                    + port + url);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        String url = DEFAULT_URL;
        if (args.length > 1)
            url = args[1];
        new HttpFileServer().run(port, url);
    }

}
