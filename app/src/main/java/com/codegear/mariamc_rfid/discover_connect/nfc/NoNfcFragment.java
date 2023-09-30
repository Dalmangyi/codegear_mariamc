package com.codegear.mariamc_rfid.discover_connect.nfc;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codegear.mariamc_rfid.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoNfcFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoNfcFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public NoNfcFragment() {
        // Required empty public constructor
    }

    public static NoNfcFragment newInstance(String param1, String param2) {
        NoNfcFragment fragment = new NoNfcFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static NoNfcFragment newInstance() {
        return new NoNfcFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_nfc, container, false);
    }
}