package juanmanuelco.facci.com.soschat.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import juanmanuelco.facci.com.soschat.Adaptadores.AdaptadorDispositivos;
import juanmanuelco.facci.com.soschat.ChatActivity;
import juanmanuelco.facci.com.soschat.Adaptadores.AdaptadorMensajes;
import juanmanuelco.facci.com.soschat.DB.DB_SOSCHAT;
import juanmanuelco.facci.com.soschat.R;


public class FM_mensajes extends Fragment {
    RecyclerView rv_participants;
    AdaptadorMensajes adaptadorMensajes;
    ArrayList<String[]> mensajes;
    DB_SOSCHAT db;
    TextView TV_NO_ENC;

    private SearchView searchView = null;
    private boolean searchViewShow = false;
    private SearchView.OnQueryTextListener queryTextListener;

    ArrayList<String[]> searchfound;
    ArrayList<String> searches;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_fm_mensajes, container, false);
        setHasOptionsMenu(true);
        db= new DB_SOSCHAT(getActivity());
        mensajes= new ArrayList<>();
        mensajes= db.mensajesRecibidos();

        searches = new ArrayList<>();
        searches=db.buscador_mensaje();

        Log.i("MensajesBD", mensajes.toString());

        TV_NO_ENC= v.findViewById(R.id.TV_NO_ENC);
        if(mensajes.size()<1) TV_NO_ENC.setText(R.string.NOMSG);
        else TV_NO_ENC.setText("");
        rv_participants=v.findViewById(R.id.participants_rv);
        rv_participants.setLayoutManager(new LinearLayoutManager(getActivity()));
        adaptadorMensajes = new AdaptadorMensajes(mensajes, getActivity());
        adaptadorMensajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                getActivity().startActivity(intent);
            }
        });
        rv_participants.setAdapter(adaptadorMensajes);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_principal, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText != null && !newText.isEmpty()) {
                        String palabra = newText.toLowerCase();
                        searchfound= new ArrayList<>();
                        for (int i=0; i<mensajes.size();i++ ){
                            String[] partes = searches.get(i).split(",");
                            String nombre = partes[0];
                            String mac = partes[1];
                            String tiempo = partes[2];
                            if (nombre.toLowerCase().contains(palabra)){
                                searchfound.add(new String[]{nombre,mac,tiempo});
                                //Log.i("encontrado",nombre +" "+ mac);
                            }
                        }
                        adaptadorMensajes= new AdaptadorMensajes(searchfound, getActivity());
                        rv_participants.setAdapter(adaptadorMensajes);
                        return true;
                    }else{
                        adaptadorMensajes= new AdaptadorMensajes(mensajes, getActivity());
                        rv_participants.setAdapter(adaptadorMensajes);
                        return true;
                    }
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
}
