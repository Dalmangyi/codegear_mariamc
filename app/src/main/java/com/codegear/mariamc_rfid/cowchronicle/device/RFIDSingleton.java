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
                RFIDController.mConnectedReader.Events.setInventoryStartEvent(true);
                RFIDController.mConnectedReader.Events.setInventoryStopEvent(true);
                RFIDController.mConnectedReader.reinitTransport();
                RFIDController.settings_startTrigger = RFIDController.mConnectedReader.Config.getStartTrigger();
                RFIDController.settings_stopTrigger = RFIDController.mConnectedReader.Config.getStopTrigger();
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
        TagData[] myTags;
        if (RFIDController.mConnectedReader != null) {
            if (!RFIDController.mConnectedReader.Actions.MultiTagLocate.isMultiTagLocatePerforming()) {
                myTags = RFIDController.mConnectedReader.Actions.getReadTags(READ_COUNT);
            } else {
                myTags = RFIDController.mConnectedReader.Actions.getMultiTagLocateTagInfo(READ_COUNT);
            }
        } else {
            myTags = null;
        }

        int tagCount = (myTags != null ? myTags.length : 0);
        Log.d(TAG, "RFIDSingleton eventReadNotify :"+tagCount);
        if(myTags != null && irfidSingleton != null){
            new Thread(()-> irfidSingleton.tags(myTags)).start();
        }
    }

    @Override
    public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
        Log.d(TAG, "Status Notification: " + rfidStatusEvents.StatusEventData.getStatusEventType());
        notificationFromGenericReader(rfidStatusEvents);
    }

    private void notificationFromGenericReader(RfidStatusEvents rfidStatusEvents) {

        STATUS_EVENT_TYPE statusEventType = rfidStatusEvents.StatusEventData.getStatusEventType();
        Log.d(TAG, "Reader Status Event : "+statusEventType.toString());


        //배터리 이벤트
        if (statusEventType == STATUS_EVENT_TYPE.BATTERY_EVENT) {
            final Events.BatteryData batteryData = rfidStatusEvents.StatusEventData.BatteryData;
            RFIDController.BatteryData = batteryData;
        }

        //트리거 이벤트
        else if (statusEventType == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {

            Log.d(TAG, "trigger event:"+rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent());

            if(irfidSingleton != null){

                //버튼을 눌렀는지, 땟는지 확인.
                Boolean triggerPressed = false;
                if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED)
                    triggerPressed = true;

                //트리거 버튼 눌렀을때와 모드에 따른 반응.
                if (triggerPressed && isTriggerImmediateOrRepeat(triggerPressed)) {
                    irfidSingleton.trigger(true, false);
                }
                else if(!triggerPressed && isTriggerImmediateOrRepeat(triggerPressed)){
                    irfidSingleton.trigger(false, true);
                }
            }
        }

        //인벤토리 이벤트 (시작)
        else if (statusEventType == STATUS_EVENT_TYPE.INVENTORY_START_EVENT) {
            if(irfidSingleton != null){
                irfidSingleton.inventory(true, false, false);
            }
        }
        //인벤토리 이벤트 (종료)
        else if (statusEventType == STATUS_EVENT_TYPE.INVENTORY_STOP_EVENT) {
            if (!RFIDController.getInstance().getRepeatTriggers()) {
                if (RFIDController.mIsInventoryRunning) RFIDController.isInventoryAborted = true;
                else if (RFIDController.isLocatingTag) RFIDController.isLocationingAborted = true;


                if (RFIDController.mIsInventoryRunning) {
                    if (RFIDController.isInventoryAborted) {
                        RFIDController.mIsInventoryRunning = false;
                        RFIDController.isInventoryAborted = true; //false
                        RFIDController.isTriggerRepeat = null;
                        if(irfidSingleton != null){
                            irfidSingleton.inventory(false, false, true);
                            return;
                        }
                    }
                }

            }

            if(irfidSingleton != null){
                irfidSingleton.inventory(false, true, false);
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



    public Boolean isTriggerImmediateOrRepeat(Boolean triggerPress) {

        try{
            if(RFIDController.settings_startTrigger == null){
                RFIDController.settings_startTrigger = RFIDController.mConnectedReader.Config.getStartTrigger();
            }
            if(RFIDController.settings_stopTrigger == null){
                RFIDController.settings_stopTrigger = RFIDController.mConnectedReader.Config.getStopTrigger();
            }


            if(RFIDController.settings_startTrigger != null && RFIDController.settings_stopTrigger != null){
                String strStartTriggerType = RFIDController.settings_startTrigger.getTriggerType().toString();
                String strStopTriggerType = RFIDController.settings_stopTrigger.getTriggerType().toString();

                //눌렸을때,
                if (triggerPress){
                    if(strStartTriggerType.equalsIgnoreCase(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE.toString())
                        && (!strStopTriggerType.equalsIgnoreCase(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_HANDHELD_WITH_TIMEOUT.toString()))
                    ){
                        return true;
                    }
                }
                //땟을때,
                else {
                    if(!strStartTriggerType.equalsIgnoreCase(START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD.toString())
                        && (strStopTriggerType.equalsIgnoreCase(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE.toString()))
                    ){
                        return true;
                    }
                }

            }

        }
        catch (Exception e){
        }


        return false;
    }
}
