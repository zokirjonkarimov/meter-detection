package uz.isds.meterai.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import uz.isds.meterai.R
import uz.isds.meterai.backup.CameraFragment
import uz.isds.meterai.ui.intent.CameraIntent
import uz.isds.meterai.ui.presenter.CommonPresenter
import uz.isds.meterai.ui.uistate.CameraUiState


@Composable
fun CameraScreen(presenter: CommonPresenter<CameraIntent, CameraUiState>) {
    CameraContent(presenter::onEventDispatcher)
}


@Composable
private fun CameraContent(intent: (CameraIntent) -> Unit) {
    val context = LocalContext.current
    val fragmentManager = (context as AppCompatActivity).supportFragmentManager

    val fragment = remember { CameraFragment() }
    AndroidView(factory = {
        val fragmentContainer = FragmentContainerView(it).apply {
            id = R.id.container
        }
        fragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
        fragment.onCuptureClick { intent(CameraIntent.OnCupture(it)) }
        fragmentContainer
    }, modifier = Modifier.fillMaxSize())
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun CameraContentPreview() {
    CameraContent {}
}