package com.codegear.mariamc_rfid.cowchronicle.activities.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ResLogin {
    @SerializedName("bigo")
    private String bigo;
    @SerializedName("cmpy")
    private String cmpy;
    @SerializedName("farm_list")
    private String[] res_farm_list;
    @SerializedName("membership_nm")
    private String membership_nm;
    @SerializedName("success")
    private int success;
    @SerializedName("usr_nm")
    private String usr_nm;

    private ArrayList<Map<String, String>> mFarmList = new ArrayList<>();

    public void convertData(){

        //res_farm_list => farm_list
        if (res_farm_list != null && res_farm_list.length > 0){
            mFarmList.clear();
            ObjectMapper mapper = new ObjectMapper();
            for (String farm:res_farm_list){
                try {
                    JSONArray jsonArray = new JSONArray("["+farm+"]");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        Map<String, String> map = mapper.readValue(jsonObj.toString(), Map.class);
                        mFarmList.add(map);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (JsonMappingException e) {
                    throw new RuntimeException(e);
                } catch (JsonParseException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
