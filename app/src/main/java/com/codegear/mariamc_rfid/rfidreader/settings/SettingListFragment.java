package com.codegear.mariamc_rfid.rfidreader.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;

import androidx.fragment.app.ListFragment;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.rfidreader.common.Constants;
import com.codegear.mariamc_rfid.R;

import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.CHARGE_TERMINAL_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_ADVANCED_OPTIONS_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_BEEPER_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_LED_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_PROFILES_TAB;
import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.RFID_REGULATORY_TAB;


/**
 * A list fragment representing a list of Settings.
 */
public class SettingListFragment extends ListFragment {
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private SettingAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SettingListFragment() {
    }

    public static SettingListFragment newInstance() {
        SettingListFragment fragment = new SettingListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new SettingAdapter(getActivity(), R.layout.setting_list, SettingsContent.ITEMS);
        setListAdapter(adapter);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public void onResume() {
        super.onResume();
        // settingsListUpdated();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Intent detailsIntent = new Intent(getActivity(), SettingsDetailActivity.class);
        detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        detailsIntent.putExtra(Constants.SETTING_ITEM_ID, Integer.parseInt(SettingsContent.ITEMS.get(position).id));
        //startActivity(detailsIntent);
        switch (position)
        {
            case 0: //profiles
                ((ActiveDeviceActivity)getActivity()).loadNextFragment(RFID_PROFILES_TAB);
                break;
            case 1: //RFID_advanced_options
                ((ActiveDeviceActivity)getActivity()).loadNextFragment(RFID_ADVANCED_OPTIONS_TAB);
                break;
            case 2:
                ((ActiveDeviceActivity)getActivity()).loadNextFragment(RFID_REGULATORY_TAB);
                break;
            case 3:
                ((ActiveDeviceActivity)getActivity()).loadNextFragment(RFID_BEEPER_TAB);
                break;
            case 4:
                ((ActiveDeviceActivity)getActivity()).loadNextFragment(RFID_LED_TAB);
                break;
            /*case 5:
                ((ActiveDeviceActivity)getActivity()).loadNextFragment(RFID_WIFI_TAB);
                break;*/
            case 5:
                ((ActiveDeviceActivity)getActivity()).loadNextFragment(CHARGE_TERMINAL_TAB);
                break;
            /*case 7:
                ((ActiveDeviceActivity)getActivity()).loadNextFragment(USB_MIFI_TAB);
                break;*/

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

}
