package me.sid.smartcropper.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import kotlinx.android.synthetic.main.permission_dialog.*

import me.sid.smartcropper.R

class PermissionDialog(internal var _activity: Activity,var permissionGranted: PermissionGranted) : Dialog(_activity) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permission_dialog)


        allowBtn.setOnClickListener {
            dismiss()
            permissionGranted.granted()
        }


    }

    interface PermissionGranted{
        fun granted()
    }



}