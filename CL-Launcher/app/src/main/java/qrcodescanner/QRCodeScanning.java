package qrcodescanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by IMFCORP\alok.acharya on 30/3/17.
 */

public class QRCodeScanning extends Activity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    private static final int REQUEST_WRITE_PERMISSION = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QrScanner();
    }

    public void QrScanner(){

        // Start camera

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(QRCodeScanning.this, new
                    String[]{Manifest.permission.CAMERA}, REQUEST_WRITE_PERMISSION);
        }else{
           setCameraView();
        }

    }


    private void setCameraView(){
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   setCameraView();
                } else {
                    Toast.makeText(QRCodeScanning.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mScannerView!=null)
            mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);

        Intent resultIntent = new Intent();
        if(resultIntent!=null){
            resultIntent.putExtra("DATA",rawResult.getText());
            setResult(RESULT_OK,resultIntent);
        }
        finish();
    }
}
