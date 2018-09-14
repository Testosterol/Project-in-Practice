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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sportbud.calldorado.com.sportbud.R;
import sportbud.calldorado.com.sportbud.Server.Url;
import sportbud.calldorado.com.sportbud.Server.Volley;

public class RegistrationActivity extends AppCompatActivity{

    private TextView tvLogin;
    private EditText fullName, email_to_register, password_to_register;
    private Button registerButton;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_registration);
        registerButton = findViewById(R.id.register_button);
        fullName = findViewById(R.id.fullname_register);
        email_to_register = findViewById(R.id.email_register);
        password_to_register =  findViewById(R.id.password_register);
        tvLogin = findViewById(R.id.tv_signin);

        registerButton.setOnClickListener(v -> {
            String name = fullName.getText().toString();
            String email = email_to_register.getText().toString();
            String password = password_to_register.getText().toString();

            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {


                // show consent - > if agree - > register user // todo: consent

                registerUser(name, email, password);
            } else {
                Snackbar.make(v, "Please enter the credentials!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void registerUser(final String name, final String email, final String password){
        String tag_string_req = "req_register";
        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, Url.URL, response -> {
            hideDialog();
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
            hideDialog();
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };
        Volley.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
