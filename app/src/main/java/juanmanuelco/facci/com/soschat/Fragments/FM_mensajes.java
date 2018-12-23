package juanmanuelco.facci.com.soschat.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import juanmanuelco.facci.com.soschat.DB.DB_SOSCHAT;
import juanmanuelco.facci.com.soschat.R;


public class FM_mensajes extends Fragment {
    private DB_SOSCHAT db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_fm_mensajes, container, false);
        return v;
    }


}
