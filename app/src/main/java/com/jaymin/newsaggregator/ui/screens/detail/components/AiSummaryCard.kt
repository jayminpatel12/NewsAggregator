package com.jaymin.newsaggregator.ui.screens.detail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaymin.newsaggregator.ui.screens.detail.ArticleDetailUiState
import com.jaymin.newsaggregator.ui.theme.AppTheme

@Composable
fun SmartSummaryCard(
    uiState: ArticleDetailUiState,
    onGenerateClick: () -> Unit
) {
    val borderGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppTheme.spacing.small),
        shape = AppTheme.shapes.medium,
        border = BorderStroke(1.dp, borderGradient),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .background(AppTheme.gradients.ai)
                .padding(AppTheme.spacing.medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = AppTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(AppTheme.spacing.small))

                Text(
                    text = "Smart Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.weight(1f))

                if ((!uiState.hasSummary || uiState.isSummaryError) && !uiState.isSummaryLoading) {
                    FilledTonalButton(
                        onClick = onGenerateClick,
                        contentPadding = PaddingValues(horizontal = AppTheme.spacing.medium, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (uiState.isSummaryError) "Retry" else "Generate",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.small))

            when {
                uiState.isSummaryLoading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                        Text(
                            text = "Gemini is analyzing the article...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                uiState.hasSummary -> {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn()
                    ) {
                        Text(
                            text = uiState.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = if (uiState.isSummaryError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                else -> {
                    Text(
                        text = "Get a quick AI-powered summary of this article to save your time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
