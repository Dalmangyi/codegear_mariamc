package com.codegear.mariamc_rfid.rfidreader.access_operations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.application.Application;
import com.zebra.rfid.api3.ACCESS_OPERATION_CODE;
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS;
import com.zebra.rfid.api3.BEEPER_VOLUME;
import com.zebra.rfid.api3.TagData;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.common.Constants;
import com.codegear.mariamc_rfid.rfidreader.common.InputFilterMax;
import com.codegear.mariamc_rfid.rfidreader.common.hextoascii;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;

import java.util.Timer;
import java.util.TimerTask;

import static com.codegear.mariamc_rfid.rfidreader.home.RFIDBase.filter;

public class AccessOperationsReadWriteFragment extends Fragment implements AccessOperationsFragment.OnRefreshListener {

    private TextView textRWData;
    private EditText etOffset;
    private EditText etLength;
    private AutoCompleteTextView tvTagIDField;
    private ArrayAdapter<String> adapter;
    private Spinner rw_typespinner;


    public Timer beepTimer;
    private boolean beepON = false;
    private long BEEP_STOP_TIME = 20;
    private boolean showAdvancedOptions;




    public AccessOperationsReadWriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccessOperationsReadWriteFragment.
     */
    public static AccessOperationsReadWriteFragment newInstance() {
        return new AccessOperationsReadWriteFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.dw_action));
        filter.addCategory(getResources().getString(R.string.dw_category));
        getActivity().registerReceiver(scanResultBroadcast, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_access_operations_read_write, container, false);
        rw_typespinner = view.findViewById(R.id.readwrite_type);
        rw_typespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                if (selectedType.equals("Advanced")) {
                    Application.rwAdvancedOptions = true;
                } else {
                    Application.rwAdvancedOptions = false;
                }


                SharedPreferences settings = getActivity().getSharedPreferences(Constants.APP_SETTINGS_STATUS, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Constants.ACCESS_ADV_OPTIONS, Application.rwAdvancedOptions);
                editor.commit();
                UpdateViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeSpinner();
        tvTagIDField = ((AutoCompleteTextView) getActivity().findViewById(R.id.accessRWTagID));
        etOffset = (EditText) getActivity().findViewById(R.id.accessRWOffsetValue);
        etLength = (EditText) getActivity().findViewById(R.id.accessRWLengthValue);
        textRWData = (TextView) getActivity().findViewById(R.id.accessRWData);
        etOffset.setHorizontallyScrolling(false);
        etLength.setHorizontallyScrolling(false);

        //handle Seek Operations
        handleSeekOperations();
        RFIDController.getInstance().updateTagIDs();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, Application.tagIDs);
        tvTagIDField.setAdapter(adapter);
        if (RFIDController.asciiMode == true) {
            tvTagIDField.setFilters(new InputFilter[]{filter});
            textRWData.setFilters(new InputFilter[]{filter});
        } else {
            tvTagIDField.setFilters(new InputFilter[]{filter, new InputFilter.AllCaps()});
            textRWData.setFilters(new InputFilter[]{filter, new InputFilter.AllCaps()});

        }
        if (RFIDController.accessControlTag != null) {
            if (RFIDController.asciiMode == true)
                tvTagIDField.setText(hextoascii.convert(RFIDController.accessControlTag));
            else tvTagIDField.setText((RFIDController.accessControlTag));
            etOffset.setText("2");
        } else {
            etOffset.setText("0");
        }

        //
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.APP_SETTINGS_STATUS, 0);
        showAdvancedOptions = settings.getBoolean(Constants.ACCESS_ADV_OPTIONS, false);
        if (showAdvancedOptions) {
            rw_typespinner.setSelection(1);

        }
        UpdateViews();
    }

    private void UpdateViews() {
        LinearLayout advancedOptions = (LinearLayout) getActivity().findViewById(R.id.accessRWAdvanceOption);
        if (advancedOptions != null) {
            if (Application.rwAdvancedOptions) {
                advancedOptions.setVisibility(View.VISIBLE);
            } else {
                advancedOptions.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Method to initialize the seekbars
     */
    private void handleSeekOperations() {
        etLength.setFilters(new InputFilter[]{new InputFilterMax(Long.valueOf(Constants.MAX_LEGTH))});
    }

    private void initializeSpinner() {
        Spinner memoryBankSpinner = (Spinner) getActivity().findViewById(R.id.accessRWMemoryBank);
        if (memoryBankSpinner != null) {

            ArrayAdapter<CharSequence> memoryBankAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.acess_read_write_memory_bank_array, R.layout.custom_spinner_layout);
            memoryBankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            memoryBankSpinner.setAdapter(memoryBankAdapter);
            memoryBankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            etOffset.setText("2"); // EPC
                            etLength.setText("0");
                            break;
                        case 1:
                        case 2:
                            etOffset.setText("0"); // TID USER
                            etLength.setText("0");
                            break;
                        case 4:
                            etOffset.setText("0"); // kill password
                            etLength.setText("2");
                            break;
                        case 3: // access password
                            etOffset.setText("2");
                            etLength.setText("2");
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(scanResultBroadcast);
    }

    public void handleTagResponse(final TagData response_tagData) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (response_tagData != null) {
                    ACCESS_OPERATION_CODE readAccessOperation = response_tagData.getOpCode();
                    if (readAccessOperation != null) {
                        if (response_tagData.getOpStatus() != null && !response_tagData.getOpStatus().equals(ACCESS_OPERATION_STATUS.ACCESS_SUCCESS)) {
                            String strErr = response_tagData.getOpStatus().toString().replaceAll("_", " ");
                            Toast.makeText(getActivity(), strErr.toLowerCase(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (response_tagData.getOpCode() == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ) {
                                TextView text = (TextView) getActivity().findViewById(R.id.accessRWData);
                                if (text != null) {
                                    text.setText(RFIDController.asciiMode == true ? hextoascii.convert(response_tagData.getMemoryBankData()) : response_tagData.getMemoryBankData());
                                }
                                Toast.makeText(getActivity(), R.string.msg_read_succeed, Toast.LENGTH_SHORT).show();
                                startBeepingTimer();
                            } else {
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.err_read_access_op_failed, Toast.LENGTH_SHORT).show();
                        Constants.logAsMessage(Constants.TYPE_DEBUG, "ACCESS READ", "memoryBankData is null");
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.err_access_op_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onUpdate() {
        if (isVisible() && tvTagIDField != null) {
            RFIDController.accessControlTag = tvTagIDField.getText().toString();
        }
    }

    @Override
    public void onRefresh() {
        if (RFIDController.accessControlTag != null && tvTagIDField != null) {
            tvTagIDField.setText(RFIDController.accessControlTag);
        }
    }

    public void startBeepingTimer() {
        if (RFIDController.beeperVolume != BEEPER_VOLUME.QUIET_BEEP) {
            if (!beepON) {
                beepON = true;
                beep();
                if (beepTimer == null) {
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            stopBeepingTimer();
                            beepON = false;
                        }
                    };
                    beepTimer = new Timer();
                    beepTimer.schedule(task, BEEP_STOP_TIME);
                }
            }
        }
    }

    /**
     * method to stop timer
     */
    public void stopBeepingTimer() {
        if (beepTimer != null) {
            if (RFIDController.toneGenerator != null) RFIDController.toneGenerator.stopTone();
            beepTimer.cancel();
            beepTimer.purge();
        }
        beepTimer = null;
    }

    public void beep() {
        if (RFIDController.toneGenerator != null) {
            int toneType = ToneGenerator.TONE_PROP_BEEP;
            RFIDController.toneGenerator.startTone(toneType);
        }
    }

    private BroadcastReceiver scanResultBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(getResources().getString(R.string.dw_action))) {
                displayScanResult(intent);
            }

        }
    };

    private void displayScanResult(Intent initiatingIntent) {
        String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        if (decodedData != null && tvTagIDField != null) {
            tvTagIDField.setText(decodedData);
            tvTagIDField.setSelection(decodedData.length());
        }
    }
}
