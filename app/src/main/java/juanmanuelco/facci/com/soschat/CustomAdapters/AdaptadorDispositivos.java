package juanmanuelco.facci.com.soschat.CustomAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.Filterable;
import android.widget.Toast;

import juanmanuelco.facci.com.soschat.DB.DB_SOSCHAT;
import juanmanuelco.facci.com.soschat.R;

import java.util.ArrayList;

public class AdaptadorDispositivos extends RecyclerView.Adapter<AdaptadorDispositivos.ViewHolderDatos> implements View.OnClickListener,Filterable {

    ArrayList<String[]> listado;
    ArrayList<String[]>primerListado;
    listaFilter filter;
    Context context;
    DB_SOSCHAT db;
    View view;


    private View.OnClickListener listener;

    public AdaptadorDispositivos(ArrayList<String[]> listado, Context c) {
        this.listado = listado;
        this.primerListado = listado;
        this.context=c;
        getFilter();
    }

    @NonNull
    @Override
    public AdaptadorDispositivos.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_participants, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        view.setOnClickListener(this);
        db= new DB_SOSCHAT(this.context);

        return new ViewHolderDatos(view);}

    @Override
    public void onBindViewHolder(@NonNull AdaptadorDispositivos.ViewHolderDatos viewHolderDatos, final int i) {
        String nombre= Character.toUpperCase(listado.get(i)[0].charAt(0)) + listado.get(i)[0].substring(1,listado.get(i)[0].length());
        viewHolderDatos.txtNombre.setText(nombre);
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

    @Override
    public int getItemCount() {
        return listado.size();
    }

    public void setOnClickListener(View.OnClickListener listen){
        this.listener=listen;
    }


    @Override
    public void onClick(View v) {
        if(listener!=null)
            listener.onClick(v);
    }

    @Override
    public Filter getFilter() {
        if (filter==null)  filter = new  listaFilter();
        return filter;
    }


    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView txtNombre, txtMAC;
        Switch sw_agregado;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.name_tv);
            txtMAC = itemView.findViewById(R.id.ip_tv);
            sw_agregado =(Switch) itemView.findViewById(R.id.sw_agregado);
        }
    }

    private class listaFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint!=null && constraint.length()>0){
                constraint=constraint.toString().toUpperCase();
                ArrayList<String[]> Filtrado = new ArrayList<>();
                for (int i=0; i<listado.size();i++ ){
                    if (listado.get(i).toString().toUpperCase().contains(constraint)){
                        Filtrado.add(listado.get(i));
                    }
                }
                results.count=Filtrado.size();
                results.values= Filtrado;
            }else{
                results.count=listado.size();
                results.values= listado;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listado = (ArrayList<String[]>) results.values;
            notifyDataSetChanged();
        }
    }
}
