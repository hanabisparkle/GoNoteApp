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

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    // Prototype state: false = logged out, true = logged in
    private var isUserLoggedIn = false

    private lateinit var accountSection: LinearLayout
    private lateinit var accountTitle: TextView
    private lateinit var accountSubtitle: TextView
    private lateinit var backupSwitch: SwitchMaterial
    private lateinit var backupStatusSubtitle: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountSection = view.findViewById(R.id.account_section)
        accountTitle = view.findViewById(R.id.account_title)
        accountSubtitle = view.findViewById(R.id.account_subtitle)
        backupSwitch = view.findViewById(R.id.backup_switch)
        backupStatusSubtitle = view.findViewById(R.id.backup_status_subtitle)

        updateUi()

        accountSection.setOnClickListener {
            if (isUserLoggedIn) {
                showLogoutDialog()
            } else {
                isUserLoggedIn = true
                updateUi()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.updateTitle("Settings")
    }
    private fun updateUi() {
        if (isUserLoggedIn) {
            accountTitle.text = "Prototype User"
            accountSubtitle.text = "user.prototype@email.com"
            backupSwitch.isEnabled = true
            backupSwitch.isChecked = true // Assume backup is on after login
            backupStatusSubtitle.text = "Last backup: Never"
        } else {
            accountTitle.text = "Account"
            accountSubtitle.text = "Log in to back up and sync your notes"
            backupSwitch.isEnabled = false
            backupSwitch.isChecked = false
            backupStatusSubtitle.text = "Log in to enable backup"
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out") { _, _ ->
                isUserLoggedIn = false
                updateUi()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}
