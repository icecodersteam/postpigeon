package com.icecodersteam.kiria.postpigeon;

import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.provider.Settings;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.View;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class Dialogs extends AppCompatActivity {
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sPref;
        sPref = getSharedPreferences("data", MODE_PRIVATE);
        String savedText = sPref.getString("token", "");
        if(savedText.isEmpty()) {
            Intent intent = new Intent(Dialogs.this, Auth.class);
            startActivity(intent);
            this.finish();
        }
        token = savedText;

        setContentView(R.layout.activity_dialogs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        toolbar.setTitle("Диaлоги");
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, "http://postpigeon.handco.ru/pigeon.php?act=getdialogs&token=" + token, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        /*if(response.has("error_msg")){
                            Toast.makeText(messages.this, "Need Auth", Toast.LENGTH_SHORT).show();
                        }else{
                            try {
                                List<String> strs = new ArrayList<>();
                                JSONArray array = response.getJSONArray("");
                                for (int i = 0; i < array.length(); i++){
                                    JSONObject obj = array.getJSONObject(i);
                                    int a = obj.getInt("dialog_id");
                                    int[] b = (int[])obj.get("dialog_users");
                                    strs.add(a + "  " + b[0]);
                                }
                            }
                            catch (Exception e){

                            }
                        }*/
                        try {
                            if(response.getInt("success") == 1){
                                List<String> strs = new ArrayList<>();
                                JSONArray array = response.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++){
                                    JSONObject obj = array.getJSONObject(i);
                                    int a = obj.getInt("dialog_id");
                                    String b = obj.getString("dialog_users");
                                    strs.add(String.valueOf(a) + "  " + b);
                                }
                                String[] arr = strs.toArray(new String[0]);
                                final ListView lvMain = (ListView) findViewById(R.id.dialogsListView);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Dialogs.this, android.R.layout.simple_list_item_1, arr);
                                lvMain.setAdapter(adapter);
                                lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Toast.makeText(Dialogs.this, (String)lvMain.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else{
                                Toast.makeText(Dialogs.this, response.getString("error_msg"), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(Dialogs.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Dialogs.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        QueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messages, menu);
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
        if (id == R.id.action_logout) {
            API a = new API(){
                public void done(){
                    System.out.println("Таким будет запрос потом: ");
                    System.out.print(response);
                }
            };
            a.SetParam("act", "logout");
            a.execute();
            SharedPreferences sPref;
            sPref = getSharedPreferences("data", MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("token", "");
            ed.commit();
            Intent intent = new Intent(Dialogs.this, Auth.class);
            startActivity(intent);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    void InitMenu(){
        API a = new API(){
            public void done(){
                final String ATTRIBUTE_NAME_TEXT = "text";
                final String ATTRIBUTE_NAME_CHECKED = "checked";
                final String ATTRIBUTE_NAME_IMAGE = "image";

                String[] texts = { "sometext 1", "sometext 2", "sometext 3",
                        "sometext 4", "sometext 5" };
                ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                        texts.length);
                Map<String, Object> m;
                for (int i = 0; i < 1; i++) {
                    m = new HashMap<String, Object>();
                    m.put(ATTRIBUTE_NAME_TEXT, texts[i]);
                    data.add(m);
                }
                // массив имен атрибутов, из которых будут читаться данные
                String[] from = { ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE};
                // массив ID View-компонентов, в которые будут вставлять данные
                int[] to = { R.id.menu_user_avatar, R.id.menu_user_name};

                SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), data, R.layout.usermenudata, from, to);

                // Getting a reference to listview of main.xml layout file
                ListView listView = (ListView) findViewById(R.id.left_drawer);

                // Setting the adapter to the listView
                listView.setAdapter(adapter);
            }
        };
        a.SetParam("act", "getInfo");
        a.execute();

    }
}
