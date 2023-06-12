package com.salem.photoeditor

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.moltaqa.bring.client.util.RealPathUtil
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvGallery.setOnClickListener {
            ImageDialogFragment.newInstance("avatar")
                .show(supportFragmentManager, TAG)
        }

        //Use custom font using latest support library
        //Use custom font using latest support library
        val mTextRobotoTf = ResourcesCompat.getFont(this, R.font.font_medium)

//loading font from asset

//loading font from asset
        val mEmojiTypeFace = Typeface.createFromFile("@font/font_medium")

        val mPhotoEditor = PhotoEditor.Builder(this, photoEditorView)
            .setPinchTextScalable(true)
            .setClipSourceImage(true)
            .setDefaultTextTypeface(mTextRobotoTf)
            .setDefaultEmojiTypeface(mEmojiTypeFace)
            .build()

        val shapeBuilder = ShapeBuilder()
            .withShapeOpacity(100)
            .withShapeType(ShapeType.Oval)
            .withShapeSize(50f);

        mPhotoEditor.setShape(shapeBuilder)

    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("Range", "MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var uri: Uri? = null
        if (requestCode == ImageDialogFragment.REQUEST_AVATAR_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Log.e(TAG, "Extra >> ${data.extras.toString()}")
                val photo = data?.extras?.get("data") as Bitmap
                uri = RealPathUtil.getImageUri(photo, this)
                val imagePath = RealPathUtil.getRealPath(this, uri).toString()

                //viewModel.uploadPhoto(File(imagePath))
                Log.e("prof Image From Cam >> ", "$imagePath")

                photoEditorView.source.setImageURI(uri); photoEditorView.setPadding(0,0,0,0)

            }
        } else if (requestCode == ImageDialogFragment.IMAGE_AVATAR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
//                val result = CropImage.getActivityResult(data)
                uri = data?.data
                Log.e(TAG, "Extra >> ${data.extras.toString()}")
                Log.e(TAG, "Extra >> ${data.data.toString()}")
                if (uri != null) {
                    val imagePath = RealPathUtil.getRealPath(this, uri).toString()

                    Log.e("prof Image From Gal >> ", "$imagePath")
                    photoEditorView.source.setImageURI(uri); photoEditorView.setPadding(0,0,0,0)
                }
            }
        }
    }

    private val writeStoragePermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permision ->
            Log.d(
                TAG,
                "writeStoragePermissionResult: $permision  ${permision.keys}  ${permision.values} granted ${
                    permision.values.toList().any { it }
                }"
            )
//            if (permision.values.toList().any { it })
//                pickImageFromGallery()
        }

    private fun askForStoragePermission(): Boolean =
        if (hasPermissions(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            true
        } else {
            writeStoragePermissionResult.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
            false
        }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
}