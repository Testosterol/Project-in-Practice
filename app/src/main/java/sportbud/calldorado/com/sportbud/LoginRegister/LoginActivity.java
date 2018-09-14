package sportbud.calldorado.com.sportbud.LoginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sportbud.calldorado.com.sportbud.MainActivity;
import sportbud.calldorado.com.sportbud.R;
import sportbud.calldorado.com.sportbud.Server.Url;
import sportbud.calldorado.com.sportbud.Server.Volley;

public class LoginActivity extends AppCompatActivity {

    private Button registrationButton, loginButton;
    private EditText email_to_login, password_to_login;
    private ProgressDialog progressDialog;
    private static String id = "", eventsNum = "", karol = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        registrationButton = findViewById(R.id.registration_button);
        loginButton = findViewById(R.id.signin_button);
        email_to_login = findViewById(R.id.email_to_login);
        password_to_login = findViewById(R.id.password_to_login);

        registrationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),
                    RegistrationActivity.class);
            startActivity(intent);
            finish();
        });

        loginButton.setOnClickListener(v -> {
            String email = email_to_login.getText().toString();
            String password = password_to_login.getText().toString();
            if (email.trim().length() > 0 && password.trim().length() > 0) {
                checkLogin(email, password);
                //saveEmail(email);
            } else {
                Snackbar.make(v, "Please enter the credentials!", Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private void checkLogin(final String email, final String password) {
        String tag_string_req = "req_login";
        progressDialog.setMessage("Logging in ...");
        StringRequest strReq = new StringRequest(Request.Method.POST, Url.URL, response -> {
            try {
                JSONObject jObj = new JSONObject(response);
                String userId = jObj.getString("user_id");
                if (!userId.equals("null")) {
                    //saveId(userId);
                    //session.setLogin(true);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    //retreiveNotificationSettings();
                } else {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }}, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        Volley.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
