package com.example.roomdemo

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomdemo.ui.theme.RoomDemoTheme

/**
 * Главная Activity приложения RoomDemo.
 * Точка входа в приложение, инициализирующая Compose UI и связывающая его с MainViewModel.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Получаем текущий ViewModelStoreOwner
                    val owner = LocalViewModelStoreOwner.current
                    owner?.let {
                        // Инициализируем MainViewModel с помощью пользовательской фабрики MainViewModelFactory
                        val viewModel: MainViewModel = viewModel(
                            it,
                            "MainViewModel",
                            MainViewModelFactory(
                                LocalContext.current.applicationContext as Application
                            )
                        )
                        // Передаем модель представления в экранную структуру
                        ScreenSetup(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

/**
 * Вспомогательный Composable-компонент верхнего уровня.
 * Подписывается на LiveData из ViewModel и преобразует их в состояния Compose.
 */
@Composable
fun ScreenSetup(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    // Наблюдение за полным списком товаров и результатами поиска через observeAsState
    val allProducts by viewModel.allProducts.observeAsState(listOf())
    val searchResults by viewModel.searchResults.observeAsState(listOf())

    // Передача состояний в главный экран интерфейса
    MainScreen(
        modifier = modifier,
        allProducts = allProducts,
        searchResults = searchResults,
        viewModel = viewModel
    )
}

/**
 * Главный экран пользовательского интерфейса Compose.
 * Содержит текстовые поля ввода, кнопки управления и список отображения LazyColumn.
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    allProducts: List<Product>,
    searchResults: List<Product>,
    viewModel: MainViewModel
) {
    // Состояния полей ввода имени товара и количества
    var productName by remember { mutableStateOf("") }
    var productQuantity by remember { mutableStateOf("") }

    // Флаг режима поиска (true - показывать результаты поиска, false - полный список)
    var searching by remember { mutableStateOf(false) }

    // Обработчики изменения текста в текстовых полях
    val onProductTextChange = { text: String ->
        productName = text
    }

    val onQuantityTextChange = { text: String ->
        productQuantity = text
    }

    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Текстовое поле для ввода названия товара
        CustomTextField(
            title = "Product Name",
            textState = productName,
            onTextChange = onProductTextChange,
            keyboardType = KeyboardType.Text
        )

        // Текстовое поле для ввода количества товара
        CustomTextField(
            title = "Quantity",
            textState = productQuantity,
            onTextChange = onQuantityTextChange,
            keyboardType = KeyboardType.Number
        )

        // Ряд с кнопками действий: Add, Search, Delete, Clear
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Кнопка добавления нового товара
            Button(onClick = {
                if (productQuantity.isNotEmpty()) {
                    viewModel.insertProduct(
                        Product(
                            productName,
                            productQuantity.toInt()
                        )
                    )
                    searching = false
                }
            }) {
                Text("Add")
            }

            // Кнопка поиска товара по названию
            Button(onClick = {
                searching = true
                viewModel.findProduct(productName)
            }) {
                Text("Search")
            }

            // Кнопка удаления товара из БД
            Button(onClick = {
                searching = false
                viewModel.deleteProduct(productName)
            }) {
                Text("Delete")
            }

            // Кнопка очистки полей ввода и сброса режима поиска
            Button(onClick = {
                searching = false
                productName = ""
                productQuantity = ""
            }) {
                Text("Clear")
            }
        }

        // Прокручиваемый список для отображения товаров
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Выбор списка данных в зависимости от флага поиска
            val list = if (searching) searchResults else allProducts

            // Заголовок таблицы
            item {
                TitleRow(head1 = "ID", head2 = "Product", head3 = "Quantity")
            }

            // Элементы списка товаров
            items(list) { product ->
                ProductRow(
                    id = product.id,
                    name = product.productName,
                    quantity = product.quantity
                )
            }
        }
    }
}

/**
 * Компонент строки заголовков таблицы товаров (ID, Product, Quantity).
 */
@Composable
fun TitleRow(head1: String, head2: String, head3: String) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            head1, color = Color.White,
            modifier = Modifier.weight(0.1f)
        )
        Text(
            head2, color = Color.White,
            modifier = Modifier.weight(0.2f)
        )
        Text(
            head3, color = Color.White,
            modifier = Modifier.weight(0.2f)
        )
    }
}

/**
 * Компонент отдельной строки таблицы товаров для отображения данных о продукте.
 */
@Composable
fun ProductRow(id: Int, name: String, quantity: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            id.toString(), modifier = Modifier.weight(0.1f)
        )
        Text(name, modifier = Modifier.weight(0.2f))
        Text(quantity.toString(), modifier = Modifier.weight(0.2f))
    }
}

/**
 * Пользовательское текстовое поле ввода OutlinedTextField со стилизацией шрифта.
 */
@Composable
fun CustomTextField(
    title: String,
    textState: String,
    onTextChange: (String) -> Unit,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = textState,
        onValueChange = { onTextChange(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        singleLine = true,
        label = { Text(title) },
        modifier = Modifier.padding(10.dp),
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
    )
}

/**
 * Пользовательская фабрика ViewModelProvider.Factory для создания экземпляров MainViewModel
 * с передачей контекста приложения Application в качестве параметра конструктора.
 */
class MainViewModelFactory(val application: Application) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}
