package com.example.milkapplication;

import android.database.Cursor;
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

import com.example.milkapplication.ui.gallery.GalleryFragment;
import com.example.milkapplication.ui.home.HomeFragment;

import java.io.Serializable;

public class EditActivity extends AppCompatActivity implements Serializable {

    private EditText edtAddress, edtPassword, edtMilk, edtNumber;
    private CheckBox chkPassword;
    private CheckBox[] chkWeek = new CheckBox[5];
    private Button btnEdit;

    private DeliveryDBAdapter deliveryDBAdapter;
    private int index = 0;

    private String address, password, milk, number, undo_address;
    private boolean[] weeks = new boolean[5];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtAddress = findViewById(R.id.edtAddress);
        edtPassword = findViewById(R.id.edtPassword);
        edtMilk = findViewById(R.id.edtMilk);
        edtNumber = findViewById(R.id.edtNumber);
        chkPassword = findViewById(R.id.chkPassword);
        btnEdit = findViewById(R.id.btnEdit);

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
        index = getIntent().getIntExtra("index", 0);
        loadData();

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

        undo_address = address;
        edtAddress.setText(address);
        setTitle(address+" 수정");
        if (!password.equals("") && password != null) {
            edtPassword.setText(password);
            edtPassword.setEnabled(true);
            chkPassword.setChecked(true);
        } else {
            edtPassword.setText("");
            edtPassword.setEnabled(false);
            chkPassword.setChecked(false);
        }
        edtMilk.setText(milk);
        edtNumber.setText(number);
        for (int i = 0; i < chkWeek.length; i++) {
            if (weeks[i]) chkWeek[i].setChecked(true);
            else chkWeek[i].setChecked(false);
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(edtAddress.getText()).equals("")) toast("주소를 입력하세요.");
                else if (chkPassword.isChecked() && String.valueOf(edtPassword.getText()).equals("")) toast("비밀번호를 입력하세요.");
                else if (String.valueOf(edtMilk.getText()).equals("")) toast("우유를 입력하세요.");
                else if (String.valueOf(edtNumber.getText()).equals("")) toast("갯수를 입력하세요.");
                else if (!checkedWeek()) toast("요일을 선택하세요. (중복가능)");
                else {
                    updateData();
                    toast("'"+edtAddress.getText()+"'로 수정하였습니다.");
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

    private void updateData() {
        String add = String.valueOf(edtAddress.getText());
        String pass = String.valueOf(edtPassword.getText());
        String mil = String.valueOf(edtMilk.getText());
        int num = Integer.parseInt(String.valueOf(edtNumber.getText()));
        String[] week = new String[5];
        for (int i = 0; i < week.length; i++) week[i] = Boolean.toString(chkWeek[i].isChecked());
        deliveryDBAdapter.open();
        deliveryDBAdapter.updateMilk(undo_address, add, pass, mil, num, week[0], week[1], week[2], week[3], week[4]);
        deliveryDBAdapter.close();
    }

    private void loadData() {
        deliveryDBAdapter.open();
        Cursor cursor = deliveryDBAdapter.fetchAddressMilk(getIntent().getStringExtra("address"));
        cursor.moveToFirst();
        address = cursor.getString(1);
        password = cursor.getString(2);
        milk = cursor.getString(3);
        number = Integer.toString(cursor.getInt(4));
        for (int i = 0; i < weeks.length; i++) weeks[i] = Boolean.parseBoolean(cursor.getString(i+5));
        deliveryDBAdapter.close();
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
