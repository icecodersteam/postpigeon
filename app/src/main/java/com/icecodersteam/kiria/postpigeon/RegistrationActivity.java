package com.icecodersteam.kiria.postpigeon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private String avaPath;
    private String login;
    private String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        View v = (View)findViewById(R.id.step_selector_2);
        v.setVisibility(View.GONE);
    }

    public void RegistrationClick(View v) {
        StringRequest strRequest = new StringRequest(Request.Method.POST, "http://postpigeon.handco.ru/pigeon.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegistrationActivity.this, "Nice!", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jobj = new JSONObject(response);
                            if (jobj.has("access_token")) {
                                Toast.makeText(RegistrationActivity.this, "Time to upload!", Toast.LENGTH_SHORT).show();
                                SharedPreferences sPref = getSharedPreferences("data", MODE_PRIVATE);
                                SharedPreferences.Editor ed = sPref.edit();
                                ed.putString("token", jobj.getString("access_token"));
                                ed.commit();
                                EditText name = (EditText) findViewById(R.id.registration_login);
                                new UploadData(RegistrationActivity.this).execute(new String[]{avaPath, name.getText().toString()});
                            } else if (jobj.has("error_msg")) {
                                Toast.makeText(RegistrationActivity.this, jobj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //EditText log = (EditText) findViewById(R.id.registration_login);
                //EditText pas = (EditText) findViewById(R.id.registration_password);
                EditText fname = (EditText) findViewById(R.id.registration_first);
                EditText lname = (EditText) findViewById(R.id.registration_last);
                Map<String, String> params = new HashMap<String, String>();
                params.put("act", "register");
                params.put("login", login);
                params.put("password", pass);
                //params.put("login", log.getText().toString());
                //params.put("password", pas.getText().toString());
                params.put("first_name", fname.getText().toString());
                params.put("last_name", lname.getText().toString());
                return params;
            }
        };
        QueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(strRequest);
    }

    public void GetAvatarClick(View v) {

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select avatar image"), 1);
    }

    public void RegistrationStep2(View v){

        View v1 = (View)findViewById(R.id.step_selector);
        v1.setVisibility(View.GONE);
        login = ((EditText)findViewById(R.id.registration_login)).getText().toString();
        pass = ((EditText)findViewById(R.id.registration_password)).getText().toString();
        View v2 = (View)findViewById(R.id.step_selector_2);
        v2.setVisibility(View.VISIBLE);
        //stub.setLayoutResource(R.layout.content_registration_p2);
        //View inflated = stub.inflate();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            //String a = selectedImage.toString().replace("content:/","");
            avaPath = getPath(selectedImage);
            ImageView imageView = (ImageView) findViewById(R.id.registration_avatar);
            imageView.setImageResource(R.color.transparent);
            imageView.setImageBitmap(BitmapFactory.decodeFile(avaPath));
        }

    }

    public String getPath(Uri uri) {
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }
}

class UploadData extends AsyncTask<String, Void, Void> {

    RegistrationActivity regAct;
    public UploadData(RegistrationActivity c){
        this.regAct = c;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            uploadFile(params[0], params[1]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void a){
        Intent intent = new Intent(regAct, Dialogs.class);
        regAct.startActivity(intent);
        regAct.finish();
    }

    public int uploadFile(String sourceFileUri, String userName) {

        String fileName = sourceFileUri;
        String[] temp = fileName.split("\\.");
        String ext = temp[temp.length - 1];
        String upLoadServerUri = "http://postpigeon.handco.ru/upload.php?path=" + userName;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        int serverResponseCode = 0;

        try {

            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "close");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            //dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"avatar." + ext +"\"" + lineEnd);

            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : "
                    + serverResponseMessage + ": " + serverResponseCode);

            if (serverResponseCode == 200) {

            }

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            Toast.makeText(regAct, ex.getMessage(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(regAct, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        return serverResponseCode;
    }
}
