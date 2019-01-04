package juanmanuelco.facci.com.soschat.Fragments;


import android.content.Intent;

import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;

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
import java.util.Arrays;

import juanmanuelco.facci.com.soschat.ChatActivity;
import juanmanuelco.facci.com.soschat.Adaptadores.AdaptadorDispositivos;
import juanmanuelco.facci.com.soschat.DB.DB_SOSCHAT;
import juanmanuelco.facci.com.soschat.R;


public class FM_historico extends Fragment {

    private SearchView searchView = null;
    private boolean searchViewShow = false;
    private SearchView.OnQueryTextListener queryTextListener;

    RecyclerView rv_participants;
    AdaptadorDispositivos adaptadorDispositivos;
    ArrayList<String[]> encontrados,searchfound;
    ArrayList<String> searches;
    DB_SOSCHAT db;
    TextView TV_NO_ENC;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_fm_historico, container, false);
        setHasOptionsMenu(true);
        db= new DB_SOSCHAT(getActivity());
        encontrados= new ArrayList<>();
        searches = new ArrayList<>();
        searches=db.buscador();
        encontrados= db.listaEncontrados();
        TV_NO_ENC= v.findViewById(R.id.TV_NO_ENC);
        if(encontrados.size()<1) TV_NO_ENC.setText(R.string.NOADD);
        else TV_NO_ENC.setText("");
        rv_participants=v.findViewById(R.id.participants_rv);
        rv_participants.setLayoutManager(new LinearLayoutManager(getActivity()));
        adaptadorDispositivos= new AdaptadorDispositivos(encontrados, getActivity());
        adaptadorDispositivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                getActivity().startActivity(intent);
            }
        });
        rv_participants.setAdapter(adaptadorDispositivos);
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
                        for (int i=0; i<encontrados.size();i++ ){
                            String[] partes = searches.get(i).split(",");
                            String nombre = partes[0];
                            String mac = partes[1];
                            if (nombre.toLowerCase().contains(palabra)){
                                searchfound.add(new String[]{nombre,mac});
                                //Log.i("encontrado",nombre +" "+ mac);
                            }
                        }
                        adaptadorDispositivos= new AdaptadorDispositivos(searchfound, getActivity());
                        rv_participants.setAdapter(adaptadorDispositivos);
                        return true;
                    }else{
                        adaptadorDispositivos= new AdaptadorDispositivos(encontrados, getActivity());
                        rv_participants.setAdapter(adaptadorDispositivos);
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
