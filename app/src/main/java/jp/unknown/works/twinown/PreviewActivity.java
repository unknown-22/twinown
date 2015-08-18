package jp.unknown.works.twinown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PreviewActivity extends AppCompatActivity {
    @Bind(R.id.imageView) ImageView imageView;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (url == null) {
            url = (String) getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_MEDIA_URL);
        }
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        Picasso.with(this).load(url).into(imageView);
    }
}
