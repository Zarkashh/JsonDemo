package com.example.jsondemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    private WebView browser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        browser = (WebView) findViewById(R.id.webkit);
        try {
            URL pageURL = new URL(getString(R.string.url));
            HttpURLConnection con =
                    (HttpURLConnection)pageURL.openConnection();

            String jsonString = getString(R.string.json);

            JsonThread myTask = new JsonThread(this, con, jsonString);
            Thread t1 = new Thread(myTask, "JSON Thread");
            t1.start();
        } catch (IOException e) {e.printStackTrace(); }
    }
}

    class JasonThread implements Runnable
    {

        private AppCompatActivity activity;
        private HttpURLConnection con;
        private String jsonPayLoad;

        public JsonThread(AppCompatActivity activity, HttpURLConnection con, String jsonPayLoad)
        {
            this.activity = activity;
            this.con = con;
            this.jsonPayLoad = jsonPayLoad;
        }
    }

    @Override
    public void run()
    {
        String response = "";
        if (prepareConnection()) {
            response = postJson();
        } else {
            response = "Error preparing the connection";
        }
        showResult(response);
    }

    private void showResult(String response) {
    activity.runOnUiThread(new Runnable()
    {
        @Override
        public void run()
        {
            String page = generatePage(response);
            ((MainActivity)activity).browser.loadData(page, "text/html", "UTF-8");
        }
    });
    }

    private String postJson() {
    String response = "";
    try{
        String postParameters = "jsonpayLoad" = +URLEncoder.encode(jsonPayLoad, "UTF-8");
        con.setFixedLengthStreamingMode(postParameters.getBytes().length);
        PrintWriter out = new PrintWriter(con.getOutputStream());
        out.print(postParameters);
        out.close();
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            response = readStream(con.getInputStream());
        } else {
            response = "Error contacting server: " + responseCode;
        }
    } catch (Exception e) {
        response = e.toString();//"Error executing code';
    }
    return response;
    }


        private String readStream(InputStream in) {
            StringBuilder sb = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String nextLine = "";
                while ((nextLine = reader.readLine()) != null) {
                    sb.append(nextLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

    private String generatePage(String content) {
       return "<html><body><p>" + content + "</p></body></html>";
    }


    private boolean prepareConnection() {
    try {
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        return true;

    } catch (ProtocolException e) {
        e.printStackTrace();
    }
    return false;
    }