package com.example.baitap;

import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class PhotoPagerAdapter extends RecyclerView.Adapter<PhotoPagerAdapter.PhotoViewHolder> {
    private Context context;
    private ArrayList<String> imagePaths;
    private CustomPhotoView.OnThreeFingerSwipeListener swipeListener;

    public PhotoPagerAdapter(Context context, ArrayList<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    public void setOnThreeFingerSwipeListener(CustomPhotoView.OnThreeFingerSwipeListener listener) {
        this.swipeListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomPhotoView photoView = new CustomPhotoView(context);
        photoView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return new PhotoViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);

        Glide.with(context)
                .load(imagePath)
                .into(holder.photoView);

        holder.photoView.setOnThreeFingerSwipeListener(swipeListener);
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        CustomPhotoView photoView;

        PhotoViewHolder(@NonNull CustomPhotoView itemView) {
            super(itemView);
            photoView = itemView;
        }
    }
}