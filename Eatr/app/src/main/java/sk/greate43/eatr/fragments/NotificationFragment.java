package sk.greate43.eatr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.adaptors.NotificationRecyclerViewAdaptor;
import sk.greate43.eatr.entities.Notification;
import sk.greate43.eatr.utils.Constants;


public class NotificationFragment extends Fragment {
    private static final String TAG = "NotificationFragment";
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private NotificationRecyclerViewAdaptor adaptor;
    private RecyclerView recyclerView;
    private ArrayList<Notification> notifications;


    public NotificationFragment() {
        // Required empty public constructor
    }


    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_notication, container, false);
        recyclerView = view.findViewById(R.id.fragment_notification_recycler_view);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        database = FirebaseDatabase.getInstance();

        mDatabaseReference = database.getReference();
        adaptor = new NotificationRecyclerViewAdaptor(getActivity());

        notifications = adaptor.getNotifications();


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);


        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mDatabaseReference.child(Constants.NOTIFICATION).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        return view;
    }

    private void showData(DataSnapshot dataSnapshot) {
        if (adaptor != null) {
            adaptor.clear();
        }

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue() != null) {
                collectNotification((Map<String, Object>) ds.getValue());
            }
        }
        adaptor.notifyDataSetChanged();


    }

    private void collectNotification(Map<String, Object> value) {
        Notification notification = new Notification();
        notification.setOrderId(String.valueOf(value.get(Constants.ORDER_ID)));
        notification.setNotificationId(String.valueOf(value.get(Constants.NOTIFICATION_ID)));
        notification.setMessage(String.valueOf(value.get(Constants.MESSAGE)));
        notification.setTitle(String.valueOf(value.get(Constants.TITLE)));
        notification.setCheckIfButtonShouldBeEnabled((boolean) value.get(Constants.CHECK_IF_BUTTON_SHOULD_BE_ENABLED));
        if (value.get(Constants.NOTIFICATION_IMAGE) != null) {
            notification.setNotificationImage(String.valueOf(Constants.NOTIFICATION_IMAGE));
        }
        notification.setCheckIfNotificationAlertShouldBeSent((boolean) value.get(Constants.CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SENT));
        notification.setCheckIfNotificationAlertShouldBeShown((boolean) value.get(Constants.CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SHOWN));
        notification.setSenderId(String.valueOf(value.get(Constants.SENDER_ID)));
        notification.setReceiverId(String.valueOf(value.get(Constants.RECEIVER_ID)));
        notification.setNotificationType(String.valueOf(value.get(Constants.NOTIFICATION_TYPE)));
        if (notification.getReceiverId().equals(user.getUid()) && notification.getCheckIfNotificationAlertShouldBeShown()) {
            notifications.add(notification);
        }


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.menu_item_search);
        search.setVisible(false);

        super.onPrepareOptionsMenu(menu);


    }
}
