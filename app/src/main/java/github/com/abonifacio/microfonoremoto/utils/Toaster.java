package github.com.abonifacio.microfonoremoto.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import github.com.abonifacio.microfonoremoto.MicApplication;

/**
 * Created by Augusto on 6/10/2017.
 */

public class Toaster {

    public static void showold(String msg){
        Toast.makeText(MicApplication.getAppContext(),msg,Toast.LENGTH_LONG).show();
    }

    public static void show(final String msg){
        Log.d("SNACKBAR",msg);
        MicApplication.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(MicApplication.getMainView(),msg,Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
