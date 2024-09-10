package com.example.purigone.Util

import android.content.Context
import android.widget.Toast

object UI {

    fun showToast(context : Context, message : String){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
    }
}