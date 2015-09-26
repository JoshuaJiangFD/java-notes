package joshua.java.nio.network.reactor;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by joshua on 2015/8/11.
 */
public class WriteEventHandler implements EventHandler {

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        SocketChannel socketChannel = (SocketChannel) handle.channel();
        ByteBuffer inputBuffer = (ByteBuffer) handle.attachment();
        socketChannel.write(inputBuffer);
        // Close connection
        socketChannel.close();
    }
}
