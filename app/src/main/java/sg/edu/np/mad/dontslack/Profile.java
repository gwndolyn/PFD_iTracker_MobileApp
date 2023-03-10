package sg.edu.np.mad.dontslack;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

public class Profile extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private static  User user;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_LOGIN = "accountStatus";
    private static final String KEY_USERNAME = "userName";
    DBHandler dbHandler = new DBHandler(this,null,null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /* Hiding the top bar */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        //Setting the user profile
        String Username = sharedPreferences.getString(KEY_USERNAME, "");
        if(!Username.equals("")){
            user = dbHandler.findUser(Username);
            TextView profileUsername = findViewById(R.id.textProfileUsername);
            profileUsername.setText(user.getUsername());

            TextView profilePassword = findViewById(R.id.textProfilePassword);

        }

        //Back button
        ImageView backHomePage = findViewById(R.id.backHome6);
        backHomePage.setOnClickListener(v -> {
            Intent myIntent = new Intent(Profile.this, HomePage.class);
            startActivity(myIntent);
        });

        //Change password button
        TextView changePasswordButton = findViewById(R.id.changePassword);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Profile.this,ChangePassword.class);
                Bundle args = new Bundle();
                args.putSerializable("User", (Serializable) user);
                myIntent.putExtra("Bundle",args);
                startActivity(myIntent);
            }
        });

        //Delete account button
      //  TextView deleteAccountButton = findViewById(R.id.deleteAccountButton);
      //  deleteAccountButton.setOnClickListener(v -> {
     //       AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
       //     alertDialog.setTitle("Delete Account");
      //      alertDialog.setMessage("Are you sure you want to delete this account?");
       //     alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog, which) -> dialog.dismiss());
        //    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes, I'm sure.", (dialog, which) -> {
        //        dbHandler.deleteUser(user);
         //       sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        //        SharedPreferences.Editor editor = sharedPreferences.edit();
         //       editor.putBoolean(KEY_LOGIN, false);
         //       editor.apply();
        //        Intent myIntent = new Intent(Profile.this, HomePage.class);
        //        startActivity(myIntent);
        //    });
        //    alertDialog.show();
      //  });

        //Logout account button
        TextView logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Toast.makeText(Profile.this, "Log out successfully", Toast.LENGTH_SHORT).show();
            finish();
            Intent myIntent = new Intent(Profile.this, Login.class);
            startActivity(myIntent);
        });

    }
}
