package com.example.milkapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.milkapplication.ui.gallery.GalleryFragment;
import com.example.milkapplication.ui.home.HomeFragment;
import com.example.milkapplication.ui.send.SendFragment;
import com.example.milkapplication.ui.share.ShareFragment;
import com.example.milkapplication.ui.slideshow.SlideshowFragment;
import com.example.milkapplication.ui.tools.ToolsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    private DeliveryDBAdapter deliveryDBAdapter;
    private DeliveryAdapter deliveryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        deliveryDBAdapter = new DeliveryDBAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("모든 배달지 삭제");
                builder.setMessage("삭제하면 되돌릴 수 없습니다. 모든 데이터를 삭제하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deliveryDBAdapter.open();
                        deliveryDBAdapter.deleteAllMilk();
                        deliveryDBAdapter.close();
                        alertDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "모든 데이터가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        builder.setTitle(null);
                        builder.setMessage("앱이 종료됩니다.");
                        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.finishAffinity(MainActivity.this);
                            }
                        });
                        builder.setNegativeButton(null, null);
                        alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
                builder.setNegativeButton("취소", null);

                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void onBackPressed() {

        View view = getLayoutInflater().inflate(R.layout.exitdialog, null);

        final Button btnCancel = view.findViewById(R.id.btnCancel);
        final Button btnExit = view.findViewById(R.id.btnExit);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) alertDialog.dismiss();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) alertDialog.dismiss();
                ActivityCompat.finishAffinity(MainActivity.this);
            }
        });

        builder = new AlertDialog.Builder(this);
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
