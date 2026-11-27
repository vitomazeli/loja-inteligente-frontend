package br.com.lojainteligente.frontend.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.round

enum class KoraStatusTone { Success, Warning, Danger, Neutral, Primary }
enum class KoraMetricSymbol { Products, Categories, Users, Customers, Sales }
enum class KoraActionIcon { Edit, Delete }

fun String?.koraNonTechnicalOr(fallback: String): String {
    val valor = this?.trim().orEmpty()
    return valor.takeIf {
        it.isNotEmpty() && !it.equals("string", true) && !it.equals("null", true) && !it.equals("undefined", true)
    } ?: fallback
}

fun String.koraIsSuccessFeedback(): Boolean =
    contains("sucesso", ignoreCase = true) ||
        contains("cadastrado", ignoreCase = true) ||
        contains("cadastrada", ignoreCase = true) ||
        contains("atualizado", ignoreCase = true) ||
        contains("atualizada", ignoreCase = true) ||
        contains("excluído", ignoreCase = true) ||
        contains("excluída", ignoreCase = true) ||
        contains("ativado", ignoreCase = true) ||
        contains("desativado", ignoreCase = true)

@Composable
fun KoraPageHeader(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        trailing()
    }
}

@Composable
fun KoraPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled && !loading) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(17.dp), strokeWidth = 2.dp)
            Spacer(Modifier.size(8.dp))
        }
        Text(if (loading) "Aguarde..." else text)
    }
}

@Composable
fun KoraSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    OutlinedButton(onClick = onClick, modifier = modifier, enabled = enabled && !loading) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(17.dp), strokeWidth = 2.dp)
            Spacer(Modifier.size(8.dp))
        }
        Text(text)
    }
}

@Composable
fun KoraFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        supportingText = supportingText?.let { message -> ({ Text(message) }) }
    )
}

@Composable
fun KoraSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Canvas(Modifier.size(18.dp)) {
                val lado = size.minDimension
                drawCircle(iconColor, lado * 0.28f, Offset(lado * 0.40f, lado * 0.40f), style = Stroke(lado * 0.09f))
                drawLine(iconColor, Offset(lado * 0.60f, lado * 0.60f), Offset(lado * 0.90f, lado * 0.90f), lado * 0.09f, cap = StrokeCap.Round)
            }
        }
    )
}

@Composable
fun KoraStatusBadge(
    label: String,
    tone: KoraStatusTone,
    modifier: Modifier = Modifier
) {
    val fundo: Color
    val texto: Color
    when (tone) {
        KoraStatusTone.Success -> { fundo = Color(0xFFDCFCE7); texto = Color(0xFF166534) }
        KoraStatusTone.Warning -> { fundo = Color(0xFFFEF3C7); texto = Color(0xFF92400E) }
        KoraStatusTone.Danger -> { fundo = MaterialTheme.colorScheme.errorContainer; texto = MaterialTheme.colorScheme.onErrorContainer }
        KoraStatusTone.Primary -> { fundo = MaterialTheme.colorScheme.primaryContainer; texto = MaterialTheme.colorScheme.onPrimaryContainer }
        KoraStatusTone.Neutral -> { fundo = MaterialTheme.colorScheme.surfaceVariant; texto = MaterialTheme.colorScheme.onSurfaceVariant }
    }
    Surface(modifier = modifier, color = fundo, shape = RoundedCornerShape(50)) {
        Text(label, Modifier.padding(horizontal = 11.dp, vertical = 5.dp), style = MaterialTheme.typography.labelMedium, color = texto, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun KoraDataCard(
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.ui.unit.Dp = 18.dp,
    contentSpacing: androidx.compose.ui.unit.Dp = 10.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
        shadowElevation = 1.dp
    ) {
        Column(Modifier.fillMaxWidth().padding(contentPadding), verticalArrangement = Arrangement.spacedBy(contentSpacing), content = content)
    }
}

@Composable
fun KoraIconAction(
    action: KoraActionIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val cor = if (action == KoraActionIcon.Delete) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val descricao = if (action == KoraActionIcon.Delete) "Excluir" else "Editar"
    IconButton(onClick = onClick, modifier = modifier.semantics { contentDescription = descricao }, enabled = enabled) {
        Canvas(Modifier.size(19.dp)) {
            val lado = size.minDimension
            val pincel = Stroke(lado * 0.085f, cap = StrokeCap.Round)
            fun p(x: Float, y: Float) = Offset(lado * x, lado * y)
            if (action == KoraActionIcon.Edit) {
                drawLine(cor, p(0.22f, 0.76f), p(0.72f, 0.26f), lado * 0.14f, cap = StrokeCap.Round)
                drawLine(cor, p(0.15f, 0.85f), p(0.38f, 0.80f), lado * 0.08f, cap = StrokeCap.Round)
            } else {
                drawLine(cor, p(0.24f, 0.27f), p(0.76f, 0.27f), lado * 0.09f, cap = StrokeCap.Round)
                drawLine(cor, p(0.39f, 0.16f), p(0.61f, 0.16f), lado * 0.09f, cap = StrokeCap.Round)
                drawLine(cor, p(0.31f, 0.34f), p(0.36f, 0.82f), lado * 0.09f, cap = StrokeCap.Round)
                drawLine(cor, p(0.69f, 0.34f), p(0.64f, 0.82f), lado * 0.09f, cap = StrokeCap.Round)
                drawLine(cor, p(0.36f, 0.82f), p(0.64f, 0.82f), lado * 0.09f, cap = StrokeCap.Round)
                drawLine(cor, p(0.45f, 0.40f), p(0.46f, 0.70f), lado * 0.06f, cap = StrokeCap.Round)
                drawLine(cor, p(0.55f, 0.40f), p(0.54f, 0.70f), lado * 0.06f, cap = StrokeCap.Round)
            }
        }
    }
}

@Composable
fun KoraInitialsAvatar(name: String, modifier: Modifier = Modifier) {
    val iniciais = name.trim().split(Regex("\\s+")).filter(String::isNotBlank).take(2).mapNotNull { it.firstOrNull() }.joinToString("").uppercase().ifBlank { "?" }
    Surface(modifier = modifier.size(44.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
        Box(contentAlignment = Alignment.Center) {
            Text(iniciais, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun KoraLoadingState(message: String = "Carregando...") {
    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 3.dp)
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun KoraConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String = "Excluir",
    dismissLabel: String = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmLabel, color = MaterialTheme.colorScheme.error) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(dismissLabel) } }
    )
}

@Composable
fun KoraMetricCard(
    title: String,
    value: String,
    caption: String,
    symbol: KoraMetricSymbol,
    modifier: Modifier = Modifier
) {
    KoraDataCard(modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelLarge)
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                KoraMetricGlyph(symbol, Modifier.padding(9.dp).size(20.dp))
            }
        }
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 30.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold)
        Text(caption, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
    }
}

fun koraFormatarMoeda(valor: Double): String {
    val centavos = round(valor * 100).toLong()
    val absoluto = abs(centavos)
    val inteiroCru = (absoluto / 100).toString()
    val inteiro = inteiroCru.reversed().chunked(3).joinToString(".").reversed()
    val decimal = (absoluto % 100).toString().padStart(2, '0')
    val sinal = if (centavos < 0) "-" else ""
    return "R$ $sinal$inteiro,$decimal"
}

@Composable
private fun KoraMetricGlyph(symbol: KoraMetricSymbol, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val lado = size.minDimension
        val azul = Color(0xFF2563EB)
        val linha = Stroke(lado * 0.08f, cap = StrokeCap.Round)
        when (symbol) {
            KoraMetricSymbol.Products -> {
                val caixa = Path().apply { moveTo(lado*.12f,lado*.30f); lineTo(lado*.5f,lado*.10f); lineTo(lado*.88f,lado*.30f); lineTo(lado*.88f,lado*.72f); lineTo(lado*.5f,lado*.92f); lineTo(lado*.12f,lado*.72f); close() }
                drawPath(caixa, azul, style = linha)
                drawLine(azul, Offset(lado*.12f,lado*.30f), Offset(lado*.5f,lado*.5f), lado*.08f)
                drawLine(azul, Offset(lado*.88f,lado*.30f), Offset(lado*.5f,lado*.5f), lado*.08f)
                drawLine(azul, Offset(lado*.5f,lado*.5f), Offset(lado*.5f,lado*.9f), lado*.08f)
            }
            KoraMetricSymbol.Categories -> {
                val pasta = Path().apply { moveTo(lado*.1f,lado*.28f); lineTo(lado*.42f,lado*.28f); lineTo(lado*.53f,lado*.4f); lineTo(lado*.9f,lado*.4f); lineTo(lado*.84f,lado*.78f); lineTo(lado*.14f,lado*.78f); close() }
                drawPath(pasta, azul, style = linha)
            }
            KoraMetricSymbol.Users, KoraMetricSymbol.Customers -> {
                val centro = if (symbol == KoraMetricSymbol.Customers) lado*.5f else lado*.40f
                drawCircle(azul, lado*.12f, Offset(lado*centro, lado*.3f), style = linha)
                drawArc(azul, 180f, 180f, false, Offset(lado*(centro-.28f), lado*.42f), Size(lado*.56f,lado*.42f), style = linha)
                if (symbol == KoraMetricSymbol.Users) {
                    drawCircle(azul, lado*.10f, Offset(lado*.72f,lado*.36f), style = linha)
                    drawArc(azul, 180f, 180f, false, Offset(lado*.52f,lado*.5f), Size(lado*.38f,lado*.32f), style = linha)
                }
            }
            KoraMetricSymbol.Sales -> {
                drawLine(azul, Offset(lado*.14f,lado*.84f), Offset(lado*.14f,lado*.16f), lado*.08f, cap = StrokeCap.Round)
                drawLine(azul, Offset(lado*.14f,lado*.84f), Offset(lado*.9f,lado*.84f), lado*.08f, cap = StrokeCap.Round)
                val grafico = Path().apply { moveTo(lado*.24f,lado*.66f); lineTo(lado*.44f,lado*.48f); lineTo(lado*.6f,lado*.57f); lineTo(lado*.8f,lado*.3f) }
                drawPath(grafico, azul, style = linha)
            }
        }
    }
}

@Composable
fun KoraSectionHeader(
    title: String,
    count: Int,
    addLabel: String,
    onAdd: () -> Unit,
    onRefresh: () -> Unit,
    refreshing: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(50)) {
                Text(
                    text = if (count == 1) "1 registro" else "$count registros",
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End), verticalAlignment = Alignment.CenterVertically) {
            KoraSecondaryButton(text = "Atualizar", onClick = onRefresh, enabled = !refreshing, loading = refreshing)
            if (addLabel.isNotBlank()) {
                KoraPrimaryButton(text = addLabel, onClick = onAdd)
            }
        }
    }
}

@Composable
fun KoraFormPanel(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
        shadowElevation = 1.dp
    ) {
        androidx.compose.foundation.layout.BoxWithConstraints {
            Column(
                modifier = Modifier.padding(if (maxWidth < 520.dp) 16.dp else 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    subtitle?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                content()
            }
        }
    }
}

@Composable
fun KoraStatusBadge(
    active: Boolean,
    activeLabel: String = "Ativo",
    inactiveLabel: String = "Inativo",
    modifier: Modifier = Modifier
) {
    KoraStatusBadge(
        label = if (active) activeLabel else inactiveLabel,
        tone = if (active) KoraStatusTone.Success else KoraStatusTone.Danger,
        modifier = modifier
    )
}

@Composable
fun KoraEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

