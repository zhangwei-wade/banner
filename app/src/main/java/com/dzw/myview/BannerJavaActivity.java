package com.dzw.myview;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dzw.banner.view.listener.AdapterViewListener;
import com.dzw.banner.view.listener.PagerChangeListener;
import com.dzw.myview.databinding.ActivityBannerMainBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * @author zhangwei on 2021/3/27.
 */
public class BannerJavaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBannerMainBinding viewBind = ActivityBannerMainBinding.inflate(getLayoutInflater());
        setContentView(viewBind.getRoot());
        List array = new ArrayList<Integer>();
        array.add(1);
        array.add(2);
        viewBind.bannerView.setIndicatorDotArray(new Integer[]{R.drawable.dot_normal, R.drawable.dot_focused});
        viewBind.bannerView.addLifecycleObserver(this);
        viewBind.bannerView.setData(array, new AdapterViewListener<Integer>() {
            @Override
            public void onItemClick(Integer data, int position) {
            }

            @Override
            public void loadImage(Integer data, @NotNull ImageView imageView, int position) {
                if (position == 0) {
                    imageView.setImageResource(R.mipmap.iamge_1);
                }
                if (position == 1) {
                    imageView.setImageResource(R.mipmap.iamg_2);
                }
            }
        });

        viewBind.bannerView2.setIndicatorDotArray(new Integer[]{R.drawable.dot_normal, R.drawable.dot_focused});
        viewBind.bannerView2.addLifecycleObserver(this);
        viewBind.bannerView2.setData(array, new AdapterViewListener<Integer>() {
            @Override
            public void onItemClick(Integer data, int position) {
            }

            @Override
            public void loadImage(Integer data, @NotNull ImageView imageView, int position) {
                if (position == 0) {
                    imageView.setImageResource(R.mipmap.iamge_1);
                }
                if (position == 1) {
                    imageView.setImageResource(R.mipmap.iamg_2);
                }
            }
        });


        viewBind.bannerView1.setIndicatorDotArray(new Integer[]{R.drawable.dot_normal, R.drawable.dot_focused});
        viewBind.bannerView1.addLifecycleObserver(this);
        viewBind.bannerView1.setData(array, new AdapterViewListener<Integer>() {
            @Override
            public void onItemClick(Integer data, int position) {
            }

            @Override
            public void loadImage(Integer data, @NotNull ImageView imageView, int position) {
                if (position == 0) {
                    imageView.setImageResource(R.mipmap.iamge_1);
                }
                if (position == 1) {
                    imageView.setImageResource(R.mipmap.iamg_2);
                }
            }
        });

        viewBind.bannerView3.setIndicatorDotArray(new Integer[]{R.drawable.dot_normal, R.drawable.dot_focused});
        viewBind.bannerView3.addLifecycleObserver(this);
        viewBind.bannerView3.setData(array, new AdapterViewListener<Integer>() {
            @Override
            public void onItemClick(Integer data, int position) {
            }

            @Override
            public void loadImage(Integer data, @NotNull ImageView imageView, int position) {
                if (position == 0) {
                    imageView.setImageResource(R.mipmap.iamge_1);
                }
                if (position == 1) {
                    imageView.setImageResource(R.mipmap.iamg_2);
                }
            }
        });
    }
}
