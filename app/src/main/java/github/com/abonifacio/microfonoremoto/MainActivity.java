package github.com.abonifacio.microfonoremoto;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.PopupWindow;

import github.com.abonifacio.microfonoremoto.dispositivos.DispositivoListFragment;
import github.com.abonifacio.microfonoremoto.utils.Conf;

public class MainActivity extends BaseActivty {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.loadFragement();
    }

    private void loadFragement(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.content, DispositivoListFragment.newInstance());
        transaction.commit();

    }

    @Override
    protected int getMainLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected int getContainerId() {
        return R.id.container;
    }


}
