package sk.greate43.eatr.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.BuyerActivity;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.utils.Constants;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static sk.greate43.eatr.utils.Constants.CAMERA_RESULT;
import static sk.greate43.eatr.utils.Constants.GALLERY_RESULT;
import static sk.greate43.eatr.utils.Constants.REQUEST_CAMERA_AND_WRITE_PERMISSION;
import static sk.greate43.eatr.utils.Constants.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    Profile profile;
    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etEmail;
    private ImageView imgProfilePicture;
    private Spinner spinnerUserType;
    private Button btnSaveProfile;
    private Uri imgUri;
    private ProgressDialog mProgressDialog;

    public ProfileFragment() {
    }


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
//        Bundle args = new Bundle();
//
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            profile = (Profile) getArguments().getSerializable(Constants.ARGS_FOOD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        etFirstName = view.findViewById(R.id.fragment_profile_edit_text_first_name);
        etLastName = view.findViewById(R.id.fragment_profile_edit_text_last_name);
        etEmail = view.findViewById(R.id.fragment_profile_edit_text_email_address);
        imgProfilePicture = view.findViewById(R.id.fragment_profile_image_view_profile_picture);
        spinnerUserType = view.findViewById(R.id.fragment_profile_spinner_select_user_type);
        btnSaveProfile = view.findViewById(R.id.fragment_profile_button_save_profile);
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();


        imgProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPictureFromGalleryOrCameraDialog();
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        !TextUtils.isEmpty(etFirstName.getText())
                                && !TextUtils.isEmpty(etLastName.getText())
                                && imgUri != null
                                && isEmailValid(etEmail.getText().toString())
                        ) {

                    saveUserProfile(user.getUid()
                            , etFirstName.getText().toString()
                            , etLastName.getText().toString()
                            , imgUri
                            , spinnerUserType.getSelectedItem().toString()
                            , etEmail.getText().toString()
                            );


                } else if (TextUtils.isEmpty(etFirstName.getText())) {
                    etFirstName.setError("First Name is Empty ");
                } else if (TextUtils.isEmpty(etLastName.getText())) {
                    etLastName.setError("Last Name is Empty ");
                } else if (!isEmailValid(etEmail.getText().toString())) {
                    etEmail.setError("Email Address Should Contain @ , . and com");
                }
            }
        });

        return view;
    }

    @NonNull
    private Boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            return true;
        } else if (email.contains("@") && email.contains(".") && email.contains("com")) {
            return true;
        }
        return false;
    }

    private void saveUserProfile(final String userId, final String firstName, final String lastName, Uri imgUri, final String userType, final String email) {
        showProgressDialog();

        imgProfilePicture.setDrawingCacheEnabled(true);
        imgProfilePicture.buildDrawingCache();
        Bitmap bitmap = imgProfilePicture.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        UploadTask uploadTask = storageRef.child(Constants.PHOTOS).child(userId).child(userId).putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                profile = new Profile();
                profile.setUserId(userId);
                profile.setFirstName(firstName);
                profile.setLastName(lastName);
                profile.setProfilePhotoUri(downloadUrl);
                profile.setUserType(userType);
                if (!email.isEmpty())
                    profile.setEmail(email);

                mDatabaseReference.child(Constants.PROFILE).child(userId).setValue(profile);

                if (profile.getUserType().equalsIgnoreCase(Constants.TYPE_SELLER)) {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), SellerActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }

                } else if (profile.getUserType().equalsIgnoreCase(Constants.TYPE_BUYER)) {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), BuyerActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
                }
                hideProgressDialog();

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                profile = new Profile();
                profile.setUserId(userId);
                profile.setFirstName(firstName);
                profile.setLastName(lastName);
                profile.setProfilePhotoUri("");
                profile.setUserType(userType);

                mDatabaseReference.child(Constants.PROFILE).child(userId).setValue(profile);
                hideProgressDialog();
            }
        });

    }

    private void selectPictureFromGalleryOrCameraDialog() {
        assert getActivity() != null;
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallery() {
        assert getActivity() != null;
        int HasReadPermission = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);
        if (HasReadPermission != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            return;
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY_RESULT);

    }

    private void takePhotoFromCamera() {
        assert getActivity() != null;
        int HasCameraPermission = ContextCompat.checkSelfPermission(getActivity(), CAMERA);
        int HasWriteExternalStoragePermPermission = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);

        if (HasCameraPermission != PackageManager.PERMISSION_GRANTED || HasWriteExternalStoragePermPermission != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_AND_WRITE_PERMISSION);
            return;
        }


        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_RESULT);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_RESULT) {
            if (data != null) {
                imgUri = data.getData();
                imgProfilePicture.setImageURI(Uri.parse(String.valueOf(imgUri)));
                imgProfilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Log.d(TAG, "onActivityResult: " + imgUri.getLastPathSegment());
            }

        } else if (requestCode == CAMERA_RESULT) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            assert getActivity() != null;
            String pathOfBmp = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "title", null);

            imgUri = Uri.parse(pathOfBmp);

            imgProfilePicture.setImageURI(Uri.parse(String.valueOf(imgUri)));
            imgProfilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Log.d(TAG, "onActivityResult: " + imgUri);

        }


    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}
