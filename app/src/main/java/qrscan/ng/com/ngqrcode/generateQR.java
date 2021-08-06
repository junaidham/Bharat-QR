package qrscan.ng.com.ngqrcode;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import qrscan.ng.com.ngqrcode.SystemConstants;
import qrscan.ng.com.ngqrcode.GenerateStringValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class generateQR extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private String qrCodeType;
    private String cardNumber;
    private String merchantName;
    private String merchantCity;
    private String merchantCategoryCode;
    private String merchantCountryCode;
    private String merchantCurrencyCode;
    private double txnAmount;

    private int tip_ConvenienceType;
    private int tip_convenienceFee;

    private String merchantCardType;

    // Additional data
    private String billNumber;
    private String mobileNumber;
    private String consumerId;
    private String referenceId;
    private String purpose;

    private Spinner spnMerchantType;
    private EditText txtCardNumber;
    private EditText txtMerchantName;
    private EditText txtMerchantCity;

    private RadioButton radioStatic;
    private RadioButton radioDynamic;

    private TextView lblTransactionAmt;
    private EditText txtTransactionAmt;

    private Spinner  spnSelectTipcType;
    private TextView lblTipOrConvenienceFee;

    private TextView lblTipOrConvenience;
    private EditText txtTipOrConvenience;

    AutoCompleteTextView txtCountryCode=null;
    AutoCompleteTextView txtCurrencyCode = null;
    AutoCompleteTextView txtMCCode = null;

    private EditText txtBillNumber;
    private EditText txtConsumerId;
    private EditText txtReferenceId;
    private EditText txtMobileNumber;
    private EditText txtPurpose;

   // private ProgressDialog pDialog;
    private Button btnSubmit;

    private ArrayAdapter<String> adapterCountry;
    private ArrayAdapter<String> adapterCountryCode;
    private ArrayAdapter<String> adapterCurrency;
    private ArrayAdapter<String> adapterMcc;

    String[] countryList;
    String[] countryCodeList;
    String[] currencyList;
    String[] mccList;
    String qrCodeText = "";
    String encKey;
    String message;
    int status;


    // Store QR data here in the hashmap
    private HashMap<String,Object> qrData = new HashMap<String, Object>();
    private HashMap<String, String> addData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);
        Log.w("generateQR","--onCreate");
        // Get AutoCompleteTextView reference from xml
        //countryList = getResources().getStringArray(R.array.country_arrays);

        countryCodeList = getResources().getStringArray(R.array.countrycode_arrays);
        currencyList = getResources().getStringArray(R.array.currency_arrays);
        mccList = getResources().getStringArray(R.array.mccode_array);

        txtMerchantName = (EditText) findViewById(R.id.txtMerchantName);
        txtMerchantCity = (EditText) findViewById(R.id.txtMerchantCity);
        spnMerchantType = (Spinner) findViewById(R.id.spnMerchantType);
        txtCountryCode = (AutoCompleteTextView) findViewById(R.id.txtCountryCode);
        txtCurrencyCode = (AutoCompleteTextView) findViewById(R.id.txtCurrencyCode);
        txtMCCode = (AutoCompleteTextView) findViewById(R.id.txtMCCode);

        lblTransactionAmt = (TextView) findViewById(R.id.lblTransactionAmt);
        txtTransactionAmt = (EditText) findViewById( R.id.txtTransactionAmount);
        lblTipOrConvenience = (TextView) findViewById(R.id.lblTipOrConveynaceFee);
        txtTipOrConvenience = (EditText) findViewById(R.id.txtTipOrConvenienceFee);

        lblTipOrConvenienceFee = (TextView) findViewById(R.id.lblTipOrConveynaceFee);
        txtTipOrConvenience = (EditText) findViewById(R.id.txtTipOrConvenienceFee);
        txtCardNumber = (EditText) findViewById(R.id.txtCardNumber);

        radioStatic = (RadioButton) findViewById(R.id.radioStatic);
        radioDynamic = (RadioButton) findViewById(R.id.radioDynamic);

        spnSelectTipcType = (Spinner) findViewById(R.id.spnSelectTipcType);


        txtBillNumber = (EditText) findViewById(R.id.txtBillNumber);
        txtConsumerId = (EditText) findViewById(R.id.txtConsumerId);
        txtMobileNumber = (EditText) findViewById(R.id.txtMobileNumber);
        txtPurpose = (EditText) findViewById(R.id.txtPurpose);
        txtReferenceId = (EditText) findViewById(R.id.txtReferenceId);
        qrCodeType = "2";
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        //Create adapter
      //  adapterCountry = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, countryList);
        adapterCountryCode = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,countryCodeList);
        adapterCurrency = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,currencyList);
        adapterMcc = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,mccList);

        txtCountryCode.setThreshold(1);
        txtCurrencyCode.setThreshold(1);
        txtMCCode.setThreshold(1);


        //Set adapter to AutoCompleteTextView
        txtCountryCode.setAdapter(adapterCountryCode);
        txtCountryCode.setOnItemSelectedListener(this);
        txtCountryCode.setOnItemClickListener(this);

        txtCurrencyCode.setAdapter(adapterCurrency);
        txtCurrencyCode.setOnItemClickListener(this);
        txtCurrencyCode.setOnItemSelectedListener(this);

        txtMCCode.setAdapter(adapterMcc);
        txtMCCode.setOnItemSelectedListener(this);
        txtMCCode.setOnItemClickListener(this);

        spnSelectTipcType.setOnItemSelectedListener(this);
        spnMerchantType.setOnItemSelectedListener(this);
        tip_ConvenienceType = 0;
        btnSubmit.setOnClickListener(this);


    }

    public void onBackPressed(){
        super.onBackPressed();
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioStatic:
                if (checked) {
                    // Static QR code
                    qrCodeType = "1";
                    txtTransactionAmt.setVisibility(View.GONE);
                    lblTransactionAmt.setVisibility(View.GONE);
                    //qrData.put(SystemConstants.Tag01Static,qrCodeType);
                    break;
                }
            case R.id.radioDynamic:
                if (checked) {
                    // Dynamic QR code
                    txtTransactionAmt.setVisibility(View.VISIBLE);
                    lblTransactionAmt.setVisibility(View.VISIBLE);
                    qrCodeType = "2";
                    //qrData.put(SystemConstants.Tag01Dynamic,qrCodeType);
                    break;
                }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String spnText="";
            if (adapterView==spnSelectTipcType)
                spnText = spnSelectTipcType.getItemAtPosition(i).toString();
            if (adapterView==spnMerchantType)
                 merchantCardType = spnMerchantType.getItemAtPosition(i).toString();

            if(spnText.equals("Fixed Tip/Convenience Fee") ||spnText.equals("Tip/Convenience Fee in Percent") ){
                lblTipOrConvenienceFee.setVisibility(View.VISIBLE);
                txtTipOrConvenience.setVisibility(View.VISIBLE);
            }else{
                lblTipOrConvenienceFee.setVisibility(View.GONE);
                txtTipOrConvenience.setVisibility(View.GONE);
            }

        if (spnText.equals("Fixed Tip/Convenience Fee")) {
            tip_ConvenienceType = 2;
        }
        if (spnText.equals("Tip/Convenience Fee in Percent"))
            tip_ConvenienceType=3;
        if(spnText.equals("Tip/Convenienc Fee Form User"))
            tip_ConvenienceType=1;

        if (tip_ConvenienceType==1 || tip_ConvenienceType==2 || tip_ConvenienceType==3 ){
            qrData.put(SystemConstants.Tag55,"0" + String.valueOf(tip_ConvenienceType));
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public void onClick(View view){

        if (view==btnSubmit){
            submitForm();

        }

    }

    public void submitForm(){

        if (validateData()){
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Please wait, Qr Code is genetating...");
            pDialog.setCancelable(false);
            pDialog.show();
            // Store data in bundle and invove next activity
            // qrData.put(SystemConstants.Tag00,"01");
            qrData.put(SystemConstants.Tag01,"QR");
            GenerateStringValue generateStringValue=new GenerateStringValue();
            qrCodeText = generateStringValue.getStringValue(qrData);
            Log.e("generateQR","QR Code: "+ qrCodeText);
//            Toast.makeText(getApplicationContext(),"QR Code Generated : " +qrCodeText ,Toast.LENGTH_SHORT).show();

            final Bundle bun2 = new Bundle();
            bun2.putString("qrstring", qrCodeText);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(generateQR.this, createdQR.class);
                    intent.putExtras(bun2);
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    pDialog.dismiss();

               }
            }, 1000);


        }

    }

    public boolean validateData()
    {

        String countryCode = "";
        String currencyCode = "";
        String merchantCategory = "";
        int addDataLength = 0;
        // Add all the validation here.
/*        if (qrCodeType.equals("1"))
            qrData.put(SystemConstants.Tag01Static,qrCodeType);
        else
            qrData.put(SystemConstants.Tag01Dynamic,qrCodeType);*/

        if(txtCardNumber.getText()==null || txtCardNumber.getText().toString().trim().length()==0)
        {
            Toast.makeText(getApplicationContext(), "Card number cannot be Blank", Toast.LENGTH_LONG).show();
            txtCardNumber.setError("Card number cannot be Blank");
            txtCardNumber.setFocusable(true);
            return false;
        }
        if (radioDynamic.isChecked() && txtTransactionAmt.getText().toString().length()==0 )
        {
            Toast.makeText(getApplicationContext(),"Enter transaction amount",Toast.LENGTH_SHORT).show();
            txtTransactionAmt.setError("Enter transaction amount");
            txtTransactionAmt.setFocusable(true);
            txtTransactionAmt.requestFocus();
            return false;
        }
        if (radioDynamic.isChecked() && txtTransactionAmt.getText()!=null )
        {
            // Toast.makeText(getApplicationContext(),"Enter transaction amount",Toast.LENGTH_SHORT).show();
            if (Float.parseFloat(txtTransactionAmt.getText().toString())<=0.00){
                Toast.makeText(getApplicationContext(),"Enter transaction amount",Toast.LENGTH_SHORT).show();
                txtTransactionAmt.setError("Enter a valid transaction amount");
                txtTransactionAmt.requestFocus();
                return false;
            }
        }

        if( txtMerchantName.getText().toString().length()>0){
            merchantName = txtMerchantName.getText().toString();
            qrData.put(SystemConstants.Tag59,merchantName);
        }
        if (txtMerchantCity.getText().toString().length()>0){
            merchantCity = txtMerchantCity.getText().toString();
            qrData.put(SystemConstants.Tag60,merchantCity);
        }

        if (txtCountryCode.getText().toString().length()>0){
            countryCode = txtCountryCode.getText().toString();
            if (countryCode.indexOf("-")>2) {
                merchantCountryCode = countryCode.substring(countryCode.indexOf("-")+1);
                qrData.put(SystemConstants.Tag58, merchantCountryCode);
            }else
            {
                return false;
            }
        }

        if (txtCurrencyCode.getText().toString().length()>0){
        currencyCode = txtCurrencyCode.getText().toString();
        if(currencyCode.length()>2) {
            merchantCurrencyCode = currencyCode.substring(currencyCode.indexOf("-")+1,currencyCode.length()-1);
            qrData.put(SystemConstants.Tag53, merchantCurrencyCode);
        }
        else {
            return false;
        }
    }
        if(txtMCCode.getText().toString().length()>0){
            merchantCategory = txtMCCode.getText().toString().trim();
            if(merchantCategory.length()>4) {
                merchantCategoryCode = merchantCategory.substring(0, 4);
                qrData.put(SystemConstants.Tag52, merchantCategoryCode);
                Log.d("MCCode", merchantCategoryCode);
            }
            else
            {
                return  false;
            }
        }
        // It can be card or merchant id.
        if (txtCardNumber.getText()!=null){
            cardNumber = txtCardNumber.getText().toString();
            if (merchantCardType.toUpperCase().equals("VISA")){
                qrData.put(SystemConstants.Tag02,cardNumber);
            }
            else if (merchantCardType.toUpperCase().equals("MASTER")){
                qrData.put(SystemConstants.Tag04,cardNumber);
            }
            else if (merchantCardType.toUpperCase().equals("NPCI")){
                qrData.put(SystemConstants.Tag06,cardNumber);
            }
            else if (merchantCardType.toUpperCase().equals("AMEX")){
                qrData.put(SystemConstants.Tag11,cardNumber);
            }
        }

            if (tip_ConvenienceType>0) {
                tip_convenienceFee = Integer.parseInt(txtTipOrConvenience.getText().toString());
                if (tip_convenienceFee > 0) {
                    if (tip_ConvenienceType == 2)
                        qrData.put(SystemConstants.Tag56, tip_convenienceFee);
                    else if (tip_ConvenienceType == 3)
                        qrData.put(SystemConstants.Tag57, tip_convenienceFee);
                } else {
                    txtTipOrConvenience.setError("Enter valid Tip or Convenience Fee");
                    txtTipOrConvenience.setFocusable(true);
                    txtTipOrConvenience.requestFocus();
                    return false;
                }
            }

        // Additional fields
        if  (txtBillNumber.getText().toString().trim().length()> 0) {
            billNumber = txtBillNumber.getText().toString().trim();
            addData.put(SystemConstants.Tag6201,billNumber);
            addDataLength = addDataLength + billNumber.length();
        }
        if ( txtMobileNumber.getText().toString().trim().length()>0){
            mobileNumber = txtMobileNumber.getText().toString();
            addData.put(SystemConstants.Tag6202,mobileNumber);
        }
        if (txtConsumerId.getText().toString().trim().length()>0){
            consumerId = txtConsumerId.getText().toString().trim();
            addData.put(SystemConstants.Tag6206,consumerId);
            addDataLength = addDataLength + consumerId.length();
        }
        if (txtReferenceId.getText().toString().toString().length()>0) {
            referenceId = txtReferenceId.getText().toString().trim();
            addData.put(SystemConstants.Tag6205,referenceId);
            addDataLength = addDataLength + referenceId.length();
        }
        if (txtPurpose.getText().toString().trim().length()>0) {
            purpose = txtPurpose.getText().toString().trim();
            addData.put(SystemConstants.Tag6208,purpose);
            addDataLength = addDataLength + purpose.length();
        }

        if  (addDataLength>89)
        {
            Toast.makeText(getApplicationContext(),"Additional data can not exceed 99 characters",Toast.LENGTH_SHORT).show();
            txtPurpose.setError("Additional Data can not exceed 99 characters");
            txtPurpose.requestFocus();
            return false;
        }
        if (addDataLength>0 && addDataLength<89){
            qrData.put(SystemConstants.Tag62,addData);
        }

        if (radioDynamic.isChecked() && txtTransactionAmt.getText()!=null )
        {
            if (Float.parseFloat(txtTransactionAmt.getText().toString())>0.00){
                txnAmount = Float.parseFloat(txtTransactionAmt.getText().toString());
                qrData.put(SystemConstants.Tag54,txnAmount);
               // qrData.put(SystemConstants.Tag01Dynamic,qrCodeType);
            }
        }

        return true;
    }

   /* RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
    ProgressDialog pDialog = new ProgressDialog(this);

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
                pDialog.dismiss();
            }
        }) {

            *//**
             * Passing some request headers
             * *//*
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
               // headers.put(ConstantsKey.SECURITY_TOKEN, mPreferences.getSecurityToken(ConstantsKey.SECURITY_TOKEN));
                headers.put("DEVICE ID", getDeviceId());
                return headers;
            }
        };

// Adding request to request queue
        MyRequestQueue.add(jsonObjReq);

    }

    private void parseJson(JSONObject response) {
        try {
            JSONObject result = new JSONObject(response.toString());
            status = result.getInt("status");
            if(status == 402) {
                encKey = result.getString("key");
            }
            if(!encKey.equals("")){
                post_Json_Data(getJSONObject(true), URLs.URL_POSTQRCODE);
            }
            message = result.getString("message");



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String getDeviceId() {
        TelephonyManager TM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
// IMEI No.
        String imeiNo = TM.getDeviceId();
        return  imeiNo;
    }

    public JSONObject getJSONObject(boolean isNext) {
        JSONObject postParam = new JSONObject();
        try {
            if(isNext){
                postParam.put("qrString", qrCodeText);
            }else{
                postParam.put("machineId", "123456");
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
        return postParam;
    }*/
}
