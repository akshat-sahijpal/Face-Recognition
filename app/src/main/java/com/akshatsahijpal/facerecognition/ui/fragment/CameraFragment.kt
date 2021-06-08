package com.akshatsahijpal.facerecognition.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.akshatsahijpal.facerecognition.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.util.concurrent.ListenableFuture
import java.io.File


class CameraFragment : Fragment(R.layout.fragment_camera) {
    private var camera: Camera? = null
    private var currentLens: Int = CameraSelector.LENS_FACING_BACK
    private lateinit var CameraPreview: PreviewView
    private lateinit var cameraProvider: ListenableFuture<ProcessCameraProvider>// used for binding camera to a lifecycle owner
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CameraPreview = view.findViewById(R.id.CamPrev)
        view.findViewById<FloatingActionButton>(R.id.rotateCamera).setOnClickListener {
            if (currentLens == CameraSelector.LENS_FACING_BACK) {
                currentLens = CameraSelector.LENS_FACING_FRONT
            } else if (currentLens == CameraSelector.LENS_FACING_FRONT) {
                currentLens = CameraSelector.LENS_FACING_BACK
            }
            bind()
        }
        view.findViewById<FloatingActionButton>(R.id.captureImage).setOnClickListener {
            takePhoto()
        }
        triggerCamera()
    }

    private var imageCapture: ImageCapture? = null
    private fun takePhoto() {
        var photoFile =
            File(requireActivity().externalMediaDirs.firstOrNull(),"CapturePro-${System.currentTimeMillis()}.jpg")
        var imageOptions: ImageCapture.OutputFileOptions = ImageCapture.OutputFileOptions.Builder(
            photoFile
        ).build()
        imageCapture?.takePicture(
            imageOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        requireContext(),
                        "Image Saved ${outputFileResults.savedUri} at ${requireActivity().externalCacheDir!!.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("TAG", "onImageSaved:  ${requireActivity().externalCacheDir!!.absolutePath}")
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        "Failed: ${exception.message} and ${exception.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun triggerCamera() {
        bind()
    }

    private fun bind() {
        cameraProvider = ProcessCameraProvider.getInstance(requireContext())
        cameraProvider.addListener({
            val prov: ProcessCameraProvider = cameraProvider.get()
            bindPreview(prov)
        }, ContextCompat.getMainExecutor(requireContext()))
    }
    private fun bindPreview(prov: ProcessCameraProvider) {
        prov.unbindAll()
        val preview = Preview.Builder()
            .build()
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(currentLens)
                .build()
        val surface: Preview.SurfaceProvider =
            CameraPreview.createSurfaceProvider(camera?.cameraInfo)
        preview.setSurfaceProvider(surface)
        imageCapture = view?.display?.let {
            ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_ON)
                .setTargetAspectRatio(AspectRatio.RATIO_16_9) // width:height
                .setTargetRotation(it.rotation)
                .build()
        }
        camera = prov.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
    }
}