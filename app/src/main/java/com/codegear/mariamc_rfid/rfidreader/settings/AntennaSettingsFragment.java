package com.codegear.mariamc_rfid.rfidreader.settings;

import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_ADVANCED_OPTIONS_TAB;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.rfidreader.common.Constants;
import com.codegear.mariamc_rfid.rfidreader.common.CustomProgressDialog;
import com.codegear.mariamc_rfid.rfidreader.common.LinkProfileUtil;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.Antennas;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFModeTableEntry;
import com.codegear.mariamc_rfid.R;

public class AntennaSettingsFragment extends BackPressedFragment {

    private ArrayAdapter<String> linkAdapter;
    private EditText powerLevel;
    private Spinner linkProfileSpinner;
    private Spinner tariSpinner;
    private int[] powerLevels;
    private Spinner PIESpinner;
    private String previousSelection = "";
    private LinkProfileUtil linkProfileUtil;
    Context context;
    private static final String TAG = "AntennaSettingsFragment";

    public AntennaSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AntennaSettingsFragment.
     */
    public static AntennaSettingsFragment newInstance() {
        return new AntennaSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_antenna_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeViews();

        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected() && RFIDController.mConnectedReader.isCapabilitiesReceived() && RFIDController.rfModeTable != null && RFIDController.antennaRfConfig != null) {
            powerLevels = RFIDController.mConnectedReader.ReaderCapabilities.getTransmitPowerLevelValues();
            linkProfileUtil = LinkProfileUtil.getInstance();
            linkProfileSpinner.setEnabled(true);
            PIESpinner.setEnabled(true);
            tariSpinner.setEnabled(true);

            linkAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_small_font, linkProfileUtil.getSimpleProfiles());
            linkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            linkProfileSpinner.setAdapter(linkAdapter);
            linkProfileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setTariSpinnerFromSelection();
                    if (!previousSelection.equalsIgnoreCase("")) setPIESpinnerFromSelection();
                    previousSelection = linkProfileSpinner.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            ArrayAdapter<CharSequence> pieAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.pie_array, R.layout.custom_spinner_layout);
            pieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            PIESpinner.setAdapter(pieAdapter);

            int modeIndex = (int) RFIDController.antennaRfConfig.getrfModeTableIndex();
            powerLevel.setText(String.valueOf(powerLevels[RFIDController.antennaRfConfig.getTransmitPowerIndex()]));
            setTariSpinner(modeIndex);
            setPIESpinner(modeIndex);
            linkProfileSpinner.setSelection(linkProfileUtil.getSelectedLinkProfilePosition(RFIDController.antennaRfConfig.getrfModeTableIndex()));
        }
    }

    private int GetTariResourceIndex(int index) {

        RFModeTableEntry rfModeTableEntry = getRFModeTableEntry(index);

        if ((rfModeTableEntry.getMinTariValue() == 25000 && linkProfileUtil.isMinTari_1250()) || (rfModeTableEntry.getMaxTariValue() == 23000 && rfModeTableEntry.getMinTariValue() == 12500)) {
            if (linkProfileUtil.isStepTari_6300()) return R.array.tari_array_25_6300;
            else return R.array.tari_array_12_25;
        } else if (rfModeTableEntry.getMinTariValue() == 25000 && linkProfileUtil.isStepTari_non_0() || (rfModeTableEntry.getMaxTariValue() == 23000 && rfModeTableEntry.getMinTariValue() == 18800))
            return R.array.tari_array_18_25;
        else if (rfModeTableEntry.getMaxTariValue() == 18800 && rfModeTableEntry.getMinTariValue() == 12500) {
            if (linkProfileUtil.isStepTari_6300()) return R.array.tari_array_18_6300;
            else return R.array.tari_array_18;
        } else if (rfModeTableEntry.getMinTariValue() == 18800) return R.array.tari_array_18_only;
        else if (rfModeTableEntry.getMinTariValue() == 25000 && !linkProfileUtil.isStepTari_non_0())
            return R.array.tari_array_25_only;
        else if (rfModeTableEntry.getMaxTariValue() == 6250) return R.array.tari_array_625;
        else if (rfModeTableEntry.getMaxTariValue() == 668) return R.array.tari_array_668;
        else if (rfModeTableEntry.getMaxTariValue() == 0) return R.array.tari0_array;
        else return R.array.tari_array;
    }

    private RFModeTableEntry getRFModeTableEntry(int modeIndex) {
        RFModeTableEntry rfModeTableEntry = RFIDController.rfModeTable.getRFModeTableEntryInfo(0);
        for (int i = 0; i < RFIDController.rfModeTable.length(); i++) {
            rfModeTableEntry = RFIDController.rfModeTable.getRFModeTableEntryInfo(i);
            if (modeIndex == rfModeTableEntry.getModeIdentifer()) {
                break;
            }
        }
        return rfModeTableEntry;
    }

    private void setTariSpinnerFromSelection() {
        setTariSpinner(linkProfileUtil.getMatchingIndex(linkProfileSpinner.getSelectedItem().toString()));
    }

    private void setTariSpinner(int modeIndex) {
        if (RFIDController.antennaRfConfig != null) {
            String tariConfig = String.valueOf(RFIDController.antennaRfConfig.getTari());

            RFModeTableEntry rfModeTableEntry = getRFModeTableEntry(modeIndex);

            ArrayAdapter<CharSequence> tariAdapter = ArrayAdapter.createFromResource(getActivity(), GetTariResourceIndex(modeIndex), R.layout.custom_spinner_layout);
            tariAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            tariSpinner.setAdapter(tariAdapter);
            tariAdapter.notifyDataSetChanged();
            tariSpinner.setSelection(0);

            String[] tariArrayList = getResources().getStringArray(GetTariResourceIndex(modeIndex));
            if (RFIDController.antennaRfConfig.getTari() == 0)
                tariConfig = String.valueOf(rfModeTableEntry.getMinTariValue());

            for (int idx = 0; idx < tariArrayList.length; idx++) {
                if (tariConfig.equals(tariArrayList[idx])) {
                    tariSpinner.setSelection(idx);
                    break;
                }
            }
        }
    }

    private void setPIESpinnerFromSelection() {
        String currentSelection = linkProfileSpinner.getSelectedItem().toString();
        //if (currentSelection.startsWith("AUTOMAC") || previousSelection.startsWith("AUTOMAC"))
        setPIESpinner(linkProfileUtil.getMatchingIndex(currentSelection));
    }

    private void setPIESpinner(int modeIndex) {
        ArrayAdapter<CharSequence> pieAdapter = ArrayAdapter.createFromResource(getActivity(), GetPIEResourceIndex(modeIndex), R.layout.custom_spinner_layout);
        pieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PIESpinner.setAdapter(pieAdapter);
        pieAdapter.notifyDataSetChanged();
        PIESpinner.setSelection(0);

        RFModeTableEntry rfModeTableEntry = getRFModeTableEntry(modeIndex);
        switch (rfModeTableEntry.getPieValue()) {
            case 2000:
                PIESpinner.setSelection(1);
                break;
        }
    }

    private int GetPIEResourceIndex(int index) {
        if (getRFModeTableEntry(index).getPieValue() == 668) return R.array.pie_array_668;
        if (getRFModeTableEntry(index).getPieValue() == 0) return R.array.pie0_array;
        else if (!linkProfileUtil.isPie_1500()) return R.array.pie_array_2000;
        else return R.array.pie_array;
    }

    private void initializeViews() {
        powerLevel = (EditText) getActivity().findViewById(R.id.powerLevel);
        tariSpinner = (Spinner) getActivity().findViewById(R.id.tari);
        tariSpinner.setEnabled(false);
        linkProfileSpinner = (Spinner) getActivity().findViewById(R.id.linkProfile);
        linkProfileSpinner.setEnabled(false);
        PIESpinner = (Spinner) getActivity().findViewById(R.id.PIE);
        PIESpinner.setEnabled(false);


        getActivity().findViewById(R.id.saveConfigButton).setOnClickListener(v -> {
            saveConfigClicked(v);
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onBackPressed() {
        if (getActivity() instanceof ActiveDeviceActivity)
            ((ActiveDeviceActivity) getActivity()).loadNextFragment(RFID_ADVANCED_OPTIONS_TAB);
    }


    //수동 저장
    public void saveConfigClicked(View v){
        if(isSettingsChanged()){
            Toast.makeText(getActivity(), "설정이 성공적으로 반영되었습니다.", Toast.LENGTH_LONG).show();
        }
    }



    public static void replaceFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId) {
    }


    private boolean isSettingsChanged() {
        boolean isSettingsChanged = false;
        if (RFIDController.antennaRfConfig != null) {
            if ((powerLevel != null && !powerLevel.getText().toString().isEmpty()) && (tariSpinner != null)) {
                int powerLevelIndex = -1;
                try {
                    powerLevelIndex = getPowerLevelIndex(Integer.parseInt(powerLevel.getText().toString()));
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "전력 수준에 유효한 값을 입력하십시오.", Toast.LENGTH_LONG).show();
                }
                if (powerLevelIndex == -1) {
                    if (getActivity() instanceof SettingsDetailActivity)
                        ((SettingsDetailActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + getString(R.string.error_invalid_fields_antenna_config));
                    else if (getActivity() instanceof ActiveDeviceActivity)
                        ((ActiveDeviceActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + getString(R.string.error_invalid_fields_antenna_config));
                    return false;
                }
                int linkedProfileIndex = getSelectedLinkedProfileIndex();
                if (linkedProfileIndex == -1) {
                    if (getActivity() instanceof SettingsDetailActivity)
                        ((SettingsDetailActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + getString(R.string.error_invalid_fields_antenna_config));
                    else if (getActivity() instanceof ActiveDeviceActivity)
                        ((ActiveDeviceActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + getString(R.string.error_invalid_fields_antenna_config));
                    return false;
                }
                int tariValue = -1;
                try {
                    tariValue = Integer.parseInt(tariSpinner.getSelectedItem().toString());
                    if (RFIDController.antennaRfConfig.getTari() == 0 && linkedProfileIndex == RFIDController.antennaRfConfig.getrfModeTableIndex()) {
                        RFModeTableEntry rfModeTableEntry = getRFModeTableEntry(linkedProfileIndex);
                        if (rfModeTableEntry.getMinTariValue() == tariValue) tariValue = 0;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Tari 값에 유효한 값을 입력하십시오.", Toast.LENGTH_LONG).show();
                }
                if (tariValue == -1) {
                    if (getActivity() instanceof SettingsDetailActivity)
                        ((SettingsDetailActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + getString(R.string.error_invalid_fields_antenna_config));
                    else if (getActivity() instanceof ActiveDeviceActivity)
                        ((ActiveDeviceActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + getString(R.string.error_invalid_fields_antenna_config));
                    return false;
                }
                if (powerLevelIndex != RFIDController.antennaRfConfig.getTransmitPowerIndex() || linkedProfileIndex != RFIDController.antennaRfConfig.getrfModeTableIndex() || tariValue != RFIDController.antennaRfConfig.getTari()) {
                    new Task_SaveAntennaConfiguration(powerLevelIndex, linkedProfileIndex, tariValue, context).execute();
                    isSettingsChanged = true;
                }
            }
        }
        return isSettingsChanged;
    }

    private int getSelectedLinkedProfileIndex() {
        int modeIndex = 0;
        String profile = linkProfileSpinner.getSelectedItem().toString();
        String tari = tariSpinner.getSelectedItem().toString();
        String PIE = PIESpinner.getSelectedItem().toString();
        return linkProfileUtil.getMatchingIndex(profile, tari, PIE);
    }

    private int getPowerLevelIndex(int powerLevel) {
        for (int i = 0; i < powerLevels.length; i++) {
            if (powerLevel == powerLevels[i]) {
                return i;
            }
        }
        return -1;
    }

    private class Task_SaveAntennaConfiguration extends AsyncTask<Void, Void, Boolean> {
        private CustomProgressDialog progressDialog;
        private OperationFailureException operationFailureException;
        private InvalidUsageException invalidUsageException;
        private final int powerLevel;
        private final int linkedProfile;
        private final int tari;
        Context current_context;

        public Task_SaveAntennaConfiguration(int powerLevelIndex, int linkedProfileIndex, int tariValue, Context context) {
            powerLevel = powerLevelIndex;
            linkedProfile = linkedProfileIndex;
            tari = tariValue;
            current_context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CustomProgressDialog(getActivity(), getString(R.string.antenna_progress_title));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Antennas.AntennaRfConfig antennaRfConfig;
            try {
                antennaRfConfig = RFIDController.mConnectedReader.Config.Antennas.getAntennaRfConfig(1);
                antennaRfConfig.setTransmitPowerIndex(powerLevel);
                antennaRfConfig.setrfModeTableIndex(linkedProfile);
                antennaRfConfig.setTari(tari);
                RFIDController.mConnectedReader.Config.Antennas.setAntennaRfConfig(1, antennaRfConfig);
                RFIDController.antennaRfConfig = antennaRfConfig;
                ProfileContent.UpdateActiveProfile();
                return true;
            } catch (InvalidUsageException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
                invalidUsageException = e;
            } catch (OperationFailureException e) {
                if (e != null && e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
                operationFailureException = e;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.cancel();
            if (!result) {
                if (invalidUsageException != null) {
                    if (getActivity() instanceof SettingsDetailActivity)
                        ((SettingsDetailActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + invalidUsageException.getVendorMessage());
                    else if (getActivity() instanceof ActiveDeviceActivity)
                        ((ActiveDeviceActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + invalidUsageException.getVendorMessage());
                }
                if (operationFailureException != null) {
                    //   if(getActivity() instanceof SettingsDetailActivity)
                    //          ((SettingsDetailActivity) getActivity()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + operationFailureException.getVendorMessage());
                    if (current_context instanceof ActiveDeviceActivity)
                        ((ActiveDeviceActivity) current_context).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, "Failed to apply settings" + "\n" + operationFailureException.getVendorMessage());
                }
            }
            if (invalidUsageException == null && operationFailureException == null) {
                if (getActivity() instanceof SettingsDetailActivity)
                    Toast.makeText((SettingsDetailActivity) getActivity(), R.string.status_success_message, Toast.LENGTH_SHORT).show();
                else if (current_context instanceof ActiveDeviceActivity)
                    Toast.makeText((ActiveDeviceActivity) current_context, "설정이 성공적으로 적용되었습니다.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
            //AdvancedOptionItemFragment fragment = AdvancedOptionItemFragment.newInstance();
            //replaceFragment(getFragmentManager(), fragment, R.id.settings_content_frame);
        }
    }

    public void deviceConnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (RFIDController.antennaRfConfig != null) {
                    int modeIndex = (int) RFIDController.antennaRfConfig.getrfModeTableIndex();
                    linkProfileUtil = LinkProfileUtil.getInstance();
                    setTariSpinner(modeIndex);
                    powerLevel.setText(String.valueOf(powerLevels[RFIDController.antennaRfConfig.getTransmitPowerIndex()]));
                    linkAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_small_font, linkProfileUtil.getSimpleProfiles());
                    linkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    linkProfileSpinner.setAdapter(linkAdapter);
                    linkProfileSpinner.setSelection(linkProfileUtil.getSelectedLinkProfilePosition(RFIDController.antennaRfConfig.getrfModeTableIndex()));
                }
            }
        });
    }

    public void deviceDisconnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                powerLevel.setText("");
                tariSpinner.setSelection(0);
                linkProfileSpinner.setAdapter(null);
            }
        });
    }

}
