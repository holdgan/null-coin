package com.coinomi.wallet.begin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.coinomi.core.coins.CoinType;
import com.coinomi.wallet.Configuration;
import com.coinomi.wallet.Constants;
import com.coinomi.wallet.ExchangeRatesProvider;
import com.coinomi.wallet.R;
import com.coinomi.wallet.WalletApplication;
import com.coinomi.wallet.ui.CoinExchangeListAdapter;
import com.coinomi.wallet.ui.ExchangeRateLoader;
import com.coinomi.wallet.ui.widget.HeaderWithFontIcon;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.coinomi.wallet.ExchangeRatesProvider.getRates;


/**
 * Fragment that restores a wallet
 */
public class SelectCoinsActivty extends Activity {
    private static final Logger log = LoggerFactory.getLogger(SelectCoinsActivty.class);
    //    private Listener mListener;
    private String message;
    private boolean isMultipleChoice;
    private ListView coinList;
    private Button nextButton;

    private Configuration config;
    //    private Activity activity;
    private CoinExchangeListAdapter adapter;
    private Bundle args;
    private Context mContext;
//    private LoaderManager loaderManager;

    private static final int ID_RATE_LOADER = 0;

//    public static Fragment newInstance(Bundle args) {
//        SelectCoinsActivty fragment = new SelectCoinsActivty();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public static Fragment newInstance(String message, boolean isMultipleChoice, Bundle args) {
////        args = args != null ? args : new Bundle();
//        args.putString(Constants.ARG_MESSAGE, message);
//        args.putBoolean(Constants.ARG_MULTIPLE_CHOICE, isMultipleChoice);
//        return newInstance(args);
//    }

    public SelectCoinsActivty() {
        // Required empty public constructor
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (Listener) activity;
//            this.activity = activity;
//            WalletApplication application = (WalletApplication) activity.getApplication();
//            config = application.getConfiguration();
//            loaderManager = getLoaderManager();
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_select_coins_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = SelectCoinsActivty.this;

        args = getIntent().getExtras();
//        args.putString(Constants.ARG_MESSAGE, message);
//        args.putBoolean(Constants.ARG_MULTIPLE_CHOICE, isMultipleChoice);

        WalletApplication application = (WalletApplication) getApplication();
        config = application.getConfiguration();
//            loaderManager = getLoaderManager();

//        if (getArguments() != null) {
//            Bundle args = getArguments();
        isMultipleChoice = args.getBoolean(Constants.ARG_MULTIPLE_CHOICE);
        message = args.getString(Constants.ARG_MESSAGE);
//        }

        adapter = new CoinExchangeListAdapter(mContext, Constants.SUPPORTED_COINS);

        String localSymbol = config.getExchangeCurrencyCode();
        adapter.setExchangeRates(getRates(mContext, localSymbol));
//        loaderManager.initLoader(ID_RATE_LOADER, null, rateLoaderCallbacks);


//        View view = inflater.inflate(R.layout.fragment_select_coins_list, container, false);

        nextButton = (Button) findViewById(R.id.button_next);
        if (isMultipleChoice) {
            nextButton.setEnabled(false);
//            nextButton.setOnClickListener(getNextOnClickListener());
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> ids = new ArrayList<String>();
                    SparseBooleanArray selected = coinList.getCheckedItemPositions();
                    for (int i = 0; i < selected.size(); i++) {
                        if (selected.valueAt(i)) {
                            CoinType type = getCoinType(selected.keyAt(i));
                            ids.add(type.getId());
                        }
                    }
                    selectCoins(ids);
                }
            });
        } else {
            nextButton.setVisibility(View.GONE);
        }

        coinList = (ListView) findViewById(R.id.coins_list);
        // Set header if needed
        if (message != null) {
            HeaderWithFontIcon header = new HeaderWithFontIcon(mContext);
//            header.setFontIcon(R.string.font_icon_coins);
            header.setMessage(R.string.select_coins);
            coinList.addHeaderView(header, null, false);
        } else {
//            View topPaddingView = new View(activity);
//            topPaddingView.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.half_standard_margin));
//            coinList.addHeaderView(topPaddingView, null, false);
        }
        if (isMultipleChoice) coinList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        coinList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                update(position);
            }
        });
        coinList.setAdapter(adapter);


    }

    @Override
    public void onDestroy() {
//        loaderManager.destroyLoader(ID_RATE_LOADER);

        super.onDestroy();
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//
//
//        return view;
//    }

//    private View.OnClickListener getNextOnClickListener() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ArrayList<String> ids = new ArrayList<String>();
//                SparseBooleanArray selected = coinList.getCheckedItemPositions();
//                for (int i = 0; i < selected.size(); i++) {
//                    if (selected.valueAt(i)) {
//                        CoinType type = getCoinType(selected.keyAt(i));
//                        ids.add(type.getId());
//                    }
//                }
//                selectCoins(ids);
//            }
//        };
//    }

    private void update(int currentSelection) {
        if (isMultipleChoice) {
            boolean isCoinSelected = false;
            SparseBooleanArray selected = coinList.getCheckedItemPositions();
            for (int i = 0; i < selected.size(); i++) {
                if (selected.valueAt(i)) {
                    isCoinSelected = true;
                    break;
                }
            }
            nextButton.setEnabled(isCoinSelected);
        } else if (currentSelection >= 0) {
            CoinType type = getCoinType(currentSelection);
            selectCoins(Lists.newArrayList(type.getId()));
        }
    }

    private CoinType getCoinType(int position) {
        return (CoinType) coinList.getItemAtPosition(position);
    }

    private void selectCoins(ArrayList<String> ids) {
//        if (mListener != null) {
//            Bundle args = getArguments() == null ? new Bundle() : getArguments();
        args.putStringArrayList(Constants.ARG_MULTIPLE_COIN_IDS, ids);

//            args.putString(Constants.ARG_MESSAGE, message);
//            args.putBoolean(Constants.ARG_MULTIPLE_CHOICE, isMultipleChoice);

//            mListener.onCoinSelection(args);
        Intent intent = new Intent();
        intent.setClass(mContext, FinalizeWalletRestorationActivity.class);
        intent.putExtras(args);
        startActivity(intent);
//        }
    }


//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    public interface Listener {
        public void onCoinSelection(Bundle args);
    }

    private final LoaderManager.LoaderCallbacks<Cursor> rateLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
            String localCurrency = config.getExchangeCurrencyCode();
            return new ExchangeRateLoader(mContext, config, localCurrency);
        }

        @Override
        public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
            if (data != null && data.getCount() > 0) {
                List<ExchangeRatesProvider.ExchangeRate> rates = new ArrayList<ExchangeRatesProvider.ExchangeRate>(data.getCount());

                data.moveToFirst();
                do {
                    ExchangeRatesProvider.ExchangeRate exchangeRate = ExchangeRatesProvider.getExchangeRate(data);
                    rates.add(exchangeRate);
                } while (data.moveToNext());

                adapter.setExchangeRates(rates);
            }
        }

        @Override
        public void onLoaderReset(final Loader<Cursor> loader) {
        }
    };
}