package sk.greate43.eatr.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sk.greate43.eatr.R;

public class HistoryFragment extends Fragment {


    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_history, container, false);

        return view;
    }


}
