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
import com.polusov.infinitediagram.R
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_compress.pdfFromGallery
import kotlinx.android.synthetic.main.activity_compress.photoFromCamera
import kotlinx.android.synthetic.main.activity_compress.photoFromGallery
import me.dmdev.rxpm.base.PmSupportActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date

class CompressActivity : PmSupportActivity<CompressPm>() {

    companion object {
        const val PDF = 1
        const val GALLERY = 2
        const val PHOTO = 3
        private val FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".module.fileprovider"
    }

    lateinit var rxPermission: RxPermissions

    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compress)
        rxPermission = RxPermissions(this)


        photoFromGallery.setOnClickListener {
            startActivityForResult(fromGallery(), GALLERY)
        }

        pdfFromGallery.setOnClickListener {
            startActivityForResult(pdfFromGallery(), PDF)
        }

        photoFromCamera.setOnClickListener {
            rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    startActivityForResult(takePicture(), PHOTO)
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
        val photoURI: Uri = FileProvider.getUriForFile(
            this@CompressActivity,
            FILE_PROVIDER_AUTHORITY,
            createImageFile("jpg")
        )
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    }

    private fun createImageFile(extension: String): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm").format(Date())
        val storageDir = File(Environment.getExternalStorageDirectory(), "/Pictures/InfiniteView/")
        return File(storageDir, "${timeStamp}.$extension").apply {
            filePath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY -> {
                    val inputStream = contentResolver.openInputStream(data!!.data!!)
                    copyStreamToFile(inputStream, createImageFile("jpg"))
                }
                PDF -> {
                    val inputStream = contentResolver.openInputStream(data!!.data!!)
                    copyStreamToFile(inputStream, createImageFile("pdf"))
                }
                PHOTO -> {

                }
            }
            openFile(File(filePath))
        }
    }

    private fun copyStreamToFile(inputStream: InputStream, outputFile: File): File {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
        return outputFile
    }

    private fun openFile(file: File) {
        val fileUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, file)
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (file.absolutePath.contains(".pdf")) {
                intent.setDataAndType(fileUri, "application/pdf")
            } else if (file.absolutePath.contains(".jpg") || file.absolutePath.contains(".jpeg")) {
                intent.setDataAndType(fileUri, "image/jpeg")
            }

            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No application found which can open the file", Toast.LENGTH_SHORT).show()
        }
    }
}