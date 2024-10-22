package com.example.myapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("运动"));
        tabLayout.addTab(tabLayout.newTab().setText("成就"));

        // 默认加载界面
        loadFragment(new RunFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // 运动
                        loadFragment(new RunFragment());
                        break;
                    case 1: // 成就
                        loadFragment(new AchievementFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
