package com.plung.imagesearchapp.ui.gallery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagingSource
import com.nhaarman.mockitokotlin2.any
import com.plung.imagesearchapp.api.UnsplashApi
import com.plung.imagesearchapp.api.UnsplashResponse
import com.plung.imagesearchapp.data.UnsplashPagingSource
import com.plung.imagesearchapp.data.UnsplashPhoto
import com.plung.imagesearchapp.data.UnsplashRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GalleryViewModelTest {

    private val query = "dogs"

    lateinit var pagingSource: UnsplashPagingSource

    @Mock
    lateinit var unsplashApi: UnsplashApi

    /**
     * InstantTaskExecutorRule
        - It will tell JUnit to force tests to be executed synchronously,
        especially when using Architecture Components.
     * */

    @Rule
    @JvmField
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private var testDispatcher = TestCoroutineDispatcher()
    private var testCoroutineScope = TestCoroutineScope()


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        pagingSource = UnsplashPagingSource(unsplashApi, query)
    }

    @Test
    fun `http error or content not found - failure - 4xx`() {
        testCoroutineScope.launch(testDispatcher) {
            val error = RuntimeException("404", Throwable())
            given(unsplashApi.searchPhotos(any(), any(), any())).willThrow(error)
            val expectedResult = PagingSource.LoadResult.Error<Int, UnsplashPhoto>(error)
            assertEquals(
                expectedResult, pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = 0,
                        loadSize = 1,
                        placeholdersEnabled = false
                    )
                )
            )
        }
    }

    @Test
    fun `http error on authentication - failure - 4xx`() {
        testCoroutineScope.launch(testDispatcher) {
            val error = RuntimeException("401", Throwable())
            given(unsplashApi.searchPhotos(any(), any(), any())).willThrow(error)
            val expectedResult = PagingSource.LoadResult.Error<Int, UnsplashPhoto>(error)
            assertEquals(
                expectedResult, pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = 0,
                        loadSize = 1,
                        placeholdersEnabled = false
                    )
                )
            )
        }
    }

    @Test
    fun `http internal server error - failure - 5xx`() {
        testCoroutineScope.launch(testDispatcher) {
            val error = RuntimeException("500", Throwable())
            given(unsplashApi.searchPhotos(any(), any(), any())).willThrow(error)
            val expectedResult = PagingSource.LoadResult.Error<Int, UnsplashPhoto>(error)
            assertEquals(
                expectedResult, pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = 0,
                        loadSize = 1,
                        placeholdersEnabled = false
                    )
                )
            )
        }
    }

    @Test
    fun `photos list can not be loaded - failure - null`() {
        testCoroutineScope.launch(testDispatcher) {
            given(unsplashApi.searchPhotos(any(), any(), any())).willReturn(null)
            val expectedResult = PagingSource.LoadResult.Error<Int, UnsplashPhoto>(NullPointerException())
            assertEquals(
                expectedResult.toString(), pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = 0,
                        loadSize = 1,
                        placeholdersEnabled = false
                    )
                ).toString()
            )
        }
    }

    @Test
    fun `photos have loaded by page source - success - initial page`() {
        testCoroutineScope.launch(testDispatcher) {
            val loadResult = PagingSource.LoadResult.Page(
                data = unsplashResponse.results.map {
                    UnsplashPhoto(it.id)
                },
                prevKey = null,
                nextKey = 1
            )
            assertEquals(
                loadResult, pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = 0,
                        loadSize = 1,
                        placeholdersEnabled = false
                    )
                )
            )

        }
    }

    @Test
    fun `photos paging source append - success - next page`() {
        testCoroutineScope.launch(testDispatcher) {
            given(unsplashApi.searchPhotos(any(), any(), any())).willReturn(nextUnsplashResponse)
            val expectedResult = PagingSource.LoadResult.Page(
                data = unsplashResponse.results.map { UnsplashPhoto(it.id) },
                prevKey = 1,
                nextKey = 2
            )
            assertEquals(
                expectedResult, pagingSource.load(
                    PagingSource.LoadParams.Append(
                        key = 1,
                        loadSize = 1,
                        placeholdersEnabled = false
                    )
                )
            )
        }
    }

    @Test
    fun `photos paging source prepend - success - previous page`() {
        testCoroutineScope.launch(testDispatcher)  {
            given(unsplashApi.searchPhotos(any(), any(), any())).willReturn(unsplashResponse)
            val expectedResult = PagingSource.LoadResult.Page(
                data = unsplashResponse.results.map { UnsplashPhoto(it.id) },
                prevKey = null,
                nextKey = 1
            )
            assertEquals(
                expectedResult, pagingSource.load(
                    PagingSource.LoadParams.Prepend(
                        key = 0,
                        loadSize = 1,
                        placeholdersEnabled = false
                    )
                )
            )
        }
    }

    companion object {
        val unsplashResponse = UnsplashResponse(
            results = listOf(
                UnsplashPhoto(id = "1"), UnsplashPhoto(id = "2")
            ),
        )
        val nextUnsplashResponse = UnsplashResponse(
            results = listOf(
                UnsplashPhoto(id = "3"), UnsplashPhoto(id = "4")
            ),
        )
    }
}