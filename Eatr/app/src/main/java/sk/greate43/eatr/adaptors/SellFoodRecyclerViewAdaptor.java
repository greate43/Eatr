package sk.greate43.eatr.adaptors;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.holders.SellFoodRecyclerViewHolder;
import sk.greate43.eatr.interfaces.SwipeListener;

/**
 * Created by great on 11/12/2017.
 */

public class SellFoodRecyclerViewAdaptor extends RecyclerView.Adapter<SellFoodRecyclerViewHolder> implements SwipeListener {

    private static final String TAG = "SellFoodRecyclerViewAda";


    DatabaseReference mDatabaseReference;
    private StorageReference storageReference;
    private ArrayList<Food> sellers;
    private LayoutInflater inflater;
    private SellerActivity sellerActivity;

    public SellFoodRecyclerViewAdaptor(SellerActivity sellerActivity, DatabaseReference mDatabaseReference) {
        this.sellerActivity = sellerActivity;
        inflater = sellerActivity.getLayoutInflater();
        sellers = new ArrayList<>();
        this.mDatabaseReference = mDatabaseReference;

    }

    public ArrayList<Food> getSellers() {
        return sellers;
    }

    @Override
    public SellFoodRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.posted_food_list, parent, false);


        return new SellFoodRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SellFoodRecyclerViewHolder holder, int position) {
        if (sellers == null || sellers.size() == 0) {
            holder.imgFoodItem.setImageResource(R.drawable.ic_launcher_background);

        } else {

            holder.populate(sellerActivity, sellers.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return ((sellers != null && sellers.size() != 0) ? sellers.size() : 1);
    }


    public void clear() {
        int size = sellers.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                sellers.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onSwipe(int position) {
        if (!sellers.isEmpty()) {
            Log.d(TAG, "onSwipe: " + position);
            Log.d(TAG, "onSwipe: " + sellers.get(position).getDishName());
            mDatabaseReference.child("eatr").child("greate43").child(sellers.get(position).getDishName()).removeValue();
            storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(sellers.get(position).getImageUri());

            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    Log.d(TAG, "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Log.d(TAG, "onFailure: did not delete file");
                }
            });
            removeItem(position);

        }

    }

    private void removeItem(int position) {
        sellers.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

}