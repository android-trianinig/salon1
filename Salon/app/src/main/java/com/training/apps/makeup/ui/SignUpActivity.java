package com.training.apps.makeup.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.training.apps.makeup.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    private String userType;
    private String mUserName;
    private String mUserPhoneNumber;
    private String mUserEmail;
    private String mUserCityName;
    private String mUserAddress;
    private double mUserCityLat;
    private double mUserCityLong;
    private double mUserLat;
    private double mUserLong;
    private String mUserPassword;
    private BroadcastReceiver broadcastReceiver;


    @BindView(R.id.sign_up_client_user_name)
    EditText userNameView;

    @BindView(R.id.sign_up_client_email)
    EditText userEmailView;

    @BindView(R.id.sign_up_client_city)
    Spinner userCitySpinner;

    @BindView(R.id.sign_up_client_phone)
    EditText userPhoneView;

    @BindView(R.id.sign_up_client_password)
    EditText userPasswordView;

    @BindView(R.id.sign_up_client_location)
    TextView userLocationTextView;

    @BindView(R.id.user_image)
    ImageView userImageView;

    @BindView(R.id.new_photo_text)
    TextView newPhotoTextView;

//    @BindView(R.id.add_user_image_layout)
//    RelativeLayout addNewPhotoLayout;

    @BindView(R.id.new_user_sign_up_btn)
    Button addNewUser;
    private int PICK_IMAGE = 1212;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = getResources();
        final String[] citiesLatLong = res
                .getStringArray(R.array.cities_LatLong);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("new") != null) {
                this.userType = intent.getStringExtra("new");
            }
            switch (userType) {
                case "client":
                    setContentView(R.layout.activity_sign_up_clients);
                    break;
                case "provider":
                    setContentView(R.layout.activity_sign_up_providers);
                    break;
                default:
                    throw new IllegalArgumentException("user type not defined");
            }
        }
        ButterKnife.bind(this);
        userCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUserCityName = (String) parent.getSelectedItem();
                String[] latLong = citiesLatLong[position].split(", ");
                mUserCityLat = Double.parseDouble(latLong[0]);
                mUserCityLong = Double.parseDouble(latLong[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        userLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SignUpActivity.this, SelectLocationActivity.class);
                intent1.putExtra("lat", mUserCityLat);
                intent1.putExtra("long", mUserCityLong);
                startActivity(intent1);
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mUserAddress = intent.getStringExtra("userAddress");
                mUserLat = intent.getDoubleExtra("userLat", 0);
                mUserLong = intent.getDoubleExtra("userLong", 0);

                userLocationTextView.setText(mUserAddress);
            }
        };
        LocalBroadcastManager.getInstance(this).
                registerReceiver(broadcastReceiver, new IntentFilter("location-confirmed"));
        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                        Uri selectedImage = data.getData();
                        newPhotoTextView.setVisibility(View.GONE);
                        Glide.with(this).load(selectedImage)
                                .into(userImageView);
                        Bitmap userPhoto = BitmapFactory.decodeStream(inputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public void onConfirmSignUpClick(View view) {
        mUserName = userNameView.getText().toString();
        mUserEmail = userEmailView.getText().toString();
        mUserPhoneNumber = userPhoneView.getText().toString();
        mUserPassword = userPasswordView.getText().toString();
        Toast.makeText(this, mUserName +
                        " " + mUserEmail +
                        " " + mUserCityName +
                        " " + mUserPhoneNumber +
                        " " + mUserPassword +
                        " " + mUserCityLat +
                        " " + mUserCityLong
                , Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
