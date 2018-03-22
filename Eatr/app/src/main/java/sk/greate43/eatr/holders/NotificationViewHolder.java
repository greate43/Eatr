package sk.greate43.eatr.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Notification;

/**
 * Created by great on 3/22/2018.
 */

public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tvTitle;
    public TextView tvMessage;
    public CircleImageView img;
    public Button yes;
    public Button no;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.notification_list_title);
        tvMessage = itemView.findViewById(R.id.notification_list_message);
        img = itemView.findViewById(R.id.notification_list_circleImageView);
        yes = itemView.findViewById(R.id.notification_list_button_yes);
        no = itemView.findViewById(R.id.notification_list_button_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    public void populate(Notification notification) {
        tvMessage.setText(notification.getMessage());
        tvTitle.setText(notification.getTitle());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notification_list_button_yes:
                break;
            case R.id.notification_list_button_no:
                break;
        }
    }
}
