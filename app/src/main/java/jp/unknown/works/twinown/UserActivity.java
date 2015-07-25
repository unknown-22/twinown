package jp.unknown.works.twinown;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.User;

public class UserActivity extends AppCompatActivity {
    static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        setContentView(R.layout.activity_user);
    }

    public static class UserFragment extends Fragment {
        @Bind(R.id.statusIconView) ImageView statusIconView;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_user, container, false);
            ButterKnife.bind(this, view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusIconView.setTransitionName(Utils.SHARED_ELEMENT_NAME_STATUS_ICON);
            }
            Picasso.with(getActivity()).load(user.getBiggerProfileImageURL()).into(statusIconView);
            return view;
        }
    }
}
