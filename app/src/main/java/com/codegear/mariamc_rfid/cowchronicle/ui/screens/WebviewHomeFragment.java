package com.codegear.mariamc_rfid.cowchronicle.ui.screens;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codegear.mariamc_rfid.BuildConfig;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.cowchronicle.consts.BottomNavEnum;
import com.codegear.mariamc_rfid.cowchronicle.storage.UserStorage;
import com.codegear.mariamc_rfid.cowchronicle.utils.Base64Util;
import com.codegear.mariamc_rfid.cowchronicle.utils.Sha256Util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import im.delight.android.webview.AdvancedWebView;

public class WebviewHomeFragment extends Fragment implements AdvancedWebView.Listener  {


    private AppCompatActivity activity;

    private AppCompatEditText etUrl;
    private AdvancedWebView mWebView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btnGo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setTitle("카우크로니클");

        View view = inflater.inflate(R.layout.activity_webview, null, false);

        //디버그 모드일때만, 주소창 보이게 설정
        if(BuildConfig.DEBUG){
            View llUrlContainer = view.findViewById(R.id.llUrlContainer);
            llUrlContainer.setVisibility(View.VISIBLE);
        }

        etUrl = view.findViewById(R.id.etUrl);
        etUrl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    //키패드 내리기
                    InputMethodManager imm = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etUrl.getWindowToken(), 0);

                    //페이지 이동
                    String newUrl = etUrl.getText().toString();
                    goUrl(newUrl);
                    return true;
                }

                return false;
            }
        });

        btnGo = view.findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUrl = etUrl.getText().toString();
                goUrl(newUrl);
            }
        });

        mWebView = view.findViewById(R.id.webview);
        mWebView.setListener(getActivity(), this);
        mWebView.setMixedContentAllowed(false);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 특정 도메인에 대해서만 단말기의 기본 브라우저로 띄움
                if (url.contains("www.aiak.or.kr") || url.contains("ct.wwsires.com") || url.contains("www.mtrace.go.kr")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                } else {
                    // 특정 도메인에 해당하지 않으면 기본브라우저로 실행
                    view.loadUrl(url);
                    return true;
                }
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        try {
            initLoad();
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getActivity(), "인코딩 실패:"+e.toString(), Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        UserStorage.getInstance().setBottomNavItem(BottomNavEnum.BN_COW_CHRONICLE_WEBVIEW);
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

        String loadUrl = "http://marivet.co.kr/_api_login.php";

        //로그인한 경우면, url 만들기.
        UserStorage userStorage = UserStorage.getInstance();
        if(userStorage.isLogin()){

            //2023.10.12 전달인자 만들기 (ex.usr_id=chalet2cha)
            String userId = userStorage.getPrevLoginId();
            String str1 = "usr_id="+userId;
            String base64Str1 = Base64Util.encode(str1); //BASE64

            //2023.10.12 검증코드 만들기 (ex.chalet2cha|21-20231012)
            SimpleDateFormat sdf = new SimpleDateFormat("HH-yyyyMMdd");
            String strCurrent = sdf.format(new Date()).toString();
            String str2 = ""+userId+"|"+strCurrent;
            String sha256hex = Sha256Util.encode(str2); //SHA256
            String sha256hexUpperCase = sha256hex.toUpperCase();

            //URL만들기
            loadUrl = "http://marivet.co.kr/_api_login.php?sub="+base64Str1+sha256hexUpperCase;
        }

        goUrl(loadUrl);
    }

    private void goUrl(String url){
        etUrl.setText(url);
        mWebView.loadUrl(url);
    }
}
