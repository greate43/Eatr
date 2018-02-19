package sk.greate43.eatr.recyclerCustomItem;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.SimpleOnItemTouchListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by great on 2/12/2018.
 */

public class RecyclerItemClickListener extends SimpleOnItemTouchListener {

    private static final String TAG = "RecyclerItemClick";
    private final OnRecyclerClickListenier mListener;
    private final GestureDetectorCompat mDetectorCompat;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnRecyclerClickListenier listener) {
        mListener = listener;
        mDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null) {
                    Log.d(TAG, "onSingleTapUp:item clicked ");
                    mListener.OnItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: starts");
                super.onLongPress(e);
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null) {
                    Log.d(TAG, "onLongPress: long item clicked");
                    mListener.OnItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: starts");
        if (mDetectorCompat != null) {
            boolean result = mDetectorCompat.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent: returned " + result);
        } else {
            Log.d(TAG, "onInterceptTouchEvent: returned : false ");
            return false;
        }
        return false;
    }

    public interface OnRecyclerClickListenier {
        void OnItemClick(View v, int position);

        void OnItemLongClick(View v, int position);

    }
}


