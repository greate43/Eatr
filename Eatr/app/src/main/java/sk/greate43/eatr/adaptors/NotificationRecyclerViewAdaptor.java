package sk.greate43.eatr.adaptors;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Notification;
import sk.greate43.eatr.holders.NotificationViewHolder;

/**
 * Created by great on 3/22/2018.
 */

public class NotificationRecyclerViewAdaptor extends RecyclerView.Adapter<NotificationViewHolder> {
    LayoutInflater inflater;
    ArrayList<Notification> notifications;
    Activity activity;

    public NotificationRecyclerViewAdaptor(Activity activity) {
        inflater = activity.getLayoutInflater();
        this.activity = activity;
        notifications = new ArrayList<>();
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notification_list, parent, false);


        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        if (notifications == null || notifications.size() == 0) {
            // holder.imgFoodItem.setImageResource(R.drawable.ic_launcher_background);
            holder.tvTitle.setText("No new Updates!");
            holder.tvMessage.setVisibility(View.GONE);
            holder.no.setVisibility(View.GONE);
            holder.yes.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);
        } else {
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.no.setVisibility(View.VISIBLE);
            holder.yes.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.VISIBLE);
            holder.populate(notifications.get(position), activity);
        }
    }

    @Override
    public int getItemCount() {
        if (notifications != null && !notifications.isEmpty()) {
            return notifications.size();
        } else {
            return 1;
        }
    }

    public void clear() {
        int size = notifications.size();
        if (size > 0) {
            notifications.clear();

            notifyItemRangeRemoved(0, size);
        }
    }
}
