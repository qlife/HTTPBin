package example.syt.httpbin;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    final private String IP_URL = "https://httpbin.org/ip";
    final private String HEADERS_URL = "https://httpbin.org/headers";
    protected TextView mIpView;
    protected TextView mHeadersView;
    JSONObject mIpJson = null;
    JSONObject mHeadersJson = null;

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {

        HttpURLConnection urlConnection = null;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

        char[] buffer = new char[1024];

        String jsonString = new String();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        jsonString = sb.toString();

        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIpView = (TextView) findViewById(R.id.ip);
        mIpView.setText(R.string.loading);
        mHeadersView = (TextView) findViewById(R.id.headers);
        mHeadersView.setText(R.string.loading);
    }

    @Override
    protected void onResume() {
        super.onResume();
        doApiCall();
    }

    @Override
    protected void onPause() {
        mIpView.setText(R.string.loading);
        mHeadersView.setText(R.string.loading);
        super.onPause();
    }

    private void doApiCall() {
        HandlerThread thread = new HandlerThread("handler_thread");
        thread.start();
        new Handler(thread.getLooper()).post(new Runnable() {

            @Override
            public void run() {
                fetchJSON();

                mIpView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mIpJson != null) {
                                mIpView.setText(mIpJson.toString(4));
                            }
                        } catch (JSONException e) {
                            mIpView.setText("JSON exception");
                            e.printStackTrace();
                        }
                    }
                });

                mHeadersView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mHeadersJson != null) {
                                mHeadersView.setText(mHeadersJson.toString(4));
                            }
                        } catch (JSONException e) {
                            mHeadersView.setText("JSON exception");
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    private void fetchJSON() {
        try {
            mIpJson = getJSONObjectFromURL(IP_URL);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            mHeadersJson = getJSONObjectFromURL(HEADERS_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
