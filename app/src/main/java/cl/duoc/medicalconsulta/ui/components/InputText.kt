package cl.duoc.medicalconsulta.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun InputText(
    valor: String,
    error: String?,
    label: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = {
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}