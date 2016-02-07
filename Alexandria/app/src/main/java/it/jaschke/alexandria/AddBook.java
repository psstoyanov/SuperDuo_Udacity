package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import it.jaschke.alexandria.Barcode.BarcodeCaptureActivity;
import it.jaschke.alexandria.Utils.Utility;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private EditText ean;
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT = "eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";

    // Barcode scanner variables.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView barcodeValue;


    // intent request code to handle updating play services if needed.
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final int RC_HANDLE_GMS = 9001;






    public AddBook() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ean != null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        getActivity().setTitle(R.string.scan);

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ean = (EditText) rootView.findViewById(R.id.ean);



        ean.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }
                if (ean.length() < 13) {
                    //clearFields();
                    return;
                }
                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });



        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
                ean.setText("");
            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                String eanToRemove = ean.getText().toString();
                if (eanToRemove.length() == 10 && !eanToRemove.startsWith("978")) {
                    eanToRemove = "978" + eanToRemove;
                }
                clearFields();
                ean.setText("");
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, eanToRemove);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);

            }
        });


        Utility.hasAutoFocus(getContext());
        Utility.hasFlash(getContext());

        statusMessage = (TextView) rootView.findViewById(R.id.status_message);

        autoFocus = (CompoundButton) rootView.findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) rootView.findViewById(R.id.use_flash);


        if(Utility.hasCamera(getContext()))
        {
            rootView.findViewById(R.id.scan_button).setVisibility(View.VISIBLE);
            if (Utility.hasFlash(getContext()))
            {
                useFlash.setVisibility(View.VISIBLE);
            }
        }

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {


                // Launch barcode activity.
                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);
                if (Utility.hasAutoFocus(getContext()))
                {
                    intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                }
                //intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
                intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

                startActivityForResult(intent, RC_BARCODE_CAPTURE);

            }
        });


        if (savedInstanceState != null) {
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
        }

        return rootView;
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (ean.getText().length() == 0) {
            return null;
        }
        String eanStr = ean.getText().toString();
        if (eanStr.length() == 10 && !eanStr.startsWith("978")) {
            eanStr = "978" + eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst())
        {
            updateEmptyView();
            return;
        }


        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        // To remove the printl null pointer exception, add ""+ msg*
        // check if the string is not null or it's length is greater than zero.
        Log.d(TAG, "Error authors: " + authors);
        if (authors!= null && authors.length() > 0 )
        {
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
        }



        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            new DownloadImage((ImageView) rootView.findViewById(R.id.bookCover)).execute(imgUrl);
            rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);
        }
        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);


        rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // This code comes from Mobile Vision API.
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    ean.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    // Clear the fields when appropriate.
    private void clearFields() {
        TextView bookTitle = (TextView) rootView.findViewById(R.id.bookTitle);


        if (bookTitle == null || bookTitle.getText().toString().length() > 0)
        {
            ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
            ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
            ((TextView) rootView.findViewById(R.id.authors)).setText("");
            ((TextView) rootView.findViewById(R.id.categories)).setText("");
            rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
        }

    }

    // Show the toast when there is no network.
    private void updateEmptyView()
    {
        if (!Utility.isNetworkAvailable(getActivity()) )
        {
            Context context = getActivity();
            int text = R.string.empty_add_book_list_no_network;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }


}
