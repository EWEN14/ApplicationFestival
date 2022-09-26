package com.example.applicationfestival;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapterFestival extends RecyclerView.Adapter<CustomAdapterFestival.MyViewHolder> {

    ArrayList<InfosGroupePartial> nomsGroupes;
    Context context;

    public CustomAdapterFestival(Context context, ArrayList<InfosGroupePartial> nomsGroupes) {
        this.context = context;
        this.nomsGroupes = nomsGroupes;
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
                Intent intent = new Intent(context, DetailGroupe.class);
                intent.putExtra("nom_groupe", nomsGroupes.get(position).getNomJson());
                intent.putExtra("position", position);
                view.getContext().startActivity(intent);
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
}
