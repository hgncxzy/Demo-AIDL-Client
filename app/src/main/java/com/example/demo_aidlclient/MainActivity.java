package com.example.demo_aidlclient;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.xzy.study.aidl.R;

import aidl.xzy.aidl.IPerson;
/**
 * AIDL 客户端
 *
 * @author xzy
 */
public class MainActivity extends Activity {
    private IPerson person;
    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }

        // 因为有可能有多个应用同时进行RPC操作，所以同步该方法
        @Override
        public synchronized void onServiceConnected(ComponentName arg0, IBinder binder) {
            // 获得IPerson接口
            person = IPerson.Stub.asInterface(binder);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.button1);
        //客户端中不需要在manifest中配置 RemoteService
        // intent 中的 action 为服务端 AndroidManifest 中服务定义的 aciton 的名称
        Intent intent = new Intent("forServiceAidl");
        // 包名设置为服务端 app 包名
        intent.setPackage("com.xzy.study.aidlserver");
        bindService(intent, conn, Service.BIND_AUTO_CREATE);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (person != null) {
                    try {
                        // RPC方法调用
                        String name = person.getName();
                        Toast.makeText(MainActivity.this, "远程进程调用成功！值为 ： " + name,
                                Toast.LENGTH_LONG).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "远程进程调用失败！ ",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
}
