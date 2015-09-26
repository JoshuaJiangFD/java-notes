package joshua.java.nio.network.reactor;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by joshua on 2015/8/11.
 */
public class AcceptEventHandler implements EventHandler {

    private Selector dumultiplexer;

    public AcceptEventHandler(Selector dumultiplexer) {
        this.dumultiplexer = dumultiplexer;
    }

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        ServerSocketChannel serverSocketChannel=(ServerSocketChannel)handle.channel();
        /**
         * get the socketChannel for this connection
         */
        SocketChannel socketChannel=serverSocketChannel.accept();
        if(socketChannel!=null){
            socketChannel.configureBlocking(false);
            socketChannel.register(dumultiplexer,SelectionKey.OP_READ);
        }
    }
}
