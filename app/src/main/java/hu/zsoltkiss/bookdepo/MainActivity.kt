package hu.zsoltkiss.bookdepo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import hu.zsoltkiss.bookdepo.data.BookDepoDatabase
import hu.zsoltkiss.bookdepo.data.repository.BookListRepository
import hu.zsoltkiss.bookdepo.data.repository.BookListRepositoryImpl
import hu.zsoltkiss.bookdepo.ui.screens.booklist.BookListScreen
import hu.zsoltkiss.bookdepo.ui.screens.booklist.BookListViewModelImpl
import hu.zsoltkiss.bookdepo.ui.theme.BarColor
import hu.zsoltkiss.bookdepo.ui.theme.BookDepoTheme
import hu.zsoltkiss.bookdepo.ui.theme.ListBg
import hu.zsoltkiss.bookdepo.ui.theme.StartButtonBg
import hu.zsoltkiss.bookdepo.util.IDProvider
import hu.zsoltkiss.bookdepo.util.IDProviderImpl

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {

    private val db = BookDepoDatabase.getDatabase(this)
    private val idProvider: IDProvider = IDProviderImpl()

    // ido hianyaban nem hasznaltam DI-t, de termeszetesen ezeket Koin-tol,
    // Hilt-tol, Dagger-tol kellene megszerezni
    private val bookRepo: BookListRepository = BookListRepositoryImpl(
        currentUserId = idProvider.currentUserId,
        bookDao = db.bookDao()
    )
    private val viewModel: BookListViewModelImpl = BookListViewModelImpl(
        repo = bookRepo,
        dao = db.bookDao()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val startButtonEnabled by viewModel.startButtonEnabled.collectAsStateWithLifecycle()

            BookDepoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        BookDepoTopBar()
                    },
                    bottomBar = {
                        BookDepoBottomBar(
                            enabled = startButtonEnabled,
                            onStartAction = viewModel::onClickStart
                        )
                    }

                ) { innerPadding ->
                    Box(modifier = Modifier.background(ListBg).padding(innerPadding).fillMaxSize()) {
                        BookListScreen(
                            modifier = Modifier.background(ListBg).align(Alignment.Center),
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }


}

@Composable
fun BookDepoTopBar() {
    Row(
        modifier = Modifier
            .background(BarColor)
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Books", style = MaterialTheme.typography.titleLarge, color = Color.White)
    }

}

@Composable
fun BookDepoBottomBar(
    enabled: Boolean,
    onStartAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(BarColor)
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onStartAction,
            modifier = Modifier.width(120.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            enabled = enabled
        ) {
            Text("Start", style = MaterialTheme.typography.titleMedium, color = StartButtonBg)
        }
    }


}