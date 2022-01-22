package com.github.dodobest.attendhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import java.text.MessageFormat;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebviewActivity";
    private static final int URL = 1;
    private static final int JS = 2;
    private static final String ID = "id";
    private static final String PW = "1234";

    WebView webView;
    Handler handler = new Handler();
    WebViewThread thread;
    //    private TextView textViewResult;
    private EditText urlText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

//        textViewResult = findViewById(R.id.resultView);
        urlText = findViewById(R.id.urlText);


        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new ViewClient());
        webView.setWebChromeClient(new ChromeClient());

        Button button = findViewById(R.id.button);
        // 람다로 변환하기
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus()!=null) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                String url = urlText.getText().toString();
                Log.d(TAG, "onClick Start");
//                webView.loadUrl(url);
                thread = new WebViewThread(url);
                thread.start();
            }
        });

    }


    private class ViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "WebView get URL which is "+url);

            Message message = Message.obtain();
            message.obj = "Done";
            message.what = URL;
            thread.processHandler.sendMessage(message);
        }
    }

    class ChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            String msg = consoleMessage.message();
            if (msg.contains("Mixed Content")) return super.onConsoleMessage(consoleMessage);

            Log.d(TAG, "console Message: "+ msg);

            Message message = Message.obtain();
            message.obj = msg;
            message.what = JS;
            thread.processHandler.sendMessage(message);

            return super.onConsoleMessage(consoleMessage);
        }
    }

    class WebViewThread extends Thread {
        String url;
        volatile ProcessHandler processHandler = new ProcessHandler();
        Looper looper;

        public WebViewThread() {
            this("");
        }

        public WebViewThread(String url) {
            super();

            if(!url.startsWith("http://") && !url.startsWith("https://"))
                url = "https://" + url;
            this.url = url;
        }

        public void run() {
            Looper.prepare();

            loadUrlWithHandler(url);
            Log.d(TAG, "URL is "+url);

            if (url.contains("cgv")) {
                String path = "//*[@id=\"popAdEvent\"]/div/div/div/div/div[2]/a";
                clickElementById(findElementByXpath(path));

                path = "//*[@id=\"a_footer_login_btn\"]";
                clickElementById(findElementByXpath(path));

                try{
                    Thread.sleep(1500);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }

                path = findElementByXpath("//*[@id=\"mainContentPlaceHolder_Login_tbUserID\"]");
                sendKeyToElementById(path, ID);

                path = findElementByXpath("//*[@id=\"mainContentPlaceHolder_Login_tbPassword\"]");
                sendKeyToElementById(path, PW);

                path = "//*[@id=\"ContainerView\"]/div/div/div/div/div[4]/button";
                clickElementById(findElementByXpath(path));
            }
        }

        @JavascriptInterface
        public void callback(String param) {

        }

        private void waitUntilElementLocated(String scriptSentence) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(scriptSentence);
                }
            });

            looper = Looper.myLooper();
            Looper.loop();
        }

        private void getCurrentUrl() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, webView.getUrl());
                }
            });
        }

        private void loadUrlWithHandler(String url) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url);
                }
            });



            looper = Looper.myLooper();
            Looper.loop();
        }

        private String makeJavascriptSentence(String sentence, Boolean needFormatSetting) {
            if (needFormatSetting) {
                return "javascript:(function()'{'" + sentence + "'}')()";
            } else {
                return "javascript:(function(){" + sentence + "})()";
            }
        }

        private void clickElementById(String elementId) {
            String sentence = "l={0}const e=new Event(''click'');l.dispatchEvent(e);";
            sentence = makeJavascriptSentence(sentence, true);

            String executeCode = MessageFormat.format(sentence, elementId);
            Log.d(TAG, executeCode);
            loadUrlWithHandler(executeCode);
        }

        private void sendKeyToElementById(String elementId, String key) {
            String sentence = "l={0}l.value =\"" + key + "\";";
            sentence = makeJavascriptSentence(sentence, true);

            String executeCode = MessageFormat.format(sentence, elementId);
            Log.d(TAG, executeCode);
            loadUrlWithHandler(executeCode);
        }

        private String findElementByXpath(String path) {
            return (String)MessageFormat.format("document.evaluate(''{0}'', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
                    , path);
        }

        class ProcessHandler extends Handler {

            public void handleMessage(Message msg) {
                final String output = (String)msg.obj;
                if (msg.what == URL && output.contains("Done")) {
                    Log.d(TAG, "LoadURL Done");
                    if (looper != null) looper.quit();
                } else if (msg.what == JS && !output.contains("null")) {
                    Log.d(TAG, "JS Done");
                    if (looper != null) looper.quit();
                }
            }
        }
    }
}