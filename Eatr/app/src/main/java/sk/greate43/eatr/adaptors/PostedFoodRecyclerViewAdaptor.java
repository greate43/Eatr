package sk.greate43.eatr.adaptors;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.holders.PostedFoodRecyclerViewHolder;
import sk.greate43.eatr.interfaces.SwipeListener;
import sk.greate43.eatr.utils.Constants;

/**
 * Created by great on 11/12/2017.
 */

public class PostedFoodRecyclerViewAdaptor extends RecyclerView.Adapter<PostedFoodRecyclerViewHolder> implements SwipeListener {

    private static final String TAG = "SellFoodRecyclerViewAda";


    DatabaseReference mDatabaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ArrayList<Food> foods;
    private LayoutInflater inflater;
    private SellerActivity sellerActivity;

    public PostedFoodRecyclerViewAdaptor(SellerActivity sellerActivity, DatabaseReference mDatabaseReference) {
        this.sellerActivity = sellerActivity;
        inflater = sellerActivity.getLayoutInflater();
        foods = new ArrayList<>();
        this.mDatabaseReference = mDatabaseReference;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }

    public ArrayList<Food> getFoods() {
        return foods;
    }

    @Override
    public PostedFoodRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.posted_food_list, parent, false);


        return new PostedFoodRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostedFoodRecyclerViewHolder holder, int position) {
        if (foods == null || foods.size() == 0) {
           // holder.imgFoodItem.setImageResource(R.drawable.ic_launcher_background);

        } else {

            holder.populate(sellerActivity, foods.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (foods != null && !foods.isEmpty()) {
            return foods.size();
        } else {
            return 0;
        }

    }


    public void clear() {
        int size = foods.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                foods.remove(0);
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
        if (!foods.isEmpty()) {
            Log.d(TAG, "onSwipe: " + position);
            Log.d(TAG, "onSwipe: " + foods.get(position).getPushId());
            mDatabaseReference.child(Constants.FOOD).child(user.getUid()).child(foods.get(position).getPushId()).removeValue();
            storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(foods.get(position).getImageUri());

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
        foods.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

}