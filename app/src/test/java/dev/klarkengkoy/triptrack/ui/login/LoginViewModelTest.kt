package dev.klarkengkoy.triptrack.ui.login

import android.util.Log
import app.cash.turbine.test
import com.firebase.ui.auth.AuthUI
import dev.klarkengkoy.triptrack.data.UserDataStore
import dev.klarkengkoy.triptrack.data.repository.AuthRepository
import dev.klarkengkoy.triptrack.data.repository.TripsRepository
import dev.klarkengkoy.triptrack.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userDataStore: UserDataStore = mockk()
    private val tripsRepository: TripsRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private val signInProviderFactory: SignInProviderFactory = mockk()

    @Before
    fun setup() {
        // Mock static Log class
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
    }

    @Test
    fun `onSignInRequested emits Launch event`() = runTest {
        // Given
        coEvery { authRepository.isUserSignedIn } returns flowOf(false)
        val mockConfig = mockk<AuthUI.IdpConfig>()
        every { signInProviderFactory.create(any()) } returns mockConfig
        
        val viewModel = LoginViewModel(userDataStore, tripsRepository, authRepository, signInProviderFactory)

        // When / Then
        // For SharedFlow, we must start collecting BEFORE the event is emitted.
        viewModel.signInEvent.test {
            viewModel.onSignInRequested(SignInType.GOOGLE)
            
            val item = awaitItem()
            assertTrue(item is SignInEvent.Launch)
        }
    }

    @Test
    fun `onSignInRequested sets isLoading to true`() = runTest {
        // Given
        coEvery { authRepository.isUserSignedIn } returns flowOf(false)
        val mockConfig = mockk<AuthUI.IdpConfig>()
        every { signInProviderFactory.create(any()) } returns mockConfig
        
        val viewModel = LoginViewModel(userDataStore, tripsRepository, authRepository, signInProviderFactory)

        // When
        viewModel.onSignInRequested(SignInType.GOOGLE)

        // Then
        viewModel.isLoading.test {
            // StateFlow will re-emit the current value immediately.
            // If it's already true, we get true. If it's false then true, we might get false then true.
            val item = awaitItem()
            if (!item) {
                val loading = awaitItem()
                assertTrue(loading)
            } else {
                assertTrue(true)
            }
        }
    }

    @Test
    fun `initial state reflects auth status`() = runTest {
        // Given
        coEvery { authRepository.isUserSignedIn } returns flowOf(true)
        coEvery { tripsRepository.syncTrips() } returns Unit
        
        val viewModel = LoginViewModel(userDataStore, tripsRepository, authRepository, signInProviderFactory)

        // Then
        viewModel.isUserSignedIn.test {
            val isSignedIn = awaitItem()
            assertTrue(isSignedIn)
        }
    }
}
