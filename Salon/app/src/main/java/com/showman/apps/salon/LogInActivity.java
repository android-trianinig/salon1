package com.showman.apps.salon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LogInActivity extends AppCompatActivity {

    @BindView(R.id.log_in_user_phone)
    EditText userPhoneNumber;
    @BindView(R.id.log_in_password)
    EditText userPassword;
    @BindView(R.id.log_in_btn)
    Button logInBtn;
    @BindView(R.id.sign_in_btn)
    Button signInBtn;
    @BindView(R.id.skip_log_in_btn)
    Button skipBtn;

    private String mUserPhone;
    private String mUserPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        ButterKnife.bind(this);
    }

    public void onLogInBtnClicked(View view) {
        //fot testing only
        mUserPhone = userPhoneNumber.getText().toString();
        mUserPassword = userPassword.getText().toString();
        if ((mUserPhone!= null && !mUserPhone.isEmpty()) && (mUserPassword != null && !mUserPassword.isEmpty())) {
            Toast.makeText(this, "phone: " + mUserPhone + " password: " + mUserPassword, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "please type your phone and password", Toast.LENGTH_SHORT).show();
        }
    }

}
