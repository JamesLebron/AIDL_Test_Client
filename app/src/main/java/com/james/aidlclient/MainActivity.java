package com.james.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.james.aidlserver.Book;
import com.james.aidlserver.BookManager;

import java.util.List;


/**
 * 客户端AIDL 操作步骤
 * 1.创建ServiceConnection,利用返回的Ibinder 实例化 BookManager
 * 2.在onStart 的时候绑定服务
 *
 * 添加了3个方法
 * addBookIn tag是in
 * addBookOut tag是out
 * addBookInOut tag是inout
 */
public class MainActivity extends AppCompatActivity {

    private boolean mBound;
    private BookManager mBookManager;
    private List<Book> mBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBookManager = BookManager.Stub.asInterface(service);
            mBound = true;
            if (mBookManager != null) {
                try {
                    mBooks = mBookManager.getBooks();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            connectServer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBound) {
            unbindService(mConnection);
        }
    }

    private void connectServer() {
        Intent intent = new Intent();
        intent.setAction("com.james.aidlserver");
        intent.setPackage("com.james.aidlserver");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void addBookIn(View view) {
        if (!mBound) {
            connectServer();
            Toast.makeText(this, "当前与服务端,暂未连接,正在重连,请稍后再试...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBookManager == null)
            return;
        Book book = new Book();
        book.setName("装逼培训宝典In");
        book.setPrice(100);
        try {
            Book returnBook = mBookManager.addBookIn(book);
            Log.i("James", book.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addBookOut(View view) {
        if (!mBound) {
            connectServer();
            Toast.makeText(this, "当前与服务端,暂未连接,正在重连,请稍后再试...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBookManager == null)
            return;
        Book book = new Book();
        book.setName("装逼培训宝典Out");
        book.setPrice(100);
        try {
            Book returnBook = mBookManager.addBookOut(book);
            Log.i("James", book.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addBookInOut(View view) {
        if (!mBound) {
            connectServer();
            Toast.makeText(this, "当前与服务端,暂未连接,正在重连,请稍后再试...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBookManager == null)
            return;
        Book book = new Book();
        book.setName("装逼培训宝典InOut");
        book.setPrice(100);
        try {
            Book returnBook = mBookManager.addBookInout(book);
            Log.i("James", book.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
