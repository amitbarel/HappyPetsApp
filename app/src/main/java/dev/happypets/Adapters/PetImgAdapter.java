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
import java.util.Arrays;

import dev.happypets.Objects.AnimalType;
import dev.happypets.Objects.Pet;
import dev.happypets.R;

public class PetImgAdapter extends RecyclerView.Adapter<PetImgAdapter.PetViewHolder> {

    private Context context;
    private ArrayList<AnimalType> petKinds;

    public PetImgAdapter(Context context, ArrayList<AnimalType> kinds) {
        this.context = context;
        petKinds = kinds;
    }

    public class PetViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView animal_name;
        private final AppCompatImageView animal_image;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            animal_name = itemView.findViewById(R.id.txt_animal);
            animal_image = itemView.findViewById(R.id.img_animal);

        }
    }

    @NonNull
    @Override
    public PetImgAdapter.PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animal_item, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetImgAdapter.PetViewHolder holder, int position) {
        AnimalType type = petKinds.get(position);
        holder.animal_name.setText(type.getKind());
        holder.animal_image.setImageResource(type.getImageSrc());
    }

    @Override
    public int getItemCount() {
        return petKinds == null ? 0 : petKinds.size();
    }

    public AnimalType getItem(int position) {
        return petKinds.get(position);
    }

}
