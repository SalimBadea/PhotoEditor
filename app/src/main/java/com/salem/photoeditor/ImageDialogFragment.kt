package com.salem.photoeditor

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.moltaqa.bring.client.util.RealPathUtil
import com.salem.photoeditor.databinding.FragmentImageDialogBinding
import com.salem.photoeditor.R
import pl.tajchert.nammu.Nammu
import pl.tajchert.nammu.PermissionCallback
import java.lang.Exception


class ImageDialogFragment : BottomSheetDialogFragment() {

    private var camera_request_code: Int = 0
    private var gallery_request_code: Int = 0
    private var selectedWay: String = ""
    private var ba_status: String = ""
    private var mActivity: AppCompatActivity? = null
    private var mFragment: Fragment? = null
    private var mCurrentPhotoPath: String? = null

    private var binding: FragmentImageDialogBinding? = null

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Handle the selected image URI
        if (uri != null) {
            val imagePath = RealPathUtil.getRealPath(requireContext(), uri).toString()
//            when (selectedImage) {
//
//                Images.ID_PHOTO -> {
//                    binding.idPhoto.setImageURI(uri); binding.idPhoto.setPadding(0)
//                    viewModel.onEvent(RegisterEvent.IdImageChanged(imagePath))
//                }
//                Images.PROFILE_IMAGE -> {
//                    binding.profileImage.setImageURI(uri); binding.profileImage.setPadding(0)
//                    viewModel.onEvent(RegisterEvent.ProfileImageChanged(imagePath))
//                }
//                Images.LICENSE_IMAGE -> {
//                    binding.licenseImage.setImageURI(uri); binding.licenseImage.setPadding(0)
//                    viewModel.onEvent(RegisterEvent.LicenseImageChanged(imagePath))
//                }
//            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImageDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetMap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            ba_status = it.getString("BA_STATUS", "")
        }
        initViews()
        observeViewModel()
    }

    private fun observeViewModel() {

    }

    private fun initViews() {
        Nammu.init(context)
        if (!Nammu.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Nammu.askForPermission(
                requireActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE, storagePermissionCallback
            )
        }
        if (!Nammu.checkPermission(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {

            Nammu.askForPermission(
                requireActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE, storagePermissionCallback
            )

        }
        binding?.camLayout?.setOnClickListener {
            selectedWay = "camera"
            binding?.camLayout?.background = ContextCompat.getDrawable(requireContext(), R.drawable.active_background)
            binding?.galleryLayout?.background = ContextCompat.getDrawable(requireContext(), android.R.color.transparent)
        }

        binding?.galleryLayout?.setOnClickListener {
            selectedWay = "gallery"
            binding?.galleryLayout?.background = ContextCompat.getDrawable(requireContext(), R.drawable.active_background)
            binding?.camLayout?.background = ContextCompat.getDrawable(requireContext(), android.R.color.transparent)
        }

        binding?.btnCloseDialog?.setOnClickListener {
            dialog?.dismiss()
        }

        binding?.btnNext?.setOnClickListener {
            Nammu.init(context)
         if (ba_status == "avatar") {
                camera_request_code = REQUEST_AVATAR_IMAGE_CAPTURE
                gallery_request_code = IMAGE_AVATAR_REQUEST_CODE

            }

            if (selectedWay == "camera") {
                capturePicture()

            } else {
//                selectImage()
                val intentForLoadingImage = Intent(Intent.ACTION_GET_CONTENT)
                intentForLoadingImage.type = "image/*"
                try {
                    if (intentForLoadingImage.resolveActivity(activity?.packageManager!!) != null) {
                        parentFragment?.startActivityForResult(
                            intentForLoadingImage,
                            gallery_request_code
                        )
                    }
                } catch (e: ActivityNotFoundException) {
                }
            }
            dismiss()
        }
    }

    private fun selectImage() {
        imagePicker.launch("image/*")
    }

    private fun capturePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            if (takePictureIntent.resolveActivity(activity?.packageManager!!) != null) { // its always null
//                val photoFile: File
//                try {
//                    photoFile = createImageFile()
//                } catch (ex: IOException) {
//                    ex.printStackTrace()
//                    return
//                }
//
//                if (photoFile != null) {
//                    mCurrentPhotoPath = photoFile.absolutePath
//                    val photoUri: Uri = FileProvider.getUriForFile(
//                        mActivity ?: mFragment?.context!!,
//                        "com.orcav.amanetelmadina.fileprovider",
//                        photoFile
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//                    parentFragment?.startActivityForResult(takePictureIntent, camera_request_code)
//                    parentFragment?.startActivityForResult(takePictureIntent, camera_request_code)
//                }
                parentFragment?.startActivityForResult(
                    takePictureIntent,
                    camera_request_code
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "capturePicture: ${e.message}")
        }
    }

    private val storagePermissionCallback: PermissionCallback = object : PermissionCallback {
        override fun permissionGranted() {
        }

        override fun permissionRefused() {
//            showToast(getString(R.string.field_is_empty_error_message))
        }

    }
    private val cameraPermissionCallback: PermissionCallback = object : PermissionCallback {
        override fun permissionGranted() {
            capturePicture()
        }

        override fun permissionRefused() {
//            showToast(getString(R.string.permission_error))
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        val IMAGE_NATIONALITY_REQUEST_CODE: Int = 200
        val REQUEST_NATIONALITY_IMAGE_CAPTURE: Int = 100

        val IMAGE_AVATAR_REQUEST_CODE: Int = 201
        val REQUEST_AVATAR_IMAGE_CAPTURE: Int = 101

        val IMAGE_CAR_REQUEST_CODE: Int = 202
        val REQUEST_CAR_IMAGE_CAPTURE: Int = 102


        private const val TAG = "ImageDialogFragment"

        @JvmStatic
        fun newInstance(image: String) =
            ImageDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("BA_STATUS", image)
                }
            }
    }
}