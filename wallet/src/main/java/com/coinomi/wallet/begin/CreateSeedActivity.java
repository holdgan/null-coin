package com.coinomi.wallet.begin;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coinomi.core.wallet.Wallet;
import com.coinomi.wallet.Constants;
import com.coinomi.wallet.R;

/**
 * Created by sun on 16/9/1.
 */
public class CreateSeedActivity extends Activity {
    private Context mContext;
    private boolean hasExtraEntropy = false;
    private TextView mnemonicView;
    private Button bt_createseed_next;
    private Button bt_createseed_copy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createseed);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        AppManager.getAppManager().addActivity(this);
        mContext = CreateSeedActivity.this;
//        sp = getSharedPreferences("User", Context.MODE_PRIVATE);

        mnemonicView = (TextView) findViewById(R.id.seed);
        bt_createseed_next = (Button) findViewById(R.id.bt_createseed_next);
        bt_createseed_copy = (Button) findViewById(R.id.bt_createseed_copy);

        generateNewMnemonic();

        View.OnClickListener generateNewSeedListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateNewMnemonic();
            }
        };

        bt_createseed_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle args = new Bundle();
                args.putString(Constants.ARG_SEED, mnemonicView.getText().toString());
                args.putString("verification", "create");
                intent.setClass(mContext, VerificationSeedActivity.class);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
        bt_createseed_copy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    cm.setText(mnemonicView.getText());
                    Toast.makeText(mContext, "已复制到粘贴板", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mnemonicView.setOnClickListener(generateNewSeedListener);
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
}
