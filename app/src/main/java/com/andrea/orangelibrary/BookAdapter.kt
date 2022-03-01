package com.andrea.orangelibrary

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.activity_book.view.iv_main_book
import kotlinx.android.synthetic.main.activity_book.view.tv_main_title
import kotlinx.android.synthetic.main.activity_book.view.btn_delete

class BookAdapter(private val mContext: Context, private val listBooks: List<BookEntity>):
    ArrayAdapter<BookEntity>(mContext, 0, listBooks) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.activity_book, parent, false)
        val book = listBooks[position]

        layout.tv_main_title.text = book.title
        val uriImage = ControllerImage.getUri(context, book.id.toLong())
        layout.iv_main_book.setImageURI(uriImage)

        layout.btn_delete.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(mContext)
            dialogBuilder.setMessage("¿Está seguro que desea borrar el libro: ${book.title} ?").setCancelable(false)
                .setPositiveButton("Sí", DialogInterface.OnClickListener {
                        dialog, id ->
                    val database = BookDB.getDatabase(mContext)
                    val book = BookEntity(book.title, book.author, book.editorial, book.year, book.image, book.id)
                    CoroutineScope(Dispatchers.IO).launch {
                        database.getBookDao().delete(book)
                        uriImage?.let {
                            ControllerImage.delete(mContext, book.id.toLong())
                        }
                    }
                })
                .setNegativeButton("No", DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })
            val alert = dialogBuilder.create()
            alert.setTitle("Alerta")
            alert.show()
        }

        return layout
    }
}