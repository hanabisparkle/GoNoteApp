package com.example.gonoteapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.gonoteapp.fragments.FolderListFragment
import com.example.gonoteapp.fragments.MainFragment
import com.example.gonoteapp.fragments.NewNoteFragment
import com.example.gonoteapp.fragments.NoteFullViewFragment
import com.example.gonoteapp.fragments.SettingsFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Activity utama yang menjadi host untuk semua fragment dan mengelola navigasi utama.
 * Bertanggung jawab untuk setup UI global seperti Toolbar, Bottom Navigation, dan Floating Action Button.
 */
class MainActivity : AppCompatActivity() {
    // Menyimpan nama folder yang sedang aktif, null jika di halaman utama.
    var currentFolder: String? = null
    private lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Memaksa mode terang (menonaktifkan mode gelap)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar_layout)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val createButton: FloatingActionButton = findViewById(R.id.createbutton)

        // Mengelola visibilitas UI berdasarkan fragment yang sedang ditampilkan.
        // Ini adalah cara yang efisien untuk menampilkan/menyembunyikan UI global.
        supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)

                // Sembunyikan AppBar dan BottomNav saat melihat/membuat catatan.
                if (f is NewNoteFragment || f is NoteFullViewFragment) {
                    appBarLayout.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                } else {
                    appBarLayout.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE
                }

                // Sembunyikan tombol "Create" di beberapa halaman tertentu.
                if (f is NoteFullViewFragment || f is NewNoteFragment || f is SettingsFragment) {
                    createButton.visibility = View.GONE
                } else {
                    createButton.visibility = View.VISIBLE
                }
            }
        }, true)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Menonaktifkan judul default

        toolbarTitle = findViewById(R.id.toolbar_title) // Mengambil referensi ke judul custom

        // Menangani navigasi melalui Bottom Navigation View.
        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_home -> {
                    selectedFragment = MainFragment()
                    onFolderSelected(null) // Reset folder saat kembali ke home
                }
                R.id.navigation_folders -> {
                    selectedFragment = FolderListFragment()
                    onFolderSelected(null) // Reset folder saat masuk ke daftar folder
                }
                R.id.navigation_settings -> {
                    selectedFragment = SettingsFragment()
                }
            }
            if (selectedFragment != null) {
                // Ganti fragment di container dengan fragment yang dipilih.
                supportFragmentManager.beginTransaction().replace(R.id.my_fragment_container, selectedFragment).commit()
            }
            true
        }

        // Jika aplikasi baru dibuka, tampilkan fragment home secara default.
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.navigation_home
        }

        // Menangani aksi klik pada tombol "Create" (Floating Action Button).
        createButton.setOnClickListener {
            showCreateDialog()
        }
    }

    /**
     * Memperbarui judul di Toolbar. Fungsi ini dipanggil oleh fragment (misal: FolderNotesFragment).
     */
    fun updateTitle(newTitle: String) {
        toolbarTitle.text = newTitle
    }

    /**
     * Menyimpan nama folder yang sedang dibuka. Dipanggil oleh fragment.
     */
    fun onFolderSelected(folderName: String?) {
        currentFolder = folderName
    }

    /**
     * Menangani event klik pada item di toolbar, seperti tombol "Back".
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed() // Aksi kembali standar
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Menampilkan dialog untuk memilih antara membuat "New Note" atau "New Folder".
     */
    private fun showCreateDialog() {
        val options = arrayOf("New Note", "New Folder")
        MaterialAlertDialogBuilder(this)
            .setTitle("Create")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { // Pilihan "New Note"
                        val newNoteFragment = NewNoteFragment()
                        // Jika sedang di dalam folder, kirim nama folder ke NewNoteFragment.
                        currentFolder?.let {
                            newNoteFragment.arguments = bundleOf("FOLDER_NAME" to it)
                        }
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.my_fragment_container, newNoteFragment)
                            .addToBackStack(null) // Tambahkan ke back stack agar bisa kembali
                            .commit()
                    }
                    1 -> { // Pilihan "New Folder"
                        showNewFolderDialog()
                    }
                }
            }
            .show()
    }

    /**
     * Menampilkan dialog untuk memasukkan nama folder baru.
     */
    private fun showNewFolderDialog() {
        val editText = EditText(this).apply {
            hint = "Folder Name"
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 20)
            addView(editText)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("New Folder")
            .setView(layout)
            .setPositiveButton("Create") { _, _ ->
                val folderName = editText.text.toString()
                if (folderName.isNotBlank()) {
                    // Panggil repository untuk menyimpan folder baru.
                    NoteRepository.addFolder(folderName)
                }
                // Refresh tampilan daftar folder.
                supportFragmentManager.beginTransaction()
                    .replace(R.id.my_fragment_container, FolderListFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
