package jp.unknown.works.twinown.twinown_views;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
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
    Uri imageUri;
    InputStream fileInputStream;
    @Bind(R.id.userIconButton) ImageButton userIconButton;
    @Bind(R.id.tweetTextInputLayout) TextInputLayout tweetTextInputLayout;
    @Bind(R.id.tweetEditText) EditText tweetEditText;
    @Bind(R.id.tweetLength) TextView tweetLength;
    @Bind(R.id.uploadImageView) ImageView uploadImageView;

    private static final int REQUEST_GALLERY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        userPreferenceList = UserPreference.getAll();
        toReplyStatus = (Status) getActivity().getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_STATUS);
        imageUri = getActivity().getIntent().getParcelableExtra(Intent.EXTRA_STREAM);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container, false);
        ButterKnife.bind(this, view);
        if (tweetEditText.length() == 0 && toReplyStatus != null) {
            tweetEditText.setText(String.format("@%s %s", toReplyStatus.getUser().getScreenName(), tweetEditText.getText().toString()));
            String tweetHint = String.format("@%s:%s", toReplyStatus.getUser().getScreenName(), toReplyStatus.getText());
            tweetTextInputLayout.setHint(String.format("%s (%s)", tweetHint, String.valueOf(140 - tweetEditText.getText().length())));
            tweetEditText.setSelection(tweetEditText.getText().toString().length());
            tweetEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reply_white, 0, 0, 0);
        }
        if (imageUri != null) {
            try {
                fileInputStream = getActivity().getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException ignored) {
                // ignored
            }
            Picasso.with(getActivity()).load(imageUri).into(uploadImageView);
            uploadImageView.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == REQUEST_GALLERY && resultCode == FragmentActivity.RESULT_OK) {
            try {
                imageUri = intent.getData();
                fileInputStream = getActivity().getContentResolver().openInputStream(imageUri);

            } catch (Exception ignored) {
                // ignored
            }
            Picasso.with(getActivity()).load(imageUri).into(uploadImageView);
            uploadImageView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("unused")
    @OnTextChanged(R.id.tweetEditText)
    public void updateTweetEditText(CharSequence changedText) {
        String tweetHint;
        if (changedText.length() == 0) {
            toReplyStatus = null;
            tweetEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            tweetHint = getString(R.string.tweet_hint);
        } else {
            if (toReplyStatus != null) {
                tweetHint = String.format("@%s:%s", toReplyStatus.getUser().getScreenName(), toReplyStatus.getText());
            } else {
                tweetHint = getString(R.string.tweet_hint);
            }
        }
        if (tweetHint.length() > 25) {
            tweetHint = tweetHint.substring(0, 25) + "…";
        }
        tweetLength.setText(String.valueOf(140 - changedText.length()));
        tweetTextInputLayout.setHint(String.format("%s (%s)", tweetHint, String.valueOf(140 - changedText.length())));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.tweetButton)
    public void statusUpdate() {
        if (tweetEditText.length() != 0) {
            TwinownHelper.updateStatus(userPreferenceList.get(currentUserIndex), tweetEditText.getText().toString(), toReplyStatus, fileInputStream);
            getActivity().finish();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.imageUploadButton)
    public void imageUpload() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY);
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
