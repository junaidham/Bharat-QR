package qrscan.ng.com.ngqrcode;

import android.app.ProgressDialog;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mastercard.mpqr.pushpayment.exception.FormatException;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.parser.Parser;

public class createdQR extends AppCompatActivity {
    ImageView imageView;
    EditText editText;
    ProgressDialog pDialog;

    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_qr);
        Log.d("createdQR","--onCreate");
        imageView = (ImageView)findViewById(R.id.imageView);
        editText = (EditText)findViewById(R.id.qrCodeText);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Generating ...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();

        Bundle bun = getIntent().getExtras();
        if( bun != null ){
            String qrString = bun.getString("qrstring");
            Log.e("onCreate","qrString"+ qrString);
            try {
                bitmap=TextToImageEncode(qrString);
                imageView.setImageBitmap(bitmap);
                pDialog.dismiss();

                PushPaymentData qrcode;
                qrcode = Parser.parseWithoutTagValidation(qrString);
                qrcode.validate();
                Log.w("onCreate","dump_data"+qrcode.dumpData());
                editText.setText(qrcode.dumpData().toString());
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }
        }


    }


        Bitmap TextToImageEncode(String Value) throws WriterException {
            BitMatrix bitMatrix;
            try {
                bitMatrix = new MultiFormatWriter().encode(
                        Value,
                        BarcodeFormat.DATA_MATRIX.QR_CODE,
                        QRcodeWidth, QRcodeWidth, null
                );

            } catch (IllegalArgumentException Illegalargumentexception) {

                return null;
            }
            int bitMatrixWidth = bitMatrix.getWidth();

            int bitMatrixHeight = bitMatrix.getHeight();

            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

            for (int y = 0; y < bitMatrixHeight; y++) {
                int offset = y * bitMatrixWidth;

                for (int x = 0; x < bitMatrixWidth; x++) {

                    pixels[offset + x] = bitMatrix.get(x, y) ?
                            getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

            bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
            return bitmap;
        }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

}
