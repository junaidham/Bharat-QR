package qrscan.ng.com.ngqrcode;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mastercard.mpqr.pushpayment.exception.FormatException;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.scan.constant.PPIntents;
import com.mastercard.mpqr.pushpayment.utils.CheckSumUtils;

/**
 * https://developer.mastercard.com/mastercard-merchant-presented-qr/documentation/
 * https://github.com/journeyapps/zxing-android-embedded
 *
 * https://developer.mastercard.com/mastercard-merchant-presented-qr/documentation/server-apis/api-reference/retrieval-api/
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button scanner;
    private Button generateQrc;
    private TextView textView1;

    //qr code scanner object
    private IntentIntegrator qrScan;
    private boolean validQrCode;
    String QRCodeString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //View objects
        scanner = (Button) findViewById(R.id.scanner);
        generateQrc = (Button) findViewById(R.id.generatQrc);
        textView1 = (TextView) findViewById(R.id.textView1);


        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        scanner.setOnClickListener(this);
        generateQrc.setOnClickListener(this);

    }




    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //IntentIntegrator
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        PushPaymentData qrCode = (PushPaymentData) data.getSerializableExtra(PPIntents.PUSH_PAYMENT_DATA);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                    //setting values to textviews
                QRCodeString = result.getContents();

               /* try {
                   // String crc = qrCode.getCRC();
                    String merchantCategory = qrCode.getMerchantCategoryCode();
                    String merchantCity = qrCode.getMerchantCity();
                    String postalCode = qrCode.getPostalCode();
                    String merchantName = qrCode.getMerchantName();
                    String additionalData = String.valueOf(qrCode.getAdditionalData());

                   // Log.w("MainActivity","CRC: "+crc);
                    Log.d("MainActivity","merchantCategory: "+merchantCategory);
                    Log.d("MainActivity","merchantCity: "+merchantCity);
                    Log.d("MainActivity","merchantPostalCode: "+postalCode);
                    Log.e("MainActivity","merchantName: "+merchantName);
                    Log.e("MainActivity","additionalData: "+additionalData);
                } catch (FormatException e) {
                    e.printStackTrace();
                }*/


                if(!CheckSumUtils.validateChecksumCRC16(QRCodeString)){
                    Log.e("MainActivity","If-QRCodeString: "+QRCodeString);
                    validQrCode=false;
                    textView1.setText("Invalid QR Code");
                    return;
                }
                else{
                    Log.e("MainActivity","else-QRCodeString: "+QRCodeString);
                    Bundle bun = new Bundle();
                    bun.putString("response", QRCodeString);
                    Intent intent = new Intent(MainActivity.this, QrActivity.class);
                    intent.putExtras(bun);
                    startActivity(intent);
                   // parseQRCode(QRCodeString);
                   // textView1.setText(result.getContents().toString());

                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }




    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        if( view == scanner) {
            qrScan.initiateScan(IntentIntegrator.QR_CODE_TYPES);
            qrScan.setOrientationLocked(true);
            qrScan.addExtra("SCAN_WIDTH",240);
            qrScan.addExtra("SCAN_HEIGHT",240);
            qrScan.addExtra("SCAN_MODE", "QR_CODE_MODE");
        }
        else {
            generateQR();
        }
    }
    public void generateQR(){
        Intent intent = new Intent(MainActivity.this, generateQR.class);
        startActivity(intent);
    }
}


