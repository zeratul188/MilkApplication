package com.example.milkapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeliveryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Delivery> deliveryList;
    private DeliveryDBAdapter deliveryDBAdapter;
    private TextView txtEmpty;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    public DeliveryAdapter(Context context, ArrayList<Delivery> deliveryList, DeliveryDBAdapter deliveryDBAdapter, TextView txtEmpty) {
        this.context = context;
        this.deliveryList = deliveryList;
        this.deliveryDBAdapter = deliveryDBAdapter;
        this.txtEmpty = txtEmpty;
    }

    @Override
    public int getCount() {
        return deliveryList.size();
    }

    @Override
    public Object getItem(int position) {
        return deliveryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = View.inflate(context, R.layout.item, null);

        TextView txtMilk = convertView.findViewById(R.id.txtMilk);
        TextView txtAddress = convertView.findViewById(R.id.txtAddress);
        TextView txtPassword = convertView.findViewById(R.id.txtPasssword);
        TextView txtNumber = convertView.findViewById(R.id.txtNumber);
        ImageView imgDelete = convertView.findViewById(R.id.imgDelete);
        ImageView imgEdit = convertView.findViewById(R.id.imgEdit);

        txtAddress.setText(deliveryList.get(position).getAddress());
        txtMilk.setText(deliveryList.get(position).getMilk());
        txtPassword.setText(deliveryList.get(position).getPassword());
        txtNumber.setText(Integer.toString(deliveryList.get(position).getNumber()));

        final int index = position;

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(context);
                builder.setTitle("배달지 삭제");
                builder.setMessage("'"+deliveryList.get(position).getAddress()+"'을 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toast("'"+deliveryList.get(index).getAddress()+"'을 삭제하였습니다.", false);

                        deliveryDBAdapter.open();
                        deliveryDBAdapter.deleteMilk(deliveryList.get(index).getAddress());
                        deliveryDBAdapter.close();
                        deliveryList.remove(index);

                        if (deliveryList.isEmpty()) txtEmpty.setVisibility(View.VISIBLE);
                        else txtEmpty.setVisibility(View.INVISIBLE);

                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("index", index+1);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private void toast(String message, boolean longer) {
        int length;
        if (longer) length = Toast.LENGTH_LONG;
        else length = Toast.LENGTH_SHORT;
        Toast.makeText(context, message, length).show();
    }
}