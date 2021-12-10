package com.ppb2.kalfian.covidtracker.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.ppb2.kalfian.covidtracker.R

fun customModal(ctx: Context, title: String, subTitle: String, okTitle: String, cancelTitle: String, disableCancel: Boolean, btnOkOnClick: View.OnClickListener) {
    val dialog = Dialog(ctx)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(R.layout.custom_modal)
    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


    val titleView = dialog.findViewById<TextView>(R.id.modal_title)
    val subTitleView = dialog.findViewById<TextView>(R.id.modal_subtitle)
    val btnCancel = dialog.findViewById<Button>(R.id.btn_modal_batal)
    val btnOk = dialog.findViewById<Button>(R.id.btn_modal_ok)

    titleView.text = title
    subTitleView.text = subTitle

    btnCancel.text = cancelTitle
    btnOk.text = okTitle

    if(disableCancel) {
        btnCancel.visibility = View.GONE
    }

    btnOk.setOnClickListener(btnOkOnClick)
    btnCancel.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}