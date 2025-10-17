package com.example.gonoteapp

import android.os.Bundle
import android.text.Layout
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
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
    var currentFolder: String? = null
    private lateinit var toolbarTitle: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbarTitle = findViewById(R.id.toolbar_title)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val createButton: FloatingActionButton = findViewById(R.id.createbutton)

        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_home -> {
                    selectedFragment = MainFragment()
                    createButton.visibility = View.VISIBLE
                    onFolderSelected(null)
                }
                R.id.navigation_folders -> {
                    selectedFragment = FolderListFragment()
                    createButton.visibility = View.VISIBLE
                    onFolderSelected(null)
                }
                R.id.navigation_settings -> {
                    selectedFragment = SettingsFragment()
                    createButton.visibility = View.GONE
                }
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.my_fragment_container, selectedFragment).commit()
            }
            true
        }

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.navigation_home
        }

        createButton.setOnClickListener {
            showCreateDialog()
        }
    }

    fun updateTitle(newTitle: String) {
        toolbarTitle.text = newTitle
    }

    fun onFolderSelected(folderName: String?) {
        currentFolder = folderName
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
                        val newNoteFragment = NewNoteFragment()
                        currentFolder?.let {
                            newNoteFragment.arguments = bundleOf("FOLDER_NAME" to it)
                        }
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.my_fragment_container, newNoteFragment)
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
                supportFragmentManager.beginTransaction()
                    .replace(R.id.my_fragment_container, FolderListFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}