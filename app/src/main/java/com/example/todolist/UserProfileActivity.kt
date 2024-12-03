package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class UserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout file for this activity
        setContentView(R.layout.activity_user_profile)

        // Set up the toolbar with a back button
        val toolbar = findViewById<Toolbar>(R.id.userProfileToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Enable back button

        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back button click
        }

        // Initialize views
        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPhone = findViewById<EditText>(R.id.editTextPhone)
        val buttonSave = findViewById<Button>(R.id.btnSaveProfile)

        // SharedPreferences to store user data
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)

        // Load previously saved data
        val name = sharedPreferences.getString("name", "")
        val email = sharedPreferences.getString("email", "")
        val phone = sharedPreferences.getString("phone", "")

        // Pre-fill the fields with loaded data
        editTextName.setText(name)
        editTextEmail.setText(email)
        editTextPhone.setText(phone)

        // Save data when button is clicked
        buttonSave.setOnClickListener {
            val inputName = editTextName.text.toString()
            val inputEmail = editTextEmail.text.toString()
            val inputPhone = editTextPhone.text.toString()

            // Validate user inputs
            if (inputName.isNotEmpty() && inputEmail.isNotEmpty() && inputPhone.isNotEmpty()) {
                val editor = sharedPreferences.edit()
                editor.putString("name", inputName)
                editor.putString("email", inputEmail)
                editor.putString("phone", inputPhone)
                editor.apply()

                Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
