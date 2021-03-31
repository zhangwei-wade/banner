package com.dzw.myview

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dzw.banner.view.listener.AdapterCustomViewListener
import com.dzw.banner.view.listener.AdapterViewListener
import com.dzw.banner.view.listener.PagerChangeListener
import com.dzw.myview.databinding.ActivityBannerMainBinding

class BannerMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBannerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val list = mutableListOf<Int>()
        list.add(10)
        list.add(20)
        list.add(20)
        list.add(20)
        binding.bannerView.apply {
            indicatorDotArray = arrayOf(R.drawable.dot_normal, R.drawable.dot_focused)
            addLifecycleObserver(this@BannerMainActivity)
            setData(list, object : AdapterViewListener<Int> {
                override fun loadImage(data: Int, imageView: ImageView, position: Int) {
                    if (position == 0) {
                        imageView.setImageResource(R.mipmap.iamge_1)
                    }
                    if (position == 1) {
                        imageView.setImageResource(R.mipmap.iamg_2)
                    }
                    if (position == 3) {
                        imageView.setImageResource(R.mipmap.iamg_2)
                    }
                    if (position == 2) {
                        imageView.setImageResource(R.mipmap.iamge_1)
                    }
                }

                override fun onItemClick(data: Int, position: Int) {
                    Toast.makeText(this@BannerMainActivity, "测试$data  $position", Toast.LENGTH_LONG).show()
                }
            })
            pagerChangeListener = object : PagerChangeListener {
                override fun onPagerChange(position: Int) {
                }
            }
        }

        binding.bannerView2.apply {
//            indicatorDotArray = arrayOf(R.drawable.dot_normal, R.drawable.dot_focused)
            addLifecycleObserver(this@BannerMainActivity)
            setData(list,R.layout.banner_item_layout, object : AdapterCustomViewListener<Int> {
                override fun loadImage(data: Int, view: View, position: Int) {
                    val imageView = view.findViewById<ImageView>(R.id.banner_iv)
                    if (position == 0) {
                        imageView.setImageResource(R.mipmap.iamge_1)
                    }
                    if (position == 1) {
                        imageView.setImageResource(R.mipmap.iamg_2)
                    }
                    if (position == 3) {
                        imageView.setImageResource(R.mipmap.iamg_2)
                    }
                    if (position == 2) {
                        imageView.setImageResource(R.mipmap.iamge_1)
                    }
                }

                override fun onItemClick(data: Int, position: Int) {
                    Toast.makeText(this@BannerMainActivity, "测试$data  $position", Toast.LENGTH_LONG).show()
                }
            })
            viewPager2.apply {
                offscreenPageLimit = 1
                val recyclerView = getChildAt(0) as RecyclerView
                recyclerView.apply {
                    val padding = 100
                    // setting padding on inner RecyclerView puts overscroll effect in the right place
                    setPadding(padding, 0, padding, 0)
                    clipToPadding = false
                }
            }
//            val compositePageTransformer = CompositePageTransformer()
//            compositePageTransformer.addTransformer(ScaleInTransformer())
//            compositePageTransformer.addTransformer(ZoomOutPageTransformer())
//            compositePageTransformer.addTransformer(MarginPageTransformer(2))
            viewPager2.setPageTransformer(ZoomOutPageTransformer())
        }

        binding.bannerView1.apply {
            indicatorDotArray = arrayOf(R.drawable.dot_normal, R.drawable.dot_focused)
            addLifecycleObserver(this@BannerMainActivity)
            setData(list, object : AdapterViewListener<Int> {
                override fun loadImage(data: Int, imageView: ImageView, position: Int) {
                    if (position == 0) {
                        imageView.setImageResource(R.mipmap.iamge_1)
                    }
                    if (position == 1) {
                        imageView.setImageResource(R.mipmap.iamg_2)
                    }
                    if (position == 3) {
                        imageView.setImageResource(R.mipmap.iamg_2)
                    }
                    if (position == 2) {
                        imageView.setImageResource(R.mipmap.iamge_1)
                    }
                }

                override fun onItemClick(data: Int, position: Int) {
                    Toast.makeText(this@BannerMainActivity, "测试$data  $position", Toast.LENGTH_LONG).show()
                }
            })
        }


        binding.bannerView3.apply {
            indicatorDotArray = arrayOf(R.drawable.dot_normal, R.drawable.dot_focused)
            addLifecycleObserver(this@BannerMainActivity)
            setData(list, object : AdapterViewListener<Int> {
                override fun loadImage(data: Int, imageView: ImageView, position: Int) {
                    if (position == 0) {
                        imageView.setImageResource(R.mipmap.iamge_1)
                    }
                    if (position == 1) {
                        imageView.setImageResource(R.mipmap.iamg_2)
                    }
                    if (position == 3) {
                        imageView.setImageResource(R.mipmap.iamg_2)
                    }
                    if (position == 2) {
                        imageView.setImageResource(R.mipmap.iamge_1)
                    }
                }

                override fun onItemClick(data: Int, position: Int) {
                    Toast.makeText(this@BannerMainActivity, "测试$data  $position", Toast.LENGTH_LONG).show()
                }
            })
        }

    }
}