package github.com.abonifacio.microfonoremoto.dispositivos;

import android.graphics.Color;
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
import github.com.abonifacio.microfonoremoto.utils.UdpListener;
import okhttp3.ResponseBody;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Dispositivo}
 */
public class DispositivoRecyclerViewAdapter extends RecyclerView.Adapter<DispositivoRecyclerViewAdapter.ViewHolder> implements SwipeRefreshLayout.OnRefreshListener{

    private List<Dispositivo> mValues = new ArrayList<>();
    private final SwipeRefreshLayout refreshLayout;
    private final DispositivoService service;

    public DispositivoRecyclerViewAdapter(SwipeRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
        this.refreshLayout.setRefreshing(true);
        this.refreshLayout.setOnRefreshListener(this);
        this.service = ClienteHttp.getDispositivoService();
        this.loadDispositivos();
    }

    @SuppressWarnings("unchecked")
    private void loadDispositivos(){
        new ClienteHttp.Request<List<Dispositivo>>(new ClienteHttp.Callback<List<Dispositivo>>() {
            @Override
            public void onSuccess(List<Dispositivo> arg) {

                mValues = arg;
                notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError() {
                refreshLayout.setRefreshing(false);
            }
        }).execute(service.list());

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
                holder.actionButton.setImageResource(R.drawable.ic_compare_arrows_black_24dp);
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
            if(item.getStatus()!=Dispositivo.PLAYING){
                toggleOn(item,position,true);
            }else{
                item.setStatus(Dispositivo.PLAYING);
                notifyItemChanged(position);
                toggleOn(item,position,false);
            }
        }else{
            toggleOn(item,position,true);
        }
    }

    @SuppressWarnings("unchecked")
    private void toggleOn(final Dispositivo item, final int position,final boolean play){
        item.setIp(MicApplication.getDeviceIp());
        new ClienteHttp.Request<ResponseBody>(new ClienteHttp.Callback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody body) {
                if(play){
                    UdpListener.startListening(item.getPuerto());
                }
                item.setStatus(play? Dispositivo.PLAYING : Dispositivo.READY);
                notifyItemChanged(position);
            }

            @Override
            public void onError() {}
        }).execute(service.listen(item));
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
