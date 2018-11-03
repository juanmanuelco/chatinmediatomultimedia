package com.android.wondercom.CustomAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.wondercom.R;

import java.util.ArrayList;

public class AdaptadorDispositivos extends RecyclerView.Adapter<AdaptadorDispositivos.ViewHolderDatos> implements View.OnClickListener {

    ArrayList<String[]>listado;


    private View.OnClickListener listener;

    public AdaptadorDispositivos(ArrayList<String[]> listado) {
        this.listado = listado;
    }

    @NonNull
    @Override
    public AdaptadorDispositivos.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_participants, null, false);
        view.setOnClickListener(this);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorDispositivos.ViewHolderDatos viewHolderDatos, int i) {
        viewHolderDatos.txtNombre.setText(listado.get(i)[0]);
        viewHolderDatos.txtMAC.setText(listado.get(i)[1]);
    }

    public void setOnClickListener(View.OnClickListener listen){
        this.listener=listen;
    }

    @Override
    public int getItemCount() {
        return listado.size();
    }

    @Override
    public void onClick(View v) {
        if(listener!=null)
            listener.onClick(v);
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView txtNombre, txtMAC;
        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            txtNombre=itemView.findViewById(R.id.name_tv);
            txtMAC=itemView.findViewById(R.id.ip_tv);
        }
    }
}
