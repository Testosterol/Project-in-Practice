package sportbud.calldorado.com.sportbud.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sportbud.calldorado.com.sportbud.LoginRegister.LoginActivity;
import sportbud.calldorado.com.sportbud.MainActivity;
import sportbud.calldorado.com.sportbud.R;
import sportbud.calldorado.com.sportbud.Server.Url;
import sportbud.calldorado.com.sportbud.Server.Volley;

public class ProfileActivity extends AppCompatActivity {

    private EditText fullname, age_toSave, weight_toSave, height_toSave, sports_toSave;
    private Button saveButton, backButton;
    private TextView imageButton, setDefaultImage;
    private ImageView imageViewLoad;
    private static int IMG_RESULT = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullname = findViewById(R.id.fullname_profile);
        age_toSave = findViewById(R.id.age_profile);
        weight_toSave = findViewById(R.id.weight_profile);
        height_toSave = findViewById(R.id.height_profile);
        sports_toSave = findViewById(R.id.sports_profile);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button);
        imageButton = findViewById(R.id.image_button);
        setDefaultImage = findViewById(R.id.defaultImage_button);
        imageViewLoad = findViewById(R.id.imageView1);


        saveButton.setOnClickListener(v -> {
            String name = fullname.getText().toString();
            String age = age_toSave.getText().toString();
            String weight = weight_toSave.getText().toString();
            String height = height_toSave.getText().toString();
            String sports = sports_toSave.getText().toString();
            if (!name.isEmpty() && !age.isEmpty() && !weight.isEmpty() && !height.isEmpty() &&
                    !sports.isEmpty()) {
                //saveUserProfile(name, age, weight, height, sports);
                Toast.makeText(ProfileActivity.this, "Profile saved!", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(v, "Please enter the credentials!", Snackbar.LENGTH_LONG).show();
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        setDefaultImage.setOnClickListener(v -> {
            imageViewLoad.setImageResource(R.drawable.defaultprofile);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defaultprofile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            String profilePicture = Base64.encodeToString(image,Base64.DEFAULT);
            //saveProfilePicture(profilePicture); //todo: user profile pic
        });
        imageButton.setOnClickListener(v -> getUserImage());
    }
    private void getUserImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMG_RESULT);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
            ImageView my_img_view = findViewById (R.id.imageView1);
            my_img_view.setImageBitmap(bitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            String profilePicture = Base64.encodeToString(image,Base64.DEFAULT);
            saveProfilePicture(profilePicture); //todo: user profile pic
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveProfilePicture(final String profilePicture){
        // Tag used to cancel the request
        //final String email = LoginActivity.getVariable(); // todo: get email to pair the user
        String tag_string_req = "req_register";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Url.URL, response -> {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        String userId = jObj.getString("user_id");
                        String userProfilePicture = jObj.getString("user_profilePicture");
                        System.out.println(userProfilePicture);
                        if (!userId.equals("null")) {
                            //session.setLogin(true);
                            if(!userProfilePicture.equals("")) {
                                byte[] decodedString = Base64.decode(userProfilePicture, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                ImageView my_img_view = findViewById (R.id.imageView1);
                                my_img_view.setImageBitmap(decodedByte);
                            }
                        } else {
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("tag", "save_ProfilePicture");
                //params.put("email", email);
                params.put("profilePicture", profilePicture);
                return params;
            }
        };
        Volley.getInstance().addToRequestQueue(strReq, tag_string_req);
        //retreiveUserInformationPicture();
    }
}
