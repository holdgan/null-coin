package com.coinomi.wallet.begin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.coinomi.wallet.Constants;
import com.coinomi.wallet.R;
import com.coinomi.wallet.ui.ScanActivity;
import com.coinomi.wallet.util.Keyboard;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


/**
 * Created by sun on 16/9/1.
 */
public class VerificationSeedActivity extends Activity {
    private static final int REQUEST_CODE_SCAN = 0;
    private static final Logger log = LoggerFactory.getLogger(VerificationSeedActivity.class);
    private Context mContext;
    private boolean hasExtraEntropy = false;
    private Button bt_vrificationseed_next;
    private TextView restore_message;
    private MultiAutoCompleteTextView mnemonicTextView;
    private EditText et_verification;

    private String seed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_seed);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        AppManager.getAppManager().addActivity(this);
        mContext = VerificationSeedActivity.this;
//        sp = getSharedPreferences("User", Context.MODE_PRIVATE);

        bt_vrificationseed_next = (Button) findViewById(R.id.bt_vrificationseed_next);
        mnemonicTextView = (MultiAutoCompleteTextView) findViewById(R.id.seed);
        et_verification = (EditText) findViewById(R.id.et_verification);
        // Restore message
        restore_message = (TextView) findViewById(R.id.restore_message);
        ImageButton scanQrButton = (ImageButton) findViewById(R.id.scan_qr_code);


        scanQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleScan();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("verification").equals("create")) {
            seed = bundle.getString(Constants.ARG_SEED);
        }
        restore_message.setVisibility(View.GONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                R.layout.item_simple, MnemonicCode.INSTANCE.getWordList());
        mnemonicTextView.setAdapter(adapter);
        mnemonicTextView.setTokenizer(new SpaceTokenizer() {
            @Override
            public void onToken() {
                clearError(restore_message);
            }
        });


        bt_vrificationseed_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifyMnemonicAndProceed();
            }
        });

    }

    private void verifyMnemonicAndProceed() {
        Keyboard.hideKeyboard(this);
        if (verifyMnemonic()) {
            Bundle args = getIntent().getExtras();
            if (args == null) args = new Bundle();

//            if (isSeedProtected) {
//                args.putString(Constants.ARG_SEED_PASSWORD, bip39Passphrase.getText().toString());
//            }
            args.putString(Constants.ARG_SEED, mnemonicTextView.getText().toString().trim());
            args.putString(Constants.ARG_SEED_PASSWORD, et_verification.getText().toString().trim());
//            if (mListener != null) mListener.onSeedVerified(args);
            Intent intent = new Intent();
//            intent.putExtra(Constants.ARG_SEED, seed);
            intent.putExtras(args);
            intent.setClass(mContext, SetPsswordActivity.class);
            startActivity(intent);
        }
    }

    private boolean verifyMnemonic() {
        log.info("Verifying seed");
        // TODO, use util class to be ported from the NXT branch
        String seedText = mnemonicTextView.getText().toString().trim();
        ArrayList<String> seedWords = new ArrayList<>();
        for (String word : seedText.trim().split(" ")) {
            if (word.isEmpty()) continue;
            seedWords.add(word);
        }
        boolean isSeedValid = false;
        try {
            MnemonicCode.INSTANCE.check(seedWords);
            clearError(restore_message);
            isSeedValid = true;
        } catch (MnemonicException.MnemonicChecksumException e) {
            log.info("Checksum error in seed: {}", e.getMessage());
            setError(restore_message, R.string.restore_error_checksum);
        } catch (MnemonicException.MnemonicWordException e) {
            log.info("Unknown words in seed: {}", e.getMessage());
            setError(restore_message, R.string.restore_error_words);
        } catch (MnemonicException e) {
            log.info("Error verifying seed: {}", e.getMessage());
            setError(restore_message, R.string.restore_error, e.getMessage());
        }

        if (isSeedValid && seed != null && !seedText.equals(seed.trim())) {
            log.info("Typed seed does not match the generated one.");
            setError(restore_message, R.string.restore_error_mismatch);
            isSeedValid = false;
        }
        return isSeedValid;
    }

    private void clearError(TextView errorView) {
        errorView.setVisibility(View.GONE);
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

    private void handleScan() {
        startActivityForResult(new Intent(mContext, ScanActivity.class), REQUEST_CODE_SCAN);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (requestCode == REQUEST_CODE_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                mnemonicTextView.setText(intent.getStringExtra(ScanActivity.INTENT_EXTRA_RESULT));
                verifyMnemonic();
            }
        }
    }

    private abstract static class SpaceTokenizer implements MultiAutoCompleteTextView.Tokenizer {
        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != ' ') {
                i--;
            }

            return i;
        }

        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == ' ') {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        public CharSequence terminateToken(CharSequence text) {
            onToken();
            return text + " ";
        }

        abstract public void onToken();
    }
}
