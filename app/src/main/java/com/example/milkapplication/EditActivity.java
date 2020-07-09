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

    private EditText edtAddress, edtPassword;
    private CheckBox chkPassword;
    private CheckBox[] chkWeek = new CheckBox[5];
    private EditText[] edtMilk = new EditText[5];
    private EditText[] edtNumber = new EditText[5];
    private Button btnEdit;

    private DeliveryDBAdapter deliveryDBAdapter;
    private DeliveryMonsdayDBAdapter monsdayDBAdapter;
    private DeliveryTuesdayDBAdapter tuesdayDBAdapter;
    private DeliveryWednesdayDBAdapter wednesdayDBAdapter;
    private DeliveryThursdayDBAdapter thursdayDBAdapter;
    private DeliveryFridayDBAdapter fridayDBAdapter;
    private int index = 0, type = 9999;

    private String address, password, undo_address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtAddress = findViewById(R.id.edtAddress);
        edtPassword = findViewById(R.id.edtPassword);
        chkPassword = findViewById(R.id.chkPassword);
        btnEdit = findViewById(R.id.btnEdit);

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
                        edtMilk[index].setTextColor(Color.parseColor("#bbbbbb"));
                        edtNumber[index].setTextColor(Color.parseColor("#bbbbbb"));
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

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(edtAddress.getText()).equals("")) toast("주소를 입력하세요.");
                else if (chkPassword.isChecked() && String.valueOf(edtPassword.getText()).equals("")) toast("비밀번호를 입력하세요.");
                else {
                    if (checkedWeek()) {
                        updateData();
                        toast("'"+edtAddress.getText()+"'로 수정하였습니다.");
                        finish();
                    } else toast("요일과 우유를 입력해주세요.");
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

    private void updateData() {
        String add = String.valueOf(edtAddress.getText());
        String pass = String.valueOf(edtPassword.getText());
        String milk;
        int number;
        deliveryDBAdapter.open();
        deliveryDBAdapter.updateMilk(undo_address, add, pass);
        deliveryDBAdapter.close();
        if (chkWeek[0].isChecked()) {
            milk = String.valueOf(edtMilk[0].getText());
            number = Integer.parseInt(String.valueOf(edtNumber[0].getText()));
            monsdayDBAdapter.open();
            if (monsdayDBAdapter.isEmptyAddress(undo_address)) monsdayDBAdapter.insertMilk(add, pass, milk, number);
            else monsdayDBAdapter.updateMilk(undo_address, add, pass, milk, number);
            monsdayDBAdapter.close();
        } else {
            monsdayDBAdapter.open();
            if (!monsdayDBAdapter.isEmptyAddress(undo_address)) monsdayDBAdapter.deleteMilk(undo_address);
            monsdayDBAdapter.close();
        }
        if (chkWeek[1].isChecked()) {
            milk = String.valueOf(edtMilk[1].getText());
            number = Integer.parseInt(String.valueOf(edtNumber[1].getText()));
            tuesdayDBAdapter.open();
            if (tuesdayDBAdapter.isEmptyAddress(undo_address)) tuesdayDBAdapter.insertMilk(add, pass, milk, number);
            else tuesdayDBAdapter.updateMilk(undo_address, add, pass, milk, number);
            tuesdayDBAdapter.close();
        } else {
            tuesdayDBAdapter.open();
            if (!tuesdayDBAdapter.isEmptyAddress(undo_address)) tuesdayDBAdapter.deleteMilk(undo_address);
            tuesdayDBAdapter.close();
        }
        if (chkWeek[2].isChecked()) {
            milk = String.valueOf(edtMilk[2].getText());
            number = Integer.parseInt(String.valueOf(edtNumber[2].getText()));
            wednesdayDBAdapter.open();
            if (wednesdayDBAdapter.isEmptyAddress(undo_address)) wednesdayDBAdapter.insertMilk(add, pass, milk, number);
            else wednesdayDBAdapter.updateMilk(undo_address, add, pass, milk, number);
            wednesdayDBAdapter.close();
        } else {
            wednesdayDBAdapter.open();
            if (!wednesdayDBAdapter.isEmptyAddress(undo_address)) wednesdayDBAdapter.deleteMilk(undo_address);
            wednesdayDBAdapter.close();
        }
        if (chkWeek[3].isChecked()) {
            milk = String.valueOf(edtMilk[3].getText());
            number = Integer.parseInt(String.valueOf(edtNumber[3].getText()));
            thursdayDBAdapter.open();
            if (thursdayDBAdapter.isEmptyAddress(undo_address)) thursdayDBAdapter.insertMilk(add, pass, milk, number);
            else thursdayDBAdapter.updateMilk(undo_address, add, pass, milk, number);
            thursdayDBAdapter.close();
        } else {
            thursdayDBAdapter.open();
            if (!thursdayDBAdapter.isEmptyAddress(undo_address)) thursdayDBAdapter.deleteMilk(undo_address);
            thursdayDBAdapter.close();
        }
        if (chkWeek[4].isChecked()) {
            milk = String.valueOf(edtMilk[4].getText());
            number = Integer.parseInt(String.valueOf(edtNumber[4].getText()));
            fridayDBAdapter.open();
            if (fridayDBAdapter.isEmptyAddress(undo_address)) fridayDBAdapter.insertMilk(add, pass, milk, number);
            else fridayDBAdapter.updateMilk(undo_address, add, pass, milk, number);
            fridayDBAdapter.close();
        } else {
            fridayDBAdapter.open();
            if (!fridayDBAdapter.isEmptyAddress(undo_address)) fridayDBAdapter.deleteMilk(undo_address);
            fridayDBAdapter.close();
        }
    }

    private void loadData() {
        deliveryDBAdapter.open();
        Cursor cursor = deliveryDBAdapter.fetchAddressMilk(getIntent().getStringExtra("address"));
        cursor.moveToFirst();
        address = cursor.getString(1);
        password = cursor.getString(2);
        deliveryDBAdapter.close();
        monsdayDBAdapter.open();
        cursor = monsdayDBAdapter.fetchAddressMilk(getIntent().getStringExtra("address"));
        cursor.moveToFirst();
        if (!monsdayDBAdapter.isEmptyAddress(address)) {
            edtMilk[0].setText(cursor.getString(3));
            edtNumber[0].setText(Integer.toString(cursor.getInt(4)));
            chkWeek[0].setChecked(true);
        }
        monsdayDBAdapter.close();
        tuesdayDBAdapter.open();
        cursor = tuesdayDBAdapter.fetchAddressMilk(getIntent().getStringExtra("address"));
        cursor.moveToFirst();
        if (!tuesdayDBAdapter.isEmptyAddress(address)) {
            edtMilk[1].setText(cursor.getString(3));
            edtNumber[1].setText(Integer.toString(cursor.getInt(4)));
            chkWeek[1].setChecked(true);
        }
        tuesdayDBAdapter.close();
        wednesdayDBAdapter.open();
        cursor = wednesdayDBAdapter.fetchAddressMilk(getIntent().getStringExtra("address"));
        cursor.moveToFirst();
        if (!wednesdayDBAdapter.isEmptyAddress(address)) {
            edtMilk[2].setText(cursor.getString(3));
            edtNumber[2].setText(Integer.toString(cursor.getInt(4)));
            chkWeek[2].setChecked(true);
        }
        wednesdayDBAdapter.close();
        thursdayDBAdapter.open();
        cursor = thursdayDBAdapter.fetchAddressMilk(getIntent().getStringExtra("address"));
        cursor.moveToFirst();
        if (!thursdayDBAdapter.isEmptyAddress(address)) {
            edtMilk[3].setText(cursor.getString(3));
            edtNumber[3].setText(Integer.toString(cursor.getInt(4)));
            chkWeek[3].setChecked(true);
        }
        thursdayDBAdapter.close();
        fridayDBAdapter.open();
        cursor = fridayDBAdapter.fetchAddressMilk(getIntent().getStringExtra("address"));
        cursor.moveToFirst();
        if (!fridayDBAdapter.isEmptyAddress(address)) {
            edtMilk[4].setText(cursor.getString(3));
            edtNumber[4].setText(Integer.toString(cursor.getInt(4)));
            chkWeek[4].setChecked(true);
        }
        fridayDBAdapter.close();
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
