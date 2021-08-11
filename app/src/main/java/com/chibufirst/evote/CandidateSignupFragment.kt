package com.chibufirst.evote

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chibufirst.evote.databinding.FragmentCandidateSignupBinding
import com.chibufirst.evote.models.Student
import com.chibufirst.evote.util.Constants
import com.chibufirst.evote.util.Util
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.FileNotFoundException

class CandidateSignupFragment : Fragment() {

    private var binding: FragmentCandidateSignupBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var profileUrl = ""

    companion object {
        private val TAG: String = CandidateSignupFragment::class.java.simpleName
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                uploadImage(imageUri!!)
                try {
                    val imageStream = requireContext().contentResolver.openInputStream(imageUri)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    binding!!.photoImage.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Util.displayLongMessage(requireContext(), "Something went wrong.")
                }
            } else {
                Util.displayLongMessage(requireContext(), "Nothing Selected.")
            }
        }

    private fun getFileExtension(uri: Uri): String {
        val resolver = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(resolver.getType(uri))!!
    }

    private fun uploadImage(imageUri: Uri) {
        val ref = storage.reference.child(
            "images/${System.currentTimeMillis()}.${
                getFileExtension(imageUri)
            }"
        )
        val uploadTask = ref.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Util.displayShortMessage(requireContext(), "Error: \n${it.message}")
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { t ->
            if (t.isSuccessful) {
                profileUrl = t.result.toString()
                Util.displayLongMessage(requireContext(), "Image uploaded.")
            } else {
                Util.displayShortMessage(
                    requireContext(),
                    "Unsuccessful: \n${t.exception?.message}"
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCandidateSignupBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage
        binding!!.apply {
            chooseButton.setOnClickListener { selectPhoto() }
            signupButton.setOnClickListener { validateUserInputs() }
            progressLayout.setOnClickListener(null)
        }
        toggleProgressLayout(false)
    }

    private fun selectPhoto() {
        val imageIntent = Intent(Intent.ACTION_GET_CONTENT)
        imageIntent.type = "image/*"
        startForResult.launch(imageIntent)
    }

    private fun validateUserInputs() {
        Util.hideKeyboard(requireActivity())
        binding!!.apply {
            when {
                fullnameEditText.text.isEmpty() -> {
                    Util.displayLongMessage(requireContext(), "Full name required.")
                    fullnameEditText.requestFocus()
                    return
                }
                regnoEditText.text.isEmpty() -> {
                    Util.displayLongMessage(
                        requireContext(),
                        "Please enter your registration number."
                    )
                    regnoEditText.requestFocus()
                    return
                }
                emailEditText.text.isEmpty() -> {
                    Util.displayLongMessage(requireContext(), "Please enter your email address.")
                    emailEditText.requestFocus()
                    return
                }
                bioEditText.text.isEmpty() -> {
                    Util.displayLongMessage(requireContext(), "Say something about yourself.")
                    bioEditText.requestFocus()
                    return
                }
                passwordEditText.text.isEmpty() -> {
                    Util.displayLongMessage(requireContext(), "Password required.")
                    passwordEditText.requestFocus()
                    return
                }
                passwordEditText.text.toString().length < 6 -> {
                    Util.displayLongMessage(
                        requireContext(),
                        "Your password should be at least 6 characters."
                    )
                    passwordEditText.requestFocus()
                    return
                }
                passwordEditText.text.toString() != confirmPasswordEditText.text.toString() -> {
                    Util.displayLongMessage(requireContext(), "The two passwords does not match.")
                    confirmPasswordEditText.requestFocus()
                    return
                }
                else -> {
                    val student = Student(
                        Constants.CANDIDATE,
                        fullnameEditText.text.toString(),
                        regnoEditText.text.toString(),
                        emailEditText.text.toString(),
                        genderSpinner.selectedItem.toString(),
                        programSpinner.selectedItem.toString(),
                        levelSpinner.selectedItem.toString(),
                        profileUrl,
                        positionSpinner.selectedItem.toString(),
                        bioEditText.text.toString()
                    )
                    createAccount(student, passwordEditText.text.toString())
                }
            }
        }
    }

    private fun createAccount(student: Student, password: String) {
        toggleProgressLayout(true)
        auth.createUserWithEmailAndPassword(student.email!!, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = student.fullName
                        photoUri = Uri.parse(student.photo)
                    }
                    user!!.updateProfile(profileUpdates)
                    db.collection(Constants.USERS)
                        .document(student.email!!)
                        .set(student)
                        .addOnSuccessListener {
                            Util.displayShortMessage(
                                requireContext(),
                                "Details saved"
                            )
                        }
                        .addOnFailureListener { e ->
                            Util.displayLongMessage(
                                requireContext(),
                                "Error saving details: \n${e.message}"
                            )
                        }
                    Util.displayLongMessage(requireContext(), "Registration successful.")
                    findNavController().navigate(R.id.action_candidateSignupFragment_to_loginFragment)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Util.displayLongMessage(
                        requireContext(),
                        "Registration was not successful.\n${task.exception?.message}"
                    )
                    toggleProgressLayout(false)
                }
            }
    }

    private fun toggleProgressLayout(isShown: Boolean) {
        if (isShown) {
            binding!!.progressLayout.visibility = View.VISIBLE
        } else {
            binding!!.progressLayout.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}