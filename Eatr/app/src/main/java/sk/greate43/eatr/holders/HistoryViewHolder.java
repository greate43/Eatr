package sk.greate43.eatr.holders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.utils.Constants;

/**
 * Created by great on 2/28/2018.
 */

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "HistoryRecyclerViewHold";
    public TextView tvPurchasedDate;
    public TextView tvOrderId;
    public TextView tvSellerName;
    public TextView tvBuyerName;
    public TextView tvPrice;
    public TextView tvOrderIdLbl;
    public TextView tvSellerIdLbl;
    public TextView tvBuyerIdLbl;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public HistoryViewHolder(View itemView) {
        super(itemView);
        tvOrderId = itemView.findViewById(R.id.history_list_order_id);
        tvPurchasedDate = itemView.findViewById(R.id.history_list_text_view_purchased_date);
        tvPrice = itemView.findViewById(R.id.history_list_price);
        tvSellerName = itemView.findViewById(R.id.history_list_seller_name);
        tvBuyerName = itemView.findViewById(R.id.history_list_buyer_name);

        tvBuyerIdLbl = itemView.findViewById(R.id.history_list_buyer_name_lbl);
        tvSellerIdLbl = itemView.findViewById(R.id.history_list_seller_name_lbl);
        tvOrderIdLbl = itemView.findViewById(R.id.history_list_order_id_lbl);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();
    }

    public void populate(Food food) {
        tvPrice.setText(String.valueOf("PKR " + food.getPrice() * food.getNumberOfServingsPurchased()));
        tvOrderId.setText(String.valueOf(food.getPushId()));


        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        tvPurchasedDate.setText(sfd.format(new Date(food.getPurchasedDate())));
        getBuyerOrSellerDetails(food.getPostedBy(), food.getPurchasedBy());

    }


    private void getBuyerOrSellerDetails(String postedBy, String purchasedBy) {
        if (postedBy != null) {
            mDatabaseReference.child(Constants.PROFILE).orderByChild(Constants.USER_ID).equalTo(postedBy).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    showProfileData(dataSnapshot, Constants.TYPE_SELLER);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

        if (purchasedBy != null) {
            mDatabaseReference.child(Constants.PROFILE).orderByChild(Constants.USER_ID).equalTo(purchasedBy).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showProfileData(dataSnapshot, Constants.TYPE_BUYER);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

    }

    private void showProfileData(DataSnapshot dataSnapshot, String userType) {
        if (dataSnapshot.getValue() == null) {
            return;
        }
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            collectProfile((Map<String, Object>) ds.getValue(), userType);
        }
    }

    private void collectProfile(Map<String, Object> value, String userType) {
        Profile profile = new Profile();
        profile.setUserId(String.valueOf(value.get(Constants.USER_ID)));
        profile.setFirstName(String.valueOf(value.get(Constants.FIRST_NAME)));
        profile.setLastName(String.valueOf(value.get(Constants.LAST_NAME)));
        profile.setProfilePhotoUri(String.valueOf(value.get(Constants.PROFILE_PHOTO_URI)));
        if (String.valueOf(value.get(Constants.EMAIL)) != null) {
            profile.setEmail(String.valueOf(value.get(Constants.EMAIL)));
        }
        profile.setUserType(String.valueOf(value.get(Constants.USER_TYPE)));

        switch (userType) {
            case Constants.TYPE_BUYER:

                tvBuyerName.setText(String.valueOf(profile.getFullname()));
                break;
            case Constants.TYPE_SELLER:
                tvSellerName.setText(String.valueOf(profile.getFullname()));

                break;
        }
    }


}
