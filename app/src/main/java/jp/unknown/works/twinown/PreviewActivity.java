package jp.unknown.works.twinown;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.ExtendedMediaEntity;
import uk.co.senab.photoview.PhotoView;

public class PreviewActivity extends AppCompatActivity {
    @Bind(R.id.viewPager) ViewPager viewPager;
    private ArrayList<ExtendedMediaEntity> extendedMediaEntities;
    private int firstPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (extendedMediaEntities == null) {
            //noinspection unchecked
            extendedMediaEntities = (ArrayList<ExtendedMediaEntity>) getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_MEDIA_URLS);
            if (getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_MEDIA_POSITION) != null) {
                firstPosition = (int) getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_MEDIA_POSITION);
            }
        }
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        viewPager.setAdapter(new PreviewPagerAdapter(this, extendedMediaEntities));
        viewPager.setCurrentItem(firstPosition, false);
    }

    class PreviewPagerAdapter extends PagerAdapter {
        private ArrayList<PhotoView> imageViewArrayList = new ArrayList<>();

        public PreviewPagerAdapter(Context context, ArrayList<ExtendedMediaEntity> extendedMediaEntities) {
            for (ExtendedMediaEntity extendedMediaEntity : extendedMediaEntities) {
                View view = getLayoutInflater().inflate(R.layout.preview_image, null);
                PhotoView imageView = (PhotoView) view.findViewById(R.id.previewImageView);
                Picasso.with(context).load(extendedMediaEntity.getMediaURLHttps()).into(imageView);
                imageViewArrayList.add(imageView);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView imageView = imageViewArrayList.get(position);
            if(imageView.getParent() != null) {
                ((ViewGroup) imageView.getParent()).removeView(imageView);
            }
            container.addView(imageView);
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
