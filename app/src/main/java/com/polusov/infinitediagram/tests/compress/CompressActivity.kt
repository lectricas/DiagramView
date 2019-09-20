package com.polusov.infinitediagram.tests.compress

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.polusov.infinitediagram.BuildConfig
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_compress.pdfFromGallery
import kotlinx.android.synthetic.main.activity_compress.photoFromCamera
import kotlinx.android.synthetic.main.activity_compress.photoFromGallery
import kotlinx.android.synthetic.main.activity_compress.showFile
import me.dmdev.rxpm.base.PmSupportActivity
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import android.database.Cursor
import com.polusov.infinitediagram.R

class CompressActivity : PmSupportActivity<CompressPm>() {

    //    lateinit var compressedFile: File
//    lateinit var originalFile: File
//
    companion object {

        //        const val REQUEST_TAKE_PHOTO = 500
//        const val AUTHORITY = "com.polusov.infinitediagram.fileprovider"
//        private const val MAX_IMAGE_SIZE = 1024000
        private val FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".module.fileprovider"
    }

    lateinit var rxPermission: RxPermissions

    lateinit var uri: Uri

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
                    startActivityForResult(takePicture(), 3)
                }
        }
    }

    override fun onBindPresentationModel(pm: CompressPm) {}

    override fun providePresentationModel(): CompressPm {
        return CompressPm()
    }

    private fun fromGallery(): Intent {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        return intent
    }

    private fun pdfFromGallery(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        return intent
    }

    private fun takePicture(): Intent {
        val photoURI: Uri =
            FileProvider.getUriForFile(this@CompressActivity, FILE_PROVIDER_AUTHORITY, createImageFile())
        uri = photoURI
        return Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
        val storageDir = File(Environment.getExternalStorageDirectory(), "/Pictures/InfiniteView/")
        return File(storageDir, "${timeStamp}.jpg")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1, 2 -> {
                    val path = FileUtils.getPath(this, data!!.data!!)
                    Timber.d("path = $path")
//                    uri =
                }
                3 -> {
                    //do nothing
                }
                else -> {

                }
            }
        }
//        showFile.text = uri.toString()
//        openFile(uri)
    }

    private fun openFile(url: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (url.path.contains(".pdf")) {
                intent.setDataAndType(uri, "application/pdf")
            } else if (url.path.contains(".jpg") || url.path.contains(".jpeg")) {
                intent.setDataAndType(uri, "image/jpeg")
            }

            Timber.d("uri = $uri")

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No application found which can open the file", Toast.LENGTH_SHORT).show()
        }
    }
}