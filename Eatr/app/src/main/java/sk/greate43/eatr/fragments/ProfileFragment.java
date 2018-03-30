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
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
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
    private CircleImageView imgProfilePicture;
    private Spinner spinnerUserType;
    private Button btnSaveProfile;
    private Uri imgUri;
    private ProgressDialog mProgressDialog;
    Boolean allowToCheckUserType;

    public ProfileFragment() {
    }

    public static final String ALLOW_TO_CHECK_USER_TYPE = "ALLOW_TO_CHECK_USER_TYPE";

    @NonNull
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putBoolean(ALLOW_TO_CHECK_USER_TYPE, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProfileFragment newInstance(Profile profile) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putBoolean(ALLOW_TO_CHECK_USER_TYPE, false);
        args.putSerializable(Constants.ARGS_PROFILE, profile);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            allowToCheckUserType = getArguments().getBoolean(ALLOW_TO_CHECK_USER_TYPE);
            profile = (Profile) getArguments().getSerializable(Constants.ARGS_PROFILE);
        }
    }

    View viewSnackBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);


        setHasOptionsMenu(true);


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
                viewSnackBar = v;
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
                            , v
                    );


                } else if (imgUri == null) {
                    Snackbar.make(viewSnackBar, "Please Select the image ", Snackbar.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(etFirstName.getText())) {
                    etFirstName.setError("First Name is Empty ");
                } else if (TextUtils.isEmpty(etLastName.getText())) {
                    etLastName.setError("Last Name is Empty ");
                } else if (!isEmailValid(etEmail.getText().toString())) {
                    etEmail.setError("Email Address Should Contain @ , . and com");
                }
            }
        });

        if (profile != null) {
            imgUri = Uri.parse(profile.getProfilePhotoUri());
            etFirstName.setText(profile.getFirstName());
            etLastName.setText(profile.getLastName());
            etEmail.setText(profile.getEmail());
            if (imgUri != null)
                setProfileImage(imgUri);
            if (profile.getUserType() != null && profile.getUserType().equalsIgnoreCase(Constants.TYPE_BUYER)) {
                spinnerUserType.setSelection(0);
            } else if (profile.getUserType() != null && profile.getUserType().equalsIgnoreCase(Constants.TYPE_SELLER)) {
                spinnerUserType.setSelection(1);

            }


        }

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

    private void saveUserProfile(final String userId, final String firstName, final String lastName, Uri imgUri, final String userType, final String email, final View view) {
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
                if (allowToCheckUserType) {
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
                } else {
                    Snackbar.make(view, "Profile was Successfully Updated", Snackbar.LENGTH_LONG).show();
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


                if (allowToCheckUserType) {
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
                } else {
                    Snackbar.make(view, "Profile was Successfully Updated But Image wasn't uploaded", Snackbar.LENGTH_LONG).show();
                }


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


            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
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


            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, REQUEST_CAMERA_AND_WRITE_PERMISSION);
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
                setProfileImage(imgUri);
            }

        } else if (requestCode == CAMERA_RESULT) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            assert getActivity() != null;
            String pathOfBmp = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "title", null);

            imgUri = Uri.parse(pathOfBmp);
            setProfileImage(imgUri);
        }

    }


    private void setProfileImage(Uri imgUri) {
        Picasso.with(getActivity())
                .load(imgUri)
                .fit()
                .centerCrop()
                .into(imgProfilePicture, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: ");
                    }

                    @Override
                    public void onError() {

                    }
                });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: 1");
                    choosePhotoFromGallery();
                }
                break;
            case REQUEST_CAMERA_AND_WRITE_PERMISSION:

                if (grantResults.length > 0) {
                    Log.d(TAG, "onRequestPermissionsResult: 0");
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d(TAG, "onRequestPermissionsResult: 2 ");
                    if (cameraPermission && readExternalStorage) {
                        takePhotoFromCamera();
                    }
                }

                break;

        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.menu_item_search);
        search.setVisible(false);

        super.onPrepareOptionsMenu(menu);


    }
}
