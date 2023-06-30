package com.nguyen.coroutines4.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.nguyen.coroutines4.R
import com.nguyen.coroutines4.databinding.ActivityMainBinding

/**
 * Show layout.activity_main and setup data binding.
 */
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    /**
     * Inflate layout.activity_main and setup data binding.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get MainViewModel by passing a database to the factory
        val database = getDatabase(this)
        val repository = TitleRepository(getNetworkService(), database.titleDao)
        val viewModel = ViewModelProvider(this, MainViewModel.FACTORY(repository))
                .get(MainViewModel::class.java)

        // When rootLayout is clicked call onMainViewClicked in ViewModel
        binding.rootLayout.setOnClickListener {
            viewModel.onMainViewClicked()
        }

        // update the title when the [MainViewModel.title] changes
        viewModel.title.observe(this) { value ->
            value?.let {
                binding.title.text = it
            }
        }

        viewModel.taps.observe(this) { value ->
            binding.taps.text = value
        }

        // show the spinner when [MainViewModel.spinner] is true
        viewModel.spinner.observe(this) { value ->
            value.let { show ->
                binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
            }
        }

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        viewModel.snackbar.observe(this) { text ->
            text?.let {
                Snackbar.make(binding.rootLayout, text, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        }
    }
}
