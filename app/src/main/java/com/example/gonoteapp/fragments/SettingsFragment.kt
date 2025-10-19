package com.example.gonoteapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gonoteapp.MainActivity
import com.example.gonoteapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * Fragment untuk menampilkan halaman pengaturan.
 * Saat ini mengelola status login (prototipe) dan opsi backup.
 */
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    // Status login pengguna (prototipe). false = keluar, true = masuk.
    private var isUserLoggedIn = false

    private lateinit var accountSection: LinearLayout
    private lateinit var accountTitle: TextView
    private lateinit var accountSubtitle: TextView
    private lateinit var backupSwitch: SwitchMaterial
    private lateinit var backupStatusSubtitle: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi semua view dari layout.
        accountSection = view.findViewById(R.id.account_section)
        accountTitle = view.findViewById(R.id.account_title)
        accountSubtitle = view.findViewById(R.id.account_subtitle)
        backupSwitch = view.findViewById(R.id.backup_switch)
        backupStatusSubtitle = view.findViewById(R.id.backup_status_subtitle)

        updateUi() // Perbarui UI berdasarkan status login awal.

        // Menangani klik pada bagian akun.
        accountSection.setOnClickListener {
            if (isUserLoggedIn) {
                showLogoutDialog() // Jika sudah login, tampilkan dialog logout.
            } else {
                // Jika belum login, simulasikan proses login.
                isUserLoggedIn = true
                updateUi()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Set judul di toolbar MainActivity.
        (activity as? MainActivity)?.updateTitle("Settings")
    }

    /**
     * Memperbarui tampilan UI (teks dan status switch) berdasarkan status login.
     */
    private fun updateUi() {
        if (isUserLoggedIn) {
            accountTitle.text = "Prototype User"
            accountSubtitle.text = "user.prototype@email.com"
            backupSwitch.isEnabled = true
            backupSwitch.isChecked = true // Anggap backup aktif setelah login.
            backupStatusSubtitle.text = "Last backup: Never"
        } else {
            accountTitle.text = "Account"
            accountSubtitle.text = "Log in to back up and sync your notes"
            backupSwitch.isEnabled = false
            backupSwitch.isChecked = false
            backupStatusSubtitle.text = "Log in to enable backup"
        }
    }

    /**
     * Menampilkan dialog konfirmasi untuk logout.
     */
    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { _, _ ->
                // Jika dikonfirmasi, ubah status login dan perbarui UI.
                isUserLoggedIn = false
                updateUi()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
