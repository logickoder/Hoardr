package com.zuri.pjt_95_hoardr.ui.registration

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.FirebaseFirestoreKtxRegistrar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zuri.pjt_95_hoardr.R
import com.zuri.pjt_95_hoardr.databinding.FragmentThirdRegistrationBinding
import com.zuri.pjt_95_hoardr.model.User
import java.util.concurrent.Executors
import java.util.regex.Pattern


/**
 * A simple [Fragment] subclass.
 * Use the [ThirdRegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdRegistrationFragment : Fragment() {
    private var _binding: FragmentThirdRegistrationBinding? = null
    private val sharedViewModel: RegistrationViewModel by activityViewModels()
    private lateinit var  db: FirebaseFirestore

    private var count = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Access a Cloud Firestore instance
         db = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentThirdRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
        }

        binding.registerBtn.setOnClickListener { registerUser() }

        val passwordField = binding.passwordField
        val passwordInput = passwordField.editText?.text.toString()
        val passwordRequirements = binding.passwordRequirementText

        passwordField.editText?.doOnTextChanged { passwordInput, _, _, _ ->
            // Respond to input text change
            validatePassword(passwordInput, passwordField, passwordRequirements)
        }

    }

    private fun checkPasswordMatch(
        passwordField: TextInputLayout,
        confirmPasswordField: TextInputLayout
    ): Boolean {
        val passwordValue = passwordField.editText?.text.toString()
        val confirmPasswordValue = confirmPasswordField.editText?.text.toString()

        return passwordValue == confirmPasswordValue
    }

    private fun registerUser() {
        val firstName = sharedViewModel.firstName.value
        val surname = sharedViewModel.lastName.value
        val email = sharedViewModel.emailAddress.value
        val phoneNumber = sharedViewModel.phoneNumber.value

        val applicationContext = this.requireContext()


        val doesPasswordMatch =
            checkPasswordMatch(binding.passwordField, binding.confirmPasswordField)

        if (doesPasswordMatch) {
            val userPassword = binding.passwordField.editText?.text.toString()
            createNewUser(firstName, surname, email, phoneNumber, userPassword)

        } else {
            Toast.makeText(
                applicationContext,
                "Please make sure the password entered matches your confirmation password",
                Toast.LENGTH_SHORT
            )
        }

    }

    private fun createNewUser(
        firstName: String?,
        surname: String?,
        email: String?,
        phoneNumber: String?,
        userPassword: String
    ) {
        val newUser = hashMapOf(
            "first" to firstName,
            "last" to surname,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "password" to userPassword
        )
        val handler = Handler(Looper.getMainLooper())


        // Add a new document with a generated ID
        db.collection("users")
            .add(newUser)
            .addOnSuccessListener { documentReference ->

                Log.d(
                    "RegistrationFragment",
                    "DocumentSnapshot added with ID: ${documentReference.id}"
                )

                handler.post {
                    Toast.makeText(this.requireContext(),
                        "Creating new account was successful",
                        Toast.LENGTH_SHORT).show()
                         findNavController().navigate(R.id.loginFragment)
                }

            }
            .addOnFailureListener { e ->
                Log.e("RegistrationFragment", "Error adding document", e)
            }
    }


    private fun validatePassword(
        passwordText: CharSequence?,
        outlinePasswordField: TextInputLayout,
        passwordRequirementTextView: TextView,

        ) {
        val uppercase: Pattern = Pattern.compile("[A-Z]")
        val lowercase: Pattern = Pattern.compile("[a-z]")
        val specialChar = Pattern.compile("\\@")
        val digit: Pattern = Pattern.compile("[0-9]")


        if (!lowercase.matcher(passwordText).find() || !uppercase.matcher(passwordText)
                .find() || !digit.matcher(passwordText)
                .find() || !specialChar.matcher(passwordText)
                .find() || passwordText?.length!! < 8 || passwordText.length > 10
        ) {
            outlinePasswordField.error =
                "The password you entered is wrong, kindly confirm if you\n" +
                        "missed any of the requirements below."
            //Make Password Requirements visible
            passwordRequirementTextView.visibility = View.VISIBLE

        } else {
            outlinePasswordField.error = null
            outlinePasswordField.boxStrokeColor = Color.parseColor("#34eb77")
            //Make Password Requirements disappear
            passwordRequirementTextView.visibility = View.GONE
        }
    }

    companion object {}
}