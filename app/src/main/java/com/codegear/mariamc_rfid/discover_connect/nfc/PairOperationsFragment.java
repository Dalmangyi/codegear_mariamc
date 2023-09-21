package com.codegear.mariamc_rfid.discover_connect.nfc;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.DeviceDiscoverActivity;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.InitReadersListFragment;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.ScanAndPairFragment;
import com.codegear.mariamc_rfid.rfidreader.reader_connection.ScanBarcodeAndPairFragment;
import com.google.android.material.tabs.TabLayout;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.ReaderDevice;

import static com.codegear.mariamc_rfid.scanner.helpers.ActiveDeviceAdapter.READER_LIST_TAB;


public class PairOperationsFragment extends Fragment {

    private ViewPager viewPager;
    private PairOperationAdapter mAdapter;
    TabLayout tabLayout;
    static Dialog scan_and_pair_help;
    LinearLayout scanAndPairLayout, BarcodePairLayout;
    public static Fragment currentFragment;

    public PairOperationsFragment() {
    }

    public static PairOperationsFragment newInstance() {
        return new PairOperationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_pair_device, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_devicepair, menu);
        menu.findItem(R.id.action_readerlist).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String className = getActivity().getClass().getName();
                if (className.equals("com.codegear.mariamc_rfid.DeviceDiscoverActivity")) {
                    Fragment fragment = InitReadersListFragment.getInstance();
                    ((DeviceDiscoverActivity) getActivity()).switchToFragment(fragment);
                } else if (className.equals("com.codegear.mariamc_rfid.ActiveDeviceActivity"))
                    ((ActiveDeviceActivity) getActivity()).loadNextFragment(READER_LIST_TAB);
                return true;

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_and_pair_help: {
                scan_and_pair_help = new Dialog(getActivity());
                scan_and_pair_help.requestWindowFeature(Window.FEATURE_NO_TITLE);
                scan_and_pair_help.setContentView(R.layout.dialog_pair_operations_help);
                scan_and_pair_help.setCancelable(false);
                scan_and_pair_help.setCanceledOnTouchOutside(false);
                scanAndPairLayout = (LinearLayout) scan_and_pair_help.findViewById(R.id.dialogScanAndPairlayout);
                BarcodePairLayout = scan_and_pair_help.findViewById(R.id.CordlessPair);
                scan_and_pair_help.show();
                Fragment fragmentInFrame = mAdapter.getFragment(viewPager.getCurrentItem());

                if (fragmentInFrame instanceof ScanAndPairFragment) {
                    scanAndPairLayout.setVisibility(View.VISIBLE);
                    BarcodePairLayout.setVisibility(View.GONE);
                }

                if (fragmentInFrame instanceof ScanBarcodeAndPairFragment) {
                    scanAndPairLayout.setVisibility(View.GONE);
                    BarcodePairLayout.setVisibility(View.VISIBLE);
                }


                TextView declineButton = (TextView) scan_and_pair_help.findViewById(R.id.btn_ok);
                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Close dialog
                        scan_and_pair_help.dismiss();
                        scan_and_pair_help = null;
                    }
                });
            }
            return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initialization
        mAdapter = new PairOperationAdapter(getActivity(), getActivity().getSupportFragmentManager());
        viewPager = (ViewPager) getActivity().findViewById(R.id.pairDevicePager);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(1);
        tabLayout = getActivity().findViewById(R.id.pair_device_tabs);
        tabLayout.setupWithViewPager(viewPager);
        mAdapter.notifyDataSetChanged();


        if(getActivity() instanceof DeviceDiscoverActivity){
            ((DeviceDiscoverActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.device_register));
        }else if(getActivity() instanceof ActiveDeviceActivity){
            ((ActiveDeviceActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.device_register));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mAdapter.notifyDataSetChanged();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentFragment = mAdapter.getFragment(viewPager.getCurrentItem());
                switch (position) {
                    case 0:
                        //mAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        //   mAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        // Fragment fragment = ScanBarcodeAndPairFragment.newInstance(getActivity(),dialog);
                        if (isResumed()) {
                            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
                            getActivity().getSupportFragmentManager().executePendingTransactions();
                            mAdapter.notifyDataSetChanged();
                        }

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        currentFragment = null;
        super.onDestroy();
    }

    public void RFIDReaderAppeared(ReaderDevice device) {
        if (RFIDController.readersList.contains(device) == false)
            RFIDController.readersList.add(device);
    }


}
