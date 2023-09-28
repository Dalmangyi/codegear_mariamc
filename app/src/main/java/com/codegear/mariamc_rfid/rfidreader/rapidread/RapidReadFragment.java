package com.codegear.mariamc_rfid.rfidreader.rapidread;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.application.Application;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.common.Constants;
import com.codegear.mariamc_rfid.rfidreader.common.ResponseHandlerInterfaces;
import com.codegear.mariamc_rfid.rfidreader.inventory.InventoryListItem;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.codegear.mariamc_rfid.rfidreader.settings.ISettingsUtil;
import com.zebra.rfid.api3.RFIDResults;

import java.util.concurrent.TimeUnit;


import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.ActiveProfile;
import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.mIsInventoryRunning;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.INVENTORY_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_SETTINGS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.SETTINGS_TAB;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * <p/>
 * Use the {@link RapidReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * Fragment to handle the rapid read operation and UI
 */
public class RapidReadFragment extends Fragment implements ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler, ResponseHandlerInterfaces.BatchModeEventHandler, ResponseHandlerInterfaces.ResponseStatusHandler {
    private static RapidReadFragment mRapidReadFragment = null;
    private static final String TAG = "RapidReadFragment";
    MatchModeProgressView progressView;
    private TextView tagReadRate;
    private TextView tvUniqueTags;
    private TextView tvTotalTags;
    private ExtendedFloatingActionButton btnInventory;
    private TextView timeText;
    private TextView tvUniqueTagTitle;
    private TextView tvTotalTagTitle;
    private LinearLayout inventoryData;
    private FrameLayout batchModeRR;
    boolean batchModeEventReceived = false;
    private ISettingsUtil settingsUtil;

    public RapidReadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RapidReadFragment.
     */
    public static RapidReadFragment newInstance() {
        //if( mRapidReadFragment == null )
        mRapidReadFragment = new RapidReadFragment();
        return mRapidReadFragment;
    }

    public static Fragment newInstance(int position) {

        RapidReadFragment fragment = new RapidReadFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_COUNT, counter);
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
        return inflater.inflate(R.layout.fragment_rapid_read, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_rr, menu);
        menu.findItem(R.id.action_inventory).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ((ActiveDeviceActivity) getActivity()).loadNextFragment(INVENTORY_TAB);
                return true;
            }
        });

        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (RFIDController.mConnectedReader == null) {
                    Toast.makeText(getContext(), "연결된 장치가 없습니다.", Toast.LENGTH_SHORT).show();
                    return true;
                }

                //((ActiveDeviceActivity)getActivity()).showRFIDSettings(null);
                ((ActiveDeviceActivity) getActivity()).setCurrentTabFocus(SETTINGS_TAB, RFID_SETTINGS_TAB);

                return true;

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActiveDeviceActivity mainActivity = (ActiveDeviceActivity) getActivity();
        mainActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        btnInventory = (ExtendedFloatingActionButton) mainActivity.findViewById(R.id.rr_inventoryButton);
        tvUniqueTags = (TextView) mainActivity.findViewById(R.id.uniqueTagContent);
        tvUniqueTagTitle = (TextView) mainActivity.findViewById(R.id.uniqueTagTitle);
        tvTotalTags = (TextView) mainActivity.findViewById(R.id.totalTagContent);
        tvTotalTagTitle = (TextView) mainActivity.findViewById(R.id.totalTagTitle);
        tagReadRate = (TextView) getActivity().findViewById(R.id.readRateContent);
        batchModeRR = (FrameLayout) getActivity().findViewById(R.id.batchModeRR);
        inventoryData = (LinearLayout) getActivity().findViewById(R.id.inventoryDataLayout);
        onRapidReadSelected();
    }


    public void onRapidReadSelected() {

        if (RFIDController.mIsInventoryRunning) {
            btnInventory.setIconResource(R.drawable.ic_play_stop);
        } else {
            btnInventory.setIconResource(android.R.drawable.ic_media_play);
        }
        if (RFIDController.isBatchModeInventoryRunning != null && RFIDController.isBatchModeInventoryRunning) {
            inventoryData.setVisibility(View.GONE);
            batchModeRR.setVisibility(View.VISIBLE);
        } else {
            inventoryData.setVisibility(View.VISIBLE);
            batchModeRR.setVisibility(View.GONE);
        }
        if (RFIDController.mRRStartedTime == 0) Application.TAG_READ_RATE = 0;
        else
            Application.TAG_READ_RATE = (int) (Application.TOTAL_TAGS / (RFIDController.mRRStartedTime / (float) 1000));
        tagReadRate.setText(Application.TAG_READ_RATE + Constants.TAGS_SEC);
        timeText = (TextView) getActivity().findViewById(R.id.readTimeContent);
        if (timeText != null) {
            String min = String.format("%d", TimeUnit.MILLISECONDS.toMinutes(RFIDController.mRRStartedTime));
            String sec = String.format("%d", TimeUnit.MILLISECONDS.toSeconds(RFIDController.mRRStartedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(RFIDController.mRRStartedTime)));
            if (min.length() == 1) {
                min = "0" + min;
            }
            if (sec.length() == 1) {
                sec = "0" + sec;
            }
            timeText.setText(min + ":" + sec);
        }
        progressView = getActivity().findViewById(R.id.MatchModeView);
        if (Application.TAG_LIST_MATCH_MODE) {
            progressView.setVisibility(View.VISIBLE);
        } else {
            progressView.setVisibility(View.GONE);
        }
        if (Application.missedTags > 9999) {
            //orignal size is 60sp - reduced size 45sp
            tvUniqueTags.setTextSize(45);
        }
        updateTexts();
        getActivity().findViewById(R.id.tv_prefilter_enabled).setVisibility(RFIDController.getInstance().isPrefilterEnabled() ? View.VISIBLE : View.GONE);
        com.google.android.material.floatingactionbutton.FloatingActionButton bt_clear = getActivity().findViewById(R.id.bt_clear);
        bt_clear.setVisibility(ActiveProfile.id.equals("1") ? View.VISIBLE : View.INVISIBLE);
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RFIDController.mIsInventoryRunning) {
                    Toast.makeText(getContext(), "Inventory is running", Toast.LENGTH_SHORT).show();
                } else {
                    RFIDController.getInstance().clearAllInventoryData();
                    resetTagsInfo();
                    Application.TAG_LIST_LOADED = false;

                }

            }
        });


        settingsUtil = (ActiveDeviceActivity) getActivity();
        if (Application.TAG_LIST_MATCH_MODE) {
            Log.d("Nikhil1", "onRFIDFragment() RFIDinventoryfragment LoadTagcsv will get call next");
            settingsUtil.LoadTagListCSV();
        }

    }

    public void updateTexts() {
        Log.d(TAG,"RapidReadFragment updateTexts "+Application.UNIQUE_TAGS+","+Application.TOTAL_TAGS);
        if (Application.TAG_LIST_MATCH_MODE) {
            if (tvUniqueTags != null && tvTotalTags != null) {
                tvTotalTags.setText(String.valueOf(Application.matchingTags));
                tvUniqueTags.setText(String.valueOf(Application.missedTags));
            }
            if (tvTotalTagTitle != null && tvUniqueTagTitle != null) {
                tvTotalTagTitle.setText(R.string.rr_total_tag_title_MM);
                tvUniqueTagTitle.setText(R.string.rr_unique_tags_title_MM);
            }
            updateProgressView();
        } else {
            if (tvUniqueTags != null && tvTotalTags != null) {
                tvUniqueTags.setText(String.valueOf(Application.UNIQUE_TAGS));
                tvUniqueTags.postInvalidate();
                tvTotalTags.setText(String.valueOf(Application.TOTAL_TAGS));
                tvTotalTags.postInvalidate();
            }
            if (tvTotalTagTitle != null && tvUniqueTagTitle != null) {
                tvTotalTagTitle.setText(R.string.rr_total_tag_title);
                tvUniqueTagTitle.setText(R.string.rr_unique_tags_title);
            }
        }
    }

    private void updateProgressView() {
        if (Application.missedTags != 0) {
            progressView.mSweepAngle = 360 * Application.matchingTags / (Application.missedTags + Application.matchingTags);
        } /*else if (Application.matchingTags != 0 && Application.missedTags == 0 && RFIDController.mIsInventoryRunning ) {
            progressView.bCompleted = true;
        } */ else if (Application.matchingTags != 0 && Application.missedTags == 0) {
            progressView.bCompleted = true;
        } else {
            progressView.mSweepAngle = 0;
        }
        if (progressView.mSweepAngle >= 360) {
            progressView.mSweepAngle = 0;
        }
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressView.invalidate();
                    progressView.requestLayout();
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * method to reset tags info on the screen before starting inventory operation
     */
    public void resetTagsInfo() {
        updateTexts();
        progressView.bCompleted = false;
        tagReadRate.setText(Application.TAG_READ_RATE + Constants.TAGS_SEC);
        timeText.setText(Constants.ZERO_TIME);
    }

    /**
     * method to start inventory operation on trigger press event received
     */
    public void triggerPressEventRecieved() {
        if (!RFIDController.mIsInventoryRunning && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ActiveDeviceActivity activity = (ActiveDeviceActivity) getActivity();
                    if (activity != null) {
                        activity.inventoryStartOrStop(null);
                    }
                }
            });
        }
    }

    /**
     * method to stop inventory operation on trigger release event received
     */
    public void triggerReleaseEventRecieved() {
        if ((RFIDController.mIsInventoryRunning == true) && getActivity() != null) {
            //RFIDController.mInventoryStartPending = false;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ActiveDeviceActivity activity = (ActiveDeviceActivity) getActivity();
                    if (activity != null) {
                        activity.inventoryStartOrStop(null);
                    }
                }
            });
        }
    }

    public void handleStatusResponse(final RFIDResults results) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (results.equals(RFIDResults.RFID_BATCHMODE_IN_PROGRESS)) {
                    if (tvUniqueTags != null) {
                        inventoryData.setVisibility(View.GONE);
                        batchModeRR.setVisibility(View.VISIBLE);
                    }
                } else if (results.equals(RFIDResults.RFID_OPERATION_IN_PROGRESS)) {

                    if (btnInventory != null) {
                        btnInventory.setIconResource(R.drawable.ic_play_stop);
                        btnInventory.setText(R.string.stop);
                    }
                    mIsInventoryRunning = true;

                } else if (!results.equals(RFIDResults.RFID_API_SUCCESS)) {
                    RFIDController.mIsInventoryRunning = false;
                    if (btnInventory != null) {
                        btnInventory.setIconResource(android.R.drawable.ic_media_play);
                    }
                    RFIDController.isBatchModeInventoryRunning = false;
                }
            }
        });
    }


    /**
     * method to update inventory details on the screen on operation end summary received
     */
    public void updateInventoryDetails() {
        updateTexts();
        tagReadRate.setText(Application.TAG_READ_RATE + Constants.TAGS_SEC);
    }

    /**
     * method to reset inventory operation status on the screen
     */
    public void resetInventoryDetail() {
        if (getActivity() != null) getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //if (!ActiveProfile.id.equals("1"))
                {
                    if (btnInventory != null && !RFIDController.mIsInventoryRunning && (RFIDController.isBatchModeInventoryRunning == null || !RFIDController.isBatchModeInventoryRunning)) {
                        btnInventory.setIconResource(android.R.drawable.ic_media_play);
                    }
                    if (tvUniqueTags != null) {
                        inventoryData.setVisibility(View.VISIBLE);
                    }
                    if (Application.TAG_LIST_MATCH_MODE) progressView.setVisibility(View.VISIBLE);

                    if (batchModeRR != null) {
                        batchModeRR.setVisibility(View.GONE);
                    }

                    if (Application.TAG_LIST_MATCH_MODE && Application.matchingTags != 0 && Application.missedTags == 0) {
                        progressView.bCompleted = true;
                    }
                }
            }
        });
    }

    @Override
    public void batchModeEventReceived() {
        batchModeEventReceived = true;
        if (btnInventory != null) {
            btnInventory.setIconResource(R.drawable.ic_play_stop);
        }
    }

    @Override
    public void handleTagResponse(InventoryListItem inventoryListItem, boolean isAddedToList) {
        updateTexts();
        if (tagReadRate != null) {
            if (RFIDController.mRRStartedTime == 0) Application.TAG_READ_RATE = 0;
            else
                Application.TAG_READ_RATE = (int) (Application.TOTAL_TAGS / (RFIDController.mRRStartedTime / (float) 1000));
            tagReadRate.setText(Application.TAG_READ_RATE + Constants.TAGS_SEC);
        }
    }
}
