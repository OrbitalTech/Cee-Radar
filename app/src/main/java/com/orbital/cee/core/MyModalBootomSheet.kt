package com.orbital.cee.core

import android.util.Log
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.R
import androidx.compose.material.Surface
import androidx.compose.material.SwipeableDefaults

import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.OnRemeasuredModifier
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt



/**
 * Possible values of [ModalBottomSheetState].
 */
@ExperimentalMaterialApi
enum class ModalBottomSheetValue {
    /**
     * The bottom sheet is not visible.
     */
    Hidden,

    /**
     * The bottom sheet is visible at full height.
     */
    Expanded,

    /**
     * The bottom sheet is partially visible at 50% of the screen height. This state is only
     * enabled if the height of the bottom sheet is more than 50% of the screen height.
     */
    HalfExpanded
}

/**
 * State of the [ModalBottomSheetLayout] composable.
 *
 * @param initialValue The initial value of the state. <b>Must not be set to
 * [ModalBottomSheetValue.HalfExpanded] if [isSkipHalfExpanded] is set to true.</b>
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param confirmValueChange Optional callback invoked to confirm or veto a pending state change.
 * @param isSkipHalfExpanded Whether the half expanded state, if the sheet is tall enough, should
 * be skipped. If true, the sheet will always expand to the [Expanded] state and move to the
 * [Hidden] state when hiding the sheet, either programmatically or by user interaction.
 * <b>Must not be set to true if the initialValue is [ModalBottomSheetValue.HalfExpanded].</b>
 * If supplied with [ModalBottomSheetValue.HalfExpanded] for the initialValue, an
 * [IllegalArgumentException] will be thrown.
 */
@ExperimentalMaterialApi
@Suppress("Deprecation")
fun ModalBottomSheetState(
    initialValue: ModalBottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmValueChange: (ModalBottomSheetValue) -> Boolean = { true },
    isSkipHalfExpanded: Boolean = false
) = ModalBottomSheetState(
    initialValue = initialValue,
    animationSpec = animationSpec,
    isSkipHalfExpanded = isSkipHalfExpanded,
    confirmStateChange = confirmValueChange
)

/**
 * State of the [ModalBottomSheetLayout] composable.
 *
 * @param initialValue The initial value of the state. <b>Must not be set to
 * [ModalBottomSheetValue.HalfExpanded] if [isSkipHalfExpanded] is set to true.</b>
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param isSkipHalfExpanded Whether the half expanded state, if the sheet is tall enough, should
 * be skipped. If true, the sheet will always expand to the [Expanded] state and move to the
 * [Hidden] state when hiding the sheet, either programmatically or by user interaction.
 * <b>Must not be set to true if the initialValue is [ModalBottomSheetValue.HalfExpanded].</b>
 * If supplied with [ModalBottomSheetValue.HalfExpanded] for the initialValue, an
 * [IllegalArgumentException] will be thrown.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@ExperimentalMaterialApi
class ModalBottomSheetState @Deprecated(
    message = "This constructor is deprecated. confirmStateChange has been renamed to " +
            "confirmValueChange.",
    replaceWith = ReplaceWith("ModalBottomSheetState(" +
            "initialValue, animationSpec, confirmStateChange, isSkipHalfExpanded)")
) constructor(
    initialValue: ModalBottomSheetValue,
    internal val animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    internal val isSkipHalfExpanded: Boolean = false,
    confirmStateChange: (ModalBottomSheetValue) -> Boolean
) {

    internal val swipeableState = SwipeableV2State(
        initialValue = initialValue,
        animationSpec = animationSpec,
        confirmValueChange = confirmStateChange,
        positionalThreshold = PositionalThreshold,
        velocityThreshold = VelocityThreshold
    )

    val currentValue: ModalBottomSheetValue
        get() = swipeableState.currentValue

    val targetValue: ModalBottomSheetValue
        get() = swipeableState.targetValue

    /**
     * Whether the bottom sheet is visible.
     */
    val isVisible: Boolean
        get() = swipeableState.currentValue != ModalBottomSheetValue.Hidden

    internal val hasHalfExpandedState: Boolean
        get() = swipeableState.hasAnchorForValue(ModalBottomSheetValue.HalfExpanded)

    @Deprecated(
        message = "This constructor is deprecated. confirmStateChange has been renamed to " +
                "confirmValueChange.",
        replaceWith = ReplaceWith("ModalBottomSheetState(" +
                "initialValue, animationSpec, confirmStateChange, false)")
    )
    @Suppress("Deprecation")
    constructor(
        initialValue: ModalBottomSheetValue,
        animationSpec: AnimationSpec<Float>,
        confirmStateChange: (ModalBottomSheetValue) -> Boolean
    ) : this(initialValue, animationSpec, isSkipHalfExpanded = false, confirmStateChange)

    init {
        if (isSkipHalfExpanded) {
            require(initialValue != ModalBottomSheetValue.HalfExpanded) {
                "The initial value must not be set to HalfExpanded if skipHalfExpanded is set to" +
                        " true."
            }
        }
    }

    /**
     * Show the bottom sheet with animation and suspend until it's shown. If the sheet is taller
     * than 50% of the parent's height, the bottom sheet will be half expanded. Otherwise it will be
     * fully expanded.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun show() {
        val targetValue = when {
            hasHalfExpandedState -> ModalBottomSheetValue.HalfExpanded
            else -> ModalBottomSheetValue.Expanded
        }
        animateTo(targetValue)
    }

    /**
     * Half expand the bottom sheet if half expand is enabled with animation and suspend until it
     * animation is complete or cancelled
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    internal suspend fun halfExpand() {
        if (!hasHalfExpandedState) {
            return
        }
        animateTo(ModalBottomSheetValue.HalfExpanded)
    }

    /**
     * Fully expand the bottom sheet with animation and suspend until it if fully expanded or
     * animation has been cancelled.
     * *
     * @throws [CancellationException] if the animation is interrupted
     */
    internal suspend fun expand() {
        if (!swipeableState.hasAnchorForValue(ModalBottomSheetValue.Expanded)) {
            return
        }
        animateTo(ModalBottomSheetValue.Expanded)
    }

    /**
     * Hide the bottom sheet with animation and suspend until it if fully hidden or animation has
     * been cancelled.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun hide() = animateTo(ModalBottomSheetValue.Hidden)

    internal suspend fun animateTo(
        target: ModalBottomSheetValue,
        velocity: Float = swipeableState.lastVelocity
    ) = swipeableState.animateTo(target, velocity)

    internal suspend fun snapTo(target: ModalBottomSheetValue) = swipeableState.snapTo(target)

    internal fun requireOffset() = swipeableState.requireOffset()

    internal val lastVelocity: Float get() = swipeableState.lastVelocity

    internal val isAnimationRunning: Boolean get() = swipeableState.isAnimationRunning

    companion object {
        /**
         * The default [Saver] implementation for [ModalBottomSheetState].
         * Saves the [currentValue] and recreates a [ModalBottomSheetState] with the saved value as
         * initial value.
         */
        fun Saver(
            animationSpec: AnimationSpec<Float>,
            confirmValueChange: (ModalBottomSheetValue) -> Boolean,
            skipHalfExpanded: Boolean,
        ): Saver<ModalBottomSheetState, *> = Saver(
            save = { it.currentValue },
            restore = {
                ModalBottomSheetState(
                    initialValue = it,
                    animationSpec = animationSpec,
                    isSkipHalfExpanded = skipHalfExpanded,
                    confirmValueChange = confirmValueChange
                )
            }
        )

        /**
         * The default [Saver] implementation for [ModalBottomSheetState].
         * Saves the [currentValue] and recreates a [ModalBottomSheetState] with the saved value as
         * initial value.
         */
        @Deprecated(
            message = "This function is deprecated. confirmStateChange has been renamed to " +
                    "confirmValueChange.",
            replaceWith = ReplaceWith("Saver(animationSpec, confirmStateChange, " +
                    "skipHalfExpanded)")
        )
        fun Saver(
            animationSpec: AnimationSpec<Float>,
            skipHalfExpanded: Boolean,
            confirmStateChange: (ModalBottomSheetValue) -> Boolean
        ): Saver<ModalBottomSheetState, *> = Saver(
            animationSpec = animationSpec,
            confirmValueChange = confirmStateChange,
            skipHalfExpanded = skipHalfExpanded
        )
    }
}

/**
 * Create a [ModalBottomSheetState] and [remember] it.
 *
 * @param initialValue The initial value of the state.
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param confirmValueChange Optional callback invoked to confirm or veto a pending state change.
 * @param skipHalfExpanded Whether the half expanded state, if the sheet is tall enough, should
 * be skipped. If true, the sheet will always expand to the [Expanded] state and move to the
 * [Hidden] state when hiding the sheet, either programmatically or by user interaction.
 * <b>Must not be set to true if the [initialValue] is [ModalBottomSheetValue.HalfExpanded].</b>
 * If supplied with [ModalBottomSheetValue.HalfExpanded] for the [initialValue], an
 * [IllegalArgumentException] will be thrown.
 */
@ExperimentalMaterialApi
@Composable
fun rememberModalBottomSheetState(
    initialValue: ModalBottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmValueChange: (ModalBottomSheetValue) -> Boolean = { true },
    skipHalfExpanded: Boolean = false,
): ModalBottomSheetState {
    // Key the rememberSaveable against the initial value. If it changed we don't want to attempt
    // to restore as the restored value could have been saved with a now invalid set of anchors.
    // b/152014032
    return key(initialValue) {
        rememberSaveable(
            initialValue, animationSpec, skipHalfExpanded, confirmValueChange,
            saver = ModalBottomSheetState.Saver(
                animationSpec = animationSpec,
                skipHalfExpanded = skipHalfExpanded,
                confirmValueChange = confirmValueChange
            )
        ) {
            ModalBottomSheetState(
                initialValue = initialValue,
                animationSpec = animationSpec,
                isSkipHalfExpanded = skipHalfExpanded,
                confirmValueChange = confirmValueChange
            )
        }
    }
}

/**
 * Create a [ModalBottomSheetState] and [remember] it.
 *
 * @param initialValue The initial value of the state.
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param skipHalfExpanded Whether the half expanded state, if the sheet is tall enough, should
 * be skipped. If true, the sheet will always expand to the [Expanded] state and move to the
 * [Hidden] state when hiding the sheet, either programmatically or by user interaction.
 * <b>Must not be set to true if the [initialValue] is [ModalBottomSheetValue.HalfExpanded].</b>
 * If supplied with [ModalBottomSheetValue.HalfExpanded] for the [initialValue], an
 * [IllegalArgumentException] will be thrown.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Deprecated(
    message = "This function is deprecated. confirmStateChange has been renamed to " +
            "confirmValueChange.",
    replaceWith = ReplaceWith("rememberModalBottomSheetState(" +
            "initialValue, animationSpec, confirmStateChange, false)")
)

/**
 * Create a [ModalBottomSheetState] and [remember] it.
 *
 * @param initialValue The initial value of the state.
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */

/**
 * <a href="https://material.io/components/sheets-bottom#modal-bottom-sheet" class="external" target="_blank">Material Design modal bottom sheet</a>.
 *
 * Modal bottom sheets present a set of choices while blocking interaction with the rest of the
 * screen. They are an alternative to inline menus and simple dialogs, providing
 * additional room for content, iconography, and actions.
 *
 * ![Modal bottom sheet image](https://developer.android.com/images/reference/androidx/compose/material/modal-bottom-sheet.png)
 *
 * A simple example of a modal bottom sheet looks like this:
 *
 * @sample androidx.compose.material.samples.ModalBottomSheetSample
 *
 * @param sheetContent The content of the bottom sheet.
 * @param modifier Optional [Modifier] for the entire component.
 * @param sheetState The state of the bottom sheet.
 * @param sheetShape The shape of the bottom sheet.
 * @param sheetElevation The elevation of the bottom sheet.
 * @param sheetBackgroundColor The background color of the bottom sheet.
 * @param sheetContentColor The preferred content color provided by the bottom sheet to its
 * children. Defaults to the matching content color for [sheetBackgroundColor], or if that is not
 * a color from the theme, this will keep the same content color set above the bottom sheet.
 * @param scrimColor The color of the scrim that is applied to the rest of the screen when the
 * bottom sheet is visible. If the color passed is [Color.Unspecified], then a scrim will no
 * longer be applied and the bottom sheet will not block interaction with the rest of the screen
 * when visible.
 * @param content The content of rest of the screen.
 */
@Stable
private class SwipeAnchorsModifier(
    private val onDensityChanged: (density: Density) -> Unit,
    private val onSizeChanged: (layoutSize: IntSize) -> Unit,
    inspectorInfo: InspectorInfo.() -> Unit,
) : LayoutModifier, OnRemeasuredModifier, InspectorValueInfo(inspectorInfo) {

    private var lastDensity: Float = -1f
    private var lastFontScale: Float = -1f

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        if (density != lastDensity || fontScale != lastFontScale) {
            onDensityChanged(Density(density, fontScale))
            lastDensity = density
            lastFontScale = fontScale
        }
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }

    override fun onRemeasured(size: IntSize) {
        onSizeChanged(size)
    }

    override fun toString() = "SwipeAnchorsModifierImpl(updateDensity=$onDensityChanged, " +
            "onSizeChanged=$onSizeChanged)"
}
@ExperimentalMaterialApi
internal fun <T> Modifier.swipeableV2(
    state: SwipeableV2State<T>,
    orientation: Orientation,
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    interactionSource: MutableInteractionSource? = null
) = draggable(
    state = state.draggableState,
    orientation = orientation,
    enabled = enabled,
    interactionSource = interactionSource,
    reverseDirection = reverseDirection,
    startDragImmediately = state.isAnimationRunning,
    onDragStopped = { velocity -> launch { state.settle(velocity) } }
)
@ExperimentalMaterialApi
internal fun <T> Modifier.swipeAnchors(
    state: SwipeableV2State<T>,
    possibleValues: Set<T>,
    anchorChangeHandler: AnchorChangeHandler<T>? = null,
    calculateAnchor: (value: T, layoutSize: IntSize) -> Float?,
) = this.then(SwipeAnchorsModifier(
    onDensityChanged = { state.density = it },
    onSizeChanged = { layoutSize ->
        val previousAnchors = state.anchors
        val newAnchors = mutableMapOf<T, Float>()
        possibleValues.forEach {
            val anchorValue = calculateAnchor(it, layoutSize)
            if (anchorValue != null) {
                newAnchors[it] = anchorValue
            }
        }
        if (previousAnchors != newAnchors) {
            val previousTarget = state.targetValue
            val stateRequiresCleanup = state.updateAnchors(newAnchors)
            if (stateRequiresCleanup) {
                anchorChangeHandler?.onAnchorsChanged(previousTarget, previousAnchors, newAnchors)
            }
        }
    },
    inspectorInfo = debugInspectorInfo {
        name = "swipeAnchors"
        properties["state"] = state
        properties["possibleValues"] = possibleValues
        properties["anchorChangeHandler"] = anchorChangeHandler
        properties["calculateAnchor"] = calculateAnchor
    }
))

@Composable
@ExperimentalMaterialApi
fun ModalBottomSheetLayout(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    isShowReport:Boolean = false,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val orientation = Orientation.Vertical
    val anchorChangeHandler = remember(sheetState, scope) {
        ModalBottomSheetAnchorChangeHandler(
            state = sheetState,
            animateTo = { target, velocity ->
                scope.launch { sheetState.animateTo(target, velocity = velocity) }
            },
            snapTo = { target -> scope.launch { sheetState.snapTo(target) } }
        )
    }
    BoxWithConstraints(modifier) {
        val fullHeight = constraints.maxHeight.toFloat()
        Box(Modifier.fillMaxSize()) {
            content()
            Scrim(
                color = scrimColor,
                onDismiss = {
                    if (sheetState.swipeableState.confirmValueChange(ModalBottomSheetValue.Hidden)) {
                        scope.launch { sheetState.hide() }
                    }
                },
                visible = sheetState.swipeableState.targetValue != ModalBottomSheetValue.Hidden,
                isShowReport = isShowReport
            )
        }
        Surface(
            Modifier
                .align(Alignment.TopCenter) // We offset from the top so we'll center from there
                .widthIn(max = MaxModalBottomSheetWidth)
                .fillMaxWidth()
                .nestedScroll(
                    remember(sheetState.swipeableState, orientation) {
                        ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
                            state = sheetState.swipeableState,
                            orientation = orientation
                        )
                    }
                )
                .offset {
                    IntOffset(
                        0,
                        sheetState.swipeableState
                            .requireOffset()
                            .roundToInt()
                    )
                }
                .swipeableV2(
                    state = sheetState.swipeableState,
                    orientation = orientation,
                    enabled = sheetState.swipeableState.currentValue != ModalBottomSheetValue.Hidden,
                )
                .swipeAnchors(
                    state = sheetState.swipeableState,
                    possibleValues = setOf(ModalBottomSheetValue.Hidden, ModalBottomSheetValue.HalfExpanded, ModalBottomSheetValue.Expanded),
                    anchorChangeHandler = anchorChangeHandler
                ) { state, sheetSize ->
                    when (state) {
                        ModalBottomSheetValue.Hidden -> fullHeight
                        ModalBottomSheetValue.HalfExpanded -> when {
                            sheetSize.height < fullHeight / 2f -> null
                            sheetState.isSkipHalfExpanded -> null
                            else -> fullHeight / 2f
                        }

                        ModalBottomSheetValue.Expanded -> if (sheetSize.height != 0) {
                            max(0f, fullHeight - sheetSize.height)
                        } else null
                    }
                }
                .semantics {
                    if (sheetState.isVisible) {
                        dismiss {
                            if (sheetState.swipeableState.confirmValueChange(ModalBottomSheetValue.Hidden)) {
                                scope.launch { sheetState.hide() }
                            }
                            true
                        }
                        if (sheetState.swipeableState.currentValue == ModalBottomSheetValue.HalfExpanded) {
                            expand {
                                if (sheetState.swipeableState.confirmValueChange(ModalBottomSheetValue.Expanded)) {
                                    scope.launch { sheetState.expand() }
                                }
                                true
                            }
                        } else if (sheetState.hasHalfExpandedState) {
                            collapse {
                                if (sheetState.swipeableState.confirmValueChange(ModalBottomSheetValue.HalfExpanded)) {
                                    scope.launch { sheetState.halfExpand() }
                                }
                                true
                            }
                        }
                    }
                },
            shape = sheetShape,
            elevation = sheetElevation,
            color = sheetBackgroundColor,
            contentColor = sheetContentColor
        ) {
            Column(content = sheetContent)
        }
    }
}
@Immutable
@kotlin.jvm.JvmInline
internal value class Strings private constructor(@Suppress("unused") private val value: Int) {
    companion object {
        val NavigationMenu = Strings(0)
        val CloseDrawer = Strings(1)
        val CloseSheet = Strings(2)
        val DefaultErrorMessage = Strings(3)
        val ExposedDropdownMenu = Strings(4)
        val SliderRangeStart = Strings(5)
        val SliderRangeEnd = Strings(6)
    }
}
@Composable
internal  fun getString(string: Strings): String {
    LocalConfiguration.current
    val resources = LocalContext.current.resources
    return when (string) {
        Strings.NavigationMenu -> resources.getString(R.string.navigation_menu)
        Strings.CloseDrawer -> resources.getString(R.string.close_drawer)
        Strings.CloseSheet -> resources.getString(R.string.close_sheet)
        Strings.DefaultErrorMessage -> resources.getString(R.string.default_error_message)
        Strings.ExposedDropdownMenu -> resources.getString(R.string.dropdown_menu)
        Strings.SliderRangeStart -> resources.getString(R.string.range_start)
        Strings.SliderRangeEnd -> resources.getString(R.string.range_end)
        else -> ""
    }
}
@Composable
private fun Scrim(
    color: Color,
    onDismiss: () -> Unit,
    visible: Boolean,
    isShowReport:Boolean
) {
    if (color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = TweenSpec()
        )
        val closeSheet = getString(Strings.CloseSheet)
        val dismissModifier = if (visible) {
            Modifier
                .pointerInput(onDismiss) { detectTapGestures { onDismiss() } }
                .semantics(mergeDescendants = true) {
                    contentDescription = closeSheet
                    onClick { onDismiss(); true }
                }
        } else {
            Modifier
        }

        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            Log.d("DEBUG_CANVAS_SIZE_BALAB",size.height.toString())
            if(isShowReport){
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x00ffffff),
                            Color(0x02000000),
                            Color(0x4D000000),
                            Color(0x4D000000),
                            Color(0x4D000000),
                            Color(0x4D000000),
                            Color(0x4D000000),
                            Color(0x4D000000),
                            Color(0x4D000000),
                            Color(0x4D000000),
                        ),
                        center = Offset(size.width/2, size.height/3f),
                        tileMode = TileMode.Mirror,
                        radius = 750f
                    ),
                    center = Offset(size.width/2, size.height/2.754f),
                    radius= 1400f,
                    alpha = alpha,
                )
            }else{
                drawRect(color = color, alpha = alpha)
            }


        }
    }
}

/**
 * Contains useful Defaults for [ModalBottomSheetLayout].
 */
object ModalBottomSheetDefaults {

    /**
     * The default elevation used by [ModalBottomSheetLayout].
     */
    val Elevation = 16.dp

    /**
     * The default scrim color used by [ModalBottomSheetLayout].
     */
    val scrimColor: Color
        @Composable
        get() = MaterialTheme.colors.onSurface.copy(alpha = 0.32f)
}

@OptIn(ExperimentalMaterialApi::class)
private fun ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
    state: SwipeableV2State<*>,
    orientation: Orientation
): NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.Drag) {
            state.dispatchRawDelta(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return if (source == NestedScrollSource.Drag) {
            state.dispatchRawDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = available.toFloat()
        val currentOffset = state.requireOffset()
        return if (toFling < 0 && currentOffset > state.minOffset) {
            state.settle(velocity = toFling)
            // since we go to the anchor with tween settling, consume all for the best UX
            available
        } else {
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        state.settle(velocity = available.toFloat())
        return available
    }

    private fun Float.toOffset(): Offset = Offset(
        x = if (orientation == Orientation.Horizontal) this else 0f,
        y = if (orientation == Orientation.Vertical) this else 0f
    )

    @JvmName("velocityToFloat")
    private fun Velocity.toFloat() = if (orientation == Orientation.Horizontal) x else y

    @JvmName("offsetToFloat")
    private fun Offset.toFloat(): Float = if (orientation == Orientation.Horizontal) x else y
}

@OptIn(ExperimentalMaterialApi::class)
private fun ModalBottomSheetAnchorChangeHandler(
    state: ModalBottomSheetState,
    animateTo: (target: ModalBottomSheetValue, velocity: Float) -> Unit,
    snapTo: (target: ModalBottomSheetValue) -> Unit,
) = AnchorChangeHandler<ModalBottomSheetValue> { previousTarget, previousAnchors, newAnchors ->
    val previousTargetOffset = previousAnchors[previousTarget]
    val newTarget =
        when (previousTarget) {
            ModalBottomSheetValue.Hidden -> ModalBottomSheetValue.Hidden
            ModalBottomSheetValue.HalfExpanded, ModalBottomSheetValue.Expanded -> {
            val hasHalfExpandedState = newAnchors.containsKey(ModalBottomSheetValue.HalfExpanded)
            val newTarget = if (hasHalfExpandedState) ModalBottomSheetValue.HalfExpanded
            else if (newAnchors.containsKey(ModalBottomSheetValue.Expanded)) ModalBottomSheetValue.Expanded else ModalBottomSheetValue.Hidden
            newTarget
        }
    }
    val newTargetOffset = newAnchors.getValue(newTarget)
    if (newTargetOffset != previousTargetOffset) {
        if (state.isAnimationRunning) {
            // Re-target the animation to the new offset if it changed
            animateTo(newTarget, state.lastVelocity)
        } else {
            // Snap to the new offset value of the target if no animation was running
            snapTo(newTarget)
        }
    }
}

@Stable
@ExperimentalMaterialApi
internal object SwipeableV2Defaults {
    /**
     * The default animation used by [SwipeableV2State].
     */
    @ExperimentalMaterialApi
    val AnimationSpec = SpringSpec<Float>()

    /**
     * The default velocity threshold (1.8 dp per millisecond) used by [rememberSwipeableV2State].
     */
    @ExperimentalMaterialApi
    val VelocityThreshold: Dp = 125.dp

    /**
     * The default positional threshold (56 dp) used by [rememberSwipeableV2State]
     */
    @ExperimentalMaterialApi
    val PositionalThreshold: Density.(totalDistance: Float) -> Float =
        fixedPositionalThreshold(56.dp)

    /**
     * A [AnchorChangeHandler] implementation that attempts to reconcile an in-progress animation
     * by re-targeting it if necessary or finding the closest new anchor.
     * If the previous anchor is not in the new set of anchors, this implementation will snap to the
     * closest anchor.
     *
     * Consider implementing a custom handler for more complex components like sheets.
     * The [animate] and [snap] lambdas hoist the animation and snap logic. Usually these will just
     * delegate to [SwipeableV2State].
     *
     * @param state The [SwipeableV2State] the change handler will read from
     * @param animate A lambda that gets invoked to start an animation to a new target
     * @param snap A lambda that gets invoked to snap to a new target
     */
    @ExperimentalMaterialApi
    internal fun fixedPositionalThreshold(threshold: Dp): Density.(distance: Float) -> Float = {
        threshold.toPx()
    }

    @ExperimentalMaterialApi
    internal fun <T> ReconcileAnimationOnAnchorChangeHandler(
        state: com.orbital.cee.core.SwipeableV2State<T>,
        animate: (target: T, velocity: Float) -> Unit,
        snap: (target: T) -> Unit
    ) = AnchorChangeHandler { previousTarget, previousAnchors, newAnchors ->
        val previousTargetOffset = previousAnchors[previousTarget]
        val newTargetOffset = newAnchors[previousTarget]
        if (previousTargetOffset != newTargetOffset) {
            if (newTargetOffset != null) {
                animate(previousTarget, state.lastVelocity)
            } else {
                snap(newAnchors.closestAnchor(offset = state.requireOffset()))
            }
        }
    }
}
@ExperimentalMaterialApi
internal fun interface AnchorChangeHandler<T> {

    /**
     * Callback that is invoked when the anchors have changed, after the [SwipeableV2State] has been
     * updated with them. Use this hook to re-launch animations or interrupt them if needed.
     *
     * @param previousTargetValue The target value before the anchors were updated
     * @param previousAnchors The previously set anchors
     * @param newAnchors The newly set anchors
     */
    fun onAnchorsChanged(
        previousTargetValue: T,
        previousAnchors: Map<T, Float>,
        newAnchors: Map<T, Float>
    )
}

@Stable
@ExperimentalMaterialApi
internal class SwipeableV2State<T>(
    initialValue: T,
    internal val animationSpec: AnimationSpec<Float> = com.orbital.cee.core.SwipeableV2Defaults.AnimationSpec,
    internal val confirmValueChange: (newValue: T) -> Boolean = { true },
    internal val positionalThreshold: Density.(totalDistance: Float) -> Float =
        com.orbital.cee.core.SwipeableV2Defaults.PositionalThreshold,
    internal val velocityThreshold: Dp = com.orbital.cee.core.SwipeableV2Defaults.VelocityThreshold,
) {

    /**
     * The current value of the [SwipeableV2State].
     */
    var currentValue: T by mutableStateOf(initialValue)
        private set

    /**
     * The target value. This is the closest value to the current offset (taking into account
     * positional thresholds). If no interactions like animations or drags are in progress, this
     * will be the current value.
     */
    val targetValue: T by derivedStateOf {
        animationTarget ?: run {
            val currentOffset = offset
            if (currentOffset != null) {
                computeTarget(currentOffset, currentValue, velocity = 0f)
            } else currentValue
        }
    }

    /**
     * The current offset, or null if it has not been initialized yet.
     *
     * The offset will be initialized during the first measurement phase of the node that the
     * [swipeableV2] modifier is attached to. These are the phases:
     * Composition { -> Effects } -> Layout { Measurement -> Placement } -> Drawing
     * During the first composition, the offset will be null. In subsequent compositions, the offset
     * will be derived from the anchors of the previous pass.
     * Always prefer accessing the offset from a LaunchedEffect as it will be scheduled to be
     * executed the next frame, after layout.
     *
     * To guarantee stricter semantics, consider using [requireOffset].
     */
    @get:Suppress("AutoBoxing")
    var offset: Float? by mutableStateOf(null)
        private set

    /**
     * Require the current offset.
     *
     * @throws IllegalStateException If the offset has not been initialized yet
     */
    fun requireOffset(): Float = checkNotNull(offset) {
        "The offset was read before being initialized. Did you access the offset in a phase " +
                "before layout, like effects or composition?"
    }

    /**
     * Whether an animation is currently in progress.
     */
    val isAnimationRunning: Boolean get() = animationTarget != null

    /**
     * The fraction of the progress going from [currentValue] to [targetValue], within [0f..1f]
     * bounds.
     */
    /*@FloatRange(from = 0f, to = 1f)*/
    val progress: Float by derivedStateOf {
        val a = anchors[currentValue] ?: 0f
        val b = anchors[targetValue] ?: 0f
        val distance = abs(b - a)
        if (distance > 1e-6f) {
            val progress = (this.requireOffset() - a) / (b - a)
            // If we are very close to 0f or 1f, we round to the closest
            if (progress < 1e-6f) 0f else if (progress > 1 - 1e-6f) 1f else progress
        } else 1f
    }

    /**
     * The velocity of the last known animation. Gets reset to 0f when an animation completes
     * successfully, but does not get reset when an animation gets interrupted.
     * You can use this value to provide smooth reconciliation behavior when re-targeting an
     * animation.
     */
    var lastVelocity: Float by mutableStateOf(0f)
        private set

    /**
     * The minimum offset this state can reach. This will be the smallest anchor, or
     * [Float.NEGATIVE_INFINITY] if the anchors are not initialized yet.
     */
    val minOffset by derivedStateOf { anchors.minOrNull() ?: Float.NEGATIVE_INFINITY }

    /**
     * The maximum offset this state can reach. This will be the biggest anchor, or
     * [Float.POSITIVE_INFINITY] if the anchors are not initialized yet.
     */
    val maxOffset by derivedStateOf { anchors.maxOrNull() ?: Float.POSITIVE_INFINITY }

    private var animationTarget: T? by mutableStateOf(null)
    internal val draggableState = DraggableState {
        offset = ((offset ?: 0f) + it).coerceIn(minOffset, maxOffset)
    }

    internal var anchors by mutableStateOf(emptyMap<T, Float>())

    internal var density: Density? = null

    /**
     * Update the anchors.
     * If the previous set of anchors was empty, attempt to update the offset to match the initial
     * value's anchor.
     *
     * @return true if the state needs to be adjusted after updating the anchors, e.g. if the
     * initial value is not found in the initial set of anchors. false if no further updates are
     * needed.
     */
    internal fun updateAnchors(newAnchors: Map<T, Float>): Boolean {
        val previousAnchorsEmpty = anchors.isEmpty()
        anchors = newAnchors
        val initialValueHasAnchor = if (previousAnchorsEmpty) {
            val initialValueAnchor = anchors[currentValue]
            val initialValueHasAnchor = initialValueAnchor != null
            if (initialValueHasAnchor) offset = initialValueAnchor
            initialValueHasAnchor
        } else true
        return !initialValueHasAnchor || !previousAnchorsEmpty
    }

    /**
     * Whether the [value] has an anchor associated with it.
     */
    fun hasAnchorForValue(value: T): Boolean = anchors.containsKey(value)

    /**
     * Snap to a [targetValue] without any animation.
     * If the [targetValue] is not in the set of anchors, the [currentValue] will be updated to the
     * [targetValue] without updating the offset.
     *
     * @throws CancellationException if the interaction interrupted by another interaction like a
     * gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
     *
     * @param targetValue The target value of the animation
     */
    suspend fun snapTo(targetValue: T) {
        val targetOffset = anchors[targetValue]
        if (targetOffset != null) {
            try {
                draggableState.drag {
                    animationTarget = targetValue
                    dragBy(targetOffset - requireOffset())
                }
                this.currentValue = targetValue
            } finally {
                animationTarget = null
            }
        } else {
            currentValue = targetValue
        }
    }

    /**
     * Animate to a [targetValue].
     * If the [targetValue] is not in the set of anchors, the [currentValue] will be updated to the
     * [targetValue] without updating the offset.
     *
     * @throws CancellationException if the interaction interrupted by another interaction like a
     * gesture interaction or another programmatic interaction like a [animateTo] or [snapTo] call.
     *
     * @param targetValue The target value of the animation
     * @param velocity The velocity the animation should start with, [lastVelocity] by default
     */
    suspend fun animateTo(
        targetValue: T,
        velocity: Float = lastVelocity,
    ) {
        val targetOffset = anchors[targetValue]
        if (targetOffset != null) {
            try {
                draggableState.drag {
                    animationTarget = targetValue
                    var prev = offset ?: 0f
                    animate(prev, targetOffset, velocity, animationSpec) { value, velocity ->
                        // Our onDrag coerces the value within the bounds, but an animation may
                        // overshoot, for example a spring animation or an overshooting interpolator
                        // We respect the user's intention and allow the overshoot, but still use
                        // DraggableState's drag for its mutex.
                        offset = value
                        prev = value
                        lastVelocity = velocity
                    }
                    lastVelocity = 0f
                }
            } finally {
                animationTarget = null
                val endOffset = requireOffset()
                val endState = anchors
                    .entries
                    .firstOrNull { (_, anchorOffset) -> abs(anchorOffset - endOffset) < 0.5f }
                    ?.key
                this.currentValue = endState ?: currentValue
            }
        } else {
            currentValue = targetValue
        }
    }

    /**
     * Find the closest anchor taking into account the velocity and settle at it with an animation.
     */
    suspend fun settle(velocity: Float) {
        val previousValue = this.currentValue
        val targetValue = computeTarget(
            offset = requireOffset(),
            currentValue = previousValue,
            velocity = velocity
        )
        if (confirmValueChange(targetValue)) {
            animateTo(targetValue, velocity)
        } else {
            // If the user vetoed the state change, rollback to the previous state.
            animateTo(previousValue, velocity)
        }
    }

    /**
     * Swipe by the [delta], coerce it in the bounds and dispatch it to the [draggableState].
     *
     * @return The delta the [draggableState] will consume
     */
    fun dispatchRawDelta(delta: Float): Float {
        val currentDragPosition = offset ?: 0f
        val potentiallyConsumed = currentDragPosition + delta
        val clamped = potentiallyConsumed.coerceIn(minOffset, maxOffset)
        val deltaToConsume = clamped - currentDragPosition
        if (abs(deltaToConsume) > 0) {
            draggableState.dispatchRawDelta(deltaToConsume)
        }
        return deltaToConsume
    }

    private fun computeTarget(
        offset: Float,
        currentValue: T,
        velocity: Float
    ): T {
        val currentAnchors = anchors
        val currentAnchor = currentAnchors[currentValue]
        val currentDensity = requireDensity()
        val velocityThresholdPx = with(currentDensity) { velocityThreshold.toPx() }
        return if (currentAnchor == offset || currentAnchor == null) {
            currentValue
        } else if (currentAnchor < offset) {
            // Swiping from lower to upper (positive).
            if (velocity >= velocityThresholdPx) {
                currentAnchors.closestAnchor(offset, true)
            } else {
                val upper = currentAnchors.closestAnchor(offset, true)
                val distance = abs(currentAnchors.getValue(upper) - currentAnchor)
                val relativeThreshold = abs(positionalThreshold(currentDensity, distance))
                val absoluteThreshold = abs(currentAnchor + relativeThreshold)
                if (offset < absoluteThreshold) currentValue else upper
            }
        } else {
            // Swiping from upper to lower (negative).
            if (velocity <= -velocityThresholdPx) {
                currentAnchors.closestAnchor(offset, false)
            } else {
                val lower = currentAnchors.closestAnchor(offset, false)
                val distance = abs(currentAnchor - currentAnchors.getValue(lower))
                val relativeThreshold = abs(positionalThreshold(currentDensity, distance))
                val absoluteThreshold = abs(currentAnchor - relativeThreshold)
                if (offset < 0) {
                    // For negative offsets, larger absolute thresholds are closer to lower anchors
                    // than smaller ones.
                    if (abs(offset) < absoluteThreshold) currentValue else lower
                } else {
                    if (offset > absoluteThreshold) currentValue else lower
                }
            }
        }
    }

    private fun requireDensity() = requireNotNull(density) {
        "SwipeableState did not have a density attached. Are you using Modifier.swipeable with " +
                "this=$this SwipeableState?"
    }

    companion object {
        /**
         * The default [Saver] implementation for [SwipeableV2State].
         */
        @ExperimentalMaterialApi
        fun <T : Any> Saver(
            animationSpec: AnimationSpec<Float>,
            confirmValueChange: (T) -> Boolean,
            positionalThreshold: Density.(distance: Float) -> Float,
            velocityThreshold: Dp
        ) = Saver<com.orbital.cee.core.SwipeableV2State<T>, T>(
            save = { it.currentValue },
            restore = {
                SwipeableV2State(
                    initialValue = it,
                    animationSpec = animationSpec,
                    confirmValueChange = confirmValueChange,
                    positionalThreshold = positionalThreshold,
                    velocityThreshold = velocityThreshold
                )
            }
        )
    }
}
private fun <T> Map<T, Float>.maxOrNull() = maxOfOrNull { (_, offset) -> offset }
private fun <T> Map<T, Float>.minOrNull() = minOfOrNull { (_, offset) -> offset }
private fun <T> Map<T, Float>.closestAnchor(
    offset: Float = 0f,
    searchUpwards: Boolean = false
): T {
    require(isNotEmpty()) { "The anchors were empty when trying to find the closest anchor" }
    return minBy { (_, anchor) ->
        val delta = if (searchUpwards) anchor - offset else offset - anchor
        if (delta < 0) Float.POSITIVE_INFINITY else delta
    }.key
}
private val PositionalThreshold: Density.(Float) -> Float = { 56.dp.toPx() }
private val VelocityThreshold = 125.dp
private val MaxModalBottomSheetWidth = 640.dp
