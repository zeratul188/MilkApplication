package com.example.milkapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    private EditText edtAddress, edtPassword;
    private CheckBox chkPassword;
    private CheckBox[] chkWeek = new CheckBox[5];
    private EditText[] edtMilk = new EditText[5];
    private EditText[] edtNumber = new EditText[5];
    private Button btnAdd;
    private DeliveryDBAdapter deliveryDBAdapter;
    private DeliveryMonsdayDBAdapter monsdayDBAdapter;
    private DeliveryTuesdayDBAdapter tuesdayDBAdapter;
    private DeliveryWednesdayDBAdapter wednesdayDBAdapter;
    private DeliveryThursdayDBAdapter thursdayDBAdapter;
    private DeliveryFridayDBAdapter fridayDBAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("배달지 추가");

        edtAddress = findViewById(R.id.edtAddress);
        edtPassword = findViewById(R.id.edtPassword);
        chkPassword = findViewById(R.id.chkPassword);
        btnAdd = findViewById(R.id.btnAdd);

        for (int i = 0; i < chkWeek.length; i++) {
            int temp = getResources().getIdentifier("chkWeek"+(i+1), "id", getPackageName());
            chkWeek[i] = findViewById(temp);
            temp = getResources().getIdentifier("edtMilk"+(i+1), "id", getPackageName());
            edtMilk[i] = findViewById(temp);
            temp = getResources().getIdentifier("edtNumber"+(i+1), "id", getPackageName());
            edtNumber[i] = findViewById(temp);
            final int index = i;
            chkWeek[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        chkWeek[index].setTextColor(Color.parseColor("#f0f0f0"));
                        edtMilk[index].setEnabled(true);
                        edtNumber[index].setEnabled(true);
                        edtMilk[index].setHintTextColor(Color.parseColor("#888888"));
                        edtNumber[index].setHintTextColor(Color.parseColor("#888888"));
                    } else {
                        chkWeek[index].setTextColor(Color.parseColor("#444444"));
                        edtMilk[index].setText("");
                        edtNumber[index].setText("");
                        edtMilk[index].setEnabled(false);
                        edtNumber[index].setEnabled(false);
                        edtMilk[index].setHintTextColor(Color.parseColor("#bbbbbb"));
                        edtNumber[index].setHintTextColor(Color.parseColor("#bbbbbb"));
                    }
                }
            });
        }

        deliveryDBAdapter = new DeliveryDBAdapter(this);
        monsdayDBAdapter = new DeliveryMonsdayDBAdapter(this);
        tuesdayDBAdapter = new DeliveryTuesdayDBAdapter(this);
        wednesdayDBAdapter = new DeliveryWednesdayDBAdapter(this);
        thursdayDBAdapter = new DeliveryThursdayDBAdapter(this);
        fridayDBAdapter = new DeliveryFridayDBAdapter(this);

        chkPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chkPassword.setTextColor(Color.parseColor("#f0f0f0"));
                    edtPassword.setHint("현관문 비밀번호");
                    edtPassword.setEnabled(true);
                } else {
                    chkPassword.setTextColor(Color.parseColor("#444444"));
                    edtPassword.setHint("비밀번호 여부 체크");
                    edtPassword.setText("");
                    edtPassword.setEnabled(false);
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(edtAddress.getText()).equals("")) toast("주소를 입력하세요.");
                else if (chkPassword.isChecked() && String.valueOf(edtPassword.getText()).equals("")) toast("비밀번호를 입력하세요.");
                else {
                    if (checkedWeek()) {
                        String address = String.valueOf(edtAddress.getText());
                        String password;
                        if (String.valueOf(edtPassword.getText()).equals("")) password = "";
                        else password = String.valueOf(edtPassword.getText());
                        deliveryDBAdapter.open();
                        deliveryDBAdapter.insertMilk(address, password);
                        deliveryDBAdapter.close();
                        String milk;
                        int number;
                        if (chkWeek[0].isChecked()) {
                            milk = String.valueOf(edtMilk[0].getText());
                            number = Integer.parseInt(String.valueOf(edtNumber[0].getText()));
                            monsdayDBAdapter.open();
                            monsdayDBAdapter.insertMilk(address, password, milk, number);
                            monsdayDBAdapter.close();
                        }
                        if (chkWeek[1].isChecked()) {
                            milk = String.valueOf(edtMilk[1].getText());
                            number = Integer.parseInt(String.valueOf(edtNumber[1].getText()));
                            tuesdayDBAdapter.open();
                            tuesdayDBAdapter.insertMilk(address, password, milk, number);
                            tuesdayDBAdapter.close();
                        }
                        if (chkWeek[2].isChecked()) {
                            milk = String.valueOf(edtMilk[2].getText());
                            number = Integer.parseInt(String.valueOf(edtNumber[2].getText()));
                            wednesdayDBAdapter.open();
                            wednesdayDBAdapter.insertMilk(address, password, milk, number);
                            wednesdayDBAdapter.close();
                        }
                        if (chkWeek[3].isChecked()) {
                            milk = String.valueOf(edtMilk[3].getText());
                            number = Integer.parseInt(String.valueOf(edtNumber[3].getText()));
                            thursdayDBAdapter.open();
                            thursdayDBAdapter.insertMilk(address, password, milk, number);
                            thursdayDBAdapter.close();
                        }
                        if (chkWeek[4].isChecked()){
                            milk = String.valueOf(edtMilk[4].getText());
                            number = Integer.parseInt(String.valueOf(edtNumber[4].getText()));
                            fridayDBAdapter.open();
                            fridayDBAdapter.insertMilk(address, password, milk, number);
                            fridayDBAdapter.close();
                        }
                        toast("'"+edtAddress.getText()+"'를 추가하였습니다.");
                        finish();
                    } else {
                        toast("요일과 우유를 입력해주세요.");
                    }
                }
            }
        });

    }

    private boolean checkedWeek() {
        for (int i = 0; i < chkWeek.length; i++) {
            if (chkWeek[i].isChecked()) {
                if (String.valueOf(edtMilk[i].getText()).equals("") || String.valueOf(edtNumber[i].getText()).equals("")) return false;
            }
        }
        for (int i = 0; i < chkWeek.length; i++) if (chkWeek[i].isChecked()) return true;
        return false;
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
