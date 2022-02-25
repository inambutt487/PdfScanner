package me.sid.smartcropper.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import kotlinx.android.synthetic.main.exit_dialog.*

class ExitDialog(internal var _activity: Activity) : Dialog(_activity) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(me.sid.smartcropper.R.layout.exit_dialog)


        exitBtn.setOnClickListener {
            dismiss()
            _activity?.finishAffinity()
        }

        noBtn.setOnClickListener {
            dismiss()
        }
    }
}