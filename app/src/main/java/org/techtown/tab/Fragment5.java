package org.techtown.tab;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;


public class Fragment5 extends Fragment {

    EditText editText11;
    EditText editText12;
    EditText editText13;
    EditText editText14;
    private ImageView mImageView;
    private static final int REQUEST_UPLOAD = 100;
    private static final int REQUEST_PERMISSION_UPLOAD = 100;


    OnDatabaseCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callback = (OnDatabaseCallback) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment5, container, false);

        editText11 = (EditText) rootView.findViewById(R.id.editText11);
        editText12 = (EditText) rootView.findViewById(R.id.editText12);
        editText13 = (EditText) rootView.findViewById(R.id.editText13);
        editText14 = (EditText) rootView.findViewById(R.id.editText14);

        Button button10 = (Button) rootView.findViewById(R.id.button10);
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText11.getText().toString();
                String location = editText12.getText().toString();
                String mobile = editText13.getText().toString();
                String condition = editText14.getText().toString();

                callback.insert(name, location, mobile, condition);
            }
        });
        mImageView = rootView.findViewById(R.id.imgView);

        // ????????? ?????? ????????? ??????
        Button uploadButton = rootView.findViewById(R.id.btnUploadPicture);
        uploadButton.setOnClickListener(v -> {

            // EXTERNAL_STORAGE ?????? ??????
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                showUploadDialog();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_UPLOAD);
            }
        });



        return rootView;
    }

    public void showUploadDialog() {

        // ????????? ?????? ?????? ???????????? ?????????
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = Intent.createChooser(galleryIntent, "?????? ?????????");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        startActivityForResult(chooser, REQUEST_UPLOAD);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EXTERNAL STORAGE ?????? ?????? ?????????
        if (requestCode == REQUEST_PERMISSION_UPLOAD) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showUploadDialog();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ????????? ????????? ??????
        if (requestCode == REQUEST_UPLOAD) {
            if (resultCode == RESULT_OK && data != null) {
                // ????????? ??????
                Bitmap bitmap = null;
                if (data.getExtras() != null) {
                    // ????????? ?????? ??????
                    bitmap = (Bitmap) data.getExtras().get("data");
                } else {
                    // ?????????(??????) ?????? ??????
                    Uri uri = data.getData();
                    if (uri != null) {
                        String path = BitmapHelper.getRealPathFromUri(getActivity(), uri);
                        bitmap = BitmapHelper.getBitmapFromPath(path);
                    }
                }
                if (bitmap != null) {
                    mImageView.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(getActivity(), "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
