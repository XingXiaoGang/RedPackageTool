package com.xingxiaogang.wxtools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gang.accessibility.AccessibilityService;
import com.gang.accessibility.Statics;
import com.gang.accessibility.tasks.RedPackageTask;
import com.gang.accessibility.utils.AccessibilityUtils;
import com.gang.accessibility.utils.SharePref;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.end).setOnClickListener(this);
        findViewById(R.id.permission).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateState();
    }

    private void updateState() {
        if (AccessibilityUtils.isAccessibilitySettingsOn(this)) {
            ((Button) findViewById(R.id.permission)).setText("授权(已授权)");
        } else {
            ((Button) findViewById(R.id.permission)).setText("授权");
        }
        if (SharePref.getBoolean(this, SharePref.KEY_IS_OPEN, false)) {
            ((Button) findViewById(R.id.start)).setText("开启(已开启)");
        } else {
            ((Button) findViewById(R.id.start)).setText("开启");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.permission: {
                try {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                    Toast.makeText(this, "找到红包助手，打开开关", Toast.LENGTH_LONG).show();
                } catch (Exception ignore) {

                }
                break;
            }
            case R.id.start: {
                if (!AccessibilityUtils.isAccessibilitySettingsOn(this)) {
                    Toast.makeText(this, "请先授权,否则无法实现", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharePref.setBoolean(this, SharePref.KEY_IS_OPEN, true);
                SharePref.setString(this, SharePref.KEY_CURRENT_TASK, RedPackageTask.class.getName());

                Intent intent = new Intent(this, AccessibilityService.class);
                intent.putExtra("task_impl", RedPackageTask.class.getName());
                intent.putExtra(Statics.Key.COMMAND, Statics.START);
                startService(intent);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!MainActivity.this.isFinishing()) {
                            updateState();
                        }
                    }
                }, 500);
                break;
            }
            case R.id.end: {
                SharePref.setBoolean(this, SharePref.KEY_IS_OPEN, false);
                SharePref.setString(this, SharePref.KEY_CURRENT_TASK, "");

                Intent intent = new Intent(this, AccessibilityService.class);
                intent.putExtra(Statics.Key.COMMAND, Statics.STOP);
                startService(intent);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!MainActivity.this.isFinishing()) {
                            updateState();
                        }
                    }
                }, 500);
                break;
            }
        }
    }
}
