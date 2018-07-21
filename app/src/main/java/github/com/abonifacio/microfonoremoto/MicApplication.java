package github.com.abonifacio.microfonoremoto;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;

import github.com.abonifacio.microfonoremoto.dispositivos.Dispositivo;
import github.com.abonifacio.microfonoremoto.utils.Conf;

/**
 * Created by Augusto on 6/10/2017.
 */

public class MicApplication extends Application{
    private static Context context;
    private static Activity activity;
    private static View mainView;
    private static String deviceIp;
    private static Dispositivo thisDispositivo;

    private static final String EXTRA_SERVER_ADRESS = "serverAdressExtra";
    private static final String EXTRA_SAMPLE_SIZE = "recorderSampleSizeExtra";
    private static final String EXTRA_SAMPLE_RATE = "recorderSampleRateExtra";
    private static final String EXTRA_STEREO = "recorderStereoExtra";

    public void onCreate() {
        super.onCreate();
        MicApplication.context = getApplicationContext();
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        deviceIp = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        thisDispositivo = new Dispositivo();
        thisDispositivo.setNombre(android.os.Build.MODEL);
        thisDispositivo.setMac(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
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

    public static void loadPreferences(){
        SharedPreferences sp = activity.getPreferences(Context.MODE_PRIVATE);
        Conf.SERVER_HOST = sp.getString(EXTRA_SERVER_ADRESS,null);
        setThisDispositivoValues(sp.getInt(EXTRA_SAMPLE_RATE,44100),sp.getInt(EXTRA_SAMPLE_SIZE,16),sp.getBoolean(EXTRA_STEREO,true));
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

    public static Dispositivo getThisDispositivo(){
        return thisDispositivo;
    }

    public static void savePreferences(){
        SharedPreferences sp = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString(EXTRA_SERVER_ADRESS, Conf.SERVER_HOST);
        e.putInt(EXTRA_SAMPLE_RATE,MicApplication.getThisDispositivo().getSampleRate());
        e.putInt(EXTRA_SAMPLE_SIZE,MicApplication.getThisDispositivo().getSampleSize());
        e.putBoolean(EXTRA_STEREO,MicApplication.getThisDispositivo().getStereo());
        e.apply();
    }

    public static void setThisDispositivoValues(int sampleRate,int sampleSize, boolean stereo){
        thisDispositivo.setSampleRate(sampleRate);
        thisDispositivo.setSampleSize(sampleSize);
        thisDispositivo.setStereo(stereo);
    }

    public static void setThisDispositivoPort(int port){
        thisDispositivo.setPuerto(port);
    }

}
