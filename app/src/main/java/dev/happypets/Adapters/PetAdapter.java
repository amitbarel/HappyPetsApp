package dev.happypets.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import dev.happypets.Objects.Pet;
import dev.happypets.R;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import org.json.JSONArray;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private Context context;
    private ArrayList<Pet> pets;

    public PetAdapter(Context context, ArrayList<Pet> pets) {
        this.context = context;
        this.pets = pets;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pet_item, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {

        holder.petName.setText(pets.get(position).getName());
        holder.petType.setText(pets.get(position).getType().getKind());

        Glide.with(context)
                .load(pets.get(position).getPhotoUrl())
                .into(holder.petImage);
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView petName;
        TextView petType;
        ImageView petImage;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.pet_name);
            petType = itemView.findViewById(R.id.pet_type);
            petImage = itemView.findViewById(R.id.pet_image);
        }
    }

    public void updatePets(ArrayList<Pet> newPets) {
        pets.clear();
        pets.addAll(newPets);
        if (pets.isEmpty()) {
            Log.d("PetAdapter", "empty object");
        } else {
            notifyDataSetChanged();
        }
    }
}

