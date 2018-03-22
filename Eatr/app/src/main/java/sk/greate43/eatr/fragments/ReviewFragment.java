package sk.greate43.eatr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import sk.greate43.eatr.R;


public class ReviewFragment extends Fragment {


    TextView tv;
    RatingBar rbReview;
    CircleImageView imgSellerPic;

    public ReviewFragment() {
        // Required empty public constructor
    }

    public static ReviewFragment newInstance() {
        ReviewFragment fragment = new ReviewFragment();
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
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        tv = view.findViewById(R.id.fragment_review_text_view);
        imgSellerPic = view.findViewById(R.id.fragment_review_circleImageView_);
        rbReview = view.findViewById(R.id.fragment_review_ratingBar);

        return view;
    }

}
