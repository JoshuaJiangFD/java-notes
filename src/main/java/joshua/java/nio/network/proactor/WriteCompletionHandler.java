package joshua.java.nio.network.proactor;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by joshua on 2015/8/11.
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, SessionState> {

    private AsynchronousSocketChannel socketChannel;

    public WriteCompletionHandler(
            AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer bytesWritten, SessionState sessionState) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, SessionState attachment) {
        // Handle write failure.....
    }
}
