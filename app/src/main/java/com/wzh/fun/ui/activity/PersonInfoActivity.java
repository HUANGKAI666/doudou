package com.wzh.fun.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.wzh.fun.R;
import com.wzh.fun.ui.fragment.circleFragment.PersonFragment;



public class PersonInfoActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PersonFragment fragment = new PersonFragment();
        fragmentTransaction.add(R.id.circle,fragment);
        fragmentTransaction.commit();
    }
}
