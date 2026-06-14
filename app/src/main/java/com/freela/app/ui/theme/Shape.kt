package com.freela.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val FreelaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),  // card di default
    large = RoundedCornerShape(26.dp),   // hero card
    extraLarge = RoundedCornerShape(32.dp),
)

val PillShape = RoundedCornerShape(50)
