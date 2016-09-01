package com.coinomi.wallet.begin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coinomi.core.wallet.Wallet;
import com.coinomi.wallet.Constants;
import com.coinomi.wallet.R;
import com.coinomi.wallet.util.Keyboard;
import com.coinomi.wallet.util.PasswordQualityChecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sun on 16/9/1.
 */
public class SetPsswordActivity extends Activity {
    private Context mContext;
    private boolean hasExtraEntropy = false;
    private TextView mnemonicView;
    private Button bt_createseed_next;
    private static final Logger log = LoggerFactory.getLogger(SetPsswordActivity.class);

    private boolean isPasswordGood;
    private boolean isPasswordsMatch;
    private PasswordQualityChecker passwordQualityChecker;
    private EditText password1;
    private EditText password2;
    private TextView errorPassword;
    private TextView errorPasswordsMismatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpassword);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        AppManager.getAppManager().addActivity(this);
        mContext = SetPsswordActivity.this;
//        sp = getSharedPreferences("User", Context.MODE_PRIVATE);

        errorPassword = (TextView) findViewById(R.id.password_error);
        errorPasswordsMismatch = (TextView) findViewById(R.id.passwords_mismatch);

        passwordQualityChecker = new PasswordQualityChecker(mContext);
        isPasswordGood = false;
        isPasswordsMatch = false;

        clearError(errorPassword);
        clearError(errorPasswordsMismatch);

        password1 = (EditText) findViewById(R.id.password1);
        password2 = (EditText) findViewById(R.id.password2);

        password1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View textView, boolean hasFocus) {
                if (hasFocus) {
                    clearError(errorPassword);
                } else {
                    checkPasswordQuality();
                }
            }
        });

        password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View textView, boolean hasFocus) {
                if (hasFocus) {
                    clearError(errorPasswordsMismatch);
                } else {
                    checkPasswordsMatch();
                }
            }
        });

        // Next button
        Button finishButton = (Button) findViewById(R.id.button_next);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Keyboard.hideKeyboard(SetPsswordActivity.this);
                checkPasswordQuality();
                checkPasswordsMatch();
                if (isPasswordGood && isPasswordsMatch) {
                    Bundle args = getIntent().getExtras();
                    args.putString(Constants.ARG_PASSWORD, password1.getText().toString());
//                    mListener.onPasswordSet(args);
                    String message = getResources().getString(R.string.select_coins);

                    Intent intent = new Intent();
//                    Bundle args = new Bundle();
                    args.putBoolean(Constants.ARG_MULTIPLE_CHOICE, true);
                    args.putString(Constants.ARG_MESSAGE, message);
                    intent.setClass(mContext, SelectCoinsActivty.class);
                    intent.putExtras(args);
                    startActivity(intent);

//                    replaceFragment(SelectCoinsFragment.newInstance(message, true, args));

                } else {
                    Toast.makeText(mContext,
                            R.string.password_error, Toast.LENGTH_LONG).show();
                }
            }
        });
        finishButton.setImeOptions(EditorInfo.IME_ACTION_DONE);

    }

    private void generateNewMnemonic() {
        String mnemonic;
        if (hasExtraEntropy) {
            mnemonic = Wallet.generateMnemonicString(256);
        } else {
            mnemonic = Wallet.generateMnemonicString(192);
        }
        mnemonicView.setText(mnemonic);

        System.out.println(mnemonic);
    }

    private void checkPasswordQuality() {
        String pass = password1.getText().toString();
        isPasswordGood = false;
        try {
            passwordQualityChecker.checkPassword(pass);
            isPasswordGood = true;
            clearError(errorPassword);
        } catch (PasswordQualityChecker.PasswordTooCommonException e1) {
            log.info("Entered a too common password {}", pass);
            setError(errorPassword, R.string.password_too_common_error, pass);
        } catch (PasswordQualityChecker.PasswordTooShortException e2) {
            log.info("Entered a too short password");
            setError(errorPassword, R.string.password_too_short_error,
                    passwordQualityChecker.getMinPasswordLength());
        }
        log.info("Password good = {}", isPasswordGood);
    }

    private void checkPasswordsMatch() {
        String pass1 = password1.getText().toString();
        String pass2 = password2.getText().toString();
        isPasswordsMatch = pass1.equals(pass2);
        if (!isPasswordsMatch) showError(errorPasswordsMismatch);
        log.info("Passwords match = {}", isPasswordsMatch);
    }


    private void setError(TextView errorView, int messageId, Object... formatArgs) {
        setError(errorView, getResources().getString(messageId, formatArgs));
    }

    private void setError(TextView errorView, String message) {
        errorView.setText(message);
        showError(errorView);
    }

    private void showError(TextView errorView) {
        errorView.setVisibility(View.VISIBLE);
    }

    private void clearError(TextView errorView) {
        errorView.setVisibility(View.GONE);
    }

}
