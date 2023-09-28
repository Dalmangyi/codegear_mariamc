package com.codegear.mariamc_rfid.cowchronicle.device;

import android.util.Log;

import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.Events;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.STATUS_EVENT_TYPE;
import com.zebra.rfid.api3.TagData;

public class RFIDSingleton implements RfidEventsListener {

    private static final String TAG = "RFIDSingleton";
    private static RFIDSingleton instance;
    public static RFIDSingleton getInstance(){
        if(instance == null){
            instance = new RFIDSingleton();
        }
        return instance;
    }




    private IRFIDSingletonTag irfidSingletonTag = null;



    public RFIDSingleton(){
        init();
    }

    public void init(){
        try {
            if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.Events != null) {
                RFIDController.mConnectedReader.Events.removeEventsListener(this);
                RFIDController.mConnectedReader.Events.addEventsListener(this);
                RFIDController.mConnectedReader.Events.setHandheldEvent(true);
                RFIDController.mConnectedReader.reinitTransport();
            }
        } catch (InvalidUsageException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        } catch (OperationFailureException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        } catch (ClassCastException e) {
            if (e != null && e.getStackTrace().length > 0) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            }
        }
    }

    public void setIRFIDSingletonTag(IRFIDSingletonTag irfidSingletonTag){
        this.irfidSingletonTag = irfidSingletonTag;
    }



    @Override
    public void eventReadNotify(RfidReadEvents e) {
        Log.d(TAG, "RFIDSingleton eventReadNotify");

        if(!Application.useCowChronicleTagging){
            return;
        }

        final int READ_COUNT = 100;
        TagData[] myTags = null;
        if (RFIDController.mConnectedReader != null) {
            if (!RFIDController.mConnectedReader.Actions.MultiTagLocate.isMultiTagLocatePerforming()) {
                myTags = RFIDController.mConnectedReader.Actions.getReadTags(READ_COUNT);
            } else {
                myTags = RFIDController.mConnectedReader.Actions.getMultiTagLocateTagInfo(READ_COUNT);
            }
        }

        int tagCount = (myTags != null ? myTags.length : 0);
        Log.d(TAG, "RFIDSingleton eventReadNotify :"+tagCount);
        if(irfidSingletonTag != null){
            irfidSingletonTag.tags(myTags);
        }
    }

    @Override
    public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
        Log.d(TAG, "Status Notification: " + rfidStatusEvents.StatusEventData.getStatusEventType());
        notificationFromGenericReader(rfidStatusEvents);
    }

    private void notificationFromGenericReader(RfidStatusEvents rfidStatusEvents) {

        if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.BATTERY_EVENT) {
            final Events.BatteryData batteryData = rfidStatusEvents.StatusEventData.BatteryData;
            RFIDController.BatteryData = batteryData;
        }
    }


    public static void deviceDisconnect(){
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected() ) {
            RFIDController.is_disconnection_requested = true;
            try {

                if (RFIDController.mIsInventoryRunning)
                    RFIDController.mConnectedReader.Actions.Inventory.stop();

                RFIDController.mConnectedReader.disconnect();
                RFIDController.mConnectedReader.Dispose();
            } catch (InvalidUsageException e) {
                Log.d(TAG, "Returned SDK Exception");
            } catch (OperationFailureException e) {
                Log.d(TAG, "Returned SDK Exception");
            } catch (Exception e) {
            }
            RFIDController.mConnectedReader = null;
        }
    }
}
