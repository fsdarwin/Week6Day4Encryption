package com.example.week6day4encryption;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.week6day4encryption.model.helpers.SQLDBHelper;
import com.example.week6day4encryption.model.pojos.User;
import com.example.week6day4encryption.model.view.Main2Activity;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FRANK: ";
    private CipherWrapper cipherWrapper;
    private KeystoreWrapper keystoreWrapper;
    private String alias = "Master Key";
    private KeyPair masterKey;
    TextView vTvTitle;
    EditText vEtUserName;
    EditText vEtPassword;
    Button vBtnSignIn;
    Button vBtnNewAccount;
    SQLDBHelper sqldbHelper = new SQLDBHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vTvTitle = findViewById(R.id.tvTitle);
        vEtUserName = findViewById(R.id.etUserName);
        vEtPassword = findViewById(R.id.etPassword);
        vBtnSignIn = findViewById(R.id.btnSignIn);
        vBtnNewAccount = findViewById(R.id.btnNewAccount);

        try {
            initWrappers();

            keystoreWrapper.createKeyPair(alias);

            masterKey = keystoreWrapper.getAsymKey(alias);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNewAccount:
                Log.d(TAG, "onClick: in onClick");

                try {
                    String userName = vEtUserName.getText().toString();
                    String plainText = vEtPassword.getText().toString();
                    Log.d(TAG, "btnNewAccount: PlainText password " + plainText);

                    String encryptedData = cipherWrapper.encrypt(plainText, masterKey.getPublic());
                    Log.d(TAG, "btnNewAccount: EncryptedData " + encryptedData);

                    User user = new User(userName, encryptedData);

                    sqldbHelper.insertUser(user);
                    vEtUserName.setText("");
                    vEtPassword.setText("");
                    vTvTitle.setText(getString(R.string.log_in_error_message_1));

                } catch (
                        Exception e) {
                }
                break;
            case R.id.btnSignIn:
                String userName = vEtUserName.getText().toString();
                String plainText = vEtPassword.getText().toString();
                Log.d(TAG, "btnSignIn: plainText: " + plainText);

                User maybeUser = sqldbHelper.selectUser(userName);
                String encryptedData = maybeUser.getPassword();
                Log.d(TAG, "btnSignIn: encryptedData = " + encryptedData);
                String decryptedData = null;

                try {
                    decryptedData = cipherWrapper.decrypt(encryptedData, masterKey.getPrivate());
                    Log.d(TAG, "btnSignIn: DecryptedData " + decryptedData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (decryptedData != null && decryptedData.equals(plainText)) {
                    Log.d(TAG, "btnSignIn if: Successful password check");
                    Intent intent = new Intent(this, Main2Activity.class);
                    intent.putExtra("user", userName);
                    intent.putExtra("pass", plainText);
                    startActivity(intent);
                } else {
                    vTvTitle.setText(getString(R.string.log_in_error_message_2));
                }
                break;
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
