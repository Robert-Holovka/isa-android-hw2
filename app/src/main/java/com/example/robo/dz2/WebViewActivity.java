package com.example.robo.dz2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;

public class WebViewActivity extends AppCompatActivity {

    private String url;
    private String homePage;

    private ActionBar actionBar;
    private ProgressBar loadingBar;
    private WebView mwebView;
    private EditText urlInput;
    private Button btnGO;

    private Menu myMenu;
    private MenuItem btnFavorite;
    private MenuItem btnBack;
    private MenuItem btnForward;

    private HashMap<Integer, String> favoritePages;
    private HashMap<String, Integer> favoritePageTitles;
    private boolean pagePreviousState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        pagePreviousState = true;
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("");
        actionBar.setIcon(R.mipmap.ic_launcher);
        favoritePages = new HashMap<>();
        favoritePageTitles = new HashMap<>();

        loadingBar = findViewById(R.id.loading_bar);
        mwebView = findViewById(R.id.web_view);
        urlInput = findViewById(R.id.url_editText_webView);
        btnGO = findViewById(R.id.btn_GO);
        homePage = "https://www.google.hr";

        loadingBar.setMax(100);

        //Fetch URL from first activity
        url = getIntent().getDataString();

        mwebView.loadUrl(url);
        mwebView.getSettings().setJavaScriptEnabled(true);

        mwebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                updateMenuIcons(false);
                actionBar.setIcon(R.mipmap.ic_launcher);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingBar.setVisibility(View.INVISIBLE);

                //display url of current page in EditText
                String tempUrl = mwebView.getUrl();
                urlInput.setText(tempUrl);
                urlInput.setSelection(tempUrl.length());

                updateMenuIcons(true);
            }
        });
        mwebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                loadingBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                BitmapDrawable drawableIcon = new BitmapDrawable(icon);
                actionBar.setIcon(drawableIcon);
            }
        });

        btnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = urlInput.getText().toString();
                if(url.equals("https://")){
                    Toast.makeText(getApplicationContext(), "Enter valid address!", Toast.LENGTH_SHORT).show();
                }else{
                    mwebView.loadUrl(url);
                }
                hideSoftKeyboard(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.browser_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        btnBack = menu.findItem(R.id.menu_back);
        btnForward = menu.findItem(R.id.menu_forward);
        btnFavorite = menu.findItem(R.id.menu_favoritePage);
        myMenu = menu;

        MenuItem setAsHomePage = menu.findItem(R.id.menu_setAsHomePage);

        SpannableString s = new SpannableString("Set as Homepage");
        int pinkColor = getResources().getColor(R.color.colorAccent);

        s.setSpan(new ForegroundColorSpan(pinkColor), 0, s.length(), 0);
        setAsHomePage.setTitle(s);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemID = item.getItemId();

        switch (itemID) {
            case R.id.menu_back: mwebView.goBack();
                break;
            case R.id.menu_forward: mwebView.goForward();
                break;
            case R.id.menu_reload:  mwebView.reload();
                break;
            case R.id.menu_homePage: mwebView.loadUrl(homePage);
                break;
            case R.id.menu_setAsHomePage: homePage = mwebView.getUrl();
                break;
            case R.id.menu_favoritePage: toggleFavoritePage();
                break;
            default: mwebView.loadUrl(favoritePages.get(itemID));
                break;
        }

        updateMenuIcons(true);
        return true;
    }

    private void toggleFavoritePage(){

        String tempPageTitle = mwebView.getTitle();

        if(favoritePageTitles.containsKey(tempPageTitle)){
            //delete site from favorites
            myMenu.removeItem(favoritePageTitles.get(tempPageTitle));
            favoritePages.remove(favoritePageTitles.get(tempPageTitle));
            favoritePageTitles.remove(tempPageTitle);
            btnFavorite.setIcon(R.drawable.favorite_icon_white);

        } else {
            //add site to favorites
            Random generator = new Random();
            int newId;
            boolean exists = true;

            //Check if ID exists
            do{
                newId = generator.nextInt();
                try{
                    String name = getResources().getResourceName(newId);
                } catch(Resources.NotFoundException e){
                    exists = false;
                }

            } while(exists);

            myMenu.add(Menu.NONE, newId, Menu.NONE, mwebView.getTitle());
            favoritePages.put(newId, mwebView.getUrl());
            favoritePageTitles.put(mwebView.getTitle(), newId);
            btnFavorite.setIcon(R.drawable.favorite_icon_pink);
        }
    }

    private void updateMenuIcons(boolean pageLoaded){

        btnBack.setEnabled(mwebView.canGoBack());
        btnForward.setEnabled(mwebView.canGoForward());

        //Update the favorite page icon
        if(pageLoaded && pagePreviousState){
            btnFavorite.setEnabled(true);
            if(favoritePageTitles.containsKey(mwebView.getTitle())){
                btnFavorite.setIcon(R.drawable.favorite_icon_pink);
            }else {
                btnFavorite.setIcon(R.drawable.favorite_icon_white);
            }
        } else {
            btnFavorite.setEnabled(false);
            btnFavorite.setIcon(R.drawable.favorite_icon_white);
            pagePreviousState = !pagePreviousState;
        }
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
