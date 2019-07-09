package sk.greate43.eatr.holders;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Profile;

/**
 * Created by great on 12/24/2017.
 */

public class PostedFoodHeaderHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "PostedFoodHeaderHolder";
    ImageView imgProfilePic;
    TextView tvName;

    public PostedFoodHeaderHolder(View itemView) {
        super(itemView);
        imgProfilePic = itemView.findViewById(R.id.posted_food_header_profile_picture);
        tvName = itemView.findViewById(R.id.posted_food_user_name);
    }

    public void populate(Context context, Profile profile) {
        itemView.setTag(profile);
        if (profile.getProfilePhotoUri() != null && !profile.getProfilePhotoUri().isEmpty()) {
            Picasso.get()
                    .load(profile.getProfilePhotoUri())
                    .fit()
                    .centerCrop()
                    .into(imgProfilePic);
        }

        tvName.setText(profile.getFirstName() + " " + profile.getLastName());

    }

}
