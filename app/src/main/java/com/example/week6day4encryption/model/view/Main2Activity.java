package com.example.week6day4encryption.model.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.week6day4encryption.R;
import com.example.week6day4encryption.model.helpers.SQLDBHelper;
import com.example.week6day4encryption.model.pojos.User;
import com.example.week6day4encryption.model.wrappers.CipherWrapper;
import com.example.week6day4encryption.model.wrappers.KeystoreWrapper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "FRANK: ";
    private CipherWrapper cipherWrapper;
    private KeystoreWrapper keystoreWrapper;
    private String alias = "Master Key";
    private KeyPair masterKey;

    Intent intent;
    String userName;
    String password;
    TextView vTvUserName;
    TextView vTvPassword;
    EditText vEtNewPassword;
    Button vBtnUpdatePassword;
    SQLDBHelper sqldbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        sqldbHelper = new SQLDBHelper(this);

        vTvUserName = findViewById(R.id.tvUserName);
        vTvPassword = findViewById(R.id.tvPassword);
        vEtNewPassword = findViewById(R.id.etNewPassword);
        vBtnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        intent = getIntent();
        userName = intent.getStringExtra("user");
        password = intent.getStringExtra("pass");

        vTvUserName.setText(userName);
        vTvPassword.setText(password);

        try {
            initWrappers();

            keystoreWrapper.createKeyPair(alias);

            masterKey = keystoreWrapper.getAsymKey(alias);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void onClick(View view) {

        String newPass = vEtNewPassword.getText().toString();
        
        if (newPass != null && !newPass.isEmpty()) {

            vTvPassword.setText(newPass);
            try {

                String encryptedData = cipherWrapper.encrypt(newPass, masterKey.getPublic());
                Log.d(TAG, "onCreate: EncryptedData " + encryptedData);

                User user = new User(userName, encryptedData);
                sqldbHelper.updateUser(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initWrappers()
            throws
            NoSuchAlgorithmException, NoSuchPaddingException, CertificateException, KeyStoreException,
            IOException, NoSuchProviderException, InvalidAlgorithmParameterException {
        cipherWrapper = new CipherWrapper("RSA/ECB/PKCS1Padding");
        keystoreWrapper = new KeystoreWrapper(getApplicationContext());
    }
}
