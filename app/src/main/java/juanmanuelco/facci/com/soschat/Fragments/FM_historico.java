package juanmanuelco.facci.com.soschat.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import juanmanuelco.facci.com.soschat.ChatActivity;
import juanmanuelco.facci.com.soschat.Adaptadores.AdaptadorDispositivos;
import juanmanuelco.facci.com.soschat.DB.DB_SOSCHAT;
import juanmanuelco.facci.com.soschat.R;


public class FM_historico extends Fragment {

    RecyclerView rv_participants;
    AdaptadorDispositivos adaptadorDispositivos;
    ArrayList<String[]> encontrados;
    DB_SOSCHAT db;
    TextView TV_NO_ENC;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_fm_historico, container, false);
        db= new DB_SOSCHAT(getActivity());
        encontrados= new ArrayList<>();
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


}
