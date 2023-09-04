package com.codegear.mariamc_rfid.rfidreader.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.BatteryStatistics;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

import java.util.ArrayList;
import java.util.List;

public class BatteryStatsFragment extends Fragment {

    private static final String TAG = "BatteryStatsFragment";
    List<BatteryStatisticsData> batteryTitleList = new ArrayList<>();
    BatteryStatistics batteryStats = new BatteryStatistics();

    public static BatteryStatsFragment newInstance() {
        return new BatteryStatsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_battery_stats, container, false);
        RecyclerView batteryRecyclerView = view.findViewById(R.id.battery_stats_recyclerview);
        BatteryStaticsAdapter batteryStaticsAdapter = new BatteryStaticsAdapter(batteryTitleList);
        batteryRecyclerView.setAdapter(batteryStaticsAdapter);
        batteryRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAndUpdateBatteryStats();
    }

    private void fetchAndUpdateBatteryStats() {

        if (RFIDController.mConnectedReader == null) {
            Toast.makeText(getActivity(), "연결된 장치가 없습니다. ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!RFIDController.mIsInventoryRunning) {
            try {
                if (RFIDController.mConnectedReader.Config != null)
                    batteryStats = RFIDController.mConnectedReader.Config.getBatteryStats();
                else return;
            } catch (InvalidUsageException | NullPointerException e) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            } catch (OperationFailureException e) {
                if (e.getResults() == RFIDResults.RFID_OPERATION_IN_PROGRESS) {
                    Toast.makeText(getActivity(), "Operation in progress, Battery statistics cannot be fetched", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Inventory is in progress, Battery statistics cannot be fetched", Toast.LENGTH_SHORT).show();
        }
        String header = "배터리 자산 정보";
        List<String> itemTitle = new ArrayList<>();
        List<String> itemValue = new ArrayList<>();
        itemTitle.add("제조일자");
        itemValue.add(batteryStats.getManufactureDate());
        itemTitle.add("모델 번호");
        itemValue.add(batteryStats.getModelNumber());
        itemTitle.add("배터리 ID");
        itemValue.add(batteryStats.getBatteryId());


        String header1 = "배터리 수명 통계";
        List<String> itemTitle1 = new ArrayList<>();
        List<String> itemValue1 = new ArrayList<>();
        itemTitle1.add("건강 상태");
        itemValue1.add(batteryStats.getHealth() + "%");
        itemTitle1.add("소비된 충전 주기");
        itemValue1.add(String.valueOf(batteryStats.getCycleCount()));

        String header2 = "배터리 상태";
        List<String> itemTitle2 = new ArrayList<>();
        List<String> itemValue2 = new ArrayList<>();
        itemTitle2.add("충전 퍼센트");
        itemValue2.add(batteryStats.getPercentage() + "%");
        itemTitle2.add("충전 상태");
        itemValue2.add(String.valueOf(batteryStats.getChargeStatus()));

        String header3 = "배터리 온도";
        List<String> itemTitle3 = new ArrayList<>();
        List<String> itemValue3 = new ArrayList<>();
        itemTitle3.add("현재 온도");
        itemValue3.add(batteryStats.getTemperature() + "\u00B0" + "C");


        batteryTitleList.add(new BatteryStatisticsData(header, itemTitle, itemValue));
        batteryTitleList.add(new BatteryStatisticsData(header1, itemTitle1, itemValue1));
        batteryTitleList.add(new BatteryStatisticsData(header2, itemTitle2, itemValue2));
        batteryTitleList.add(new BatteryStatisticsData(header3, itemTitle3, itemValue3));

    }

}