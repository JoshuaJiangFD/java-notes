package joshua.java.nio.network.proactor;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by joshua on 2015/8/11.
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,SessionState>{

    private AsynchronousServerSocketChannel listener;


    public AcceptCompletionHandler(AsynchronousServerSocketChannel listener) {
        this.listener = listener;
    }

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, SessionState sessionState) {
        // accept the next connection
        SessionState newSessionState = new SessionState();
        listener.accept(newSessionState, this);

        // handle this connection
        ByteBuffer inputBuffer = ByteBuffer.allocate(2048);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel,inputBuffer);
        socketChannel.read(inputBuffer, sessionState, readCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, SessionState attachment) {
        // Handle connection failure...
    }
}
