package com.example.baitap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.github.chrisbanes.photoview.PhotoView;

public class CustomPhotoView extends PhotoView {
    private OnThreeFingerSwipeListener swipeListener;
    private static final int MIN_POINTERS = 3;
    private static final float MIN_SWIPE_DISTANCE = 100;

    public interface OnThreeFingerSwipeListener {
        void onSwipeLeft();
        void onSwipeRight();
    }

    public CustomPhotoView(Context context) {
        super(context);
    }

    public CustomPhotoView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public CustomPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }

    public void setOnThreeFingerSwipeListener(OnThreeFingerSwipeListener listener) {
        this.swipeListener = listener;
    }

    private float initialX = 0;
    private boolean isThreeFingerGesture = false;
    private boolean hasSwipped = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();

        // Xử lý gesture 3 ngón tay
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                isThreeFingerGesture = false;
                hasSwipped = false;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (pointerCount >= MIN_POINTERS) {
                    isThreeFingerGesture = true;
                    initialX = event.getX();
                    hasSwipped = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isThreeFingerGesture && pointerCount >= MIN_POINTERS && !hasSwipped) {
                    float currentX = event.getX();
                    float deltaX = currentX - initialX;

                    if (Math.abs(deltaX) > MIN_SWIPE_DISTANCE) {
                        if (swipeListener != null) {
                            if (deltaX > 0) {
                                swipeListener.onSwipeRight();
                            } else {
                                swipeListener.onSwipeLeft();
                            }
                        }
                        hasSwipped = true;
                        isThreeFingerGesture = false;
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() - 1 < MIN_POINTERS) {
                    isThreeFingerGesture = false;
                    hasSwipped = false;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                isThreeFingerGesture = false;
                hasSwipped = false;
                break;
        }

        if (isThreeFingerGesture && pointerCount >= MIN_POINTERS) {
            return true;
        }

        return super.dispatchTouchEvent(event);
    }
}