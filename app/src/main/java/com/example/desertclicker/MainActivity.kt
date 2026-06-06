package com.example.desertclicker

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.desertclicker.data.Datasource
import com.example.desertclicker.model.Desert
import com.example.desertclicker.ui.theme.DesertClickerTheme


private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate Called")
        enableEdgeToEdge()
        setContent {
            DesertClickerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().statusBarsPadding()
                ) {
                    DesertClickerApp(deserts = Datasource.desertList)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart Called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart Called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")
    }
}


fun determineDesertToShow(
    deserts: List<Desert>,
    desertsSold: Int
): Desert {
    var desertToShow = deserts.first()
    for(desert in deserts) {
        if(desertsSold >= desert.startProductionAmount){
            desertToShow = desert
        }else{
            break
        }
    }
    return desertToShow
}

private fun shareSoldDesertInformation(intentContext: Context, desertsSold: Int, revenue: Int) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            intentContext.getString(R.string.share_text, desertsSold, revenue)
        )
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)

    try {
        ContextCompat.startActivity(intentContext, shareIntent, null)
    }catch (e: ActivityNotFoundException){
        Toast.makeText(
            intentContext,
            intentContext.getString(R.string.sharing_not_available),
            Toast.LENGTH_LONG
        ).show()
    }
}

@Composable
fun DesertClickerApp(deserts:  List<Desert>) {
    var revenue by rememberSaveable { mutableStateOf(0) }
    var desertsSold by rememberSaveable { mutableStateOf(0) }

    val currentDesertIndex by remember { mutableStateOf(0) }

    var currentDesertPrice by remember { mutableStateOf(deserts[currentDesertIndex].price) }
    var currentDesertImageId by rememberSaveable { mutableStateOf(deserts[currentDesertIndex].imageId) }

    Scaffold(
        topBar = {
            val intentContext = LocalContext.current
            val layoutDirection = LocalLayoutDirection.current
            DesertClickerAppBar(
                onShareButtonClicked = {
                    shareSoldDesertInformation(
                        intentContext = intentContext,
                        desertsSold = desertsSold,
                        revenue = revenue
                    )
                }
            )
        }
    ) {contentPadding ->
        DesertClickerScreen(
            revenue = revenue,
            desertsSold = desertsSold,
            desertImageId = currentDesertImageId,
            onDesertClicked = {

                //Update the revenue
                revenue += currentDesertPrice
                desertsSold++

                //show the next desert
                val desertToShow = determineDesertToShow(deserts, desertsSold)
                currentDesertImageId = desertToShow.imageId
                currentDesertPrice = desertToShow.price
            },
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
fun DesertClickerAppBar(
    onShareButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_medium)),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(
            onClick = onShareButtonClicked,
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_medium))
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = stringResource(R.string.share),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun DesertClickerScreen(
    revenue: Int,
    desertsSold: Int,
    @DrawableRes desertImageId: Int,
    onDesertClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.bakery_back),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(desertImageId),
                    contentDescription = null,
                    modifier = Modifier.width(dimensionResource(R.dimen.image_size))
                        .height(dimensionResource(R.dimen.image_size))
                        .align(Alignment.Center)
                        .clickable{ onDesertClicked() },
                    contentScale = ContentScale.Crop
                )
            }
            TransactionInfo(
                revenue = revenue,
                desertsSold = desertsSold,
                modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    }
}

@Composable
fun TransactionInfo(
    revenue: Int,
    desertsSold: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        DesertSoldInfo(
            desertsSold = desertsSold,
            modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.padding_medium))
        )
        RevenueInfo(
            revenue = revenue,
            modifier = Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.padding_medium))
        )
    }
}

@Composable
private fun RevenueInfo(revenue: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.total_revenue),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = "$${revenue}",
            textAlign = TextAlign.Right,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun DesertSoldInfo(desertsSold: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.desert_sold),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = desertsSold.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

//@Composable
//fun DesertClickerApp() {
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        ImageSection(modifier = Modifier.weight(1f))
//        AmountSoldSection()
//    }
//}
//
//@Composable
//fun ImageSection(modifier: Modifier = Modifier) {
//    Image(
//        painter = painterResource(R.drawable.day_24),
//        contentDescription = stringResource(R.string.main_image),
//        modifier = modifier.fillMaxWidth(),
//        contentScale = ContentScale.Crop
//    )
//}
//
//@Composable
//fun AmountSoldSection(modifier: Modifier = Modifier) {
//    Column() {
//        Row(
//            modifier = Modifier.fillMaxWidth().padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = stringResource(R.string.deserts_sold),
//                style = MaterialTheme.typography.bodySmall
//            )
//            Text(
//                text = "12",
//                style = MaterialTheme.typography.bodySmall
//            )
//        }
//        Row(
//            modifier = Modifier.fillMaxWidth().padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = stringResource(R.string.total_revenue),
//                style = MaterialTheme.typography.bodyMedium
//            )
//            Text(
//                text = "12",
//                style = MaterialTheme.typography.bodyMedium
//            )
//        }
//    }
//}
//
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AppPreview() {
    DesertClickerTheme {
        DesertClickerApp(deserts = Datasource.desertList)
    }
}