package sk.greate43.eatr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import sk.greate43.eatr.R;
import sk.greate43.eatr.adaptors.NotificationRecyclerViewAdaptor;
import sk.greate43.eatr.entities.Notification;


public class NotificationFragment extends Fragment {
    private static final String TAG = "NotificationFragment";
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private NotificationRecyclerViewAdaptor adaptor;
    private RecyclerView recyclerView;
    private ArrayList<Notification> notification;


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
        View view= inflater.inflate(R.layout.fragment_notication, container, false);
        recyclerView = view.findViewById(R.id.fragment_notification_recycler_view);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        database = FirebaseDatabase.getInstance();

        mDatabaseReference = database.getReference();
        adaptor = new NotificationRecyclerViewAdaptor(getActivity());

        notification = adaptor.getNotifications();


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);


        recyclerView.setItemAnimator(new DefaultItemAnimator());

//        mDatabaseReference.child(Constants.NOTIFICATION).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });
        return view;
    }

}
