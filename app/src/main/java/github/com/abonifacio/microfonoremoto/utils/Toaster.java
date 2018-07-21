package github.com.abonifacio.microfonoremoto.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import github.com.abonifacio.microfonoremoto.MicApplication;

/**
 * Created by Augusto on 6/10/2017.
 */

public class Toaster {

    public static void show(final String msg, final int duration){
        Log.d("SNACKBAR",msg);
        MicApplication.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Snackbar sb = Snackbar.make(MicApplication.getMainView(),msg,duration);
                sb.setAction("Cerrar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sb.dismiss();
                    }
                });
                sb.show();
            }
        });
    }

    public static void show(String msg){
        if(msg==null) msg = "Error desconocido";
        show(msg,Snackbar.LENGTH_INDEFINITE);
    }
}
