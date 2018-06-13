package com.example.szyman.secureapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPref;
    private TextView input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        this.input = findViewById(R.id.messageInput);

        Button submit = findViewById(R.id.saveButton) ;
        submit.setOnClickListener(this);

        this.sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String encrypted_message = sharedPref.getString("encrypted_message", "");

        try {
            byte[] encrypted_message_array = Base64.decode(encrypted_message, Base64.DEFAULT);

            CryptHelper crypt = new CryptHelper();
            try {
                SecretKey secret = crypt.generateKey();
                String message = crypt.decryptMsg(encrypted_message_array, secret);

                input.setText(message);

            } catch (NoSuchAlgorithmException error) {
                Log.e("CryptError:", error.getMessage());
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }

        } catch(Exception error) {
            Log.e("Base64Error", error.getMessage());
        }
    }

    @Override
    public void onClick(View v) {

        this.input = findViewById(R.id.messageInput);
        String message = this.input.getText().toString();

        CryptHelper crypt = new CryptHelper();

        try {
            SecretKey secret = crypt.generateKey();
            byte[] encrypted_message_array = crypt.encryptMsg(message, secret);

            String encrypted_message = Base64.encodeToString(encrypted_message_array, Base64.DEFAULT);

            SharedPreferences.Editor editor = this.sharedPref.edit();
            editor.putString("encrypted_message", encrypted_message);
            editor.commit();

            Toast.makeText(this, "Message was saved!", Toast.LENGTH_LONG).show();

        } catch (Exception error) {
            Log.e("Error:", error.getMessage());
        }
    }
}
