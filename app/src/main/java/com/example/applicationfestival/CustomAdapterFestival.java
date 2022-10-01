package com.example.applicationfestival;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.applicationfestival.model.InfosGroupePartial;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterFestival extends RecyclerView.Adapter<CustomAdapterFestival.MyViewHolder> implements Filterable {

    ArrayList<InfosGroupePartial> nomsGroupes;
    ArrayList<InfosGroupePartial> nomsGroupesFull;
    Context context;

    public CustomAdapterFestival(Context context, ArrayList<InfosGroupePartial> nomsGroupes) {
        this.context = context;
        this.nomsGroupes = nomsGroupes;
        this.nomsGroupesFull = new ArrayList<>(nomsGroupes);
    }

    @Override
    public CustomAdapterFestival.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayoutnomsgroupes, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomAdapterFestival.MyViewHolder holder, final int position) {
        // set the data in items
        holder.nomGroupe.setText(nomsGroupes.get(position).getNomComplet());

        // get the shared preferences for getting the favorites bands (key:value mechanism)
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("favoritesBands", 0);

        // get the "json formated" name of the group
        String nomGroupeSimple = nomsGroupes.get(position).getNomJson();
        // remove the "-json" at the end of the string
        nomGroupeSimple.replace("-json","");

        // search in the sharedPreferences if the band is in favorite or not
        if (pref.getBoolean(nomGroupeSimple, false)) {
            holder.favoriteStatus.setText("⭐"); // show a full star
        } else {
            holder.favoriteStatus.setText("☆"); // show an empty star
        }

        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : rediriger vers une nouvelle vue dans laquelle on passe un itent avec le nom du groupe
                Intent intent = new Intent(context, DetailGroupe_.class);
                intent.putExtra("nom_groupe", nomsGroupes.get(position).getNomJson());
                intent.putExtra("position", position);
                ((Activity) context).startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nomsGroupes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nomGroupe;// init the item view's
        TextView favoriteStatus;

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            nomGroupe = itemView.findViewById(R.id.nomGroupe);
            favoriteStatus = itemView.findViewById(R.id.favoriteStatus);
        }
    }

    // methos used for filtering the list of band
    @Override
    public Filter getFilter() {
        return listeGroupeFiltree;
    }

    private final Filter listeGroupeFiltree = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<InfosGroupePartial> filteredList = new ArrayList<>();

            // our constraint contain the scene and the day in a single CharSequence, so we have
            // to make it a String and split the String to the space character to get the two filtering element
            String[] constaintSplitStr = constraint.toString().split(" ");
            String sceneSelected = constaintSplitStr[0];
            String jourSelected = constaintSplitStr[1];

            // if all scenes and days are selected, then we kept the whole list of band
            if (sceneSelected.equals("toutes") && jourSelected.equals("tous")) {
                filteredList.addAll(nomsGroupesFull);
            } else {
                // if the all day are selected, then we only filter on the scene
                if (jourSelected.equals("tous")) {
                    for (InfosGroupePartial band : nomsGroupesFull) {
                        if (band.getScene().equals(sceneSelected)) {
                            filteredList.add(band);
                        }
                    }
                } // on the opposite, we only filter on the day when all scenes are selected
                else if (sceneSelected.equals("toutes")) {
                    for (InfosGroupePartial band : nomsGroupesFull) {
                        if (band.getJour().equals(jourSelected)) {
                            filteredList.add(band);
                        }
                    }
                } // else, we filter both on the scene and the day selected
                else {
                    for (InfosGroupePartial band : nomsGroupesFull) {
                        if (band.getJour().equals(jourSelected) && band.getScene().equals(sceneSelected)) {
                            filteredList.add(band);
                        }
                    }
                }
                // String sceneSelected = constraint.toString();
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            // return the filtered list of bands
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            nomsGroupes.clear();
            nomsGroupes.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
