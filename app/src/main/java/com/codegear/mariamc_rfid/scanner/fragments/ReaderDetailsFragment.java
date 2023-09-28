package com.codegear.mariamc_rfid.scanner.fragments;

import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.TAG;
import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.mConnectedReader;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.DeviceDiscoverActivity;
import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;
import com.zebra.rfid.api3.ReaderDevice;

public class ReaderDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    TextView tv_name, tv_serialno, tv_model;
    TextView tv_wifi, tv_rfid, tv_scan;
    ReaderDevice mReaderDevice;
    ImageView iconRenameReader;

    public ReaderDetailsFragment() {
        // Required empty public constructor
    }

    public static ReaderDetailsFragment newInstance() {
        return new ReaderDetailsFragment();
    }

    public static ReaderDetailsFragment newInstance(String param1, String param2) {
        ReaderDetailsFragment fragment = new ReaderDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_reader_details, container, false);
        InitializeView(view);

        iconRenameReader.setVisibility(View.GONE);
        iconRenameReader.setOnClickListener(viewRenameReader -> renameReader());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setReaderDetails();
    }

    private void setReaderDetails() {
        if (getActivity() instanceof ActiveDeviceActivity)
            mReaderDevice = ((ActiveDeviceActivity) getActivity()).connectedReaderDetails();
        else if (getActivity() instanceof DeviceDiscoverActivity)
            mReaderDevice = ((DeviceDiscoverActivity) getActivity()).connectedReaderDetails();

        tv_name.setText(mReaderDevice.getName().toString());
        String readerName = mReaderDevice.getName().toString();
        // Log.d("serialno","no-"+mReaderDevice.getRFIDReader().ReaderCapabilities.getSerialNumber());
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.getHostName().equalsIgnoreCase(readerName)) {
            iconRenameReader.setVisibility(View.VISIBLE);
        }

        if (readerName != null) {
            if (readerName.startsWith("MC33")) {
                tv_model.setText("MC330XR");
                tv_wifi.setText("사용 불가");
                tv_scan.setText("사용 불가");
                String[] splitStr = readerName.split("R");

                if (mReaderDevice.getRFIDReader().ReaderCapabilities != null) {
                    tv_serialno.setText(mReaderDevice.getRFIDReader().ReaderCapabilities.getSerialNumber());
                } else tv_serialno.setText(splitStr[1]);

            } else if (readerName.startsWith("RFD40")) {

                String[] splitStr = readerName.split("-");

                tv_serialno.setText(mReaderDevice.getSerialNumber());
                if (splitStr[0].equals("RFD4030")) {
                    if (splitStr[1].contains("G0")) {
                        tv_model.setText("Standard");
                        tv_wifi.setText("사용 불가");
                        tv_scan.setText("사용 불가");
                    }

                } else if (splitStr[0].equals("RFD4031")) {
                    if (splitStr[1].contains("G0")) {
                        tv_model.setText("Premium (WiFi)");
                        tv_wifi.setText("사용 가능");
                        tv_scan.setText("사용 불가");
                    } else if (splitStr[1].contains("G1")) {
                        tv_model.setText("Premium Plus (WiFi & Scan)");
                        tv_wifi.setText("사용 가능");
                        tv_scan.setText("사용 가능");
                    }

                } else if (splitStr[0].startsWith("RFD40+")) {
                    String serialno[] = readerName.split("_");
                    int length = serialno.length;
                    tv_serialno.setText(serialno[length - 1]);
                    tv_model.setText("Premium Plus (WiFi & Scan)");
                    tv_wifi.setText("사용 가능");
                    tv_scan.setText("사용 가능");
                } else if (splitStr[0].startsWith("RFD40P")) {
                    String serialno[] = readerName.split("_");
                    int length = serialno.length;
                    tv_serialno.setText(serialno[length - 1]);
                    tv_model.setText("Premium (WiFi)");
                    tv_wifi.setText("사용 가능");
                    tv_scan.setText("사용 불가");
                }

            } else if (readerName.startsWith("RFD90+")) {
                String serialno[] = readerName.split("_");
                int length = serialno.length;
                tv_serialno.setText(serialno[length - 1]);
                tv_model.setText("Premium Plus (WiFi & Scan)");
                tv_wifi.setText("사용 가능");
                tv_scan.setText("사용 가능");
            } else if (readerName.startsWith("RFD90")) {
                readerName = mReaderDevice.getSerialNumber();
                String serialno[] = readerName.split("S/N:");
                tv_serialno.setText(serialno[1]);
                tv_model.setText("Premium Plus (WiFi & Scan)");
                tv_wifi.setText("사용 가능");
                tv_scan.setText("사용 가능");
            } else if (readerName.startsWith("RFD8500")) {
                String[] splitStr = readerName.split("RFD8500");
                tv_serialno.setText(splitStr[1]);
                tv_model.setText("RFD8500");
                tv_wifi.setText("사용 불가");
                if (Application.currentScannerId == -1) tv_scan.setText("사용 불가");
                else tv_scan.setText("사용 가능");
            }
        }

        if (tv_serialno.getText().toString().contains("S/N:")) {
            String sn = tv_serialno.getText().toString();
            String serialno[] = sn.split("S/N:");
            tv_serialno.setText(serialno[1]);
        }
    }

    public void renameReader() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rename_reader, null);
        EditText etRenameReader = view.findViewById(R.id.edit_ReaderName);
        try {
            etRenameReader.setText(mConnectedReader.Config.getFriendlyName());
        } catch (InvalidUsageException | OperationFailureException e) {
            Log.e(TAG, e.getStackTrace()[0].toString());
        }
        builder.setView(view).setTitle("장치 이름 변경").setNegativeButton("취소", (dialogInterface, i) -> {

        }).setPositiveButton("확인", (dialogInterface, i) -> {
            String newName = etRenameReader.getText().toString();
            try {
                RFIDResults rfidResults = mConnectedReader.Config.setFriendlyName(newName);

                if (rfidResults == RFIDResults.RFID_API_SUCCESS) {
                    Toast.makeText(getActivity(), "이름 변경 성공. 변경 사항을 보려면" + "\nUSB 연결인 경우: 장치를 분리하고 다시 연결해 주세요. " + "\n블루투스 연결인 경우: 장치를 페어링 해제 후, 다시 페어링해 주세요.", Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                } else if (rfidResults == RFIDResults.RFID_COMMAND_OPTION_WITHOUT_DELIMITER) {
                    Toast.makeText(getActivity(), "이름 변경 실패하였습니다. 단어 사이에 공백을 포함하지 마세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "이름 변경 실패. 알 수 없는 에러.", Toast.LENGTH_SHORT).show();
                }
            } catch (InvalidUsageException e) {
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                    Log.e(TAG, e.getInfo());
                    Toast.makeText(getActivity(), e.getInfo(), Toast.LENGTH_SHORT).show();
                }
            } catch (OperationFailureException e) {
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
        });
        builder.show();
    }

    private void InitializeView(View view) {
        tv_name = view.findViewById(R.id.readername_value);
        tv_serialno = view.findViewById(R.id.serialno_value);
        tv_model = view.findViewById(R.id.model_value);
        tv_wifi = view.findViewById(R.id.wifi_available);
        tv_rfid = view.findViewById(R.id.rfid_availabe);
        tv_scan = view.findViewById(R.id.scan_available);
        iconRenameReader = view.findViewById(R.id.icon_rename_reader);

    }

}