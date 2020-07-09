package com.example.milkapplication.ui.slideshow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.milkapplication.AddActivity;
import com.example.milkapplication.Delivery;
import com.example.milkapplication.DeliveryAdapter;
import com.example.milkapplication.DeliveryDBAdapter;
import com.example.milkapplication.R;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    private ListView listView;
    private Button btnReset, btnAdd;
    private TextView txtEmpty, txtMax;
    private ArrayList<Delivery> deliveryList;

    private boolean added = false;

    private DeliveryDBAdapter deliveryDBAdapter;
    private DeliveryAdapter deliveryAdatper;

    private AlertDialog alertDialog, edit_alertDialog;
    private AlertDialog.Builder builder, edit_builder;
    private View edit_view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        listView = root.findViewById(R.id.listView);
        btnAdd = root.findViewById(R.id.btnAdd);
        btnReset = root.findViewById(R.id.btnReset);
        txtEmpty = root.findViewById(R.id.txtEmpty);

        deliveryDBAdapter = new DeliveryDBAdapter(getActivity());

        deliveryList = new ArrayList<Delivery>();
        loadData();
        deliveryAdatper = new DeliveryAdapter(getActivity(), deliveryList, deliveryDBAdapter, txtEmpty, this);
        listView.setAdapter(deliveryAdatper);

        txtMax = root.findViewById(R.id.txtMax);

        refresh();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("배달 완료");
                builder.setMessage("'"+deliveryList.get(position).getAddress()+"'을 배달 완료하였습니까?");
                final int index = position;
                builder.setPositiveButton("배달 완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = deliveryList.get(index).getAddress();
                        deliveryList.remove(index);
                        deliveryAdatper.notifyDataSetChanged();
                        toast("'"+str+"배달 완료했습니다.", false);
                        if (deliveryList.isEmpty()) txtEmpty.setVisibility(View.VISIBLE);
                        else txtEmpty.setVisibility(View.INVISIBLE);
                        deliveryDBAdapter.open();
                        txtMax.setText("("+deliveryList.size()+"/"+deliveryDBAdapter.getTUECount()+")");
                        deliveryDBAdapter.close();
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.setNeutralButton("금일 우유 수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edit_view = getLayoutInflater().inflate(R.layout.editdialog, null);

                        final EditText edtMilk = edit_view.findViewById(R.id.edtMilk);
                        final EditText edtNumber = edit_view.findViewById(R.id.edtNumber);

                        edtMilk.setText(deliveryList.get(index).getMilk());
                        edtNumber.setText(Integer.toString(deliveryList.get(index).getNumber()));

                        edit_builder = new AlertDialog.Builder(getActivity());
                        edit_builder.setView(edit_view);
                        edit_builder.setTitle("금일 우유 수정");
                        edit_builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (String.valueOf(edtMilk.getText()).equals("") || String.valueOf(edtNumber.getText()).equals("")) toast("우유 종류와 갯수 모두 입력해주십시오.", false);
                                else {
                                    deliveryList.get(index).setMilk(String.valueOf(edtMilk.getText()));
                                    deliveryList.get(index).setNumber(Integer.parseInt(String.valueOf(edtNumber.getText())));
                                    deliveryAdatper.notifyDataSetChanged();
                                    toast("금일만 "+deliveryList.get(index).getAddress()+" 우유를 수정하였습니다.", false);
                                }
                            }
                        });
                        edit_builder.setNegativeButton("취소", null);

                        edit_alertDialog = edit_builder.create();
                        edit_alertDialog.setCancelable(false);
                        edit_alertDialog.show();
                    }
                });

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("초기화");
                builder.setMessage("모든 배달 과정이 초기화됩니다. 초기화하시겠습니까?");
                builder.setPositiveButton("초기화", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refresh();
                        toast("초기화되었습니다.", false);
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                getActivity().startActivity(intent);
                added = true;
            }
        });

        return root;
    }

    /*@Override
    public void onStart() {
        super.onStart();
        refresh();
        toast("onStart()", false);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (added) {
            refresh();
            added = false;
        }
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public void refresh() {
        deliveryList.clear();
        loadData();
        deliveryAdatper.notifyDataSetChanged();
        if (deliveryList.isEmpty()) txtEmpty.setVisibility(View.VISIBLE);
        else txtEmpty.setVisibility(View.INVISIBLE);
        deliveryDBAdapter.open();
        txtMax.setText("("+deliveryList.size()+"/"+deliveryDBAdapter.getTUECount()+")");
        deliveryDBAdapter.close();
    }

    private void loadData() {
        deliveryDBAdapter.open();
        Cursor cursor = deliveryDBAdapter.fetchTuesdayMilk();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String address = cursor.getString(1);
            String password = cursor.getString(2);
            String milk = cursor.getString(3);
            int number = cursor.getInt(4);
            boolean mon = Boolean.parseBoolean(cursor.getString(5));
            boolean tue = Boolean.parseBoolean(cursor.getString(6));
            boolean wed = Boolean.parseBoolean(cursor.getString(7));
            boolean thu = Boolean.parseBoolean(cursor.getString(8));
            boolean fri = Boolean.parseBoolean(cursor.getString(9));
            Delivery delivery = new Delivery(address, password, milk, number, mon, tue, wed, thu, fri);
            deliveryList.add(delivery);
            cursor.moveToNext();
        }
        deliveryDBAdapter.close();
    }

    private void toast(String message, boolean longer) {
        int length;
        if (longer) length = Toast.LENGTH_LONG;
        else length = Toast.LENGTH_SHORT;
        Toast.makeText(getActivity(), message, length).show();
    }
}