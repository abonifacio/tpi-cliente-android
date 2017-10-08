package github.com.abonifacio.microfonoremoto.dispositivos;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import github.com.abonifacio.microfonoremoto.R;
import github.com.abonifacio.microfonoremoto.utils.ClienteHttp;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class DispositivoListFragment extends Fragment {

    private DispositivoService service = ClienteHttp.getDispositivoService();
    private DispositivoRecyclerViewAdapter mAdapter = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DispositivoListFragment() {
    }

    public static DispositivoListFragment newInstance() {
        return new DispositivoListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dispositivo_list_fragment, container, false);
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // Set the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAdapter = new DispositivoRecyclerViewAdapter(refreshLayout);
        recyclerView.setAdapter(mAdapter);
        return view;
    }
}
