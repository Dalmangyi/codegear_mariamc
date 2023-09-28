package com.codegear.mariamc_rfid.rfidreader.settings;

import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.codegear.mariamc_rfid.ActiveDeviceActivity;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.home.RFIDBase;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.ENUM_NEW_KEYLAYOUT_TYPE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

import static com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController.mConnectedReader;


public class KeyRemapFragment extends Fragment {
    Spinner upperspinner, lowerspinner;
    public static String upper, lower;
    public static int upperTval, lowerTval;
    ArrayAdapter<String> adapter;
    protected static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
    private static final String TAG = "KeyRemapFragment";
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor editor;
    ActiveDeviceActivity mActivity;
    ENUM_NEW_KEYLAYOUT_TYPE UpperTrigger, LowerTrigger;

    String[] items = {"RFID", "Sled scanner", "Terminal scanner", "Scan notification", "No action"};
    Button apply;


    public static KeyRemapFragment newInstance() {
        KeyRemapFragment fragment = new KeyRemapFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String keylayoutType;
        super.onCreate(savedInstanceState);
        final View rootview = inflater.inflate(R.layout.activity_keymap_select, container, false);
        //getSupportActionBar().setTitle(R.string.KeyMapping);

        try {
            if ((keylayoutType = RFIDController.mConnectedReader.Config.getKeylayoutType()) != null) {
                upperTval = mConnectedReader.Config.getUpperTriggerValue(keylayoutType).getEnumValue();
                lowerTval = mConnectedReader.Config.getLowerTriggerValue(keylayoutType).getEnumValue();
            }
        } catch (InvalidUsageException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        } catch (OperationFailureException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        }
        upperspinner = rootview.findViewById(R.id.upperTrigger);
        lowerspinner = rootview.findViewById(R.id.lowerTrigger);
        apply = rootview.findViewById(R.id.applyKeyremap);
      /*  if (Application.RFD_DEVICE_MODE == DEVICE_STD_MODE)
            items = itemsStandard;
        else
            items = itemsPremiumPlus;
*/
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upperspinner.setAdapter(adapter);
        lowerspinner.setAdapter(adapter);


        upperspinner.setSelection(upperTval);
        lowerspinner.setSelection(lowerTval);


        upperspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();


                if (mConnectedReader == null) return;
                if (!RFIDController.mIsInventoryRunning) {
                    switch (position) {
                        case 0:
                            UpperTrigger = ENUM_NEW_KEYLAYOUT_TYPE.RFID;

                            break;
                        case 1:
                            UpperTrigger = ENUM_NEW_KEYLAYOUT_TYPE.SLED_SCAN;

                            break;
                        case 2:
                            UpperTrigger = ENUM_NEW_KEYLAYOUT_TYPE.TERMINAL_SCAN;

                            break;
                        case 3:
                            UpperTrigger = ENUM_NEW_KEYLAYOUT_TYPE.SCAN_NOTIFY;

                            break;
                        case 4:
                            UpperTrigger = ENUM_NEW_KEYLAYOUT_TYPE.NO_ACTION;
                            break;
                    }
                    //  upperTval = position;
                } else {
                    Toast.makeText(parent.getContext(), "인벤토리 진행 중에는 트리거 매핑이 허용되지 않습니다.", Toast.LENGTH_SHORT).show();
                    //  position = Application.keyLayoutType;
                }
                //  upperspinner.setItemChecked(position, true);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lowerspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();


                if (mConnectedReader == null) return;
                if (!RFIDController.mIsInventoryRunning) {
                    switch (position) {
                        case 0:
                            LowerTrigger = ENUM_NEW_KEYLAYOUT_TYPE.RFID;

                            break;
                        case 1:
                            LowerTrigger = ENUM_NEW_KEYLAYOUT_TYPE.SLED_SCAN;

                            break;
                        case 2:
                            LowerTrigger = ENUM_NEW_KEYLAYOUT_TYPE.TERMINAL_SCAN;

                            break;
                        case 3:
                            LowerTrigger = ENUM_NEW_KEYLAYOUT_TYPE.SCAN_NOTIFY;

                            break;
                        case 4:
                            LowerTrigger = ENUM_NEW_KEYLAYOUT_TYPE.NO_ACTION;
                            break;
                    }
                    //   lowerTval = position;

                } else {
                    Toast.makeText(parent.getContext(), "인벤토리 진행 중에는 트리거 매핑이 허용되지 않습니다.", Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!RFIDController.mIsInventoryRunning) {
                    try {
                        RFIDResults result = mConnectedReader.Config.setKeylayoutType(UpperTrigger, LowerTrigger);
                        if (result == RFIDResults.RFID_API_SUCCESS) {

                            // Log.d("getKeylayoutType","Keymap val = "+mConnectedReader.Config.getKeylayoutType() );
                            Toast.makeText(getContext(), "트리거 선택이 성공적으로 적용되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "트리거 선택 설정이 허용되지 않습니다.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (InvalidUsageException e) {
                        if (e != null && e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                        Toast.makeText(getContext(), "잘못된 사용법", Toast.LENGTH_SHORT).show();
                    } catch (OperationFailureException e) {
                        if (e != null && e.getStackTrace().length > 0) {
                            Log.e(TAG, e.getStackTrace()[0].toString());
                        }
                        Toast.makeText(getContext(), "장치에 재매핑이 설정되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "인벤토리 진행 중에는 트리거 매핑이 허용되지 않습니다.", Toast.LENGTH_SHORT).show();
                }


            }
        });


        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        // RFIDBase.activityResumed();
    }

    @Override
    public void onPause() {
        super.onPause();
        // RFIDBase.removeReaderDeviceFoundHandler(this);
    }

    @Override
    public void onDestroy() {
        RFIDBase.getInstance().resetReaderstatuscallback();

        super.onDestroy();
    }
}