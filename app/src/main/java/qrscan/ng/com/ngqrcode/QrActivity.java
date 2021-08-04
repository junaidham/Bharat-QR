package qrscan.ng.com.ngqrcode;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mastercard.mpqr.pushpayment.exception.ConflictiveTagException;
import com.mastercard.mpqr.pushpayment.exception.FormatException;
import com.mastercard.mpqr.pushpayment.exception.InvalidTagValueException;
import com.mastercard.mpqr.pushpayment.exception.MissingTagException;
import com.mastercard.mpqr.pushpayment.exception.UnknownTagException;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.parser.Parser;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by pramod on 10-10-2017.
 */

public class QrActivity  extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private EditText txtCardNumber;
//    private EditText txtCardNumberMaster;
//    private EditText txtCardNumberNCPI;
    private TextView txtMerchantName;
    private TextView txtMerchantCity;
    private EditText txtTransactionAmount;
    private EditText txtTipOrConvenience;
    private TextView txtBillNumber;
    private TextView txtMobileNumber;
    private TextView txtConsumerId;
    private TextView txtPurpose;
    private TextView lblTipOrConvenience;
    private TextView txtPaidAmount;
    private TextView txtBankIFSCCode;
    private TextView lblBankIFSCCode;
    private TextView txtMCCode;
    private TextView txtCountryCode;
    private TextView txtReferenceId;
    private TextView txtTerminalId;
    private Button btnSubmit;

    AutoCompleteTextView txtCurrencyCode = null;
    private ArrayAdapter<String> adapterCurrency;
    String[] currencyList;
  //  RequestQueue MyRequestQueue;
    ProgressDialog pDialog;

    String response;
    String encKey = "";
    String message;
    int status;
    String encryptData;

    int txnAmount=0;
    String tipType="";
    double tipAmount;
    PushPaymentData qrcode;
    int  activeUrl=0;

    private HashMap<String,Object> qrData = new HashMap<String, Object>();
    private HashMap<String, String> addData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w("QrActivity","--onCreate");
        setContentView(R.layout.activity_qr);
        txtCardNumber = (EditText)findViewById(R.id.txtCardNumber);
//        txtCardNumberMaster = (EditText)findViewById(R.id.txtCardNumber);
//        txtCardNumberNCPI = (EditText)findViewById(R.id.txtCardNumber);
        txtBankIFSCCode = (TextView) findViewById(R.id.txtBankIFSCCode);
        lblBankIFSCCode = (TextView) findViewById(R.id.lblBankIFSCCode);
        txtMCCode = (TextView) findViewById(R.id.txtMCCode);
        txtMerchantName = (TextView) findViewById(R.id.txtMerchantName);
        txtMerchantCity = (TextView) findViewById(R.id.txtMerchantCity);
        txtCountryCode = (TextView) findViewById(R.id.txtCountryCode);
        txtTransactionAmount = (EditText) findViewById(R.id.txtTransactionAmount);
        txtPaidAmount = (TextView) findViewById(R.id.txtPaidAmount);
        txtBillNumber = (TextView) findViewById(R.id.txtBillNumber);
        txtMobileNumber = (TextView) findViewById(R.id.txtMobileNumber);
        txtConsumerId = (TextView) findViewById(R.id.txtConsumerId);
        txtReferenceId = (TextView) findViewById(R.id.txtReferenceId);
        txtTerminalId = (TextView) findViewById(R.id.txtTerminalId);
        txtPurpose = (TextView) findViewById(R.id.txtPurpose);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        currencyList = getResources().getStringArray(R.array.currency_arrays);
        txtCurrencyCode = (AutoCompleteTextView) findViewById(R.id.txtCurrencyCode);
        adapterCurrency = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,currencyList);


        txtCurrencyCode.setAdapter(adapterCurrency);
        txtCurrencyCode.setOnItemClickListener(this);
        txtCurrencyCode.setOnItemSelectedListener(this);

       //  MyRequestQueue = Volley.newRequestQueue(this);
         pDialog = new ProgressDialog(this);

        txtTransactionAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (txtTransactionAmount.isEnabled()==true) {
                  //  String amount = new String(s.toString());
                    String amount;
                    amount = s.toString();
                    if (!amount.equals("")) {
                        if (amount.indexOf(".") == 0)
                            txnAmount = Integer.parseInt(amount);
                        else
                            txnAmount = (int) Float.parseFloat(amount);

                        txtPaidAmount.setText(String.valueOf(CalculateFinalAmount()));
                    }
                }
            }
        });


        lblTipOrConvenience= (TextView) findViewById(R.id.lblTipOrConveynaceFee);
        txtTipOrConvenience = (EditText) findViewById(R.id.txtTipOrConvenienceFee);

        txtTipOrConvenience.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(tipType.equals("01")) {
                    String amount = new String(s.toString());
                    if(!amount.equals("")) {
                        tipAmount = Integer.parseInt(amount);
                        txtPaidAmount.setText(String.valueOf(CalculateFinalAmount()));
                    }
                }
            }
        });



        Bundle bun = getIntent().getExtras();
        if( bun != null ){
            response = bun.getString("response");
            parseQRCode(response);
        }
        Log.w("QrActivity","Bundle-response: "+response);

    }

    private void parseQRCode(String code) {
        //PushPaymentData qrcode;
        String MerchantId="";
        String cardType = "";


        try {
            qrcode = Parser.parseWithoutTagValidation(code);
            qrcode.validate();
            System.out.println("dump_data" + qrcode.dumpData());
            Log.d("QrActivity","dump_data"+qrcode.dumpData());

            if(qrcode.getMerchantIdentifierVisa02()!=null) {
                MerchantId = qrcode.getMerchantIdentifierVisa02(); // Display card number getting the card type
                cardType = "visa";
                qrData.put(SystemConstants.Tag02,MerchantId);

            }
            if (qrcode.getMerchantIdentifierVisa03()!=null) {
                MerchantId = qrcode.getMerchantIdentifierVisa03();
                cardType = "visa";
                qrData.put(SystemConstants.Tag03,MerchantId);
            }
            if(qrcode.getMerchantIdentifierMastercard04()!=null) {
                MerchantId = qrcode.getMerchantIdentifierMastercard04();
                cardType = "master";
                qrData.put(SystemConstants.Tag04,MerchantId);
            }
            if(qrcode.getMerchantIdentifierMastercard05()!=null) {
                MerchantId = qrcode.getMerchantIdentifierMastercard05();
                cardType = "master";
                qrData.put(SystemConstants.Tag05,MerchantId);
            }
            if(qrcode.getMerchantIdentifierNPCI06()!=null) {
                MerchantId = qrcode.getMerchantIdentifierNPCI06();
                cardType = "npci";
                qrData.put(SystemConstants.Tag06,MerchantId);
            }
            if(qrcode.getMerchantIdentifierNPCI07()!=null) {
                MerchantId = qrcode.getMerchantIdentifierNPCI07();
                cardType = "npci";
                qrData.put(SystemConstants.Tag07,MerchantId);
            }
            //Add amex here.

            if(MerchantId!=""){
                txtCardNumber.setText(MerchantId);
                txtCardNumber.setEnabled(false);
            }else{
                //txtCardNumber.setText("Enter Merchant Card Number"); // Create a constant file and get the message from there remove hard coded message
                txtCardNumber.setText( getResources().getString(R.string.merchant_id_prompt));
                txtCardNumber.setEnabled(true);
            }
            /*if(cardType.equals("npci")){
                lblBankIFSCCode.setVisibility(View.VISIBLE);
                txtBankIFSCCode.setVisibility(View.VISIBLE);
                if(qrcode.getMerchantIdentifierNPCI06()!=null)
                    txtBankIFSCCode.setText(qrcode.getMerchantIdentifierNPCI06());
                else
                    txtBankIFSCCode.setText(qrcode.getMerchantIdentifierNPCI07());
            }*/

            if(qrcode.getMerchantName()!=null){
                txtMerchantName.setText(qrcode.getMerchantName());
                txtMerchantName.setEnabled(false);
                qrData.put(SystemConstants.Tag59,qrcode.getMerchantName());
            }
            if(qrcode.getMerchantCity()!=null) {
                txtMerchantCity.setText(qrcode.getMerchantCity());
                txtMerchantCity.setEnabled(false);
                qrData.put(SystemConstants.Tag60,qrcode.getMerchantCity());
            }
            if(qrcode.getCountryCode()!=null) {
                String countryCode = qrcode.getCountryCode();
                txtCountryCode.setText(countryCode);
                qrData.put(SystemConstants.Tag58,countryCode);
                txtCountryCode.setEnabled(false);
                txtCountryCode.setFocusable(false);
            }
            if(qrcode.getMerchantCategoryCode() !=null) {
                String mcCode = qrcode.getMerchantCategoryCode();
                txtMCCode.setText(mcCode);
                qrData.put(SystemConstants.Tag52,mcCode);
                txtMCCode.setEnabled(false);
            }
            if (qrcode.getTransactionCurrencyCode()!=null){
                String txnCurrencyCode = qrcode.getTransactionCurrencyCode();
                txtCurrencyCode.setText(txnCurrencyCode);
                qrData.put(SystemConstants.Tag53,txnCurrencyCode);
                txtCurrencyCode.setEnabled(false);
                txtCurrencyCode.setFocusable(false);
            }

            if(qrcode.getPointOfInitiationMethod().equals("12")) {
                String tAmount;
                tAmount = String.valueOf(qrcode.getTransactionAmount());
                if (tAmount.indexOf(".")==0)
                    txnAmount = Integer.parseInt(tAmount);
                else
                    txnAmount = (int) Float.parseFloat(tAmount);
                //txtTransactionAmount.setText(String.valueOf(qrcode.getTransactionAmount()));
                txtTransactionAmount.setText(String.valueOf(txnAmount));
                txtTransactionAmount.setEnabled(false);
                qrData.put(SystemConstants.Tag54,tAmount);
            }
            else {
                //txtTransactionAmount.setHint("Enter Transaction Amount");
                txtTransactionAmount.setHint(getResources().getString(R.string.amounterror));
                txtTransactionAmount.setEnabled(true);
                txtCurrencyCode.setHint(getResources().getString(R.string.txnCurrencyHint));
                txtCurrencyCode.setEnabled(true);
            }
            Log.e("Tag 55 value " ,  "Tag 55 value " + qrcode.getTipOrConvenienceIndicator());
            if(qrcode.getTipOrConvenienceIndicator() !=null)
            {
                lblTipOrConvenience.setVisibility(View.VISIBLE);
                txtTipOrConvenience.setVisibility(View.VISIBLE);
                txtTipOrConvenience.setEnabled(false);
                if (qrcode.getTipOrConvenienceIndicator().equals("01")) {
                    txtTipOrConvenience.setHint("Enter Tip/Convenience Fee");
                    txtTipOrConvenience.setEnabled(true);
                    tipType="01";
                }
                else if (qrcode.getTipOrConvenienceIndicator().equals("02")) {
                    txtTipOrConvenience.setText(String.valueOf(qrcode.getValueOfConvenienceFeeFixed()));
                    if(qrcode.getValueOfConvenienceFeeFixed()!=null) {
                        Log.e("TipConvenience",qrcode.getValueOfConvenienceFeeFixed().toString() );
                        tipAmount = qrcode.getValueOfConvenienceFeeFixed();
                        Log.e("Tag 56 value ", "Tag 56 value " + qrcode.getValueOfConvenienceFeeFixed());
                        tipType="02";
                        qrData.put(SystemConstants.Tag56,tipAmount );
                    }
                }
                else {
                    lblTipOrConvenience.setText("Tip/Convenience Fee (%age)");
                    txtTipOrConvenience.setText(String.valueOf(qrcode.getValueOfConvenienceFeePercentage()));
                    tipAmount = (double)qrcode.getValueOfConvenienceFeePercentage();
                    tipType="03";
                    qrData.put(SystemConstants.Tag57,tipAmount );

                }
            }else {
                    Log.e("Tag 55 value " ,  "Tag 55 value " + qrcode.getTipOrConvenienceIndicator());
                    //txtTipOrConvenience.setVisibility(View.GONE);
            }
            if(qrcode.getAdditionalData()!=null){
                txtBillNumber.setEnabled(false);
                txtMobileNumber.setEnabled(false);
                txtConsumerId.setEnabled(false);
                txtReferenceId.setEnabled(false);
                txtTerminalId.setEnabled(false);
                txtPurpose.setEnabled(false);
                if (qrcode.getAdditionalData().getBillNumber()!=null){
                    String billNumber = qrcode.getAdditionalData().getBillNumber();
                    txtBillNumber.setText(billNumber);
                    addData.put(SystemConstants.Tag6201,billNumber);
                }
                if (qrcode.getAdditionalData().getMobileNumber()!=null){
                    String mobileNo = qrcode.getAdditionalData().getMobileNumber();
                    txtMobileNumber.setText(mobileNo);
                    addData.put(SystemConstants.Tag6202,mobileNo);
                }
                if(qrcode.getAdditionalData().getConsumerId()!=null){
                    String consumerId = qrcode.getAdditionalData().getConsumerId();
                    txtConsumerId.setText(consumerId);
                    addData.put(SystemConstants.Tag6206,consumerId);
                }
                if(qrcode.getAdditionalData().getReferenceId()!=null){
                    String referenceId = qrcode.getAdditionalData().getReferenceId();
                    txtReferenceId.setText(referenceId);
                    addData.put(SystemConstants.Tag6205,referenceId);
                }
                if(qrcode.getAdditionalData().getTerminalId()!=null){
                    String terminalId = qrcode.getAdditionalData().getTerminalId();
                    txtTerminalId.setText(terminalId);
                    addData.put(SystemConstants.Tag6207,terminalId);
                }
                if (qrcode.getAdditionalData().getPurpose()!=null){
                    String purpose = qrcode.getAdditionalData().getPurpose();
                    txtPurpose.setText(purpose);
                    addData.put(SystemConstants.Tag6208,purpose);

                }

            }
            Log.e("Transaction Amount ", txtTransactionAmount.getText().toString() );
            if((!txtTransactionAmount.getText().toString().equals("")) && txnAmount >=0 ){
                Log.e("Transaction Amount ", txtTransactionAmount.getText().toString() );
                if(qrcode.getTipOrConvenienceIndicator()!=null){
                     txnAmount = Integer.parseInt(txtTransactionAmount.getText().toString());
                     tipType = qrcode.getTipOrConvenienceIndicator();

                    txtPaidAmount.setText(String.valueOf(CalculateFinalAmount()));
                    txtPaidAmount.setEnabled(false);
                }

            }




        } catch (ConflictiveTagException e) {
            System.out.println("ConflictiveTagException : " + e);
        } catch (InvalidTagValueException e) {
            System.out.println("InvalidTagValueException : " + e);
        } catch (MissingTagException e) {
            System.out.println("MissingTagException : " + e);
        } catch (UnknownTagException e) {
            System.out.println("UnknownTagException : " + e.getStackTrace());

        } catch (FormatException e) {
            System.out.println("FormatException : " + e.getStackTrace());
        }
    }

    private int CalculateFinalAmount() {
       // int txnAmount;
      //  String tipType="";
      //  double tipAmount=0;

        int finalTipAmount=0;
        int finalTxnAmount=0;
        int tipAmt = (int)tipAmount;
        if(!tipType.equals("")){
            if(tipType.equals("03"))
                finalTipAmount = txnAmount*tipAmt/100;
            else
                finalTipAmount = tipAmt;
            finalTxnAmount = txnAmount + finalTipAmount;
        }else{
            finalTxnAmount = txnAmount;
        }

        return finalTxnAmount;

    }

    public void onClick(View view){
        Log.d("btnSubmit", "Submit button clicked");
        boolean validData=true;
        if (view==btnSubmit){
            if(qrcode.getPointOfInitiationMethod().equals("11")){
                if (txtTransactionAmount.getText().toString().length()>0) {
                    float amount = Float.parseFloat(txtTransactionAmount.getText().toString());
                    if (amount>0) {
                        //qrcode.setTransactionAmount(amount);
                        qrData.put(SystemConstants.Tag54,amount);

                    }
                    else
                    {
                        txtTransactionAmount.setFocusable(true);
                        //txtTransactionAmount.setError("Enter amount for Static QR");
                        txtTransactionAmount.setError(getResources().getString(R.string.amounterror));
                        txtTransactionAmount.requestFocus();
                        validData=false;

                    }

                }
                else
                {
                    txtTransactionAmount.setFocusable(true);
//                    txtTransactionAmount.setError("Enter amount for Static QR");
                    txtTransactionAmount.setError(getResources().getString(R.string.amounterror));
                    txtTransactionAmount.requestFocus();
                    validData=false;

                }
                String currencyCode = "";
                if (!qrcode.getTransactionCurrencyCode().isEmpty()){
                    currencyCode = qrcode.getTransactionCurrencyCode();
                    qrData.put(SystemConstants.Tag53, currencyCode);
                }
                else{
                    if (txtCurrencyCode.getText().toString().length()>0){
                         currencyCode = txtCurrencyCode.getText().toString();
                        if(currencyCode.length()>2) {
                            String merchantCurrencyCode = currencyCode.substring(currencyCode.indexOf("-")+1,currencyCode.length()-1);
                            qrData.put(SystemConstants.Tag53, merchantCurrencyCode);
                        }
                        else {
                            validData = false;
                        }
                    }
                }


            }
            if(qrcode.getTipOrConvenienceIndicator() != null) {
                if (qrcode.getTipOrConvenienceIndicator().equals("01")) {
                    if (txtTipOrConvenience.getText().toString().length() > 0) {
                        float tamount = Float.parseFloat(txtTipOrConvenience.getText().toString());
                        tipAmount = tamount;
                        qrData.put(SystemConstants.Tag55, "02"); // Createa constant class and all the values remove hard code
                        qrData.put(SystemConstants.Tag56, tipAmount);

                    }
                }
            }
//            Toast.makeText(getApplicationContext(),"Button Clicked",Toast.LENGTH_SHORT).show();
            if(validData==true) {
                if (qrcode.getPointOfInitiationMethod().equals("11")){
                    qrData.put(SystemConstants.Tag01,"QR");
                    if (!addData.isEmpty()){
                        qrData.put(SystemConstants.Tag62,addData);
                    }
                    GenerateStringValue generateStringValue=new GenerateStringValue();
                    response = generateStringValue.getStringValue(qrData);
                }
                activeUrl = 1;
                post_Json_Data(getJSONObject(false), URLs.URL_GETKEY);
            }

        }
    }



    public void post_Json_Data(final JSONObject postParam, String url) {
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, postParam,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());
                        parseJson(response);
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),"Network/Timeout error",Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                // headers.put(ConstantsKey.SECURITY_TOKEN, mPreferences.getSecurityToken(ConstantsKey.SECURITY_TOKEN));
                headers.put("DEVICE ID", getDeviceId());
                return headers;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(jsonObjReq);
    }

    private void parseJson(JSONObject response) {
        try {
            message="";
            status=0;
            JSONObject result = new JSONObject(response.toString());
            status = result.getInt("status");
            if (result.has("key")){
                encKey = result.getString("key");
                post_Json_Data(getJSONObject(true), URLs.URL_POSTQRCODE);
                activeUrl=2;
            }
            message = result.getString("message");


                if(activeUrl==2){
                    if(status==402 ){
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Failed to post due to " + message,Toast.LENGTH_SHORT).show();
                    }
                }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getDeviceId() {
        TelephonyManager TM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // IMEI No.
        String imeiNo = TM.getDeviceId();
        Log.e("QrActivity","--imeiNo:"+imeiNo);
        return  imeiNo;
    }

    public JSONObject getJSONObject(boolean isNext) {
        String Algo1= "AES";
        JSONObject postParam = new JSONObject();
        try {
            if(isNext){
                String byteEncryptedData;
                //encryptData =  AES.encrypt(response);
                byteEncryptedData = AES.encrypt(response, Algo1);

                // TODO: 8/3/2021
                encryptData = Base64.encodeToString(byteEncryptedData.getBytes(),Base64.NO_WRAP);
                postParam.put("qrString", encryptData);

                Log.w("QrActivity","qrString"+encryptData);

            }else{
                postParam.put("machineId", "123456");
            }

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return postParam;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
