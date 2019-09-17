package com.polusov.infinitediagram.tests.compress

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.polusov.infinitediagram.BuildConfig
import com.polusov.infinitediagram.R
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_compress.*
import me.dmdev.rxpm.base.PmSupportActivity
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CompressActivity: PmSupportActivity<CompressPm>() {

    lateinit var filePath: String

    companion object {
        const val REQUEST_TAKE_PHOTO = 500
//        const val AUTHORITY = "com.polusov.infinitediagram.fileprovider"
        private val FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".module.fileprovider"
    }

    lateinit var rxPermission: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compress)
        Timber.d("AppStarts")
        rxPermission = RxPermissions(this)

        photoFromGallery.setOnClickListener {
            startActivityForResult(fromGallery(), 1)
        }

        pdfFromGallery.setOnClickListener {
            startActivityForResult(pdfFromGallery(), 2)
        }

        photoFromCamera.setOnClickListener {
            rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    if (it) {
                        dispatchTakePictureIntent()
                    } else {
                        Snackbar.make(container, "Please grant", 1000).show()
                    }
                }

        }
    }

    override fun onBindPresentationModel(pm: CompressPm) {

    }

    override fun providePresentationModel(): CompressPm {
        return CompressPm()
    }

    fun fromGallery(): Intent {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        return intent
    }

    fun pdfFromGallery(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        return intent
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            val photoFile = createImageFile()
            photoFile.also {
                val photoURI: Uri = FileProvider.getUriForFile(this@CompressActivity, FILE_PROVIDER_AUTHORITY, it)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
        val storageDir = File(Environment.getExternalStorageDirectory(), "/Pictures/InfiniteView/")
        return File(storageDir,"JPEG_${timeStamp}_.jpg").apply {
            filePath = absolutePath
            Timber.d("absolutePath $absoluteFile")
        }
    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(filePath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d(intentToString(data))
        Timber.d("request = $requestCode result = $resultCode")
        setPic()
    }

    private fun intentToString(intent: Intent?): String {
        if (intent == null)
            return ""

        val stringBuilder = StringBuilder("action: ")
            .append(intent.action)
            .append(" data: ")
            .append(intent.dataString)
            .append(" extras: ")

        intent.extras?.apply {
            for (key in keySet()) {
                stringBuilder.append(key).append("=").append(intent.extras!!.get(key)).append(" ")
            }
        }

        return stringBuilder.toString()
    }

}