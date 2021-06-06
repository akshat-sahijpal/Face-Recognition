package com.akshatsahijpal.facerecognition.ui.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.akshatsahijpal.cameracapture.util.Constants.Permission_broker
import com.akshatsahijpal.facerecognition.R

class PermissionsFragment : Fragment(R.layout.fragment_permissions) {
    private lateinit var errorText: TextView
    private lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        errorText = view.findViewById(R.id.errorV)
        var btn = view.findViewById<Button>(R.id.permissionGrabberButton)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            navController.navigate(R.id.action_permissionsFragment_to_cameraFragment)
        }
        btn.setOnClickListener {
            checkPermissionsForCode()
        }
    }

    private fun checkPermissionsForCode() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            navController.navigate(R.id.action_permissionsFragment_to_cameraFragment)
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), Permission_broker)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Permission_broker
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            navController.navigate(R.id.action_permissionsFragment_to_cameraFragment)
        } else if (requestCode == Permission_broker
            && grantResults[0] == PackageManager.PERMISSION_DENIED
        ) {
            errorText.visibility = View.VISIBLE
        }
    }
}