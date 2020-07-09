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

import com.example.milkapplication.ui.gallery.GalleryFragment;
import com.example.milkapplication.ui.home.HomeFragment;
import com.example.milkapplication.ui.send.SendFragment;
import com.example.milkapplication.ui.share.ShareFragment;
import com.example.milkapplication.ui.slideshow.SlideshowFragment;
import com.example.milkapplication.ui.tools.ToolsFragment;

import java.util.ArrayList;

public class DeliveryAllAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Delivery> deliveryList;
    private DeliveryDBAdapter deliveryDBAdapter;
    private TextView txtEmpty;
    private Object object = null;

    private DeliveryMonsdayDBAdapter monsdayDBAdapter;
    private DeliveryTuesdayDBAdapter tuesdayDBAdapter;
    private DeliveryWednesdayDBAdapter wednesdayDBAdapter;
    private DeliveryThursdayDBAdapter thursdayDBAdapter;
    private DeliveryFridayDBAdapter fridayDBAdapter;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    public DeliveryAllAdapter(Context context, ArrayList<Delivery> deliveryList, DeliveryDBAdapter deliveryDBAdapter, TextView txtEmpty, Object object) {
        this.context = context;
        this.deliveryList = deliveryList;
        this.deliveryDBAdapter = deliveryDBAdapter;
        this.txtEmpty = txtEmpty;
        this.object = object;
        deliveryDBAdapter = new DeliveryDBAdapter(context);
        monsdayDBAdapter = new DeliveryMonsdayDBAdapter(context);
        tuesdayDBAdapter = new DeliveryTuesdayDBAdapter(context);
        wednesdayDBAdapter = new DeliveryWednesdayDBAdapter(context);
        thursdayDBAdapter = new DeliveryThursdayDBAdapter(context);
        fridayDBAdapter = new DeliveryFridayDBAdapter(context);
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
        if (convertView == null) convertView = View.inflate(context, R.layout.item_all, null);

        TextView txtAddress = convertView.findViewById(R.id.txtAddress);
        TextView txtPassword = convertView.findViewById(R.id.txtPasssword);
        ImageView imgDelete = convertView.findViewById(R.id.imgDelete);
        ImageView imgEdit = convertView.findViewById(R.id.imgEdit);

        txtAddress.setText(deliveryList.get(position).getAddress());
        txtPassword.setText(deliveryList.get(position).getPassword());

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(context);
                builder.setTitle("배달지 삭제");
                builder.setMessage("'"+deliveryList.get(position).getAddress()+"'을 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toast("'"+deliveryList.get(position).getAddress()+"'을 삭제하였습니다.", false);

                        deliveryDBAdapter.open();
                        deliveryDBAdapter.deleteMilk(deliveryList.get(position).getAddress());
                        deliveryDBAdapter.close();

                        monsdayDBAdapter.open();
                        if (!monsdayDBAdapter.isEmptyAddress(deliveryList.get(position).getAddress())) monsdayDBAdapter.deleteMilk(deliveryList.get(position).getAddress());
                        monsdayDBAdapter.close();
                        tuesdayDBAdapter.open();
                        if (!tuesdayDBAdapter.isEmptyAddress(deliveryList.get(position).getAddress())) tuesdayDBAdapter.deleteMilk(deliveryList.get(position).getAddress());
                        tuesdayDBAdapter.close();
                        wednesdayDBAdapter.open();
                        if (!wednesdayDBAdapter.isEmptyAddress(deliveryList.get(position).getAddress())) wednesdayDBAdapter.deleteMilk(deliveryList.get(position).getAddress());
                        wednesdayDBAdapter.close();
                        thursdayDBAdapter.open();
                        if (!thursdayDBAdapter.isEmptyAddress(deliveryList.get(position).getAddress())) thursdayDBAdapter.deleteMilk(deliveryList.get(position).getAddress());
                        thursdayDBAdapter.close();
                        fridayDBAdapter.open();
                        if (!fridayDBAdapter.isEmptyAddress(deliveryList.get(position).getAddress())) fridayDBAdapter.deleteMilk(deliveryList.get(position).getAddress());
                        fridayDBAdapter.close();

                        deliveryList.remove(position);

                        if (deliveryList.isEmpty()) txtEmpty.setVisibility(View.VISIBLE);
                        else txtEmpty.setVisibility(View.INVISIBLE);

                        if (object instanceof HomeFragment) ((HomeFragment) object).refresh();
                        else if (object instanceof GalleryFragment) ((GalleryFragment) object).refresh();
                        else if (object instanceof SendFragment) ((SendFragment) object).refresh();
                        else if (object instanceof ShareFragment) ((ShareFragment) object).refresh();
                        else if (object instanceof SlideshowFragment) ((SlideshowFragment) object).refresh();
                        else if (object instanceof ToolsFragment) ((ToolsFragment) object).refresh();
                        else toast("Error null", false);

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
                if (object instanceof HomeFragment) {
                    ((HomeFragment) object).setAdded(true);
                }
                else if (object instanceof GalleryFragment) {
                    ((GalleryFragment) object).setAdded(true);
                }
                else if (object instanceof SendFragment) {
                    ((SendFragment) object).setAdded(true);
                }
                else if (object instanceof ShareFragment) {
                    ((ShareFragment) object).setAdded(true);
                }
                else if (object instanceof SlideshowFragment) {
                    ((SlideshowFragment) object).setAdded(true);
                }
                else if (object instanceof ToolsFragment)  {
                    ((ToolsFragment) object).setAdded(true);
                }
                else toast("Error null", false);

                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("address", deliveryList.get(position).getAddress());
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
