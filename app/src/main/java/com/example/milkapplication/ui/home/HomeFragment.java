package com.example.milkapplication.ui.home;

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

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ListView listView;
    private Button btnReset, btnAdd;
    private TextView txtEmpty;
    private ArrayList<Delivery> deliveryList;

    private DeliveryDBAdapter deliveryDBAdapter;
    private DeliveryAdapter deliveryAdatper;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        listView = root.findViewById(R.id.listView);
        btnAdd = root.findViewById(R.id.btnAdd);
        btnReset = root.findViewById(R.id.btnReset);
        txtEmpty = root.findViewById(R.id.txtEmpty);

        deliveryDBAdapter = new DeliveryDBAdapter(getActivity());

        deliveryList = new ArrayList<Delivery>();
        loadData();
        deliveryAdatper = new DeliveryAdapter(getActivity(), deliveryList, deliveryDBAdapter, txtEmpty);
        listView.setAdapter(deliveryAdatper);

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
                    }
                });
                builder.setNegativeButton("취소", null);

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
                builder.setPositiveButton("배달 완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deliveryList.clear();
                        loadData();
                        deliveryAdatper.notifyDataSetChanged();
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
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        deliveryList.clear();
        loadData();
        if (deliveryList.isEmpty()) txtEmpty.setVisibility(View.VISIBLE);
        else txtEmpty.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        deliveryList.clear();
        loadData();
        deliveryAdatper.notifyDataSetChanged();
        if (deliveryList.isEmpty()) txtEmpty.setVisibility(View.VISIBLE);
        else txtEmpty.setVisibility(View.INVISIBLE);
    }

    private void loadData() {
        deliveryDBAdapter.open();
        Cursor cursor = deliveryDBAdapter.fetchAllMilk();
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