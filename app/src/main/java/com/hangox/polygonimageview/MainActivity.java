package com.hangox.polygonimageview;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.hangox.polygonimageview.databinding.ActivityMainBinding;
import com.hangox.polygonimageviewlibrary.PolygonImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private List<Drawable> mDrawableList = new ArrayList<>();

    private ActivityMainBinding mBinding;

    private Drawable createVectorDrawable(@DrawableRes int resId){
        return VectorDrawableCompat.create(getResources(), resId, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mBinding.toolBar);

        Drawable[] drawables ={
                getResources().getDrawable(R.drawable.ic_mask_hex),
                createVectorDrawable(R.drawable.ic_cloud_black_24dp),
                createVectorDrawable(R.drawable.ic_lens_black_24dp),
                createVectorDrawable(R.drawable.ic_star_black_24dp),
                createVectorDrawable(R.drawable.ic_triangle),
        };

        Collections.addAll(mDrawableList,drawables);
        final PolygonImageView polygonView = mBinding.image;
        SeekBar seekBar = mBinding.seekBar;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float mMaxBorderSize =
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,72,getResources().getDisplayMetrics());
            float mMinBorderSize =
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,0,getResources().getDisplayMetrics());
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float precent = progress * 1F / seekBar.getMax();
                mBinding.image.setBorderSize((int) ((mMaxBorderSize -  mMinBorderSize) * precent + mMinBorderSize));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBinding.recycleView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mBinding.recycleView.setAdapter(new ClipImageAdapter());

        mBinding.pickerColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(MainActivity.this)
                        .setTitle("Choose color")
                        .initialColor(mBinding.image.getBorderColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {

                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                mBinding.image.setBorderColor(selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.single_circle:
                startActivity(new Intent(MainActivity.this,CircleDemoActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ClipImageViewHolder extends RecyclerView.ViewHolder{
        private ImageView mImageView;

        public ClipImageViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_view);
            itemView.setTag(this);
        }
    }

    public class ClipImageAdapter extends RecyclerView.Adapter<ClipImageViewHolder> implements View.OnClickListener {

        @Override
        public ClipImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = getLayoutInflater().inflate(R.layout.view_holder_image_view,parent,false);
            ClipImageViewHolder viewHolder = new ClipImageViewHolder(rootView);
            viewHolder.itemView.setOnClickListener(this);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ClipImageViewHolder holder, int position) {
            holder.mImageView.setImageDrawable(mDrawableList.get(position));
        }

        @Override
        public int getItemCount() {
            return mDrawableList.size();
        }

        @Override
        public void onClick(View v) {
            ClipImageViewHolder viewHolder = (ClipImageViewHolder) v.getTag();
            Drawable drawable = mDrawableList.get(viewHolder.getAdapterPosition());
            mBinding.image.setClipImageDrawable(drawable.mutate());
        }
    }
}
