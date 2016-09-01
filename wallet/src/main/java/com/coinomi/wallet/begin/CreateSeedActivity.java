package com.coinomi.wallet.begin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                Intent intent=new Intent();
                Bundle args = new Bundle();
                args.putString(Constants.ARG_SEED, mnemonicView.getText().toString());
                intent.setClass(mContext,VerificationSeedActivity.class);
                intent.putExtras(args);
                startActivity(intent);
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
