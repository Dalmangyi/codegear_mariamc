package com.codegear.mariamc_rfid.cowchronicle.activities.services;

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

public class ResCowList extends ResCommon  {

//    {
//        "rowcnt ": 115,
//        "success": 1,
//        "data" : [
//            {
//                "COW_ID_NUM":"002173840724",
//                "TAGNO":"541100000000465",
//                "KIND":"한우",
//                "SEX":"거세",
//                "SNM":"4072",
//                "BIRTHDT":"2022-05-04",
//                "MONTHS":"16",
//                "PRTY":" ",
//                "PRN_STTS":" ",
//                "BREDKND":"비육우",
//                "REGNO":" "
//            }
//        ]
//    }

    @SerializedName("rowcnt") public int rowcnt;
    @SerializedName("success") public int success;
    @SerializedName("data") public ArrayList<Map<String, String>> data;

    public ArrayList<Map<String, String>> mConvertedData;

    @Override
    public void convertData() {
        if(data != null && data.size() > 0){

            ObjectMapper mapper = new ObjectMapper();
            try {
                JSONArray jsonArray = new JSONArray("["+data+"]");
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    Map<String, String> map = mapper.readValue(jsonObj.toString(), Map.class);
                    mConvertedData.add(map);
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
