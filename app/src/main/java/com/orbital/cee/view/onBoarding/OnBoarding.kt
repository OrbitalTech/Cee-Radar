@file:OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)

package com.orbital.cee.view.onBoarding

import android.os.Build
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.orbital.cee.R
import com.orbital.cee.model.OnBoardingModel
import com.orbital.cee.view.home.HomeViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoarding(
    navController: NavController,
    model: HomeViewModel = viewModel()
) {
    val items = ArrayList<OnBoardingModel>()
    items.add(
        OnBoardingModel(
            R.raw.lottie_onb_one_cee,
            stringResource(id = R.string.oneboarding_one_title),
            stringResource(id = R.string.oneboarding_one_description)
        )
    )
    items.add(
        OnBoardingModel(
            R.raw.lottie_onb_three_cee,
            stringResource(id = R.string.oneboarding_two_title),
            stringResource(id = R.string.oneboarding_two_description)
        )
    )
    items.add(
        OnBoardingModel(
            R.raw.lottie_onb_two_cee,
            stringResource(id = R.string.oneboarding_three_title),
            stringResource(id = R.string.oneboarding_three_description)
        )
    )



    Surface(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)) {

        val pagerState = rememberPagerState(
            pageCount = items.size,
            initialOffscreenLimit = 2,
            infiniteLoop = false,
            initialPage = 0,
        )
        OnBoardingPager(
            item = items, pagerState = pagerState, modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            navController = navController,
            model = model

        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@ExperimentalPagerApi
@Composable
fun OnBoardingPager(
    item: List<OnBoardingModel>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    navController:NavController,
    model : HomeViewModel
) {
    val scope = rememberCoroutineScope()



    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            HorizontalPager(state = pagerState, count = item.size) { page ->
                val composition by rememberLottieComposition(
                    LottieCompositionSpec
                        .RawRes(resId = item[page].image )
                )

                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever,
                    isPlaying = true,
                    speed = 1f,
                    restartOnPlay = false
                )
                Column(
                    modifier = Modifier
                        .padding(top = 25.dp)
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    LottieAnimation(
                        composition,
                        progress,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        ,horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = item[page].title,
                            color = MaterialTheme.colors.primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W900
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = item[page].desc,
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = Color.Black,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W700
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                    }
                    PagerIndicator(item.size, pagerState.currentPage)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f)){
                BottomSection(item.size,pagerState.currentPage+1, onSkipClicked = {
                    scope.launch {
                        model.saveFirstLaunch(false)
                        navController.navigate("authentication")
                    }
                }, onNextClicked = {
                    if(pagerState.currentPage+1 >= item.size){
                        model.saveFirstLaunch(false)
                        navController.navigate("authentication")
                    }else{
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }

                })
            }


        }

    }
}

@ExperimentalPagerApi
@Composable
fun rememberPagerState(
    @androidx.annotation.IntRange(from = 0) pageCount: Int,
    @androidx.annotation.IntRange(from = 0) initialPage: Int = 0,
    @FloatRange(from = 0.0, to = 1.0) initialPageOffset: Float = 0f,
    @androidx.annotation.IntRange(from = 1) initialOffscreenLimit: Int = 1,
    infiniteLoop: Boolean = false
): PagerState = rememberSaveable(saver = PagerState.Saver) {
    PagerState(
//        pageCount = pageCount,
        currentPage = initialPage,
//        currentPageOffset = initialPageOffset,
//        offscreenLimit = initialOffscreenLimit,
//        infiniteLoop = infiniteLoop
    )
}

@Composable
fun PagerIndicator(size: Int, currentPage: Int) {
    Spacer(modifier = Modifier.fillMaxHeight(0.08f))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        repeat(size) {
            Indicator(isSelected = it == currentPage)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {
    val width = animateDpAsState(
        targetValue = if (isSelected) 25.dp else 10.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing)
    )
    Box(
        modifier = Modifier
            .padding(1.dp)
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                if (isSelected) Color(0xFF495CE8) else Color(0x40495CE8)
            )
    )
}

@Composable
fun BottomSection(
    itemSize : Int ,
    currentPager: Int,
    onNextClicked: () -> Unit,
    onSkipClicked: () -> Unit) {
    val percentage = (currentPager.toFloat() / itemSize.toFloat())

    Column( modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier
                .size(90.dp), contentAlignment = Alignment.Center) {
                CircularProgressBar(radius = 37.dp, percentage = percentage)
                Box(
                    modifier = Modifier
                        .background(color = Color(0xFF495CE8), shape = CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { onNextClicked.invoke() })
                        }
                        .width(60.dp)
                        .height(60.dp), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_arrow_next),
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth(), horizontalArrangement = Arrangement.Center){

            this@Column.AnimatedVisibility(
                visible = currentPager < itemSize,
                enter = fadeIn(animationSpec = tween(600)),
                exit = fadeOut(animationSpec = tween(600))
            ) {
//                ClickableText(
//                    text = AnnotatedString(stringResource(id = R.string.btn_onboarding_skip)) ,
//                    style = TextStyle(color =Color(0xFF848484)),
//                    onClick = {
//                        onSkipClicked.invoke()
//                    })
                TextButton(onClick =  onSkipClicked, contentPadding = PaddingValues(0.dp)) {
                    Text(text = stringResource(id = R.string.btn_onboarding_skip), color = Color(0XFF848484))
                }
            }

        }
    }
}
@Composable
fun CircularProgressBar(
    percentage: Float,
    radius: Dp = 50.dp,
) {
    val progressAnimationValue by animateFloatAsState(
    targetValue = percentage,
    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2) // diameter
    ) {

        Canvas(modifier = Modifier.size(radius * 2)) {
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x40495CE8),
                        Color(0x40495CE8),
                    )
                ),
                startAngle = -145f,
                sweepAngle = 360 * 1f,
                useCenter = false,
                style = Stroke(
                    2.dp.toPx(),
                    cap = StrokeCap.Round,
                ),
            )
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF495CE8),
                        Color(0xFF495CE8),
                    )
                ),
                startAngle = -145f,
                sweepAngle = 360 * progressAnimationValue,
                useCenter = false,
                style = Stroke(
                    5.dp.toPx(),
                    cap = StrokeCap.Round,
                ),
            )

        }

    }
}