package com.andrea.orangelibrary

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_book_form.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookFormActivity : AppCompatActivity() {
    private lateinit var database:BookDB
    private lateinit var bookLiveData: LiveData<BookEntity>
    private lateinit var book: BookEntity
    private var edit = false

    private var uriImage: Uri? = null
    companion object {
        val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_form)

        database = BookDB.getDatabase(this)

        spinnerController()

        val idBook = getBook()

        if(edit) {
            btn_form_add_edit.text = "Editar contacto"
        }

        btn_upload.setOnClickListener {
            pickImageGallery()
        }

        btn_form_add_edit.setOnClickListener {
            if (edit) {
                editBook(idBook)
            }
            else {
                createBook()
            }
        }
    }

    private fun spinnerController() {
        val spinner = findViewById<Spinner>(R.id.spn_form_category)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Category.values())
        spinner.adapter = adapter
    }

    private fun getBook(): Int {
        val idBook = intent.getIntExtra("id", 0)
        if (idBook != 0) {
            bookLiveData = database.getBookDao().getById(idBook)
            bookLiveData.observe(this, Observer {
                book = it
                tv_form_title.setText(book.title)
                tv_form_author.setText(book.author)
                tv_form_editorial.setText(book.editorial)
                tv_form_year.setText(book.year.toString())
                tv_form_price.setText(book.price.toString())
                spn_form_category.setSelection(book.category.ordinal)
                if (book.image != 0) {
                    val uriImage = ControllerImage.getUri(this, book.id.toLong())
                    iv_form_book.setImageURI(uriImage)
                }
            })
            edit = true
        }
        return idBook
    }

    private fun pickImageGallery() {
        ControllerImage.selectForGallery(this, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            uriImage = data!!.data
            iv_form_book.setImageURI(uriImage)
        }
    }

    private fun createBook() {
        val title = tv_form_title.text.toString()
        val author = tv_form_author.text.toString()
        val editorial = tv_form_editorial.text.toString()
        val year = tv_form_year.text.toString()
        val price = tv_form_price.text.toString()
        val category = spn_form_category.selectedItem.toString()

        val book = BookEntity(title, author, editorial, year.toInt(), price.toDouble(), Category.valueOf(category), R.drawable.ic_book_orange)
        //val book = BookEntity("Programación Orientada a Objetos C++ y Java", "José Luis López Goytia y Ángel Gutiérrez González", "PATRIA", 2014, 0)
        CoroutineScope(Dispatchers.IO).launch {
            val id = database.getBookDao().insertNew(book)[0]
            uriImage?.let {
                ControllerImage.save(this@BookFormActivity, id, it)
            }
        }
        this@BookFormActivity.finish()
    }

    private fun editBook(id: Int) {
        val title = tv_form_title.text.toString()
        val author = tv_form_author.text.toString()
        val editorial = tv_form_editorial.text.toString()
        val year = tv_form_year.text.toString()
        val price = tv_form_price.text.toString()
        val category = spn_form_category.selectedItem.toString()

        val book = BookEntity(title, author, editorial, year.toInt(), price.toDouble(), Category.valueOf(category), R.drawable.ic_book_orange, id)
        CoroutineScope(Dispatchers.IO).launch {
            database.getBookDao().update(book)
            uriImage?.let {
                ControllerImage.save(this@BookFormActivity, id.toLong(), it)
            }
        }
        this@BookFormActivity.finish()
    }
}