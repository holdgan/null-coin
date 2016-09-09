package com.coinomi.wallet.begin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.coinomi.wallet.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class WelcomeActivity extends Activity {
    private static final Logger log = LoggerFactory.getLogger(WelcomeActivity.class);


    public WelcomeActivity() {
    }

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = WelcomeActivity.this;


        findViewById(R.id.create_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, CreateSeedActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.restore_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle args = new Bundle();
                args.putString("verification", "restore");
                intent.setClass(mContext, VerificationSeedActivity.class);
                intent.putExtras(args);
                startActivity(intent);
            }
        });

    }


}
