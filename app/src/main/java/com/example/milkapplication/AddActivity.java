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

    private EditText edtAddress, edtPassword, edtMilk, edtNumber;
    private CheckBox chkPassword;
    private CheckBox[] chkWeek = new CheckBox[5];
    private Button btnAdd;
    private DeliveryDBAdapter deliveryDBAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("배달지 추가");

        edtAddress = findViewById(R.id.edtAddress);
        edtPassword = findViewById(R.id.edtPassword);
        edtMilk = findViewById(R.id.edtMilk);
        edtNumber = findViewById(R.id.edtNumber);
        chkPassword = findViewById(R.id.chkPassword);
        btnAdd = findViewById(R.id.btnAdd);

        for (int i = 0; i < chkWeek.length; i++) {
            int temp = getResources().getIdentifier("chkWeek"+(i+1), "id", getPackageName());
            chkWeek[i] = findViewById(temp);
            final int index = i;
            chkWeek[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) chkWeek[index].setTextColor(Color.parseColor("#f0f0f0"));
                    else chkWeek[index].setTextColor(Color.parseColor("#444444"));
                }
            });
        }

        deliveryDBAdapter = new DeliveryDBAdapter(this);

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
                else if (String.valueOf(edtMilk.getText()).equals("")) toast("우유를 입력하세요.");
                else if (String.valueOf(edtNumber.getText()).equals("")) toast("갯수를 입력하세요.");
                else if (!checkedWeek()) toast("요일을 선택하세요. (중복가능)");
                else {
                    String address = String.valueOf(edtAddress.getText());
                    String password;
                    if (String.valueOf(edtPassword.getText()).equals("")) password = "";
                    else password = String.valueOf(edtPassword.getText());
                    String milk = String.valueOf(edtMilk.getText());
                    int number = Integer.parseInt(String.valueOf(edtNumber.getText()));
                    String[] weeks = new String[5];
                    for (int i = 0; i < weeks.length; i++) weeks[i] = Boolean.toString(chkWeek[i].isChecked());
                    deliveryDBAdapter.open();
                    deliveryDBAdapter.insertMilk(address, password, milk, number, weeks);
                    deliveryDBAdapter.close();
                    toast("'"+edtAddress.getText()+"'를 추가하였습니다.");
                    finish();
                }
            }
        });

    }

    private boolean checkedWeek() {
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
