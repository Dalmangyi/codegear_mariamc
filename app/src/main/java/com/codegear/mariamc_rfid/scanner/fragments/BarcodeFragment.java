package com.codegear.mariamc_rfid.scanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.scanner.adapters.BarcodeListAdapter;
import com.codegear.mariamc_rfid.scanner.helpers.Barcode;
import com.codegear.mariamc_rfid.R;
//import com.codegear.mariamc_rfid.scanner.activities.ActiveScannerActivity;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;

import java.util.ArrayList;

import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SCAN_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SETTINGS_TAB;

public class BarcodeFragment extends Fragment {
    BarcodeListAdapter barcodeAdapter;
    ListView barcodesList;
    ArrayList<Barcode> barcodes;
    private View rootView;
    private int scannerID;

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            toggle(view, position);
            String tagid = new String(barcodeAdapter.getItem(position).getBarcodeData());
            RFIDController.accessControlTag = tagid;
            Application.locateTag = tagid;
            Application.PreFilterTag = tagid;
        }

    };

    private void toggle(View view, final int position) {
        Barcode barcodeItem = barcodeAdapter.getItem(position);
        Application.mSelectedItem = position;
        barcodeAdapter.notifyDataSetChanged();
    }

    public static BarcodeFragment newInstance() {
        return new BarcodeFragment();
    }

    public BarcodeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scan, menu);
        menu.findItem(R.id.action_scan_setting).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (RFIDController.mConnectedReader == null) {
                    Toast.makeText(getContext(), "연결된 장치가 없습니다.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                ((ActiveDeviceActivity) getActivity()).setCurrentTabFocus(SETTINGS_TAB, SCAN_SETTINGS_TAB);
                //((ActiveDeviceActivity) getActivity()).loadNextFragment(SCAN_SETTINGS_TAB);
                return true;
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_barcode_fargment, container, false);
        //barcodes=new ArrayList<Barcode>();
        barcodes = ((ActiveDeviceActivity) requireActivity()).getBarcodeData(barcodes, ((ActiveDeviceActivity) requireActivity()).getScannerID());


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        int iBarcodeCount = ((ActiveDeviceActivity)getActivity()).iBarcodeCount;
        TextView barcodeCount = (TextView)rootView.findViewById(R.id.barcodesListCount);
        barcodeCount.setText("스캔된 바코드: " + Integer.toString(iBarcodeCount));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        barcodesList = (ListView) getActivity().findViewById(R.id.barcodesList);
        if (barcodeAdapter == null) {
            barcodeAdapter = new BarcodeListAdapter((AppCompatActivity) getActivity(), barcodes);
        }

        barcodesList.setAdapter(barcodeAdapter);
        barcodesList.setOnItemClickListener(onItemClickListener);
        barcodeAdapter.notifyDataSetChanged();

        Button btnClear = (Button) getActivity().findViewById(R.id.btnClearList);

        if (barcodes == null || barcodes.size() <= 0) {
            btnClear.setEnabled(false);

        }
        if (barcodes.size() > 0) {
            btnClear.setEnabled(true);
        }

        barcodesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ((ActiveDeviceActivity) getActivity()).updateBarcodeCount();
    }


    public void showBarCode() {

        //barcodes.add(barcode);
        View mView = requireActivity().findViewById(R.id.btnScanTrigger);
        if (mView != null) {
            mView.setEnabled(true);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null)
                        ((ActiveDeviceActivity) getActivity()).scanTrigger(mView);
                }
            });
        }

        // barcodeAdapter.add(barcode);
        barcodeAdapter.notifyDataSetChanged();

    }

    public void clearList() {
        barcodes.clear();
        barcodeAdapter.clear();
        Application.mSelectedItem = -1;
        barcodesList.setAdapter(barcodeAdapter);
        ((ActiveDeviceActivity) requireActivity()).clearBarcodeData();
    }

}
