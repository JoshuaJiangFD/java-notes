package joshua.java.nio.network.reactor;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by joshua on 2015/8/11.
 */
public class ReadEventHandler implements EventHandler{

    private Selector demultiplexer;

    private ByteBuffer inputBuffer=ByteBuffer.allocate(2048);

    public ReadEventHandler(Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        SocketChannel socketChannel=(SocketChannel)handle.channel();
        //read data from the client connection
        socketChannel.read(inputBuffer);
        //flip the buffer to start reading from the beginning
        inputBuffer.flip();
        byte[] buffer=new byte[inputBuffer.limit()];
        inputBuffer.get(buffer);
        System.out.println("Received message from client : "+ new String(buffer));
        //Rewind the buffer to re-start reading from the beginning
        inputBuffer.rewind();
        //Register the interest for writable readiness event forthis channel in order to echo back the message
        socketChannel.register(demultiplexer, SelectionKey.OP_WRITE, inputBuffer);
    }
}
