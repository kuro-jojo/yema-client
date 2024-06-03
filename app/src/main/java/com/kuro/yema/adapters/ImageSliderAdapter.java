package com.kuro.yema.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kuro.yema.R;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {

    private Context context;
    private ArrayList<String> images;

    public ImageSliderAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create the ImageView directly in code
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return new ImageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Set the image resource for the ImageView
        Glide.with(holder.itemView).load(images.get(position)).placeholder(R.drawable.template_house_item).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            // Cast the View to ImageView
            imageView = (ImageView) itemView;
        }
    }
}
