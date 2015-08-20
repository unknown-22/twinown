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
import java.util.ArrayList;
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
    ArrayList<Uri> imageUris = new ArrayList<>();
    ArrayList<InputStream> fileInputStreams = new ArrayList<>();
    @Bind(R.id.userIconButton) ImageButton userIconButton;
    @Bind(R.id.tweetTextInputLayout) TextInputLayout tweetTextInputLayout;
    @Bind(R.id.tweetEditText) EditText tweetEditText;
    @Bind(R.id.tweetLength) TextView tweetLength;
    @Bind(R.id.uploadImageView1) ImageView uploadImageView1;
    @Bind(R.id.uploadImageView2) ImageView uploadImageView2;
    @Bind(R.id.uploadImageView3) ImageView uploadImageView3;
    @Bind(R.id.uploadImageView4) ImageView uploadImageView4;
    ArrayList<ImageView> uploadImageViews = new ArrayList<>();

    private static final int REQUEST_GALLERY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        userPreferenceList = UserPreference.getAll();
        toReplyStatus = (Status) getActivity().getIntent().getSerializableExtra(Utils.ARGUMENTS_KEYWORD_STATUS);
        String action = getActivity().getIntent().getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            imageUris.add(getActivity().getIntent().<Uri>getParcelableExtra(Intent.EXTRA_STREAM));
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            imageUris.addAll(getActivity().getIntent().<Uri>getParcelableArrayListExtra(Intent.EXTRA_STREAM));
        }

        if (imageUris.size() > 4) {
            Utils.showToastLong(getActivity(), "画像は4つまでです");
            for (int i = 4; i < imageUris.size() + 1; i++) {
                imageUris.remove(4);
            }
        }

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

        uploadImageViews.add(uploadImageView1);
        uploadImageViews.add(uploadImageView2);
        uploadImageViews.add(uploadImageView3);
        uploadImageViews.add(uploadImageView4);

        for (int i = 0; i < imageUris.size(); i++) {
            Uri imageUri = imageUris.get(i);
            if (imageUri != null) {
                try {
                    fileInputStreams.add(getActivity().getContentResolver().openInputStream(imageUri));
                } catch (FileNotFoundException ignored) {
                    // ignored
                }
                Picasso.with(getActivity()).load(imageUri).into(uploadImageViews.get(i));
                uploadImageViews.get(i).setVisibility(View.VISIBLE);
            }
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
            if (imageUris.size() < 4) {
                Uri imageUri = intent.getData();
                if (imageUri != null) {
                    try {
                        fileInputStreams.add(getActivity().getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException ignored) {
                        // ignored
                    }
                    imageUris.add(imageUri);
                    ImageView uploadImageView = uploadImageViews.get(imageUris.size() - 1);
                    Picasso.with(getActivity()).load(imageUri).into(uploadImageView);
                    uploadImageView.setVisibility(View.VISIBLE);
                }
            } else {
                Utils.showToastLong(getActivity(), "画像は4つまでです");
            }
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
        if (tweetEditText.length() != 0 || fileInputStreams.size() > 0) {
            TwinownHelper.updateStatus(userPreferenceList.get(currentUserIndex), tweetEditText.getText().toString(), toReplyStatus, fileInputStreams);
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
