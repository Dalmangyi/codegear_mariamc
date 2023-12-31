package com.codegear.mariamc_rfid.cowchronicle.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.codegear.mariamc_rfid.R;

import im.delight.android.webview.AdvancedWebView;

public class WebviewCowDetailFragment extends Fragment implements AdvancedWebView.Listener  {


    private AppCompatActivity activity;
    private AdvancedWebView mWebView;

    public String cowIdNum;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setTitle("전자이표 상세 ("+cowIdNum+")");

        View view = inflater.inflate(R.layout.activity_webview, null, false);

        mWebView = view.findViewById(R.id.webview);
        mWebView.setListener(getActivity(), this);
        mWebView.setMixedContentAllowed(false);
        
        mWebView.loadUrl("http://marivet.co.kr/v2/ahebf/chalet/fetch_data.php?var1="+ cowIdNum);

        return view;
    }


    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) { }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) { }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) { }

}
