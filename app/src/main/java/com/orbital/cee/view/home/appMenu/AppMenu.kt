package com.orbital.cee.view.home.appMenu

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.orbital.cee.R
import com.orbital.cee.view.home.BottomSheets.LoginRequired
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.appMenu.menuBottomSheets.CeeKerBottomSheet
import com.orbital.cee.view.home.appMenu.menuBottomSheets.ShowErrorMessageInBottomSheet
import com.orbital.cee.view.home.components.DynamicModal
import com.orbital.cee.view.home.components.menu
import com.orbital.cee.view.language.language
import com.orbital.cee.view.sound.sound
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun AppMenu(model : HomeViewModel = viewModel(), onCloseDrawer:() -> Unit,onClickLoginWithPhone:()->Unit){
    val navController = rememberAnimatedNavController()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val speedometerID = model.speedometerId.observeAsState()
    val cursorID = model.cursorId.observeAsState()
    val reportCounts = remember{ mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit){
        reportCounts.value = model.getMyReportCount()
    }
    val bottomSheetId = remember{ mutableStateOf(1) }
    val titleErrorMessage = remember{ mutableStateOf("")  }
    val descriptionErrorMessage = remember{ mutableStateOf("") }

    val isLoading = remember { mutableStateOf(false) }
    val showErrorModal = remember { mutableStateOf(false) }
    val errorModalTitle = remember { mutableStateOf("") }
    val errorModalDescription = remember { mutableStateOf<String?>(null) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_three_dot_loading)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )

    Box(modifier = Modifier.fillMaxSize()){
        ModalBottomSheetLayout(
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetContent = {
                when(bottomSheetId.value){
                    1->{
                        CeeKerBottomSheet(model.userInfo.value, myReportsCount = reportCounts.value,onClickClose = {
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        })
                    }
                    2->{
                        ShowErrorMessageInBottomSheet(title = titleErrorMessage.value, description = descriptionErrorMessage.value) {
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        }

                    }
                    3->{
                        ShowErrorMessageInBottomSheet(icon = R.drawable.ic_isolation_mode,title = titleErrorMessage.value, description = descriptionErrorMessage.value) {
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                        }

                    }
                    4->{
                        LoginRequired(
                            onClickLoginWithGoogle = {
                                isLoading.value = true
                            },onClickLoginWithPhone =onClickLoginWithPhone, onResult = {isSuccess,message->
                                if (isSuccess){
                                    isLoading.value = false
                                    coroutineScope.launch {
                                        modalSheetState.hide()
                                    }
                                }else{
                                    coroutineScope.launch {
                                        modalSheetState.hide()
                                    }
                                    errorModalTitle.value = "Login Failed."
                                    errorModalDescription.value = message
                                    isLoading.value = false
                                    showErrorModal.value = true
                                    // show Error
                                }
                            },
                            bottomNavBar = model.navigationBarHeight.value
                        )
                    }
                }

            },
            sheetState = modalSheetState,
        ) {
            AnimatedNavHost(navController, "home") {
                composable("home") {
                    BackHandler(true) {
                        onCloseDrawer()
                    }
                    menu(model = model, onCloseDrawer = onCloseDrawer,navController= navController, onClickCeeKer = {
                        bottomSheetId.value = 1
                        coroutineScope.launch {
                            modalSheetState.show()
                        }
                    },onClickCreateAccount = {
                        bottomSheetId.value = 4
                        coroutineScope.launch {
                            modalSheetState.show()
                        }
                    })
                }
                composable("general",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    }) {
                    General(model = model) { navController.popBackStack() }
                }
                composable("help",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    }) {
                    help { navController.popBackStack() }
                }
                composable("about",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    }) {
                    About { navController.popBackStack() }
                }
                composable("language",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    }) {
                    language(model) { navController.popBackStack() }
                }
                composable("privacy",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    }) {
                    privacy { navController.popBackStack() }
                }
                composable("sound",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    }) {
                    sound(model) { navController.popBackStack() }
                }
                composable("setting",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    }) {
                    Setting(model) { navController.popBackStack() }
                }
                composable("themes",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    }) {
                    themes(
                        speedometerID = speedometerID.value!!,
                        cursorId = cursorID.value!!,
                        onClickBack = { navController.popBackStack()},
                        onClickUnlockSpeedometer={navController.navigate("subscriptionPlan")},
                        ownSpeedometers = model.userInfo.value.ceedometers,
                        ownCursor = model.userInfo.value.cursor,
                        onClickUnlockHitexSpeedometer = {
                            titleErrorMessage.value = "We are participants in HITEX"
                            descriptionErrorMessage.value = "Visit us at HITEX and enjoy the many gifts we have prepared for you."
                            bottomSheetId.value = 3
                            coroutineScope.launch {
                                modalSheetState.show()
                            } },
                        onSelectedSpeedometer = {id->
                            model.saveSpeedometerId(id)},
                        onSelectedCursor = {id->
                            model.saveCursorId(id)
                            model.initLocationComponent()
                        }
                    )
                }
                composable("subscriptionPlan",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Up,
                            animationSpec = tween(200)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(300)
                        )
                    }) {
                    SubscriptionPlan(onClickClose = {navController.popBackStack()},onSubscriptionError = {title,description ->
                        titleErrorMessage.value = title
                        descriptionErrorMessage.value = description
                        bottomSheetId.value = 2
                        coroutineScope.launch {
                            modalSheetState.show()
                        }
                    })
                }
                composable("AddReward",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Up,
                            animationSpec = tween(200)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(300)
                        )
                    }) {
                    SubscriptionPlan(onClickClose = {navController.popBackStack()},onSubscriptionError = {title,description ->
                        titleErrorMessage.value = title
                        descriptionErrorMessage.value = description
                        bottomSheetId.value = 2
                        coroutineScope.launch {
                            modalSheetState.show()
                        }
                    })
                }
                composable("search",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Up,
                            animationSpec = tween(200)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(300)
                        )
                    }) {
                    SearchForUser(onClickUser = {
                        coroutineScope.launch {
                            model.findUserByUID(it)
                        }
                        model.clickedUserId.value = it
                        navController.navigate("userDetail")
                    },onClickBack = {
                        navController.popBackStack()
                    }, onClickSearch = {
                        coroutineScope.launch {
                            model.findUserByPhoneOrEmail(it)
                        }
                    },userss = model.resultUser, usersFound = model.usersFound)
                }
                composable("userDetail",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Up,
                            animationSpec = tween(200)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(300)
                        )
                    }) {
                    UserDetail(onClickBack = {
                        navController.popBackStack()
                    },userDetail = model.userDetail)
                }
            }
            if (showErrorModal.value) {
                DynamicModal(
                    title = errorModalTitle.value,
                    description = errorModalDescription.value,
                    icon = R.drawable.ic_cee_two,
                    positiveButtonAction = {
                        showErrorModal.value = false
                    },
                    negativeButtonAction = {},
                    positiveButtonText = stringResource(id = R.string.btn_home_alert_done),
                    positiveButtonModifier = Modifier.fillMaxWidth(0.49f),
                )
            }
        }
        if(isLoading.value){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xB2000000))
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {

                        })
                    },
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition,
                    progress,
                    modifier = Modifier.size(65.dp)
                )
            }
        }
    }








}