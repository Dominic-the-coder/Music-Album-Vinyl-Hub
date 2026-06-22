package com.example.mini_project

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_project.adapters.AlbumAdapter
import com.example.mini_project.mockData.MockData
import com.example.mini_project.model.Album
import com.example.mini_project.services.AlbumService

class AlbumListActivity : AppCompatActivity() {

    private lateinit var adapter: AlbumAdapter
    private lateinit var fullList: List<Album>

    private lateinit var albumService: AlbumService

    private fun selectChip(selected: Chip, chips: List<Chip>) {
        chips.forEach {
            it.setChipBackgroundColorResource(R.color.dark_gray)
            it.setTextColor(getColor(R.color.black))
        }

        selected.setChipBackgroundColorResource(R.color.white)
        selected.setTextColor(getColor(R.color.black))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvDetails)
        val searchBox = findViewById<EditText>(R.id.editTextText2)
        val cartButton = findViewById<ImageButton>(R.id.imageButton3)
        val btnAll = findViewById<Chip>(R.id.btnAll)
        val btnWeeknd = findViewById<Chip>(R.id.btnWeeknd)
        val btnTravis = findViewById<Chip>(R.id.btnTravis)
        val btnDrake = findViewById<Chip>(R.id.btnDrake)
        val btnUzi = findViewById<Chip>(R.id.btnUzi)
        val btnJustin = findViewById<Chip>(R.id.btnJustin)
        val btnGloria = findViewById<Chip>(R.id.btnGloria)

        val chips = listOf(
            btnAll,
            btnWeeknd,
            btnTravis,
            btnDrake,
            btnUzi,
            btnJustin,
            btnGloria
        )

        fullList = MockData.populateData()

        adapter = AlbumAdapter(fullList)

        albumService = AlbumService(fullList,adapter)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        selectChip(btnAll, chips)
        
        cartButton.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s.toString().trim().lowercase()

                val filtered = albumService.retrieveCurrentSelectedAlbums().filter {
                    it.title.lowercase().contains(q) ||
                            it.artist.lowercase().contains(q)
                }

                adapter.updateList(filtered)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnAll.setOnClickListener {
            selectChip(btnAll, chips)
            albumService.updateList(fullList)
        }

        btnWeeknd.setOnClickListener {
            selectChip(btnWeeknd, chips)
            albumService.updateList(
                fullList.filter { it.artist == "The Weeknd" }
            )
        }

        btnTravis.setOnClickListener {
            selectChip(btnTravis, chips)
            albumService.updateList(
                fullList.filter { it.artist == "Travis Scott" }
            )
        }

        btnDrake.setOnClickListener {
            selectChip(btnDrake, chips)
            albumService.updateList(
                fullList.filter { it.artist == "Drake" }
            )
        }

        btnUzi.setOnClickListener {
            selectChip(btnUzi, chips)
            albumService.updateList(
                fullList.filter { it.artist == "Lil Uzi Vert" }
            )
        }

        btnJustin.setOnClickListener {
            selectChip(btnJustin, chips)
            albumService.updateList(
                fullList.filter { it.artist == "Justin Bieber" }
            )
        }

        btnGloria.setOnClickListener {
            selectChip(btnGloria, chips)
            albumService.updateList(
                fullList.filter { it.artist == "Gloria Gaynor" }
            )
        }
    }
}