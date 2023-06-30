package com.nguyen.coroutines4.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.nguyen.coroutines4.fakes.MainNetworkFake
import com.nguyen.coroutines4.fakes.TitleDaoFake
import com.nguyen.coroutines4.main.utils.MainCoroutineScopeRule
import com.nguyen.coroutines4.main.utils.getValueForTest
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    // custom rule that configures Dispatchers.Main to use a TestCoroutineDispatcher from
    // kotlinx-coroutines-test. This allows tests to advance a virtual-clock for testing, and allows
    // code to use Dispatchers.Main in unit tests
    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()
    // JUnit rule that configures LiveData to execute each task synchronously
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(
                TitleRepository(MainNetworkFake("OK"), TitleDaoFake("initial")
                )
        )
    }

    @Test
    fun whenMainClicked_updatesTaps() {
        viewModel.onMainViewClicked()
        Truth.assertThat(viewModel.taps.getValueForTest()).isEqualTo("0 taps")
        coroutineScope.advanceTimeBy(1000)
        Truth.assertThat(viewModel.taps.getValueForTest()).isEqualTo("1 taps")
    }
}