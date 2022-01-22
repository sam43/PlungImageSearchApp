package com.plung.imagesearchapp.ui.gallery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.paging.*
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.plung.imagesearchapp.api.UnsplashApi
import com.plung.imagesearchapp.api.UnsplashResponse
import com.plung.imagesearchapp.data.UnsplashPhoto
import com.plung.imagesearchapp.data.UnsplashRepository
import com.plung.imagesearchapp.paging.UnsplashPagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import java.security.InvalidParameterException


@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GalleryViewModelTest {

    lateinit var pagingSource: UnsplashPagingSource

    @Mock
    lateinit var repository: UnsplashRepository

    @Mock
    lateinit var unsplashApi: UnsplashApi

    @Mock
    lateinit var gson: Gson

    @Mock
    lateinit var viewModel: GalleryViewModel

    @Mock
    lateinit var state: SavedStateHandle

    @Mock
    private lateinit var observer: Observer<PagingData<UnsplashPhoto>>

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
        repository = UnsplashRepository(unsplashApi = unsplashApi)
        viewModel = GalleryViewModel(repository, state)
        viewModel.photos?.observeForever(observer)
    }

    // ViewModel testing
    @Test
    fun `view model test with invalid params - returns NULL`() {
        testCoroutineScope.launch(testDispatcher)  {
            whenever(unsplashApi.searchPhotos(query, -1, 10).body()?.results).thenReturn(null)
            assertNotNull(viewModel.photos?.value)
        }
    }

    @Test
    fun `view model get photos from api - success`() {
        testCoroutineScope.launch(testDispatcher)  {
            // Mock API response
            whenever (unsplashApi.searchPhotos(query, 1, 20).body()?.results).thenReturn(
                unsplashResponse.results.map {
                    UnsplashPhoto(it.id)
                }
            )
            viewModel.photos
            verify(observer).onChanged(
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        maxSize = 100,
                        enablePlaceholders = true
                    ),
                    pagingSourceFactory = { pagingSource }
                ).liveData.value
            )
        }
    }

        // API testing
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
    fun `response error with wrong params - failure - not_valid`() {
        testCoroutineScope.launch(testDispatcher) {
            given(unsplashApi.searchPhotos("", -1, 0)).willThrow(InvalidParameterException())
            val expectedResult = PagingSource.LoadResult.Error<Int, UnsplashPhoto>(InvalidParameterException())
            assertEquals(
                expectedResult, pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = -1,
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
            given(unsplashApi.searchPhotos(query, 1, 20)).willReturn(Response.success(unsplashResponse))
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
            given(unsplashApi.searchPhotos(query, 2, 40)).willReturn(Response.success(nextUnsplashResponse))
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
            given(unsplashApi.searchPhotos(query, 1, 20)).willReturn(Response.success(unsplashResponse))
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

    @After
    @Throws(Exception::class)
    fun tearDown() {
        // nothing to-do here we took all lateinit variable
    }

    companion object {
        private const val query = "dogs"
        private val unsplashResponse = UnsplashResponse(
            results = listOf(
                UnsplashPhoto(id = "1"), UnsplashPhoto(id = "2")
            ),
        )
        private val nextUnsplashResponse = UnsplashResponse(
            results = listOf(
                UnsplashPhoto(id = "3"), UnsplashPhoto(id = "4")
            ),
        )
    }
}