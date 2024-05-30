package dev.happypets.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.recyclerview.widget.RecyclerView;

public class GridAdapter extends BaseAdapter {

    private Context context;
    private PetImgAdapter petImgAdapter;

    public GridAdapter(Context context, PetImgAdapter petImgAdapter) {
        this.context = context;
        this.petImgAdapter = petImgAdapter;
    }

    @Override
    public int getCount() {
        return petImgAdapter.getItemCount();
    }

    @Override
    public Object getItem(int position) {
        return petImgAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecyclerView.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = petImgAdapter.onCreateViewHolder(parent, petImgAdapter.getItemViewType(position));
            convertView = viewHolder.itemView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RecyclerView.ViewHolder) convertView.getTag();
        }
        petImgAdapter.onBindViewHolder((PetImgAdapter.PetViewHolder) viewHolder, position);
        return convertView;
    }
}
