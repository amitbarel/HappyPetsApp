package dev.happypets.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import dev.happypets.Objects.Pet;
import dev.happypets.R;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private Context context;
    private ArrayList<Pet> pets;

    public PetAdapter(Context context, ArrayList<Pet> pets) {
        this.context = context;
        this.pets = pets;
    }

    public class PetViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView petImage;
        private final MaterialTextView petName;
        private final MaterialTextView petType;


        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.pet_image);
            petName = itemView.findViewById(R.id.pet_name);
            petType = itemView.findViewById(R.id.pet_type);
        }
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pet_item, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = pets.get(position);
        holder.petName.setText(pet.getName());
        holder.petType.setText(pet.getType().getKind());
        holder.petImage.setImageResource(pet.getType().getImageSrc());
    }

    @Override
    public int getItemCount() {
        return pets == null ? 0 : pets.size();
    }

    public Pet getItem(int position) {
        return pets.get(position);
    }
}
