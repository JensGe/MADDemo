package de.honzont.jensge.maddemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;

/**
 * Created by Gäbeler on 23.06.2017.
 */

public class LoginviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SERVCON = "serverConnection";
    private Button loginButton;
    private TextView loginEmail, loginPassword, loginError;
    private ProgressDialog progressDialog;
    protected static String logger = LoginviewActivity.class.getSimpleName();

    private boolean appHasServerConnection;
    private boolean userHasPermission;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);

        serverConnectionTest();

        setContentView(R.layout.activity_loginview);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginEmail = (TextView) findViewById(R.id.loginEmail);
        loginPassword = (TextView) findViewById(R.id.loginPassword);
        loginError = (TextView) findViewById(R.id.loginErrorField);

        loginEmail.setText("s@bht.de");
        loginPassword.setText("000000");

        loginButton.setOnClickListener(this);

        loginEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEmailText();
                }
            }
        });

        loginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validatePasswordText();
                }
            }
        });
        loginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginButton.setError(null);
                loginButton.clearFocus();
                loginError.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginButton.setError(null);
                loginButton.clearFocus();
                loginError.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == loginButton) {
            if (validateEmailText() && validatePasswordText()) {
                validateAuthentification();
            }

        }
    }


    private void runApplication(boolean serverConnection) {
        Intent serverConnectionIntent = new Intent(this, OverviewActivity.class);
        serverConnectionIntent.putExtra(SERVCON, serverConnection);
        startActivity(serverConnectionIntent);
    }

    private boolean validateEmailText() {
        String email = loginEmail.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError(getString(R.string.noEmailError));
            return false;
        }
        else return true;
    }

    private boolean validatePasswordText() {
        String pw = loginPassword.getText().toString();
        Pattern p = Pattern.compile("/d/d/d/d/d/d");
        Matcher m = p.matcher(pw);
        if (!(pw.length() == 6) && !m.matches()) {
            loginPassword.setError("PW should be 6 Char and only Numbers");
            return false;
        }
        else return true;
    }

    private void serverConnectionTest() {


        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Trying to connect to Server (2 sec)");
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                URL url = null;
                try {
                    url = new URL("http://10.0.2.2:8080");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                HttpURLConnection conn = null;

                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(logger, "Open Connection: " + conn);

                assert conn != null;
                conn.setReadTimeout(2000);
                conn.setConnectTimeout(2000);

                try {
                    conn.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                conn.setDoInput(true);

                try {
                    conn.connect();
                    return true;

                } catch (ConnectException c) {
                    return false;
                } catch (SocketTimeoutException c) {
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean con) {
                if (!con) {
                    Toast.makeText(getApplicationContext(),"Server not reachable", Toast.LENGTH_SHORT).show();
                    appHasServerConnection = con;
                    runApplication(false);
                }
                else if (con) {
                    Toast.makeText(getApplicationContext(),"Connected to Server", Toast.LENGTH_SHORT).show();
                    appHasServerConnection = true;
                }
                progressDialog.hide();



            }
        }.execute();
    }

    private void validateAuthentification() {
        final String email = loginEmail.getText().toString(); /*.equals("s@bht.de")*/
        final String pwd = loginPassword.getText().toString(); /*.equals("000000")*/
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                URL url = null;
                HttpURLConnection conn = null;
                try {
                    url = new URL("http://10.0.2.2:8080/api/users/auth");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


                try {
                    assert url != null;
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert conn != null;
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);

                try {
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    JSONObject cred = new JSONObject();
                    cred.put("email", email);
                    cred.put("pwd", pwd);



                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    OutputStreamWriter out = new OutputStreamWriter(
                            conn.getOutputStream());
                    out.write(cred.toString());
                    Log.i(logger, "OutputStreamContent: " + out.toString());
                    out.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    Integer code = conn.getResponseCode();
                    String msg = conn.getResponseMessage();

                    Log.i(logger, "ResponseMsg: " + msg + String.valueOf(code));

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    Log.i(logger, "String: " + sb.toString());
                    if (sb.toString().contains("true")) {
                        return true;
                    }
                    else if (sb.toString().contains("false")) {
                        return false;
                    }

                } catch (ConnectException c) {
                    return null;
                } catch (SocketTimeoutException c) {
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                progressDialog.hide();
                if (aBoolean) {
                    runApplication(true);
                } else if (!userHasPermission) {
                    loginButton.setError("");
                    loginError.setText("Login Failed");
                    loginError.setTextColor(Color.RED);
                }

            }
        }.execute();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
}

