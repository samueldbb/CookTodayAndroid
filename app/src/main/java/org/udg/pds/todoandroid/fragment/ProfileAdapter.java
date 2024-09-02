package org.udg.pds.todoandroid.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.udg.pds.todoandroid.R;
import org.udg.pds.todoandroid.entity.Recepta;

import java.util.ArrayList;
import java.util.List;


public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<Recepta> receptes;

    public ProfileAdapter() {
        this.receptes = new ArrayList<>();
    }

    public ProfileAdapter(List<Recepta> receptes) {
        this.receptes = receptes;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_my_recepta, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Recepta recepta = receptes.get(position);
        holder.name.setText(recepta.nom);
        holder.description.setText(recepta.descripcio);
        holder.ingredients.setText(recepta.llista_ingredients);
        holder.passos.setText(recepta.passos);

        if (!recepta.imageUrl.isEmpty()) {
            Picasso.get().load(recepta.imageUrl).into(holder.idImage);
        }
    }

    @Override
    public int getItemCount() {
        return receptes.size();
    }

    public void setProducts(List<Recepta> receptes) {
        this.receptes = receptes;
        notifyDataSetChanged();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView idImage;
        TextView name;
        TextView description;
        TextView passos;
        TextView ingredients;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            idImage = itemView.findViewById(R.id.idImageMy);
            name = itemView.findViewById(R.id.nameMy);
            description = itemView.findViewById(R.id.descriptionMy);
            passos = itemView.findViewById(R.id.passosMy);
            ingredients = itemView.findViewById(R.id.ingredientsMy);
        }

    }
}
