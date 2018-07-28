package github.com.abonifacio.microfonoremoto.utils;

/**
 * Created by Augusto on 6/10/2017.
 */

public class Conf {
    public static final int BUFFER_SIZE = 64512;
    public static String SERVER_HOST = "http://192.168.0.37";

    public static String getServerIp(){
        return SERVER_HOST.substring(7);
    }
}
