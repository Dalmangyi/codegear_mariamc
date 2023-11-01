package com.codegear.mariamc_rfid.cowchronicle.device;

import android.util.Log;

import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.Events;
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.START_TRIGGER_TYPE;
import com.zebra.rfid.api3.STATUS_EVENT_TYPE;
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE;
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




    private IRFIDSingleton irfidSingleton = null;



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

    public void setIRFIDSingletonTag(IRFIDSingleton irfidSingleton){
        this.irfidSingleton = irfidSingleton;
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
        if(irfidSingleton != null){
            irfidSingleton.tags(myTags);
        }
    }

    @Override
    public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
        Log.d(TAG, "Status Notification: " + rfidStatusEvents.StatusEventData.getStatusEventType());
        notificationFromGenericReader(rfidStatusEvents);
    }

    private void notificationFromGenericReader(RfidStatusEvents rfidStatusEvents) {

        STATUS_EVENT_TYPE statusEventType = rfidStatusEvents.StatusEventData.getStatusEventType();

        //배터리 이벤트
        if (statusEventType == STATUS_EVENT_TYPE.BATTERY_EVENT) {
            final Events.BatteryData batteryData = rfidStatusEvents.StatusEventData.BatteryData;
            RFIDController.BatteryData = batteryData;
        }

        //트리거 이벤트
        else if (statusEventType == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {

            Boolean triggerPressed = false;
            if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                triggerPressed = true;
            }

            if (triggerPressed && isTriggerImmediateOrRepeat(triggerPressed)) {
                if(irfidSingleton != null){
                    irfidSingleton.trigger(true, false);
                }
            } else if (!triggerPressed && isTriggerImmediateOrRepeat(triggerPressed)) {
                if(irfidSingleton != null){
                    irfidSingleton.trigger(false, true);
                }
            }
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
                Log.d(TAG, "Returned SDK InvalidUsageException");
            } catch (OperationFailureException e) {
                Log.d(TAG, "Returned SDK OperationFailureException");
            } catch (Exception e) {
                Log.d(TAG, "Returned SDK Exception");
            }
            RFIDController.mConnectedReader = null;
        }
    }



    public Boolean isTriggerImmediateOrRepeat(Boolean trigPress) {
        if (trigPress
                && RFIDController.settings_startTrigger.getTriggerType().toString().equalsIgnoreCase(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE.toString())
                && (!RFIDController.settings_stopTrigger.getTriggerType().toString().equalsIgnoreCase(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_HANDHELD_WITH_TIMEOUT.toString())))
        {
            return true;
        }
        else if (!trigPress
                && !RFIDController.settings_startTrigger.getTriggerType().toString().equalsIgnoreCase(START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD.toString())
                && (RFIDController.settings_stopTrigger.getTriggerType().toString().equalsIgnoreCase(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE.toString())))
        {
            return true;
        }
        else {
            return false;
        }
    }
}
