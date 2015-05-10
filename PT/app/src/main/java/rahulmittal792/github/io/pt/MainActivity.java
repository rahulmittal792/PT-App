package rahulmittal792.github.io.pt;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;


public class MainActivity extends ActionBarActivity {
    EditText login_id_field;
    static String login_id;
    EditText login_pwd_field;
    static String login_pwd;
    Button login_btn;
    Button reset_btn;
    TextView resultText;
    LinearLayout loading_img_container;
    ImageView loading_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_id_field = (EditText)findViewById(R.id.login_id);
        login_pwd_field = (EditText)findViewById(R.id.login_pwd);
        login_btn = (Button)findViewById(R.id.login_btn);
        reset_btn = (Button)findViewById(R.id.reset_btn);
        resultText = (TextView)findViewById(R.id.result);
        loading_img_container = (LinearLayout)findViewById(R.id.loading_image_wrap);
        loading_img = (ImageView)findViewById(R.id.loading_image);

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_id_field.setText("");
                login_pwd_field.setText("");
                resultText.setText("");
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_id = login_id_field.getText().toString();
                login_pwd = login_pwd_field.getText().toString();
                if(!(login_id.equals("") || login_pwd.equals(""))) {
                    String auth_url = "https://www.pivotaltracker.com/services/v5/me";
                    if(checkInternetConnection()) {
                        loading_img_container.setVisibility(View.VISIBLE);
                        new HttpAsyncTask().execute(auth_url);
                    }
                    else {
                        Toast.makeText(getBaseContext(), "No interent connection", Toast.LENGTH_LONG).show();
                        resultText.setText("Internet not connected");
                    }
                }
                else {
                    resultText.setText("Login Id & Password must be provided");
                }
            }
        });
        if(isUserAuthenticated()) {
            Intent intent = new Intent(this, ProjectList.class);
            startActivity(intent);
        }
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));

            String auth_credentials = login_id+":"+login_pwd;
            request.setHeader("Authorization", "Basic " + Base64.encodeToString(auth_credentials.getBytes(), Base64.NO_WRAP));

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(request);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream, "iso-8859-1"), 8);
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            loading_img_container.setVisibility(View.GONE);
            try {
                JSONObject json_data = new JSONObject(result);

                if(json_data.has("api_token")) {
                    FileOutputStream fos = openFileOutput("user_auth",MODE_PRIVATE);
                    fos.write(result.getBytes());
                    fos.close();

                    Intent intent = new Intent(getBaseContext(), ProjectList.class);
                    startActivity(intent);
                }
                else {
                    resultText.setText("Username or Password are incorrect. Please check again.");
                }
            }
            catch (Exception e) {
                resultText.setText(e.toString());
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean checkInternetConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public boolean isUserAuthenticated() {
        try {
            FileInputStream fis = openFileInput("user_auth");
            if(fis.available()!=0) {
                return true;
            }
            else {
                return false;
            }
        }
        catch(Exception e){
            return false;
        }
    }
}
