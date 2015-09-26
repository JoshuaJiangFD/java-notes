package joshua.java.nio.network.reactor;

import java.nio.channels.SelectionKey;

/**
 * Created by joshua on 2015/8/11.
 */
public interface EventHandler {

    public void handleEvent(SelectionKey handle) throws Exception;
}
