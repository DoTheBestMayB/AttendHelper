package com.github.dodobest.attendhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import java.text.MessageFormat;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";
    private static final String SC = "script Done";
    private static final int URL = 1;
    private static final int JS = 2;
    private static final String ID = "idid";
    private static final String PW = "123456";

    WebView webView;
    Handler handler = new Handler();
    WebViewThread thread;
    //    private TextView textViewResult;
    private EditText urlText;

    // 의도하지 않은 lock release 방지용 변수
    private Boolean isPageRelease = false;
    private Boolean isConsoleRelease = false;

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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus()!=null) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                String url = urlText.getText().toString();
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

            if (isPageRelease) {
                thread.releaseLock();
                isPageRelease = false;
            }
        }
    }

    class ChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            String msg = consoleMessage.message();
            if (!isConsoleRelease || msg.contains("Mixed Content")) {
                return super.onConsoleMessage(consoleMessage);
            }

            Message message = Message.obtain();
            message.what = JS;

            if (msg.contains(SC)) {
                message.obj = "Done";
            } else {
                message.obj = "Again";
            }

            isConsoleRelease = false;
//            Log.d(TAG, "console Message: "+ msg);
            thread.processHandler.sendMessage(message);

            return super.onConsoleMessage(consoleMessage);
        }
    }

    class WebViewThread extends Thread {
        String url;
        String executeCode;
        ProcessHandler processHandler = new ProcessHandler();

        private ReentrantLock lock = new ReentrantLock();
        private Condition forThr = lock.newCondition();

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
            isPageRelease = true;
            loadUrl(url);
            Log.d(TAG, "URL is "+url);

            if (getCurrentUrl().contains("cgv")) {
                Log.d(TAG, "팝업 닫기 클릭");
                String path = "//*[@id=\"popAdEvent\"]/div/div/div/div/div[2]/a";
                clickElementById(findElementByXpath(path));

                Log.d(TAG, "로그인 버튼 클릭");
                path = "//*[@id=\"a_footer_login_btn\"]";
                clickElementById(findElementByXpath(path));

                Log.d(TAG, "아이디 입력");
                path = findElementByXpath("//*[@id=\"mainContentPlaceHolder_Login_tbUserID\"]");
                sendKeyToElementById(path, ID);

                Log.d(TAG, "비밀번호 입력");
                path = findElementByXpath("//*[@id=\"mainContentPlaceHolder_Login_tbPassword\"]");
                sendKeyToElementById(path, PW);

                Log.d(TAG, "로그인 버튼 클릭");
                path = "//*[@id=\"ContainerView\"]/div/div/div/div/div[4]/button";
                clickElementById(findElementByXpath(path));
            }
        }

        class ProcessHandler extends Handler {
            public void handleMessage(Message msg) {
                final String output = (String)msg.obj;
                if (msg.what == JS && output.contains("Again")) {
//                    Log.d(TAG, "JS Again");

                    isConsoleRelease = true;
                    loadUrlWithoutLock(executeCode, 500);
                } else {
                    releaseLock();
                }
            }
        }

        private String getCurrentUrl() {
            lock.lock();
            try {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        url = webView.getUrl();
                        thread.releaseLock();
                    }
                });
                try {
                    forThr.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return url;
            } finally {
                lock.unlock();
            }
        }

        private void loadUrl(String url) {
            lock.lock();
            try {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(url);
                    }
                });

                try {
                    forThr.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                lock.unlock();
            }
        }

        private void loadUrlWithoutLock(String sentence, int delayTime) {
            if (delayTime > 0) {
                try {
                    Thread.sleep(delayTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(sentence);
                }
            });
        }

        private String makeJavascriptSentence(String sentence, Boolean needFormatSetting) {
            if (needFormatSetting) {
                return "javascript:(function(callback)'{'" + sentence + "callback = function () '{'console.log(''" + SC + "'')'}';callback();'}')()";
            } else {
                return "javascript:(function(callback){" + sentence + "callback = function () '{'console.log(''" + SC + "'')'}';callback();'}')()";
            }
        }

        private void clickElementById(String elementId) {
            String sentence = "l={0}const e=new Event(''click'');l.dispatchEvent(e);";
            sentence = makeJavascriptSentence(sentence, true);

            executeCode = MessageFormat.format(sentence, elementId);
            isConsoleRelease = true;
            loadUrl(executeCode);
        }

        private void sendKeyToElementById(String elementId, String key) {
            String sentence = "l={0}l.value =\"" + key + "\";";
            sentence = makeJavascriptSentence(sentence, true);

            executeCode = MessageFormat.format(sentence, elementId);
            isConsoleRelease = true;
            loadUrl(executeCode);
        }

        private String findElementByXpath(String path) {
            return MessageFormat.format("document.evaluate(''{0}'', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;"
                    , path);
        }

        private void releaseLock() {
            lock.lock();
            try {
                forThr.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}