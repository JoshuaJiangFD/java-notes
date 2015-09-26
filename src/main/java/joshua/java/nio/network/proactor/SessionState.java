package joshua.java.nio.network.proactor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by joshua on 2015/8/11.
 */
public class SessionState {

    private Map<String, String> sessionProps = new ConcurrentHashMap<String, String>();

    public String getProperty(String key) {
        return sessionProps.get(key);
    }


    public void setProperty(String key,String value){
        sessionProps.put(key,value);
    }
}
