package com.yang.simpleplayer.common

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.appcompat.app.AlertDialog


object MoreDialogFactory {
    fun create(context: Context, @ArrayRes itemsId:Int, vararg callbacks:()->Unit) =
        AlertDialog.Builder(context).setItems(itemsId) {_, which ->
            callbacks[which]()
        }.create()

    fun create(context: Context, items: Array<CharSequence>, vararg callbacks:()->Unit) =
        AlertDialog.Builder(context).setItems(items) {_, which ->
            callbacks[which]()
        }.create()
}