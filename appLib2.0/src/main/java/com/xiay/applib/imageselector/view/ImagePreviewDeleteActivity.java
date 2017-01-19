package com.xiay.applib.imageselector.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiay.applib.R;
import com.xiay.applib.imageselector.model.LocalMedia;
import com.xiay.applib.imageselector.widget.PreviewViewPager;
import com.xiay.applib.ui.dialog.MyDialog;

import java.util.ArrayList;
import java.util.List;

import cn.xiay.dialog.ClickListener;

public class ImagePreviewDeleteActivity extends AppCompatActivity {
    public static final int REQUEST_PREVIEW_DELETE = 68;
    public static final String EXTRA_PREVIEW_LIST = "previewList";
    public static final String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";
    public static final String EXTRA_MAX_SELECT_NUM = "maxSelectNum";
    public static final String EXTRA_POSITION = "position";
    public static final String OUTPUT_LIST = "outputList";
    public static final String IS_PREVIEW = "isPreview";
    private LinearLayout barLayout;
    private Toolbar toolbar;
    private ImageView iv_delete;
    private PreviewViewPager viewPager;
    private int position;
    private List<LocalMedia> selectImages = new ArrayList<>();
    private SimpleFragmentAdapter adapter;
    boolean isPreview,isDelete;

    public static void startPreview(Activity context, List<LocalMedia> images, List<LocalMedia> selectImages, int maxSelectNum, int position,boolean isPreview) {
        Intent intent = getIntent(context, (ArrayList) images, (ArrayList) selectImages, maxSelectNum, position, isPreview);
        context.startActivityForResult(intent, REQUEST_PREVIEW_DELETE);
    }
    public static void startPreview(Activity context,int requestCode, List<LocalMedia> images, List<LocalMedia> selectImages, int maxSelectNum, int position,boolean isPreview) {
        Intent intent = getIntent(context, (ArrayList) images, (ArrayList) selectImages, maxSelectNum, position, isPreview);
        context.startActivityForResult(intent, requestCode);
    }

    @NonNull
    private static Intent getIntent(Activity context, ArrayList images, ArrayList selectImages, int maxSelectNum, int position, boolean isPreview) {
        Intent intent = new Intent(context, ImagePreviewDeleteActivity.class);
        intent.putExtra(EXTRA_PREVIEW_LIST, images);
        intent.putExtra(EXTRA_PREVIEW_SELECT_LIST, selectImages);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        intent.putExtra(IS_PREVIEW, isPreview);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.image_selector_preview_delete);
        initView();
        registerListener();
    }

    public void initView() {
        selectImages = (List<LocalMedia>) getIntent().getSerializableExtra(EXTRA_PREVIEW_SELECT_LIST);
        position = getIntent().getIntExtra(EXTRA_POSITION, 1);
        isPreview = getIntent().getBooleanExtra(IS_PREVIEW,false);
        barLayout = (LinearLayout) findViewById(R.id.bar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle((position + 1) + "/" + selectImages.size());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.image_selector_back);


        viewPager = (PreviewViewPager) findViewById(R.id.preview_pager);
        adapter = new SimpleFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        onSelectNumChange();
    }

    public void registerListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ImagePreviewDeleteActivity.this.position=position;
                toolbar.setTitle(position + 1 + "/" + selectImages.size());

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
            }
        });
        if (!isPreview){
            iv_delete = (ImageView) findViewById(R.id.iv_delete);
            iv_delete.setVisibility(View.VISIBLE);
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isDelete=true;
                    //onDoneClick(true);
                    new MyDialog(ImagePreviewDeleteActivity.this).showCancleAndSure("提示信息","确定要删除该照片吗？","取消","删除",new ClickListener() {
                        @Override
                        public void onClick(View v) {
                            LocalMedia image = selectImages.get(viewPager.getCurrentItem());
                            for (LocalMedia media : selectImages) {
                                if (media.getPath().equals(image.getPath())) {
                                    selectImages.remove(media);
                                    break;
                                }
                            }
                            onSelectNumChange();
                        }
                    });

                }
            });
        }
    }

    public class SimpleFragmentAdapter extends FragmentStatePagerAdapter {
        public SimpleFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ImagePreviewFragment.getInstance(selectImages.get(position).getPath());
        }

        @Override
        public int getCount() {
            return selectImages.size();
        }
        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    public void onSelectNumChange() {
        boolean enable = selectImages.size() != 0;
        if (enable) {
            toolbar.setTitle(position + 1 + "/" + selectImages.size());
            adapter.notifyDataSetChanged();
        } else {
            setResult();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult();
            return true;
        }
        return false;
    }
    public void setResult(){
        if (isDelete){
            Intent intent = new Intent();
            intent.putExtra(OUTPUT_LIST,(ArrayList)selectImages);
            setResult(RESULT_OK,intent);
        }
        finish();
    }
}
