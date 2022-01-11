package com.entin.lighttasks.presentation.ui.auth.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.FragmentAuthBinding
import com.entin.lighttasks.presentation.base.fragment.InternetDependable
import com.entin.lighttasks.presentation.base.fragment.initialInternetConnectionChecking
import com.entin.lighttasks.presentation.ui.auth.viewmodel.AuthViewModel
import com.entin.lighttasks.presentation.util.getSnackBar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Fragment for auth by Google
 */

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth), InternetDependable {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    private val liveConnection = MutableLiveData<Boolean>()

    /**
     * On Activity Result
     */
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Timber.log(0, e)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)

        observeInternetConnectionChecking()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        currentUser = auth.currentUser
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            /**
             * Can show error Unresolved reference: default_web_client_id
             * But works ok.
             */
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.signInButton.setOnClickListener {
            signIn()
            it.isVisible = false
        }
    }

    /**
     * Internet connection checking and observing
     */

    private fun observeInternetConnectionChecking() {
        initialInternetConnectionChecking(
            binding = binding.root,
            liveConnection = liveConnection
        )
        liveConnection.observe(viewLifecycleOwner) {
            connectionReaction(it)
        }
    }

    /**
     * Google auth
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                currentUser = auth.currentUser
                // Log
                viewModel.logSuccessAuth()
                // Navigate to Remote Tasks
                navigateToRemoteTasks()
            } else {
                // Log
                viewModel.logFailureAuth(task.exception)
                binding.signInButton.isVisible = true
                getSnackBar(getString(R.string.auth_error), requireView()).show()
            }
        }
    }

    /**
     * Button auth clicked
     */
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    /**
     * Navigation after auth
     */
    private fun navigateToRemoteTasks() {
        findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToRemoteFragment())
    }

    /**
     * No Internet reaction
     */
    @MainThread
    override fun connectionReaction(value: Boolean) {
        if (value) {
            binding.signInButton.visibility = View.VISIBLE
        } else {
            binding.signInButton.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}