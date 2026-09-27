package com.example.mini_project.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mini_project.CartActivity
import com.example.mini_project.R
import com.example.mini_project.adapters.AlbumAdapter
import com.example.mini_project.adapters.HeroAdapter
import com.example.mini_project.backend.AlbumDTO
import com.example.mini_project.backend.Auth
import com.example.mini_project.backend.CartDTO
import com.example.mini_project.backend.CartService
import com.example.mini_project.instance.RetrofitInstance
import com.example.mini_project.models.HeroItem
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var heroViewPager: ViewPager2
    private lateinit var albumRecyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var searchInput: TextInputEditText
    private lateinit var filterChipGroup: ChipGroup
    private lateinit var cartButton: ImageButton
    private lateinit var cartBadge: TextView

    private lateinit var cartService: CartService

    private var allAlbums: List<AlbumDTO> = emptyList()

    private var selectedGenre = "All"

    private val handler =
        Handler(Looper.getMainLooper())


    private val heroItems = listOf(

        HeroItem(
            image = R.drawable.vinyl_banner,
            title = "Build Your Collection",
            description = "Find records for your vinyl collection.",
            buttonText = "Shop Now"
        ),

        HeroItem(
            image = R.drawable.vinyl_new_release,
            title = "Discover New Music",
            description = "Explore amazing music on vinyl.",
            buttonText = "Explore"
        ),

        HeroItem(
            image = R.drawable.vinyl_pop,
            title = "Music Lives On Vinyl",
            description = "Experience music the classic way.",
            buttonText = "Discover"
        )
    )


    private val heroRunnable =
        object : Runnable {

            override fun run() {

                if (
                    ::heroViewPager.isInitialized &&
                    heroItems.isNotEmpty() &&
                    isAdded
                ) {

                    val nextItem =
                        (heroViewPager.currentItem + 1) %
                                heroItems.size

                    heroViewPager.setCurrentItem(
                        nextItem,
                        true
                    )

                    handler.postDelayed(
                        this,
                        4000
                    )
                }
            }
        }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )


        heroViewPager =
            view.findViewById(
                R.id.heroViewPager
            )

        albumRecyclerView =
            view.findViewById(
                R.id.albumRecyclerView
            )

        searchInput =
            view.findViewById(
                R.id.searchInput
            )

        filterChipGroup =
            view.findViewById(
                R.id.filterChipGroup
            )

        cartButton =
            view.findViewById(
                R.id.cartButton
            )

        cartBadge =
            view.findViewById(
                R.id.cartBadge
            )


        cartService =
            CartService(
                RetrofitInstance.getApi(
                    requireContext()
                )
            )


        setupHero()
        setupAlbums()
        setupSearch()
        setupFilters()
        setupCartButton()

        loadAlbums()
        updateCartBadge()
    }


    // ==========================================
    // HERO VIEWPAGER
    // ==========================================

    private fun setupHero() {

        val heroAdapter =
            HeroAdapter(
                heroItems
            ) { heroItem ->

                when (heroItem.buttonText) {

                    "Shop Now" -> {
                        albumRecyclerView.requestFocus()
                    }

                    "Explore" -> {
                        albumRecyclerView.requestFocus()
                    }

                    "Discover" -> {
                        albumRecyclerView.requestFocus()
                    }
                }
            }


        heroViewPager.adapter =
            heroAdapter

        heroViewPager.offscreenPageLimit =
            1


        handler.removeCallbacks(
            heroRunnable
        )

        handler.postDelayed(
            heroRunnable,
            4000
        )
    }


    // ==========================================
    // ALBUMS
    // ==========================================

    private fun setupAlbums() {

        albumRecyclerView.layoutManager =
            GridLayoutManager(
                requireContext(),
                2
            )

        albumRecyclerView.isNestedScrollingEnabled =
            false

        albumRecyclerView.setHasFixedSize(
            false
        )


        albumAdapter =
            AlbumAdapter(
                emptyList(),

                onAddToCartClick = { album ->
                    addAlbumToCart(album)
                },

                onAlbumClick = { album ->
                    openAlbumDetail(album)
                }
            )


        albumRecyclerView.adapter =
            albumAdapter
    }


    private fun loadAlbums() {

        val api =
            RetrofitInstance.getApi(
                requireContext()
            )


        api.getAlbums().enqueue(
            object : Callback<List<AlbumDTO>> {

                override fun onResponse(
                    call: Call<List<AlbumDTO>>,
                    response: Response<List<AlbumDTO>>
                ) {

                    if (!isAdded) {
                        return
                    }


                    if (response.isSuccessful) {

                        allAlbums =
                            response.body()
                                ?: emptyList()


                        applyFilters()


                        albumRecyclerView.post {
                            albumRecyclerView.requestLayout()
                        }

                    } else {

                        android.util.Log.e(
                            "HomeAlbums",
                            "Failed to load albums: ${response.code()}"
                        )

                        showToast(
                            "Unable to load albums. Please try again."
                        )
                    }
                }


                override fun onFailure(
                    call: Call<List<AlbumDTO>>,
                    t: Throwable
                ) {

                    if (!isAdded) {
                        return
                    }


                    android.util.Log.e(
                        "HomeAlbums",
                        "Failed to load albums",
                        t
                    )


                    showToast(
                        "Unable to load albums. Please try again."
                    )
                }
            }
        )
    }


    // ==========================================
    // ALBUM DETAIL
    // ==========================================

    private fun openAlbumDetail(
        album: AlbumDTO
    ) {

        val fragment =
            AlbumDetailFragment()


        fragment.arguments =
            Bundle().apply {

                putInt(
                    "albumId",
                    album.id
                )
            }


        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                fragment
            )
            .addToBackStack(null)
            .commit()
    }


    // ==========================================
    // SEARCH
    // ==========================================

    private fun setupSearch() {

        searchInput.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }


                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    applyFilters()
                }


                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }


    // ==========================================
    // FILTERS
    // ==========================================

    private fun setupFilters() {

        filterChipGroup.setOnCheckedStateChangeListener {
                group,
                checkedIds ->

            selectedGenre =
                if (checkedIds.isEmpty()) {

                    "All"

                } else {

                    group.findViewById<Chip>(
                        checkedIds[0]
                    )
                        ?.text
                        ?.toString()
                        ?.trim()
                        ?: "All"
                }


            applyFilters()
        }
    }


    private fun applyFilters() {

        var filteredAlbums =
            allAlbums


        val keyword =
            searchInput.text
                ?.toString()
                ?.trim()
                ?: ""


        // ======================================
        // SEARCH
        // ======================================

        if (keyword.isNotEmpty()) {

            filteredAlbums =
                filteredAlbums.filter { album ->

                    album.title.contains(
                        keyword,
                        ignoreCase = true
                    ) ||

                            album.artist.contains(
                                keyword,
                                ignoreCase = true
                            ) ||

                            album.genre?.contains(
                                keyword,
                                ignoreCase = true
                            ) == true
                }
        }


        // ======================================
        // GENRE FILTER
        // ======================================

        if (
            !selectedGenre.equals(
                "All",
                ignoreCase = true
            )
        ) {

            filteredAlbums =
                filteredAlbums.filter { album ->

                    album.genre
                        ?.trim()
                        ?.replace(
                            "-",
                            " "
                        )
                        ?.replace(
                            "_",
                            " "
                        )
                        ?.equals(
                            selectedGenre
                                .trim()
                                .replace(
                                    "-",
                                    " "
                                )
                                .replace(
                                    "_",
                                    " "
                                ),
                            ignoreCase = true
                        ) == true
                }
        }


        albumAdapter.updateAlbums(
            filteredAlbums
        )
    }


    // ==========================================
    // CART BUTTON
    // ==========================================

    private fun setupCartButton() {

        cartButton.setOnClickListener {

            startActivity(
                Intent(
                    requireContext(),
                    CartActivity::class.java
                )
            )
        }
    }


    // ==========================================
    // ADD TO CART
    // ==========================================

    private fun addAlbumToCart(
        album: AlbumDTO
    ) {

        val context =
            requireContext()


        val userId =
            Auth.getUserId(
                context
            )


        if (userId == -1) {

            showToast(
                "Please log in to add items to your cart."
            )

            return
        }


        cartService
            .addToCart(
                userId = userId,
                albumId = album.id,
                quantity = 1
            )
            .enqueue(
                object : Callback<CartDTO> {

                    override fun onResponse(
                        call: Call<CartDTO>,
                        response: Response<CartDTO>
                    ) {

                        if (!isAdded) {
                            return
                        }


                        if (response.isSuccessful) {

                            showToast(
                                "${album.title} added to your cart."
                            )


                            updateCartBadge()

                        } else {

                            android.util.Log.e(
                                "HomeCart",
                                "Add to cart failed: ${response.code()} - ${
                                    response.errorBody()
                                        ?.string()
                                }"
                            )


                            showToast(
                                "Unable to add this album to your cart."
                            )
                        }
                    }


                    override fun onFailure(
                        call: Call<CartDTO>,
                        t: Throwable
                    ) {

                        if (!isAdded) {
                            return
                        }


                        android.util.Log.e(
                            "HomeCart",
                            "Add to cart request failed",
                            t
                        )


                        showToast(
                            "Unable to add the album to your cart. Please try again."
                        )
                    }
                }
            )
    }


    // ==========================================
    // CART BADGE
    // ==========================================

    private fun updateCartBadge() {

        val userId =
            Auth.getUserId(
                requireContext()
            )


        if (userId == -1) {

            cartBadge.visibility =
                View.GONE

            return
        }


        cartService
            .getCarts()
            .enqueue(
                object : Callback<List<CartDTO>> {

                    override fun onResponse(
                        call: Call<List<CartDTO>>,
                        response: Response<List<CartDTO>>
                    ) {

                        if (!isAdded) {
                            return
                        }


                        if (response.isSuccessful) {

                            val carts =
                                response.body()
                                    ?: emptyList()


                            val userCart =
                                carts.firstOrNull {
                                    it.userId == userId
                                }


                            val itemCount =
                                userCart
                                    ?.items
                                    ?.sumOf {
                                        it.quantity
                                    }
                                    ?: 0


                            if (itemCount > 0) {

                                cartBadge.text =
                                    itemCount.toString()

                                cartBadge.visibility =
                                    View.VISIBLE

                            } else {

                                cartBadge.visibility =
                                    View.GONE
                            }
                        }
                    }


                    override fun onFailure(
                        call: Call<List<CartDTO>>,
                        t: Throwable
                    ) {

                        android.util.Log.e(
                            "HomeCart",
                            "Failed to load cart badge",
                            t
                        )
                    }
                }
            )
    }


    // ==========================================
    // RESUME
    // ==========================================

    override fun onResume() {

        super.onResume()


        handler.removeCallbacks(
            heroRunnable
        )


        handler.postDelayed(
            heroRunnable,
            4000
        )


        if (::cartBadge.isInitialized) {

            updateCartBadge()
        }
    }


    // ==========================================
    // PAUSE
    // ==========================================

    override fun onPause() {

        super.onPause()


        handler.removeCallbacks(
            heroRunnable
        )
    }


    // ==========================================
    // DESTROY VIEW
    // ==========================================

    override fun onDestroyView() {

        handler.removeCallbacks(
            heroRunnable
        )

        super.onDestroyView()
    }


    // ==========================================
    // TOAST HELPER
    // ==========================================

    private fun showToast(
        message: String
    ) {

        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}