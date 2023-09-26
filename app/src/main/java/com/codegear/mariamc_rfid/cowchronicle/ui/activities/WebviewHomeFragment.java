package com.codegear.mariamc_rfid.cowchronicle.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.utils.AndroidUtil;
import com.codegear.mariamc_rfid.cowchronicle.utils.Base64Util;
import com.codegear.mariamc_rfid.cowchronicle.utils.Sha256Util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import im.delight.android.webview.AdvancedWebView;

public class WebviewHomeFragment extends Fragment implements AdvancedWebView.Listener  {


    private AppCompatActivity activity;
    private AdvancedWebView mWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setTitle("카우크로니클");

        View view = inflater.inflate(R.layout.activity_webview, null, false);

        mWebView = view.findViewById(R.id.webview);
        mWebView.setListener(getActivity(), this);
        mWebView.setMixedContentAllowed(false);

        try {
            initLoad();
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getActivity(), "인코딩 실패:"+e.toString(), Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) { }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) { }


    private void initLoad() throws UnsupportedEncodingException {

        String loadUrl = "https://marivet.co.kr/login/index.html?url=aHR0cHM6Ly9tYXJpdmV0LmNvLmtyL2FwLw==";

        //로그인한 경우면, url 만들기.
        UserStorage userStorage = UserStorage.getInstance();
        if(userStorage.isLogin()){

            //전달인자 만들기 (ex.usr_id=chalet2cha|mobile_serial=05b136ba7aeb157d)
            String userId = userStorage.getPrevLoginId();
            String mobile_serial = AndroidUtil.getDeviceId(activity);
            String str1 = "usr_id="+userId+"|"+"mobile_serial="+mobile_serial;
            String base64Str1 = Base64Util.encode(str1);

            //검증코드 만들기 (ex.chalet2cha|09-20230914)
            SimpleDateFormat sdf = new SimpleDateFormat("hh-yyyyMMdd");
            String strCurrent = sdf.format(new Date()).toString();
            String str2 = ""+userId+"|"+strCurrent;
            String sha256hex = Sha256Util.encode(str2);

            //URL만들기
            loadUrl = "http://125.141.231.88/ap/chalet_cha.php?sub="+base64Str1+sha256hex;
        }

        mWebView.loadUrl(loadUrl);
    }


}
