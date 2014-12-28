package ru.mirea.smych;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.*;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

public class MyActivity extends FragmentActivity {

    //just drawable elements
    private TextView userNameView;
    private LoginButton loginButton;
    private EditText editStatus;
    private Button postPictureButton;
    private ImageView imageView;
    private Button updateStatusButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        userNameView = (TextView) findViewById(R.id.user_name);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        editStatus = (EditText) findViewById(R.id.edit_status);
        postPictureButton = (Button) findViewById(R.id.post_picture);
        imageView = (ImageView) findViewById(R.id.picture);
        updateStatusButton = (Button) findViewById(R.id.update_status);


        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null){
                    userNameView.setText(user.getName());
                }
            }
        });

    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()){
                changeElementsEnable(true);
            }
            else if (state.isClosed()){
                changeElementsEnable(false);
            }
        }
    };

    private void changeElementsEnable(boolean isEnable){
        loginButton.setEnabled(!isEnable);
        editStatus.setEnabled(isEnable);
        updateStatusButton.setEnabled(isEnable);
        postPictureButton.setEnabled(isEnable);
    }

    public void onUpdateStatusClick(View view) {
        requestPermission();
        Request request = Request.newStatusUpdateRequest(Session.getActiveSession(), editStatus.getText().toString(), new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                //okay
            }
        });
        request.executeAsync();
    }

    public void onPostPictureClick(View view) {
        requestPermission();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 0){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

            Request uploadRequest = Request.newUploadPhotoRequest(Session.getActiveSession(), bitmap, new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    //okay
                }
            });
            uploadRequest.executeAsync();
        }
    }

    private void requestPermission(){
        Session session = Session.getActiveSession();
        if (session != null && !session.getPermissions().contains("publish_actions")){
            session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, Arrays.asList("publish_actions")));
        }
    }

}
