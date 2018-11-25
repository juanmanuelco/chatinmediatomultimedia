package juanmanuelco.facci.com.soschat;

import android.app.SearchManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.SearchView;
import android.widget.Toast;


import java.util.ArrayList;

import juanmanuelco.facci.com.soschat.DB.DB_SOSCHAT;
import juanmanuelco.facci.com.soschat.Entities.ENCONTRADO;
import juanmanuelco.facci.com.soschat.Fragments.FM_encontrados;
import juanmanuelco.facci.com.soschat.Fragments.FM_historico;
import juanmanuelco.facci.com.soschat.Fragments.FM_mensajes;
import juanmanuelco.facci.com.soschat.InitThreads.ServerInit;

public class FuncionActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    public static ServerInit server;
    public static String chatName="";

    private SearchView searchView = null;
    private boolean searchViewShow = false;
    private SearchView.OnQueryTextListener queryTextListener;
    private int posicion;
    public ArrayList<String[]> listado2 = new ArrayList<>();
    private ArrayList<ENCONTRADO> ListaFound;
    private DB_SOSCHAT db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcion);

        db = new DB_SOSCHAT(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                posicion= tab.getPosition();
                if (tab.getPosition()==1){
                    searchViewShow=false;
                }else{
                    searchViewShow=true;
                }
                invalidateOptionsMenu();
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    BusquedaEnTab(posicion,newText);
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //BusquedaEnTab(posicion,query);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }

        return true;
    }
    public void BusquedaEnTab(int posicion, String texto){
        try{
            if (posicion==0){
                listado2.clear();
                if (texto != null && !texto.isEmpty()){
                    listado2.clear();
                    Toast.makeText(getApplicationContext(),"Posicion pagina: " + posicion +" texto: "+ texto ,Toast.LENGTH_SHORT).show();
                    ListaFound = new ArrayList<ENCONTRADO>();
                    for (ENCONTRADO item : db.encontradosLista()){
                        String nombre = item.getNickname().toLowerCase();
                        if (nombre.contains(texto)){
                            ListaFound.add(item);
                            listado2.add(new String[]{item.getNickname()});
                        }
                    }
                    Log.i("pruena3", ListaFound.toString());
                    /*adapter = new AdaptadorDispositivos(listado2);RV.setAdapter(adapter);*/
                    //adapter.actualizar(listado2);
                }
            }
            if (posicion==1){
                Toast.makeText(getApplicationContext(),"Posicion pagina: " + posicion +" texto: "+ texto ,Toast.LENGTH_SHORT).show();

            }
            if (posicion==2){
                Toast.makeText(getApplicationContext(),"Posicion pagina: " + posicion +" texto: "+ texto ,Toast.LENGTH_SHORT).show();

            }
        }catch(Exception e ){

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search ) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }


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
            View rootView = inflater.inflate(R.layout.fragment_fm_encontrados, container, false);
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
                    fm= new FM_mensajes();
                    break;
                case 2:
                    fm=new FM_historico();
                    break;
            }
            return fm;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
