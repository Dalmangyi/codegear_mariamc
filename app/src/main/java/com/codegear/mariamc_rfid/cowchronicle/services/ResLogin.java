package com.codegear.mariamc_rfid.cowchronicle.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ResLogin extends ResCommon {
    @SerializedName("bigo")
    public String bigo;
    @SerializedName("cmpy")
    public String cmpy;
    @SerializedName("farm_list")
    public String[] res_farm_list;
    @SerializedName("membership_nm")
    public String membership_nm;
    @SerializedName("success")
    public int success;
    @SerializedName("usr_nm")
    public String usr_nm;

    public ArrayList<Map<String, String>> mConvertedFarmList = new ArrayList<>();

    @Override
    public void convertData(){

        //res_farm_list => farm_list
        if (res_farm_list != null && res_farm_list.length > 0){
            mConvertedFarmList.clear();
            ObjectMapper mapper = new ObjectMapper();
            for (String farm:res_farm_list){
                try {
                    //TODO - response 문제가 많음. (서버측 json 변환에 신경써야됨)
                    String enoughFarm = farm
                            .replaceAll("\":", "\":\"")
                            .replaceAll("\\},\\{", "\"\\},\\{")
                            .replaceAll(",\"", "\",\"")
                            .replaceAll("\":\" \\}", "\":\"\"\\}");

                    String lastString = enoughFarm.substring(enoughFarm.length()-2, enoughFarm.length());
                    if(!lastString.equals("}")){
                        enoughFarm = enoughFarm.substring(0, enoughFarm.length()-1) + "\"}";
                    }

                    JSONArray jsonArray = new JSONArray("["+enoughFarm+"]");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        Map<String, String> map = mapper.readValue(jsonObj.toString(), Map.class);
                        mConvertedFarmList.add(map);
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
