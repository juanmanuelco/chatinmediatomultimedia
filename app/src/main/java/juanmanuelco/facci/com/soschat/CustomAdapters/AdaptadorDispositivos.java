package juanmanuelco.facci.com.soschat.CustomAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import android.widget.Filterable;

import juanmanuelco.facci.com.soschat.R;

import java.util.ArrayList;

public class AdaptadorDispositivos extends RecyclerView.Adapter<AdaptadorDispositivos.ViewHolderDatos> implements View.OnClickListener,Filterable {

    ArrayList<String[]> listado;
    ArrayList<String[]>primerListado;
    listaFilter filter;


    private View.OnClickListener listener;

    public AdaptadorDispositivos(ArrayList<String[]> listado) {
        this.listado = listado;
        this.primerListado = listado;
        getFilter();
    }

    @NonNull
    @Override
    public AdaptadorDispositivos.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_participants, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        view.setOnClickListener(this);
        return new ViewHolderDatos(view);}

    @Override
    public void onBindViewHolder(@NonNull AdaptadorDispositivos.ViewHolderDatos viewHolderDatos, int i) {
        viewHolderDatos.txtNombre.setText(listado.get(i)[0]);
        viewHolderDatos.txtMAC.setText(listado.get(i)[1]);
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
        if (filter==null){
            filter = new  listaFilter();
        }
        return filter;
    }


    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView txtNombre, txtMAC;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.name_tv);
            txtMAC = itemView.findViewById(R.id.ip_tv);
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
