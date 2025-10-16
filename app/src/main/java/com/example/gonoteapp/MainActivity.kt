package com.example.gonoteapp

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.gonoteapp.fragments.FolderListFragment
import com.example.gonoteapp.fragments.MainFragment
import com.example.gonoteapp.fragments.NewNoteFragment
import com.example.gonoteapp.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            var title: String = ""
            when (item.itemId) {
                R.id.navigation_home -> {
                    selectedFragment = MainFragment()
                    title = "Home"
                }
                R.id.navigation_folders -> {
                    selectedFragment = FolderListFragment()
                    title = "Folders"
                }
                R.id.navigation_settings -> {
                    selectedFragment = SettingsFragment()
                    title = "Settings"
                }
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.my_fragment_container, selectedFragment).commit()
                supportActionBar?.title = title
            }
            true
        }

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.navigation_home
        }

        val createButton: FloatingActionButton = findViewById(R.id.createbutton)
        createButton.setOnClickListener {
            showCreateDialog()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCreateDialog() {
        val options = arrayOf("New Note", "New Folder")
        AlertDialog.Builder(this)
            .setTitle("Create")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.my_fragment_container, NewNoteFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    1 -> {
                        showNewFolderDialog()
                    }
                }
            }
            .show()
    }

    private fun showNewFolderDialog() {
        val editText = EditText(this).apply {
            hint = "Folder Name"
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 20)
            addView(editText)
        }

        AlertDialog.Builder(this)
            .setTitle("New Folder")
            .setView(layout)
            .setPositiveButton("Create") { _, _ ->
                val folderName = editText.text.toString()
                if (folderName.isNotBlank()) {
                    NoteRepository.addFolder(folderName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
