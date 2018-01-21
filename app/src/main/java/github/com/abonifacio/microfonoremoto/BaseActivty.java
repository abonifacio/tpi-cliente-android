package github.com.abonifacio.microfonoremoto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import github.com.abonifacio.microfonoremoto.utils.Conf;

/**
 * Created by Augusto on 15/10/2017.
 */

public abstract class BaseActivty extends AppCompatActivity {

    private static final String EXTRA_SERVER_ADRESS = "serverAdressExtra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getMainLayout());
        MicApplication.setActivity(this);
        MicApplication.setMainView(findViewById(this.getContainerId()));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        String host = sp.getString(EXTRA_SERVER_ADRESS,null);
        if(host==null){
            this.openConfigurationDialog();
        }else{
            Conf.SERVER_HOST = host;
        }

    }

    protected abstract int getMainLayout();

    protected abstract int getContainerId();

    @Override
    protected void onDestroy() {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString(EXTRA_SERVER_ADRESS, Conf.SERVER_HOST);
        e.apply();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.action,menu);
        return true;
    }

    private void openConfigurationDialog(){
        final EditText et = new EditText(this);
        et.setText(Conf.SERVER_HOST);
        et.setHint("http://...");

        AlertDialog popup = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Configuracion")
                .setMessage("Dirección del servidor")
                .setView(et)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ip = et.getText().toString();
                        Conf.SERVER_HOST = ip;
                        Log.d("SET_SERVER_IP",ip);
                    }
                })
                .setNegativeButton("Cancelar",null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                this.openConfigurationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
