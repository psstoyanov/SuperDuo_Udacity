package it.jaschke.alexandria.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;

import java.util.List;

import it.jaschke.alexandria.R;

/**
 * Created by Raz3r on 22/11/2015.
 */
public class Utility {
    private static final String TAG = "Utility";

    private static PackageManager pm;
    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return
     */
    static public boolean isNetworkAvailable(Context c)
    {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    // Check the hardware capabilities.
    // Check if the device has camera.
    static public boolean hasCamera(Context context)
    {
        pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            Log.d(TAG, "It has a camera");
            return true;
        }
        else
        {
            return false;
        }
    }
    // Check if the device supports auto-focus.
    static public boolean hasAutoFocus(Context context)
    {
        pm = context.getPackageManager();
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS))
        {
            Log.d(TAG, "It has a autofocus");
            return true;
        }
        else
        {
            return false;
        }
    }
    //Check if the device has LED flash.
    static public boolean hasFlash(Context context)
    {
        pm = context.getPackageManager();
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
        {
            Log.d(TAG, "It has a flash");
            return true;
        }
        else
        {
            return false;
        }
    }
}
