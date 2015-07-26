package jp.unknown.works.twinown.twinown_views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import jp.unknown.works.twinown.R;
import jp.unknown.works.twinown.Utils;
import jp.unknown.works.twinown.models.UserPreference;
import jp.unknown.works.twinown.twinown_twitter.Component;
import jp.unknown.works.twinown.twinown_twitter.TwinownHelper;
import twitter4j.Status;


public class TweetFragment extends Fragment {
    List<UserPreference> userPreferenceList;
    int currentUserIndex = 0;
    Status toReplyStatus;
    @Bind(R.id.userIconButton) ImageButton userIconButton;
    @Bind(R.id.editText) EditText tweetEditText;
    @Bind(R.id.tweetLength) TextView tweetLength;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        userPreferenceList = UserPreference.getAll();
        toReplyStatus = (Status) getActivity().getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_STATUS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);
        ButterKnife.bind(this, view);
        if (tweetEditText.length() == 0 && toReplyStatus != null) {
            tweetEditText.setText(String.format("@%s %s", toReplyStatus.getUser().getScreenName(), tweetEditText.getText().toString()));
            tweetEditText.setSelection(tweetEditText.getText().toString().length());
        }
        tweetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tweetLength.setText(String.valueOf(140 - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
            return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        TwinownHelper.getUser(userPreferenceList.get(currentUserIndex));
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.tweetButton)
    public void statusUpdate() {
        if (tweetEditText.length() != 0) {
            TwinownHelper.updateStatus(userPreferenceList.get(currentUserIndex), tweetEditText.getText().toString(), toReplyStatus);
            getActivity().finish();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.userIconButton)
    public void onChangeAccount() {
        final String[] userScreenNameList = new String[userPreferenceList.size()];
        for(int i = 0; i < userPreferenceList.size(); i++) {
            userScreenNameList[i] = String.format("@%s", userPreferenceList.get(i).screenName);
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.action_change_account))
                .setItems(userScreenNameList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentUserIndex = which;
                        TwinownHelper.getUser(userPreferenceList.get(currentUserIndex));
                        Utils.showToastLong(
                                getActivity(),
                                String.format(getString(R.string.notice_change_account), userScreenNameList[which])
                        );
                    }
                })
                .show();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Component.UserEvent userEvent) {
        Picasso.with(getActivity()).load(userEvent.user.getBiggerProfileImageURL()).into(userIconButton);
    }
}
