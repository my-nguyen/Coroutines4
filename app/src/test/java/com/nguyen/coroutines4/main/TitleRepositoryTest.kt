package com.nguyen.coroutines4.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.nguyen.coroutines4.fakes.MainNetworkCompletableFake
import com.nguyen.coroutines4.fakes.MainNetworkFake
import com.nguyen.coroutines4.fakes.TitleDaoFake
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

class TitleRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // The library kotlinx-coroutines-test has the runBlockingTest function that blocks while it
    // calls suspend functions. When runBlockingTest calls a suspend function or launches a new
    // coroutine, it executes it immediately by default.
    @Test
    fun whenRefreshTitleSuccess_insertsRows() = runBlockingTest {
        val dao = TitleDaoFake("title")
        val repository = TitleRepository(MainNetworkFake("OK"), dao)

        repository.refreshTitle()
        Truth.assertThat(dao.nextInsertedOrNull()).isEqualTo("OK")

        /*
        // launch a coroutine. however, launch is a non-blocking call, that means it returns right
        // away, whereas coroutines started with launch are asynchronous code, which may complete at
        // some point in the future. Therefore to test that asynchronous code, you need some way to
        // tell the test to wait until your coroutine completes.
        GlobalScope.launch {
            repository.refreshTitle()
        }
        // test function returns immediately, and doesn't see the results of refreshTitle
        */
    }

    @Test(expected = TitleRefreshError::class)
    fun whenRefreshTitleTimeout_throws() = runBlockingTest {
        // network fake that's designed to suspend callers until the test continues them.
        val network = MainNetworkCompletableFake()
        val repository = TitleRepository(network, TitleDaoFake("title"))

        // launch a separate coroutine to call refreshTitle. the timeout should happen in a
        // different coroutine than the one runBlockingTest creates. Since this will be an
        // unfinished coroutine, at the end of the test, it will fail the test.
        // solution: add a five second timeout to the network fetch in TitleRepository.refreshTitle()
        launch {
            repository.refreshTitle()
        }

        advanceTimeBy(5_000)
    }
}