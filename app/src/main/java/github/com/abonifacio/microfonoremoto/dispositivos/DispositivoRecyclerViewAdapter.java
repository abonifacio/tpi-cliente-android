package github.com.abonifacio.microfonoremoto.dispositivos;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import github.com.abonifacio.microfonoremoto.MicApplication;
import github.com.abonifacio.microfonoremoto.R;
import github.com.abonifacio.microfonoremoto.utils.ClienteHttp;
import github.com.abonifacio.microfonoremoto.utils.Toaster;
import github.com.abonifacio.microfonoremoto.utils.UdpListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Dispositivo}
 */
public class DispositivoRecyclerViewAdapter extends RecyclerView.Adapter<DispositivoRecyclerViewAdapter.ViewHolder> implements SwipeRefreshLayout.OnRefreshListener{

    private List<Dispositivo> mValues = new ArrayList<>();
    private final SwipeRefreshLayout refreshLayout;

    public DispositivoRecyclerViewAdapter(SwipeRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
        this.refreshLayout.setRefreshing(true);
        this.refreshLayout.setOnRefreshListener(this);
        this.loadDispositivos();
    }

    @SuppressWarnings("unchecked")
    private void loadDispositivos(){
        new ClienteHttp.Request<List<Dispositivo>>(new ClienteHttp.Callback<List<Dispositivo>>() {
            @Override
            public void onSuccess(List<Dispositivo> arg) {
                if(UdpListener.isListening()){
                    String current = UdpListener.getCurrent();
                    if(current!=null){
                        for(Dispositivo d : arg){
                            if(current.equals(d.getMac())){
                                d.setStatus(Dispositivo.PLAYING);
                                break;
                            }
                        }
                    }
                }
                mValues = arg;
                notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError() {
                refreshLayout.setRefreshing(false);
            }
        }).execute(ClienteHttp.getDispositivoService().list());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dispositivo_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getNombre());
        holder.mContentView.setText(mValues.get(position).getIp());
        switch (holder.mItem.getStatus()){
            case Dispositivo.PLAYING:
                holder.container.setBackgroundResource(R.color.cardSelected);
                holder.actionButton.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                break;
            case Dispositivo.ERROR:
                holder.container.setBackgroundColor(Color.TRANSPARENT);
                holder.actionButton.setImageResource(R.drawable.ic_error_outline_black_24dp);
                holder.actionButton.setColorFilter(Color.RED);
                break;
            case Dispositivo.LOADING:
                holder.container.setBackgroundColor(Color.TRANSPARENT);
                holder.actionButton.setImageResource(R.drawable.ic_sync_black_24dp);
                break;
            default: // READY
                holder.container.setBackgroundColor(Color.TRANSPARENT);
                holder.actionButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                break;
        }
        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onListFragmentInteraction(holder.mItem,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void onRefresh() {
        this.loadDispositivos();
    }

    public void onListFragmentInteraction(Dispositivo item,int position) {
        if(UdpListener.isListening()){
            UdpListener.stopListening();
            setStatus(item,Dispositivo.READY,position);
            stop(item);
        }else{
            play(item,position);
        }
    }

    private void play(final Dispositivo item, final int position){
        this.toggle(item,new ClienteHttp.Callback<Void>() {
            @Override
            public void onSuccess(Void emptyBody) {
                UdpListener.startListening(item);
                setStatus(item,Dispositivo.PLAYING,position);
            }

            @Override
            public void onError() {
                UdpListener.stopListening();
                setStatus(item,Dispositivo.READY,position);
            }
        });
    }

    private void stop(final Dispositivo item){
        this.toggle(item,new ClienteHttp.Callback<Void>() {
            @Override
            public void onSuccess(Void emptyBody) {
                Toaster.show("Desuscrito correctamente", Snackbar.LENGTH_SHORT);
            }
            @Override
            public void onError() {}
        });
    }

    private void toggle(final Dispositivo item, ClienteHttp.Callback<Void> callback){
        item.setIp(MicApplication.getDeviceIp());
        new ClienteHttp.Request<>(callback).execute(ClienteHttp.getDispositivoService().listen(item));
    }

    private void setStatus(Dispositivo item, int status,int position){
        item.setStatus(status);
        notifyItemChanged(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageButton actionButton;
        public final RelativeLayout container;
        public Dispositivo mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.tv_nombre);
            mContentView = (TextView) view.findViewById(R.id.tv_ip);
            actionButton = (ImageButton) view.findViewById(R.id.bt_play);
            container = (RelativeLayout) view.findViewById(R.id.item_container);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}
