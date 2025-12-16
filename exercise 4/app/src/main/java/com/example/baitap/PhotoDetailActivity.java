package com.example.baitap;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;

public class PhotoDetailActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private PhotoPagerAdapter adapter;
    private ArrayList<String> imagePaths;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        viewPager = findViewById(R.id.viewPager);

        String imagePath = getIntent().getStringExtra("image_path");
        imagePaths = getIntent().getStringArrayListExtra("image_paths");
        currentPosition = getIntent().getIntExtra("position", 0);

        adapter = new PhotoPagerAdapter(this, imagePaths);

        adapter.setOnThreeFingerSwipeListener(new CustomPhotoView.OnThreeFingerSwipeListener() {
            @Override
            public void onSwipeLeft() {
                int nextPosition = viewPager.getCurrentItem() + 1;
                if (nextPosition < imagePaths.size()) {
                    viewPager.setCurrentItem(nextPosition, true);
                } else {
                    Toast.makeText(PhotoDetailActivity.this, "Đây là ảnh cuối cùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSwipeRight() {
                // Swipe phải - ảnh trước
                int prevPosition = viewPager.getCurrentItem() - 1;
                if (prevPosition >= 0) {
                    viewPager.setCurrentItem(prevPosition, true);
                } else {
                    Toast.makeText(PhotoDetailActivity.this, "Đây là ảnh đầu tiên", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition, false);

        viewPager.setUserInputEnabled(false);
    }
}