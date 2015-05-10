package rahulmittal792.github.io.pt;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


public class ProjectList extends ActionBarActivity {
    ArrayList<String> strArr;
    ArrayAdapter<String> adapter;
    ListView projectList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        projectList = (ListView) findViewById(R.id.project_list);

        try {
            strArr = new ArrayList<>();
            FileInputStream fis = openFileInput("user_auth");
            BufferedInputStream bis = new BufferedInputStream(fis);
            StringBuffer b = new StringBuffer();
            while(bis.available()!=0){
                char c = (char) bis.read();
                b.append(c);
            }

            JSONObject json_data = new JSONObject(b.toString());
            JSONArray jsonArray = json_data.getJSONArray("projects");
            String project_names = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject project = jsonArray.getJSONObject(i);
                strArr.add(project.getString("project_name"));
            }
            adapter = new ArrayAdapter<String>(this, R.layout.sample_project_list_item, strArr);
            projectList.setAdapter(adapter);
        }
        catch (Exception e) {
            Log.e("RAHUL", e.toString());
        }

        projectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String project_id = "";
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout_option) {
            try {
                String dir = getFilesDir().getAbsolutePath();
                File file = new File(dir, "user_auth");
                file.delete();

                finish();
            }
            catch (Exception e) {
                Log.e("RAHUL",e.toString());
            }
            return true;
        }
        else if (id == R.id.profile_option) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
