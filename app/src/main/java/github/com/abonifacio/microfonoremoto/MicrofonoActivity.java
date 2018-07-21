package github.com.abonifacio.microfonoremoto;

import android.os.AsyncTask;
import android.os.Bundle;

import java.util.concurrent.ExecutionException;

import github.com.abonifacio.microfonoremoto.utils.ClienteHttp;
import github.com.abonifacio.microfonoremoto.utils.PCMRecorder;
import github.com.abonifacio.microfonoremoto.utils.UdpSender;

/**
 * Created by Augusto on 21/7/2018.
 */

public class MicrofonoActivity extends BaseActivty {

    private PCMRecorder mPCMRecorder;
    private UdpSender mUdpSender;

    public static int RESULT_OK = 200;
    public static int BAD_SAMPLE_RATE = 400;
    public static int ERROR_CREATE_UDP = 500;
    public static int ERROR_REGISTER = 501;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPCMRecorder = new PCMRecorder(MicApplication.getThisDispositivo(),new PCMRecorder.ByteConsumer() {
            @Override
            public void onBytes(byte[] data, int size) {
                if(!mUdpSender.send(data,size)){
                    stop();
                    finish(ERROR_CREATE_UDP);
                };
            }
        });
        this.register();
    }

    @SuppressWarnings("unchecked")
    private void register(){
        new ClienteHttp.Request<>(new ClienteHttp.Callback<String>() {
            @Override
            public void onSuccess(String arg) {
                int port = Integer.valueOf(arg);
                MicApplication.setThisDispositivoPort(port);
                start(port);
            }
            @Override
            public void onError() {
                finish(ERROR_REGISTER);
            }
        }).execute(ClienteHttp.getDispositivoService().create(MicApplication.getThisDispositivo()));
    }

    @SuppressWarnings("unchecked")
    private void unregister(){
        final MicrofonoActivity act = this;
        new ClienteHttp.Request<>(new ClienteHttp.Callback<Void>() {

            @Override
            public void onSuccess(Void arg) {
                act.stop();
            }

            @Override
            public void onError() {}
        }).execute(ClienteHttp.getDispositivoService().delete(MicApplication.getThisDispositivo().getMac()));
    }

    private void stop(){
        mPCMRecorder.destroy();
        if(mUdpSender!=null){
            mUdpSender.stop();
        }
    }

    private void start(int puerto){
        AsyncTask<Integer, Void, UdpSender> asyncTask = new AsyncTask<Integer, Void, UdpSender>() {

            @Override
            protected UdpSender doInBackground(Integer... params) {
                return UdpSender.start(params[0]);
            }

        };
        try {
            mUdpSender = asyncTask.execute(puerto).get();
        } catch (Exception e) {
            e.printStackTrace();
            this.finish(ERROR_CREATE_UDP);
            return;
        }
        if(!mPCMRecorder.start()){
            this.finish(BAD_SAMPLE_RATE);
        }
    }

    @Override
    protected int getMainLayout() {
        return R.layout.microfono_activity;
    }

    @Override
    protected int getContainerId() {
        return R.id.microfono_main;
    }

    @Override
    protected void onDestroy() {
        super.setResult(RESULT_OK);
        this.unregister();
        super.onDestroy();
    }

    public void finish(int code) {
        super.setResult(code);
        this.finish();
    }
}