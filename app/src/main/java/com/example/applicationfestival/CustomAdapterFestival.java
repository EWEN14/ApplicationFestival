package com.example.applicationfestival;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapterFestival extends RecyclerView.Adapter<CustomAdapterFestival.MyViewHolder> {

    ArrayList<String> nomsGroupes;
    Context context;

    public CustomAdapterFestival(Context context, ArrayList<String> nomsGroupes) {
        this.context = context;
        this.nomsGroupes = nomsGroupes;
    }

    @Override
    public CustomAdapterFestival.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayoutnomsgroupes, parent, false);
        CustomAdapterFestival.MyViewHolder vh = new CustomAdapterFestival.MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(CustomAdapterFestival.MyViewHolder holder, final int position) {
        // set the data in items
        holder.nomGroupe.setText(nomsGroupes.get(position));
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast.makeText(context, nomsGroupes.get(position), Toast.LENGTH_SHORT).show();
                // TODO : rediriger vers une nouvelle vue dans laquelle on passe un itent avec le nom du groupe
                Intent intent = new Intent(context, DetailGroupe.class);
                intent.putExtra("nom_groupe", nomsGroupes.get(position));
                intent.putExtra("position", nomsGroupes.get(position));
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

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            nomGroupe = itemView.findViewById(R.id.nomGroupe);
        }
    }
}
