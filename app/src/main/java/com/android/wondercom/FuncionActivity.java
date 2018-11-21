package com.android.wondercom;


import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentTransaction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.wondercom.CustomAdapters.AdaptadorDispositivos;
import com.android.wondercom.DB.DB_SOSCHAT;
import com.android.wondercom.Entities.ENCONTRADO;
import com.android.wondercom.Fragments.FM_encontrados;
import com.android.wondercom.Fragments.FM_historico;
import com.android.wondercom.NEGOCIO.DireccionMAC;

import java.util.ArrayList;
import java.util.List;

import static com.android.wondercom.NEGOCIO.Mensajes.cargando;

public class FuncionActivity extends Activity implements ActionBar.TabListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private MenuItem myActionMenuItem;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private String posicion;
    private DB_SOSCHAT db;
    private ArrayList<ENCONTRADO> ListaFound;
    RecyclerView RV;
    public ArrayList <String[]> listado2 = new ArrayList<String[]>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcion);
        db = new DB_SOSCHAT(this);
        DireccionMAC.direccion="";
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        RV=(RecyclerView) findViewById(R.id.participants_rv);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
            actionBar.setTitle(getResources().getString(R.string.app_name));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        MenuItem searchItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    BusquedaEnTab(posicion,newText);

                    /*Log.i("CantidadConectado",String.valueOf(conectados.size()));
                    String s="";

                    if(newText!=null && !newText.isEmpty()){
                        newText = newText.toLowerCase();
                        encontrados= new ArrayList<>();
                        for (Host items: conectados2 ){
                            String nombre = items.getName().toLowerCase();
                            if (nombre.contains(newText)){
                                encontrados.add(items);
                            }
                        }
                        Log.i("QueryEncontrados", encontrados.toString());
                        mParticipantsAdapter.actualizar(encontrados);
                    }else{
                        if (a!=null && !a.isEmpty()){
                            mParticipantsAdapter.setData(a);
                        }
                        Log.i("NoSeEncontro", "No se encontro el "+ newText.toString());
                    }*/
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    BusquedaEnTab(posicion,query);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        return true;
    }

    public void BusquedaEnTab(String posicion, String texto){
        try{
            if (Integer.parseInt(posicion)==0){
                listado2.clear();
                if (texto != null && !texto.isEmpty()){
                    listado2.clear();
                    Log.i("MensajePrueba", "Estas en el fragment 1 y tu mensaje es "+texto);
                    ListaFound = new ArrayList<ENCONTRADO>();
                    for (ENCONTRADO item : db.encontradosLista()){
                        String nombre = item.getNickname().toLowerCase();
                        if (nombre.contains(texto)){
                            ListaFound.add(item);
                            listado2.add(new String[]{item.getMac_destino(),item.getNickname()});
                        }
                    }
                    Log.i("pruena3", listado2.toString());
                    AdaptadorDispositivos adaptador = new AdaptadorDispositivos(listado2);
                    adaptador.actualizar(listado2);
                    RV.setAdapter(adaptador);
                }
            }
            if (Integer.parseInt(posicion)==1){
                Log.i("MensajePrueba", "Estas en el fragment 2 y tu mensaje es "+texto);
            }
            if (Integer.parseInt(posicion)==2){
                Log.i("MensajePrueba", "Estas en el fragment 3 y tu mensaje es "+texto);
            }
        }catch(Exception e ){

        }
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
        Log.i("Posicion", String.valueOf(tab.getPosition()));
        posicion=String.valueOf(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_borrar, container, false);
            return rootView;
        }
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fm= null;
            switch (position){
                case 0:
                    fm=new FM_encontrados();
                    break;
                case 1:
                    fm= new FM_historico();
                    break;
                case 2:
                    fm=new FM_historico();
                    break;
            }
            return fm;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CERCANOS";
                case 1:
                    return "MENSAJES";
                case 2:
                    return "HISTÓRICO";
            }
            return null;
        }
    }

    @Override
    protected void onResume() {
        DireccionMAC.direccion="";
        super.onResume();
    }
}
