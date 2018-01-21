package github.com.abonifacio.microfonoremoto;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.view.View;

import github.com.abonifacio.microfonoremoto.utils.Conf;

/**
 * Created by Augusto on 6/10/2017.
 */

public class MicApplication extends Application{
    private static Context context;
    private static Activity activity;
    private static View mainView;
    private static String deviceIp;

    public void onCreate() {
        super.onCreate();
        MicApplication.context = getApplicationContext();
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        deviceIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public static Context getAppContext() {
        return MicApplication.context;
    }

    public static Activity getActivity() {
        return activity;
    }

    public static void setActivity(Activity activity) {
        MicApplication.activity = activity;
    }

    public static View getMainView() {
        return mainView;
    }

    public static void setMainView(View mainView) {
        MicApplication.mainView = mainView;
    }

    public static String getDeviceIp() {
        return deviceIp;
    }
}
