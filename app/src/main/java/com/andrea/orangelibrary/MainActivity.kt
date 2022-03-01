package com.andrea.orangelibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.activity_main.lv_books
import kotlinx.android.synthetic.main.activity_main.btn_main_add
import kotlinx.android.synthetic.main.activity_main.sv_search

class MainActivity : AppCompatActivity() {

    private lateinit var database:BookDB
    private var listBooks = emptyList<BookEntity>()
    private var itemSelected: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = BookDB.getDatabase(this)

        spinnerController()
        searchController()

        btn_main_add.setOnClickListener {
            startActivity(Intent(this, BookFormActivity::class.java))
        }

        lv_books.setOnItemClickListener  { parent, view, position, id ->
            val intent = Intent(this, BookFormActivity::class.java)
            intent.putExtra("id", listBooks[position].id)
            startActivity(intent)
        }
    }

    private fun getBooks() {
        database.getBookDao().getAll().observe(this) {
            listBooks = it
            val adapter = BookAdapter(this, listBooks)
            lv_books.adapter = adapter
        }
    }

    private fun search(bookLiveData: LiveData<List<BookEntity>>) {
        bookLiveData.observe(this@MainActivity) {
            if (it.isEmpty()) {
                Toast.makeText(this@MainActivity, "Sin resultados", Toast.LENGTH_SHORT).show()
            }
            else {
                listBooks = it
                val adapter = BookAdapter(this@MainActivity, listBooks)
                lv_books.adapter = adapter
            }
        }
    }

    private fun spinnerController() {
        val itemList = arrayListOf<String>("Todos","Título","Autor", "Editorial", "Año")

        val spinner = findViewById<Spinner>(R.id.sp_search)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemList)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                itemSelected = itemList[p2]
                if (itemSelected == "Todos" || p2 == 0) {
                    getBooks()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun searchController() {
        sv_search.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                when (itemSelected) {
                    "Todos" -> getBooks()
                    "Título" -> search(database.getBookDao().getByTitle(query))
                    "Autor" -> search(database.getBookDao().getByAuthor(query))
                    "Editorial" -> search(database.getBookDao().getByEditorial(query))
                    "Año" -> search(database.getBookDao().getByYear(query))
                    else -> {
                        Toast.makeText(this@MainActivity, "Ingrese el dato a buscar", Toast.LENGTH_SHORT).show()
                    }
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }
}
