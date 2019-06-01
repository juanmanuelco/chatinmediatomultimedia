package juanmanuelco.facci.com.soschat.CustomAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import juanmanuelco.facci.com.soschat.DB.DB_SOSCHAT;
import juanmanuelco.facci.com.soschat.R;

import java.util.ArrayList;

public class AdaptadorDispositivos extends RecyclerView.Adapter<AdaptadorDispositivos.ViewHolderDatos> implements View.OnClickListener {

    ArrayList<String[]> listado;
    ArrayList<String[]>primerListado;
    Context context;
    DB_SOSCHAT db;
    View view;

    Boolean estado=false;
    int posicion=-1;

    private View.OnClickListener listener;

    public AdaptadorDispositivos(ArrayList<String[]> listado, Context c) {
        this.listado = listado;
        this.primerListado = listado;
        this.context=c;
    }

    @NonNull
    @Override
    public AdaptadorDispositivos.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_participants, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        view.setOnClickListener(this);
        db= new DB_SOSCHAT(this.context);

        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorDispositivos.ViewHolderDatos viewHolderDatos, final int i) {
        viewHolderDatos.txtNombre.setText(listado.get(i)[0]);
        viewHolderDatos.txtMAC.setText(listado.get(i)[1]);
        if(db.validarAgregado(listado.get(i)[1])) viewHolderDatos.sw_agregado.setChecked(true);
        else viewHolderDatos.sw_agregado.setChecked(false);
        viewHolderDatos.sw_agregado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int respuesta=0;
                if(isChecked) respuesta=db.ActualizarUsuario(listado.get(i)[1], true);
                else respuesta=db.ActualizarUsuario(listado.get(i)[1], false);
                Snackbar.make(view, context.getString(respuesta), Snackbar.LENGTH_SHORT).show();
            }
        });
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
        Switch sw_agregado;
        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            txtNombre=itemView.findViewById(R.id.name_tv);
            txtMAC=itemView.findViewById(R.id.ip_tv);
            sw_agregado =(Switch) itemView.findViewById(R.id.sw_agregado);
        }
    }
}
