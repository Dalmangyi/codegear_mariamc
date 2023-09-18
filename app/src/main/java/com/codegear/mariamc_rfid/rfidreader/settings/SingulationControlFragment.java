package com.codegear.mariamc_rfid.rfidreader.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.rfidreader.common.CustomProgressDialog;
import com.zebra.rfid.api3.Antennas;
import com.zebra.rfid.api3.INVENTORY_STATE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.SESSION;
import com.zebra.rfid.api3.SL_FLAG;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.common.Constants;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;

import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.isSimplePreFilterEnabled;
import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.mConnectedReader;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_ADVANCED_OPTIONS_TAB;

public class SingulationControlFragment extends BackPressedFragment implements Spinner.OnItemSelectedListener {

    private TextView tv_preFilterEnabled;
    private Spinner sessionSpinner;
    private Spinner tagPopulationSpinner;
    private Spinner inventoryStateSpinner;
    private Spinner slFlagSpinner;
    private ArrayAdapter<CharSequence> tagPopulationAdapter;
    private FragmentActivity fragmentActivity = null;
    private Context context;
    private static final String TAG = "SingulationControlFragment";


    public SingulationControlFragment() {
        // Required empty public constructor
    }


    public static SingulationControlFragment newInstance() {
        return new SingulationControlFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_singulation_control, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tv_preFilterEnabled = (TextView) getActivity().findViewById(R.id.tv_preFilterEnabled);
        sessionSpinner = (Spinner) getActivity().findViewById(R.id.session);
        ArrayAdapter<CharSequence> sessionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.session_array, R.layout.custom_spinner_layout);
        sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sessionSpinner.setAdapter(sessionAdapter);
        tagPopulationSpinner = (Spinner) getActivity().findViewById(R.id.tagPopulation);
        tagPopulationAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.tag_population_array, R.layout.custom_spinner_layout);
        tagPopulationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagPopulationSpinner.setAdapter(tagPopulationAdapter);
        inventoryStateSpinner = (Spinner) getActivity().findViewById(R.id.inventoryState);
        ArrayAdapter<CharSequence> inventoryAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.inventory_state_array, R.layout.custom_spinner_layout);
        inventoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inventoryStateSpinner.setAdapter(inventoryAdapter);
        slFlagSpinner = (Spinner) getActivity().findViewById(R.id.slFlag);
        ArrayAdapter<CharSequence> slAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sl_flags_array, R.layout.custom_spinner_layout);
        slAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        slFlagSpinner.setAdapter(slAdapter);

        // defaults
        sessionSpinner.setSelection(0, false);
        tagPopulationSpinner.setSelection(0, false);
        inventoryStateSpinner.setSelection(0, false);
        slFlagSpinner.setSelection(0, false);
        // reader settings
        if (mConnectedReader != null && mConnectedReader.isConnected() && mConnectedReader.isCapabilitiesReceived() && RFIDController.singulationControl != null) {
            sessionSpinner.setSelection(RFIDController.singulationControl.getSession().getValue());
            tagPopulationSpinner.setSelection(tagPopulationAdapter.getPosition(RFIDController.singulationControl.getTagPopulation() + ""));
            inventoryStateSpinner.setSelection(RFIDController.singulationControl.Action.getInventoryState().getValue());
            //slFlagSpinner.setSelection(RFIDController.singulationControl.Action.getSLFlag().getValue());
            switch (RFIDController.singulationControl.Action.getSLFlag().getValue()) {
                case 0:
                    slFlagSpinner.setSelection(2);
                    break;
                case 1:
                    slFlagSpinner.setSelection(1);
                    break;
                case 2:
                    slFlagSpinner.setSelection(0);
                    break;
            }
        }
        sessionSpinner.setOnItemSelectedListener(this);
        tagPopulationSpinner.setOnItemSelectedListener(this);
        inventoryStateSpinner.setOnItemSelectedListener(this);
        slFlagSpinner.setOnItemSelectedListener(this);


        // enable settings if reader is not connected
        // enable settings if advanced prefilter is enabled
        // disable if simple prefilter is enabled
        boolean enableSLOptions = !isSimplePreFilterEnabled();

        SharedPreferences settings = getActivity().getSharedPreferences(Constants.APP_SETTINGS_STATUS, 0);
        boolean showAdvancedOptions = settings.getBoolean(Constants.PREFILTER_ADV_OPTIONS, false);
        if (showAdvancedOptions) enableSLOptions = true;


        sessionSpinner.setEnabled(enableSLOptions);
        inventoryStateSpinner.setEnabled(enableSLOptions);
        slFlagSpinner.setEnabled(enableSLOptions);
        getActivity().findViewById(R.id.tv_preFilterEnabled).setVisibility(enableSLOptions ? View.GONE : View.VISIBLE);
        fragmentActivity = getActivity();


        getActivity().findViewById(R.id.saveConfigButton).setOnClickListener(v -> {
            saveConfigClicked(v);
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //DO Nothing
    }

    public void onBackPressed() {
        if (getActivity() instanceof ActiveDeviceActivity)
            ((ActiveDeviceActivity) getActivity()).loadNextFragment(RFID_ADVANCED_OPTIONS_TAB);
    }

    //수동 저장
    public void saveConfigClicked(View v){
        if ((sessionSpinner.getSelectedItem() != null && tagPopulationSpinner.getSelectedItem() != null && inventoryStateSpinner.getSelectedItem() != null && slFlagSpinner.getSelectedItem() != null)) {
            if (isSettingsChanged()) {
                new Task_SaveSingulationConfiguration(sessionSpinner.getSelectedItemPosition(), tagPopulationSpinner.getSelectedItemPosition(), inventoryStateSpinner.getSelectedItemPosition(), slFlagSpinner.getSelectedItemPosition(), context).execute();
            }
        }
    }


    private boolean isSettingsChanged() {
        if (RFIDController.singulationControl != null) {
            if (RFIDController.singulationControl.getSession().getValue() != sessionSpinner.getSelectedItemPosition())
                return true;
            else if (RFIDController.singulationControl.getTagPopulation() != Integer.parseInt(tagPopulationSpinner.getSelectedItem().toString()))
                return true;
            else if (RFIDController.singulationControl.Action.getInventoryState().getValue() != inventoryStateSpinner.getSelectedItemPosition())
                return true;
            else {
                int pos = slFlagSpinner.getSelectedItemPosition();
                switch (pos) {
                    case 0:
                        if (RFIDController.singulationControl.Action.getSLFlag() != SL_FLAG.SL_ALL)
                            return true;
                        break;
                    case 1:
                        if (RFIDController.singulationControl.Action.getSLFlag() != SL_FLAG.SL_FLAG_DEASSERTED)
                            return true;
                        break;
                    case 2:
                        if (RFIDController.singulationControl.Action.getSLFlag() != SL_FLAG.SL_FLAG_ASSERTED)
                            return true;
                        break;
                }
            }
        }
        return false;
    }


    private class Task_SaveSingulationConfiguration extends AsyncTask<Void, Void, Boolean> {
        private CustomProgressDialog progressDialog;
        private OperationFailureException operationFailureException;
        private InvalidUsageException invalidUsageException;
        private final int session;
        private final int tagpopulation;
        private final int inventorystate;
        private final int slflag;
        Context current_context;

        public Task_SaveSingulationConfiguration(int sessionIndex, int tagPopulationIndex, int invStateIndex, int slflagindex, Context context) {
            session = sessionIndex;
            tagpopulation = tagPopulationIndex;
            inventorystate = invStateIndex;
            slflag = slflagindex;
            current_context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new CustomProgressDialog(getActivity(), getString(R.string.singulation_progress_title));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            Antennas.SingulationControl singulationControl;
            try {
                singulationControl = mConnectedReader.Config.Antennas.getSingulationControl(1);
                singulationControl.setSession(SESSION.GetSession(session));
                singulationControl.setTagPopulation(Short.parseShort(tagPopulationAdapter.getItem(tagpopulation).toString()));
                singulationControl.Action.setInventoryState(INVENTORY_STATE.GetInventoryState(inventorystate));
                switch (slflag) {
                    case 0:
                        singulationControl.Action.setSLFlag(SL_FLAG.SL_ALL);
                        break;
                    case 1:
                        singulationControl.Action.setSLFlag(SL_FLAG.SL_FLAG_DEASSERTED);
                        break;
                    case 2:
                        singulationControl.Action.setSLFlag(SL_FLAG.SL_FLAG_ASSERTED);
                        break;
                }
                mConnectedReader.Config.Antennas.setSingulationControl(1, singulationControl);
                RFIDController.singulationControl = singulationControl;
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
                    if (current_context instanceof ActiveDeviceActivity)
                        ((ActiveDeviceActivity) current_context).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + invalidUsageException.getVendorMessage());
                }
                if (operationFailureException != null) {
                    if (current_context instanceof ActiveDeviceActivity)
                        ((ActiveDeviceActivity) current_context).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, getString(R.string.status_failure_message) + "\n" + operationFailureException.getVendorMessage());
                }
            }
            if (invalidUsageException == null && operationFailureException == null && fragmentActivity != null)
                Toast.makeText(((ActiveDeviceActivity) current_context), R.string.status_success_message, Toast.LENGTH_SHORT).show();
            ((ActiveDeviceActivity) current_context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ActiveDeviceActivity) current_context).loadNextFragment(RFID_ADVANCED_OPTIONS_TAB);
                }
            });

            super.onPostExecute(result);

        }
    }

    public void deviceConnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (RFIDController.singulationControl != null) {
                    sessionSpinner.setSelection(RFIDController.singulationControl.getSession().getValue());
                    tagPopulationSpinner.setSelection(tagPopulationAdapter.getPosition(RFIDController.singulationControl.getTagPopulation() + ""));
                    inventoryStateSpinner.setSelection(RFIDController.singulationControl.Action.getInventoryState().getValue());
                    switch (RFIDController.singulationControl.Action.getSLFlag().getValue()) {
                        case 0:
                            slFlagSpinner.setSelection(2);
                            break;
                        case 1:
                            slFlagSpinner.setSelection(1);
                            break;
                        case 2:
                            slFlagSpinner.setSelection(0);
                            break;
                    }
                }
            }
        });
    }

    public void deviceDisconnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // defaults
                sessionSpinner.setSelection(0, false);
                tagPopulationSpinner.setSelection(0, false);
                inventoryStateSpinner.setSelection(0, false);
                slFlagSpinner.setSelection(0, false);
            }
        });
    }



}
