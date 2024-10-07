package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AchievementFragment extends Fragment {

    private TextView achievement1000m, achievement5000m, achievement10000m;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievement, container, false);

        achievement1000m = view.findViewById(R.id.achievement_1000m);
        achievement5000m = view.findViewById(R.id.achievement_5000m);
        achievement10000m = view.findViewById(R.id.achievement_10000m);

        // 加载成就状态
        loadAchievements();

        return view;
    }

    private void loadAchievements() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Achievements", Context.MODE_PRIVATE);

        // 检查每个成就的状态并动态设置背景颜色
        if (sharedPreferences.getBoolean("achievement_1000m", false)) {
            achievement1000m.setBackgroundColor(Color.GREEN); // 完成的成就背景为绿色
        } else {
            achievement1000m.setBackgroundColor(Color.GRAY); // 未完成的成就背景为灰色
        }

        if (sharedPreferences.getBoolean("achievement_5000m", false)) {
            achievement5000m.setBackgroundColor(Color.GREEN); // 完成的成就背景为绿色
        } else {
            achievement5000m.setBackgroundColor(Color.GRAY); // 未完成的成就背景为灰色
        }

        if (sharedPreferences.getBoolean("achievement_10000m", false)) {
            achievement10000m.setBackgroundColor(Color.GREEN); // 完成的成就背景为绿色
        } else {
            achievement10000m.setBackgroundColor(Color.GRAY); // 未完成的成就背景为灰色
        }
    }
}
