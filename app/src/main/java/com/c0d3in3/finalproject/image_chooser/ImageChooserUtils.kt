package com.c0d3in3.finalproject.image_chooser

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import com.c0d3in3.finalproject.App
import com.c0d3in3.finalproject.R
import kotlinx.android.synthetic.main.choose_resource_file.*

class ImageChooserUtils {
    companion object {
        const val PERMISSIONS_REQUEST = 11

        private fun hasReadExternalStorage() = ActivityCompat.checkSelfPermission(
            App.getContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        private fun hasWriteExternalStorage() = ActivityCompat.checkSelfPermission(
            App.getContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        private fun hasCameraPermission() = ActivityCompat.checkSelfPermission(
            App.getContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED


        private fun requestPermissions(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), PERMISSIONS_REQUEST
                )
            }
        }


        val easyImage by lazy {
            EasyImage.Builder(App.getContext()) // Chooser only
                // Will appear as a system chooser title, DEFAULT empty string
                //.setChooserTitle("Pick media")
                // Will tell chooser that it should show documents or gallery apps
                //.setChooserType(ChooserType.CAMERA_AND_DOCUMENTS)  you can use this or the one below
//                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                // Setting to true will cause taken pictures to show up in the device gallery, DEFAULT false
                .setCopyImagesToPublicGalleryFolder(false) // Sets the name for images stored if setCopyImagesToPublicGalleryFolder = true
                .setFolderName("EasyImage sample") // Allow multiple picking
                .allowMultiple(false)
                .build()
        }

        fun choosePhoto(activity: Activity){
            if (hasReadExternalStorage() && hasWriteExternalStorage() && hasCameraPermission())
                chooseResource(activity)
            else
                requestPermissions(activity)
        }

        fun chooseResource(activity: Activity) {
            val dialog = Dialog(activity)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.choose_resource_file)
            val params: ViewGroup.LayoutParams = dialog.window!!.attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = params as WindowManager.LayoutParams
            dialog.chooseFromCameraIB.setOnClickListener {
                easyImage.openCameraForImage(activity)
                dialog.dismiss()
            }

            dialog.chooseFromGalleryIB.setOnClickListener {
                easyImage.openGallery(activity)
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}