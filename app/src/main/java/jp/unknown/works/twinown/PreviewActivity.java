package jp.unknown.works.twinown;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.ExtendedMediaEntity;

public class PreviewActivity extends AppCompatActivity {
    @Bind(R.id.viewPager) ViewPager viewPager;
    private ArrayList<ExtendedMediaEntity> extendedMediaEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (extendedMediaEntities == null) {
            //noinspection unchecked
            extendedMediaEntities = (ArrayList<ExtendedMediaEntity>) getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_MEDIA_URLS);
        }
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        viewPager.setAdapter(new PreviewPagerAdapter(this, extendedMediaEntities));
    }

    class PreviewPagerAdapter extends PagerAdapter {
        private ArrayList<ImageView> imageViewArrayList = new ArrayList<>();

        public PreviewPagerAdapter(Context context, ArrayList<ExtendedMediaEntity> extendedMediaEntities) {
            for (ExtendedMediaEntity extendedMediaEntity : extendedMediaEntities) {
                ImageView imageView = new ImageView(context);
                imageView.setPadding(10, 10, 10, 10);
                Picasso.with(context).load(extendedMediaEntity.getMediaURLHttps()).into(imageView);
                imageViewArrayList.add(imageView);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViewArrayList.get(position));
            return imageViewArrayList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageViewArrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }
}
