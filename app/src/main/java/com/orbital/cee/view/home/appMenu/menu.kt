package com.orbital.cee.view.home.components

import android.graphics.*
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.*
//import com.mapbox.maps.CameraOptions
//import com.mapbox.maps.MapView
//import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
//import com.mapbox.maps.plugin.LocationPuck2D
//import com.mapbox.maps.plugin.attribution.attribution
//import com.mapbox.maps.plugin.compass.compass
//import com.mapbox.maps.plugin.gestures.gestures
//import com.mapbox.maps.plugin.locationcomponent.location
//import com.mapbox.maps.plugin.logo.logo
//import com.mapbox.maps.plugin.scalebar.scalebar
import com.orbital.cee.R
import com.orbital.cee.model.UserTiers
import com.orbital.cee.ui.theme.blurple
import com.orbital.cee.ui.theme.light_purple
import com.orbital.cee.utils.MetricsUtils.Companion.getNextLevelRequiredPoints
import com.orbital.cee.utils.Utils
import com.orbital.cee.view.home.HomeViewModel
import com.orbital.cee.view.home.appMenu.About
import com.orbital.cee.view.home.appMenu.General
import com.orbital.cee.view.home.appMenu.Setting
import com.orbital.cee.view.home.appMenu.help
import com.orbital.cee.view.home.appMenu.privacy
import com.orbital.cee.view.language.language
import com.orbital.cee.view.pressClickEffect
import com.orbital.cee.view.sound.sound
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun menu(model : HomeViewModel = viewModel(), onCloseDrawer:() -> Unit,navController: NavController,onClickCeeKer:() -> Unit,onClickCreateAccount:()->Unit) {
    val openDialog = remember{mutableStateOf(false)}
    val isShowSetting = remember{mutableStateOf(false)}
    val isHelp = remember{mutableStateOf(false)}
    val isGeneral = remember{mutableStateOf(false)}
    val isPrivacy = remember{mutableStateOf(false)}
    val isSound = remember{mutableStateOf(false)}
    val isAbout = remember{mutableStateOf(false)}
    val isLanguage = remember{mutableStateOf(false)}
    val context = LocalContext.current
    val conf = LocalConfiguration.current
    val reportCounts = remember{ mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
//    val userType = model.userType.observeAsState()
    val boxWH = (conf.screenWidthDp - 50)/2
    val rotate = if (LocalConfiguration.current.layoutDirection == LayoutDirection.Rtl.ordinal){180f}else{0f}
    LaunchedEffect(Unit){
        model.retrieveStatistics()
        reportCounts.value = model.getMyReportCount()
        model.loadUserInfoFromFirebase().collect{
            if (!it.isSuccess){
                if(it.message != "unauthorized"){
                    onCloseDrawer()
                    Toast.makeText(context,"unable",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
    val composition by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_three_dot_loading)
    )
    val appmenue by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_cee_appmenu)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )
    val progresse by animateLottieCompositionAsState(
        appmenue,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )
    val composition1 by rememberLottieComposition(
        LottieCompositionSpec
            .RawRes(R.raw.lottie_cee_drive_car)
    )
    val progress1 by animateLottieCompositionAsState(
        composition1,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )

    val distance = model.readDistance.observeAsState()
//    val maxSpeed = model.readMaxSpeed.observeAsState()
//    val alertCount = model.readAlertsCount.observeAsState()

Box(
    Modifier
        .fillMaxSize()
//        .pointerInput(Unit) {
//            detectDragGestures(onDrag = { point, offset ->
//                if (offset.x > OFFSET_X && point.position.x < POINT_X) {
//                    navController.popBackStack()
//                }
//            })
//        }
){
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colors.background)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .background(
                        color = MaterialTheme.colors.background,
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {

                        })
                    }
                ,
                horizontalArrangement = Arrangement.Start,//Spacebetwin
                verticalAlignment = Alignment.Bottom
            ) {

//                Button(onClick = { /*TODO*/ },
//                    colors = ButtonDefaults.buttonColors(
//                        backgroundColor = Color.White),
//                    modifier = Modifier
//                        .fillMaxWidth(0.45f)
//                        .height(45.dp)
//                        .clip(shape = RoundedCornerShape(12.dp))
//                        .border(
//                            border = BorderStroke(width = 2.dp, color = Color(0XFFE4E4E4),),
//                            shape = RoundedCornerShape(12.dp)
//                        )) {
//                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            modifier = Modifier.size(22.dp),
//                            painter = painterResource(id = R.drawable.ic_setting),
//                            contentDescription = "", tint = Color(0xFF495CE8))
//
//                        Spacer(modifier = Modifier.width(5.dp))
//                        Text(text = "Settings",letterSpacing = 0.sp, color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
//
//                    }
//                }
//                Spacer(modifier = Modifier.width(10.dp))
//                Button(onClick = { /*TODO*/ },
//                    colors = ButtonDefaults.buttonColors(
//                        backgroundColor = Color.White),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(45.dp)
//                        .clip(shape = RoundedCornerShape(12.dp))
//                        .border(
//                            border = BorderStroke(width = 2.dp, color = Color(0XFFE4E4E4),),
//                            shape = RoundedCornerShape(12.dp)
//                        )) {
//                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            modifier = Modifier.size(22.dp),
//                            painter = painterResource(id = R.drawable.ic_magic_star),
//                            contentDescription = "", tint = Color(0xFF495CE8))
//
//                        Spacer(modifier = Modifier.width(5.dp))
//                        Text(text = "Notifications",letterSpacing = 0.sp, color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
//
//                    }
//                }


                Row(modifier = Modifier
                    .padding(start = 24.dp, bottom = 8.dp)
                    .clickable(indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onCloseDrawer()
//                        navController.popBackStack()
                    }) {
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Icon(modifier = Modifier
                            .size(22.dp)
                            .rotate(rotate), tint = Color(0XFF495CE8) , painter = painterResource(id = R.drawable.ic_arrow_left), contentDescription ="" )
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            LottieAnimation(
                                appmenue,
                                progresse,
                                modifier = Modifier
                                    .size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(modifier = Modifier
                            .size(45.dp)
                            , tint = Color.Black , painter = painterResource(id = R.drawable.txt_cee), contentDescription ="" )
//                        Icon(modifier = Modifier
//                            .size(55.dp),painter = painterResource(id = R.drawable.ic_cee_with_text), contentDescription = "", tint = Color.Unspecified)
                    }

                }
//                IconButton(onClick = {  },enabled = false, modifier = Modifier
//                    .padding(end = 20.dp, bottom = 8.dp)
//                    .border(
//                        width = 1.dp,
//                        shape = RoundedCornerShape(18.dp),
//                        color = Color(0xFFE4E4E4)
//                    )) {
//                    Icon(modifier = Modifier.size(25.dp), tint = Color(0xFFE4E4E4),painter = painterResource(id = R.drawable.ic_bil), contentDescription ="" )
//                }
            }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
            , horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
//            Row(modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//                .padding(20.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically) {
//
//                Column() {
//                    Text(modifier = Modifier.fillMaxWidth(0.7f),text = "${model.userInfo.value.username}", fontSize = 23.sp, fontWeight = FontWeight.Bold, overflow = TextOverflow.Ellipsis)
//                    Spacer(modifier = Modifier.height(5.dp))
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Image(modifier = Modifier.size(30.dp),painter = painterResource(id = R.drawable.cee), contentDescription = "")
//                        Spacer(modifier = Modifier.width(10.dp))
//                        Text(text = "Cee Mark I", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0XFFA7A7A7))
//                    }
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Row(modifier = Modifier.padding(start = 45.dp)) {
//                        Icon(modifier = Modifier
//                            .size(50.dp)
//                            .background(color = Color.Transparent), tint = Color(0XFF495CE8),painter = painterResource(id = R.drawable.ic_user_puck), contentDescription = "")
//                    }
//                }
//            }
            if(model.isGuest()){
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .pressClickEffect()
                    .clickable(onClick = onClickCreateAccount, indication = null,
                        interactionSource = remember { MutableInteractionSource() })
                    .padding(top = 10.dp)
                    .background(color = light_purple, shape = RoundedCornerShape(20.dp))
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.SpaceBetween){
                    Column() {
                        Text(text = stringResource(id = R.string.lbl_menu_hi_guest), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = blurple)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(id = R.string.lbl_menu_create_account),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Box(modifier = Modifier
                            .height(36.dp)
                            .width(50.dp)
                            .background(color = blurple, shape = RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center){
                            Icon(painter = painterResource(id = R.drawable.arrow_right), tint = Color.Unspecified, contentDescription = "")
                        }
                        LottieAnimation(
                            composition1,
                            progress1,
                            modifier = Modifier
                                .height(52.dp)
                                .width(146.dp)
                        )

                    }
                }
            }else{
                Row(modifier = Modifier
                    .pressClickEffect()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { navController.navigate("setting") })
                    .fillMaxWidth()
                    .height(95.dp)
                    .padding(top = 10.dp)
                    .background(color = Color(0xFFF7F7F7), shape = RoundedCornerShape(22.dp)), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .size(75.dp), contentAlignment = Alignment.Center){
                        imageUri?.let {
                            coroutineScope.launch {
                                model.uploadPhotos(it).collect{response ->
                                    if (response.isSuccess){
                                        Toast.makeText(context, response.message,Toast.LENGTH_LONG).show()
                                        imageUri = null
                                    }else{
                                        Toast.makeText(context, response.message,Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            val source = ImageDecoder.createSource(context.contentResolver,it)
                            bitmap.value = ImageDecoder.decodeBitmap(source)
                        }
                        Card(
                            Modifier.size(55.dp),
                            shape = RoundedCornerShape(19.dp),

                            ){
                            SubcomposeAsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = Color(0xFFEEEEEE)),
                                model = "${model.userInfo.value.userAvatar}",
                                loading = {
                                    Box(modifier = Modifier
                                        .fillMaxSize()
                                        .background(color = Color.DarkGray), contentAlignment = Alignment.Center){
                                        LottieAnimation(
                                            composition,
                                            progress,
                                            modifier = Modifier.size(35.dp)
                                        )
                                    }

                                },
                                contentScale = ContentScale.Crop,
                                contentDescription = null
                            )

                            bitmap.value?.let { btm ->
                                Image(bitmap = btm.asImageBitmap() , contentDescription ="",
                                    modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

                            }
                        }
                    }
                    Column{
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Text(text = stringResource(id = R.string.lbl_appMenu_hi), color = Color.Black)
                            Text(text = "${model.userInfo.value.username}", fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(painter = painterResource(id = R.drawable.ic_verified_badge), contentDescription = "",tint = Color.Unspecified)
                        }
                        Text(text = stringResource(id = R.string.lbl_appMenu_editProfile),color = Color(0XFF848484), fontWeight = FontWeight.Medium, fontSize = 13.sp)

                    }
                }
            }

//            Row(modifier = Modifier
//                .pointerInput(Unit) {
//                    detectTapGestures(onTap = {
////                        isShowSetting.value = true
//                        navController.navigate("setting")
//                    })
//                }
//                .fillMaxWidth()
//                .padding(horizontal = 20.dp, vertical = 10.dp)
//                .border(width = 1.dp, color = Color(0xFFE4e4e4), shape = RoundedCornerShape(18.dp))
//                .background(
//                    color = MaterialTheme.colors.background, shape = RoundedCornerShape(18.dp)
//                ), verticalAlignment = Alignment.Top) {
//
//
//                Box(modifier = Modifier.padding(top = 12.dp), contentAlignment = Alignment.TopCenter){
//                    Box(modifier = Modifier.size(70.dp),contentAlignment = Alignment.TopCenter) {
//                        imageUri?.let {
//                            coroutineScope.launch {
//                                model.uploadPhotos(it).collect{response ->
//                                    if (response.isSuccess){
//                                        Toast.makeText(context, response.serverMessage,Toast.LENGTH_LONG).show()
//                                        imageUri = null
//                                    }else{
//                                        Toast.makeText(context, response.serverMessage,Toast.LENGTH_LONG).show()
//                                    }
//                                }
//                            }
//
//                            if (Build.VERSION.SDK_INT < 28){
//                                bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver,it)
//                            }else{
//                                val source = ImageDecoder.createSource(context.contentResolver,it)
//                                bitmap.value = ImageDecoder.decodeBitmap(source)
//                            }
//                        }
//                        Card(
//                            Modifier
//                                .size(65.dp)
//                                .padding(5.dp),
//
//                            shape = RoundedCornerShape(22.dp),
//
//                            ){
//                            SubcomposeAsyncImage(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .background(color = Color(0xFFEEEEEE)),
//                                model = "${model.userInfo.value.userAvatar}",
//                                loading = {
//                                    Box(modifier = Modifier
//                                        .fillMaxSize()
//                                        .background(color = Color.DarkGray), contentAlignment = Alignment.Center){
//                                        LottieAnimation(
//                                            composition,
//                                            progress,
//                                            modifier = Modifier.size(35.dp)
//                                        )
//                                    }
//
//                                },
//                                contentScale = ContentScale.Crop,
//                                contentDescription = null
//                            )
//
//                            bitmap.value?.let { btm ->
//                                Image(bitmap = btm.asImageBitmap() , contentDescription ="",
//                                    modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
//
//                            }
//                        }
//                    }
//                }
//                Column() {
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Row() {
//                        Text(
//                            text = stringResource(id = R.string.lbl_appMenu_hi),
//                            color = Color.Black
//                        )
//                        Text(
//                            text = "${model.userInfo.value.username}",
//                            fontWeight = FontWeight.Bold,
//                            color = Color.Black
//                        )
//                    }
//                    Spacer(modifier = Modifier.height(5.dp))
//                    Text(
//                        text = "Trust Level: ${model.userInfo.value.userLevel ?: 0}",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 16.sp,
//                        color = Color.Black
//                    )
//                    Spacer(modifier = Modifier.height(10.dp))
//                    Row(modifier = Modifier.padding(end = 12.dp)) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .weight(1f)
//                                .height(14.dp)
//                                .clip(CircleShape)
//                                .background(Color(0XFF495CE8).copy(alpha = 0.3f))
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth(
//                                        calculatePointRemainToNextLevelPersint(
//                                            model.userInfo.value.userPoint ?: 0
//                                        )
//                                    )
//                                    .height(14.dp)
//                                    .clip(CircleShape)
//                                    .background(Color(0XFF495CE8))
//                            )
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        text = "+${calculatePointRemainToNextLevel(model.userInfo.value.userPoint ?: 0)} Points to next level",
//                        fontSize = 14.sp,
//                        color = Color(0x4D000000)
//                    )
//                    Spacer(modifier = Modifier.height(13.dp))
//
////                    Text(text = stringResource(id = R.string.lbl_appMenu_editProfile),color = Color(0XFF848484), fontSize = 10.sp)
//                }
//
//            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {

                Box(modifier = Modifier
                    .pressClickEffect()
                    .clickable(
                        onClick = {
                            if (model.isGuest()) {
                                onClickCreateAccount()
                            } else {
                                onClickCeeKer()
                            }
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() })
                    .size((boxWH - 5).dp, boxWH.dp)
                    .background(color = Color(0xFFEEFBEE), shape = RoundedCornerShape(22.dp))){
                    Column(modifier = Modifier.fillMaxSize(),verticalArrangement = Arrangement.SpaceBetween) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), horizontalArrangement = Arrangement.Start) {
                            Icon(painter = painterResource(id = R.drawable.ic_diamond), contentDescription = "", tint = Color.Unspecified)
                        }
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)) {
                            Column() {
                                Text(text =if(model.isGuest()) {"Guest"}else{"Ceeker"}, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF171729))
                                Spacer(modifier = Modifier.height(4.dp))
                                progressRatioCeeKer(model.userInfo.value.userPoint ?: 0,getNextLevelRequiredPoints(model.userInfo.value.userPoint ?: 0))
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
                                    if(model.isGuest()){
                                        Text(text = "Locked", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF171729))
                                    }else{
                                        Text(text = "${model.userInfo.value.userPoint ?: 0}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF171729))
                                        Text(text = "${getNextLevelRequiredPoints(model.userInfo.value.userPoint ?: 0)}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF171729))

                                    }
                                }
                            }
                        }

                    }

                }
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier
                    .size(boxWH.dp)
                    .pressClickEffect()
                    .clickable(
                        onClick = {
                            if (model.isGuest()) {
                                onClickCreateAccount()
                            } else {
                                navController.navigate("themes")
                            }

                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() })
                    .background(color = Color(0xFFECEEFD), shape = RoundedCornerShape(22.dp))){

                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)) {
                                Icon(painter = painterResource(id = R.drawable.ic_brush), contentDescription = "", tint = Color.Unspecified)

                            }
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)) {
                                Column() {
                                    Text(text = "Explore", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF495CE8))
                                    Text(text = "Cee Themes", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF171729))
                                }

                            }
                        }

                }

//                Box(modifier = Modifier
//                    .size(boxWH.dp)
//                    .border(
//                        width = 1.dp,
//                        color = Color(0xFFE4E4E4),
//                        shape = RoundedCornerShape(18.dp)
//                    ), contentAlignment = Alignment.BottomStart){
//                    Image(modifier = Modifier.fillMaxSize(),painter = painterResource(id = if(model.isDarkMode.value) R.drawable.map_dark else R.drawable.map_light ), contentDescription ="", contentScale = ContentScale.FillBounds )
////                    AndroidView(modifier = Modifier
////                        .fillMaxSize()
////                        .clip(RoundedCornerShape(10.dp)),factory = {
////                        View.inflate(it, R.layout.main_mapview_mapbox, null)
////                        MapView(it).apply {
////                            getMapboxMap().setCamera(
////                                CameraOptions.Builder().center(Point.fromLngLat(model.lastLocation.value.longitude,model.lastLocation.value.latitude)).zoom(15.0).build()
////                            )
////                            getMapboxMap().loadStyleUri("mapbox://styles/orbital-cee/cl5wwmzcw000814qoaa1zkqrw") {sty ->
////                                if (!Permissions.hasLocationPermission(context)) {
////                                    model.isLocationNotAvailable.value = true
////                                    Permissions.requestsLocationPermission(context)
////                                } else {
////                                    if (model.checkDeviceLocationSettings(context)) {
////                                        model.initLocationComponent()
////                                        model.setupGesturesListener()
////                                    } else {
////                                        model.isLocationNotAvailable.value = true
////                                    }
////                                }
////                            }
////                            attribution.enabled = false
////                            logo.enabled = false
////                            scalebar.enabled = false
////                            compass.enabled = false
////                            gestures.scrollEnabled = false
////                            gestures.pitchEnabled = false
////
////                            val locationComponentPlugin =  this.location
////                            locationComponentPlugin.updateSettings {
////                                this.enabled = true
////                                this.pulsingEnabled = true
////                                this.pulsingColor = R.color.secondary
////                                this.locationPuck = LocationPuck2D(
////                                    bearingImage = AppCompatResources.getDrawable(
////                                        context,
////                                        R.drawable.ic_user_puck_new,
////                                    ),
//////                                    shadowImage = AppCompatResources.getDrawable(
//////                                        context,
//////                                        R.drawable.user_puck_shadow_new,
//////                                    ),
////                                    scaleExpression = interpolate {
////                                        linear()
////                                        zoom()
////                                        stop {
////                                            literal(0.0)
////                                            literal(0.6)
////                                        }
////                                        stop {
////                                            literal(20.0)
////                                            literal(1.0)
////                                        }
////                                    }.toJson()
////                                )
////                            }
//////                            locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
//////                            locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
////
////
////                        }
////                    })
//                    Box(modifier = Modifier
//                        .width(70.dp)
//                        .height(45.dp)
//                        .padding(8.dp), contentAlignment = Alignment.Center){
//                        switchButton(model.isDarkMode, isUserAdmin =(model.userType.value ==  2),onClick =  {
//                            if(model.userType.value ==  2){
//                                model.isDarkMode.value = !model.isDarkMode.value
//                            }
//                        })
//                    }
//
//                }
//                Spacer(modifier = Modifier.width(10.dp))
//                Box(modifier = Modifier
//                    .size(boxWH.dp)
//                    .border(
//                        width = 1.dp,
//                        color = Color(0xFFE4E4E4),
//                        shape = RoundedCornerShape(18.dp)
//                    )
//                    .background(
//                        color = if (model.isDarkMode.value) Color(0xFF2C2E35) else Color(
//                            0xFFECEEFD
//                        ), shape = RoundedCornerShape(18.dp)
//                    ), contentAlignment = Alignment.Center){
//                    Column( horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
//                        Box(contentAlignment = Alignment.Center){
//                            Image(modifier = Modifier
//                                .rotate(-45f)
//                                .size(40.dp),painter = painterResource(id = R.drawable.ic_light_cursor_t1), contentDescription ="" )
////                            Icon(painter = painterResource(id = R.drawable.user_puck_shadow_new), contentDescription ="" , tint = Color.Unspecified)
////                            Icon(modifier = Modifier.rotate(-45f),painter = painterResource(id = R.drawable.ic_user_puck_new), contentDescription ="" , tint = Color.Unspecified)
//                        }
//                        Spacer(modifier = Modifier.height(5.dp))
//                        Text(text = "MARK II")
//
//                    }
//
//                }
            }
            //Divider(color = Color(0XFFE4E4E4), thickness = 1.dp)
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)) {
                Text(text = stringResource(R.string.lbl_appMenu_statistic_title), fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .height(64.dp)
                            .fillMaxWidth(0.48f)
                            .background(
                                color = Color(0xffEBF8FF),
                                shape = RoundedCornerShape(14.dp)
                            )
                    ){
                        Row(modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(id = R.drawable.ic_gauge), contentDescription = "", tint = Color.Unspecified)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column() {
                                Text(text = "${model.userStatistics.value?.maxSpeed}",fontWeight = FontWeight.SemiBold,  color = Color(0xFF171729), fontSize = 14.sp)
                                Text(text = stringResource(R.string.lbl_appMenu_statistic_max_speed), color = Color(0x80000000), fontSize = 12.sp)
                            }
                            
                        }

                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .height(64.dp)
                            .fillMaxWidth()
                            .background(
                                color = Color(0xffFDEDEB),
                                shape = RoundedCornerShape(14.dp)
                            )
                    ){
                        Row(modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(id = R.drawable.ic_alerted_person), contentDescription = "", tint = Color.Unspecified)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column() {
                                Text(text = "${model.userStatistics.value?.alertedCount}",fontWeight = FontWeight.SemiBold,  color = Color(0xFF171729), fontSize = 14.sp)
                                Text(text = stringResource(R.string.lbl_appMenu_statistic_alert_count), color = Color(0x80000000), fontSize = 12.sp)
                            }

                        }
                    }
//                    Button(onClick = { /*TODO*/ },
//                        colors = ButtonDefaults.buttonColors(
//                            backgroundColor = MaterialTheme.colors.background),
//                        modifier = Modifier
//                            .fillMaxWidth(0.48f)
//                            .height(55.dp)
//                            .clip(shape = RoundedCornerShape(10.dp))
//                            .border(
//                                border = BorderStroke(width = 1.dp, color = Color(0XFFE4E4E4),),
//                                shape = RoundedCornerShape(10.dp)
//                            )) {
//                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                            Row(modifier = Modifier.fillMaxWidth()) {
//                                Icon(
//                                    modifier = Modifier.size(20.dp),
//                                    painter = painterResource(id = R.drawable.ic_red_car),
//                                    contentDescription = "", tint = Color.Red)
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Column() {
//                                    Text(text = "${model.userStatistics.value?.alertedCount}", fontSize = 12.sp, fontWeight = FontWeight.Bold,color=Color.Black, lineHeight = 0.2.sp)
//                                    Text(text = stringResource(R.string.lbl_appMenu_statistic_alert_count),fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold,lineHeight = 0.2.sp)
//                                }
//                            }
//                            //Icon(modifier = Modifier.size(20.dp), tint = Color(0XFFE4E4E4),painter = painterResource(id = R.drawable.ic_arrow), contentDescription = "")
//                        }
//
//                    }
//                    Spacer(modifier = Modifier.width(10.dp))
//                    Button(onClick = { /*TODO*/ },
//                        colors = ButtonDefaults.buttonColors(
//                            backgroundColor = MaterialTheme.colors.background),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(55.dp)
//                            .clip(shape = RoundedCornerShape(10.dp))
//                            .border(
//                                border = BorderStroke(width = 1.dp, color = Color(0XFFE4E4E4),),
//                                shape = RoundedCornerShape(10.dp)
//                            )) {
//                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                            Row(modifier = Modifier.fillMaxWidth()) {
//                                Icon(
//                                    modifier = Modifier.size(20.dp),
//                                    painter = painterResource(id = R.drawable.ic_problem),
//                                    contentDescription = "", tint = Color(0XFFFECD5C))
//
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Column() {
//                                    Text(text = "${reportCounts.value}", fontSize = 12.sp, fontWeight = FontWeight.Bold,color=Color.Black, letterSpacing = 0.sp)
//                                    Text(text = stringResource(R.string.lbl_appMenu_statistic_report_count),letterSpacing = 0.sp, color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
//                                }
//                            }
//                           // Icon(modifier = Modifier.size(18.dp), tint = Color(0XFFE4E4E4),painter = painterResource(id = R.drawable.ic_arrow), contentDescription = "")
//                        }
//                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .height(64.dp)
                            .fillMaxWidth(0.48f)
                            .background(
                                color = Color(0x19FECD5C),
                                shape = RoundedCornerShape(14.dp)
                            )
                    ){
                        Row(modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(id = R.drawable.ic_danger), contentDescription = "", tint = Color.Unspecified)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column() {
                                Text(text = "${reportCounts.value}",fontWeight = FontWeight.SemiBold,  color = Color(0xFF171729), fontSize = 14.sp)
                                Text(text = stringResource(R.string.lbl_appMenu_statistic_report_count), color = Color(0x80000000), fontSize = 12.sp)
                            }

                        }

                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .height(64.dp)
                            .fillMaxWidth()
                            .background(
                                color = Color(0xffECF9F7),
                                shape = RoundedCornerShape(14.dp)
                            )
                    ){
                        Row(modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(id = R.drawable.ic_route_p_to_p), contentDescription = "", tint = Color.Unspecified)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column() {
                                Text(text = df.format(((distance.value?.times(10000.0))?.roundToInt() ?: 1).div(10000.0)),fontWeight = FontWeight.SemiBold,  color = Color(0xFF171729), fontSize = 14.sp)
                                Text(text = stringResource(R.string.lbl_appMenu_statistic_distance), color = Color(0x80000000), fontSize = 12.sp)
                            }
                        }
                    }
                }




//                Row(modifier = Modifier.fillMaxWidth()) {
//                    Button(onClick = { /*TODO*/ },
//                        colors = ButtonDefaults.buttonColors(
//                            backgroundColor = MaterialTheme.colors.background),
//                        modifier = Modifier
//                            .fillMaxWidth(0.48f)
//                            .height(55.dp)
//                            .clip(shape = RoundedCornerShape(10.dp))
//                            .border(
//                                border = BorderStroke(width = 1.dp, color = Color(0XFFE4E4E4),),
//                                shape = RoundedCornerShape(10.dp)
//                            )) {
//                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                            Row(modifier = Modifier.fillMaxWidth()) {
//                                Icon(
//                                    modifier = Modifier.size(20.dp),
//                                    painter = painterResource(id = R.drawable.ic_noun_gauge),
//                                    contentDescription = "", tint = Color(0XFF495CE8))
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Column() {
//                                    Text(text = "${model.userStatistics.value?.maxSpeed}", fontSize = 12.sp, fontWeight = FontWeight.Bold,color=Color.Black)
//                                    Text(text = stringResource(R.string.lbl_appMenu_statistic_max_speed),fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
//                                }
//                            }
//                            //Icon(modifier = Modifier.size(20.dp), tint = Color(0XFFE4E4E4),painter = painterResource(id = R.drawable.ic_arrow), contentDescription = "")
//                        }
//
//                    }
//                    Spacer(modifier = Modifier.width(10.dp))
//                    Button(onClick = { /*TODO*/ },
//                        colors = ButtonDefaults.buttonColors(
//                            backgroundColor = MaterialTheme.colors.background),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(55.dp)
//                            .clip(shape = RoundedCornerShape(10.dp))
//                            .border(
//                                border = BorderStroke(width = 1.dp, color = Color(0XFFE4E4E4),),
//                                shape = RoundedCornerShape(10.dp)
//                            )) {
//                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                            Row(modifier = Modifier.fillMaxWidth()){
//                                Icon(
//                                    modifier = Modifier.size(20.dp),
//                                    painter = painterResource(id = R.drawable.ic_route),
//                                    contentDescription = "", tint = Color(0XFF57D654))
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Column() {
//                                    Text(text = "${df.format(((distance.value?.times(10000.0))?.roundToInt() ?: 1).div(10000.0)) } ", fontSize = 12.sp, fontWeight = FontWeight.Bold,color=Color.Black, letterSpacing = 0.sp)
//                                    Text(text = stringResource(R.string.lbl_appMenu_statistic_distance),letterSpacing = 0.sp, color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
//                                }
//                            }
//                           // Icon(modifier = Modifier.size(18.dp), tint = Color(0XFFE4E4E4),painter = painterResource(id = R.drawable.ic_arrow), contentDescription = "")
//                        }
//
//                    }
//
//                }
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount -> }
                }) {
                Text(text = stringResource(id = R.string.lbl_appMenu_alertmeFrom),fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(10.dp))
                val rad = model.geofenceRadius.observeAsState()
                val disEnabledValue by remember { mutableStateOf(false) }
                val disValues by remember { mutableStateOf(valuesListt()) }
                val disSteps by remember { mutableStateOf(0) }
                var radd  by  remember { mutableStateOf(0f) }
                val disValueRange: ClosedFloatingPointRange<Float>? by remember { mutableStateOf(null) }
                val disTutorialEnabled by remember { mutableStateOf(false) }
                val disInteractionSource = remember {
                    MutableInteractionSource()
                }
                LaunchedEffect(Unit){
                    radd = rad.value!!.toFloat()
                }

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    SunriseSlider(
                        value = radd,
                        onValueChange = { float: Float ->
                            radd = float
                        },
                        valueRangeParam = disValueRange,
                        values = disValues,
                        steps = disSteps,
                        interactionSource = disInteractionSource,
                        enabled = disEnabledValue,
                        tutorialEnabled = disTutorialEnabled,
                        onValueChangeFinished = {
                            model.saveGeofenceRadius(radd.toInt())
                        },
                        colors = sunriseSliderColorsDefaultt(),
                        isRtl = false
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "200m", fontSize = 10.sp, fontWeight = FontWeight.W500, color =if(model.geofenceRadius.value == 200){Color(0XFF495CE8)}else{Color(0xFF848484)} )
                        Text(text = "300m", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(model.geofenceRadius.value == 300){Color(0XFF495CE8)}else{Color(0xFF848484)})
                        Text(text = "400m", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(model.geofenceRadius.value == 400){Color(0XFF495CE8)}else{Color(0xFF848484)})
                        Text(text = "500m", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(model.geofenceRadius.value == 500){Color(0XFF495CE8)}else{Color(0xFF848484)})
                        Text(text = "600m", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(model.geofenceRadius.value == 600){Color(0XFF495CE8)}else{Color(0xFF848484)})
                        Text(text = "700m", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(model.geofenceRadius.value == 699){Color(0XFF495CE8)}else{Color(0xFF848484)})
                        Text(text = "800m", fontSize = 10.sp, fontWeight = FontWeight.W500, color = if(model.geofenceRadius.value == 800){Color(0XFF495CE8)}else{Color(0xFF848484)})
                    }
                }

            }
            
            //Divider(color = Color(0XFFE4E4E4), thickness = 1.dp)
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)) {
                Text(text = stringResource(R.string.lbl_setting_appBar_title), fontWeight = FontWeight.SemiBold, color = Color(0xFF171729), fontSize = 20.sp)

            }
//            Box(modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 20.dp)
//                .height(height = if (model.userType.value == 2) 155.dp else 103.dp)
//                .border(
//                    border = BorderStroke(width = 1.dp, color = Color(0XFFE4E4E4)),
//                    shape = RoundedCornerShape(10.dp)
//                )){
//                Column(modifier = Modifier.fillMaxSize()) {
//                    if(model.userType.value == 2){
//                        Row(modifier = Modifier
//                            .fillMaxWidth()
//                            .pointerInput(Unit) {
//                                detectTapGestures(onTap = {
////                                isGeneral.value = true
//                                    navController.navigate("general")
//                                })
//                            }
//                            .padding(horizontal = 15.dp)
//                            .height(50.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                            Text(text = "General", style = MaterialTheme.typography.body1)
//                            Icon(modifier = Modifier
//                                .size(20.dp)
//                                .rotate(rotate), tint = Color(0XFF848484),painter = painterResource(id = R.drawable.ic_arrow), contentDescription = "")
//
//                        }
//                        Divider(color = Color(0XFFE4E4E4), thickness = 1.dp)
//                    }
//
//                    Row(modifier = Modifier
//                        .pointerInput(Unit) {
//                            detectTapGestures(onTap = {
//                                navController.navigate("language")
//                            })
//                        }
//                        .fillMaxWidth()
//                        .padding(horizontal = 15.dp)
//                        .height(50.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                        Text(text =stringResource(R.string.lbl_setting_general_language),style = MaterialTheme.typography.body1)
//                        Icon(modifier = Modifier
//                            .size(20.dp)
//                            .rotate(rotate), tint = Color(0XFF848484),painter = painterResource(id = R.drawable.ic_arrow), contentDescription = "")
//
//                    }
//
//                    Divider(color = Color(0XFFE4E4E4), thickness = 1.dp)
//                    Row(modifier = Modifier
//                        .pointerInput(Unit) {
//                            detectTapGestures(onTap = {
//                                navController.navigate("sound")
////                                isSound.value = true
//                            })
//                        }
//                        .fillMaxWidth()
//                        .height(50.dp)
//                        .padding(horizontal = 15.dp)
//                        , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                        Text(text =stringResource(R.string.lbl_setting_general_sound_setting),style = MaterialTheme.typography.body1)
//                        Icon(modifier = Modifier
//                            .size(20.dp)
//                            .rotate(rotate), tint = Color(0XFF848484),painter = painterResource(id = R.drawable.ic_arrow), contentDescription = "")
//
//                    }
//                }
//            }

            if(model.currentUserTier.value == UserTiers.ADMIN){
                Row(modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            navController.navigate("search")
                        })
                    }
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(color = Color(0xFFF7F7F7), shape = RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Search",fontWeight = FontWeight.Medium, color = Color(0xFF171729), fontSize = 14.sp)
                    Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "",tint = Color(0xFF171729), modifier = Modifier.rotate(rotate))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Row(modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        navController.navigate("General")
                    })
                }
                .fillMaxWidth()
                .height(54.dp)
                .background(color = Color(0xFFF7F7F7), shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "General",fontWeight = FontWeight.Medium, color = Color(0xFF171729), fontSize = 14.sp)
                Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "",tint = Color(0xFF171729), modifier = Modifier.rotate(rotate))
            }
            Spacer(modifier = Modifier.height(12.dp))


            Row(modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        navController.navigate("language")
                    })
                }
                .fillMaxWidth()
                .height(54.dp)
                .background(color = Color(0xFFF7F7F7), shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.lbl_setting_general_language),fontWeight = FontWeight.Medium, color = Color(0xFF171729), fontSize = 14.sp)
                    Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "",tint = Color(0xFF171729), modifier = Modifier.rotate(rotate))
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        navController.navigate("sound")
                    })
                }
                .fillMaxWidth()
                .height(54.dp)
                .background(color = Color(0xFFF7F7F7), shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(R.string.lbl_setting_general_sound_setting),fontWeight = FontWeight.Medium, color = Color(0xFF171729), fontSize = 14.sp)
                    Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "",tint = Color(0xFF171729), modifier = Modifier.rotate(rotate))
            }
            Spacer(modifier = Modifier.height(12.dp))




            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 10.dp)) {
                Text(text = stringResource(R.string.lbl_appMenu_support_title), fontWeight = FontWeight.SemiBold, color = Color(0xFF171729), fontSize = 20.sp)

            }

            Row(modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        navController.navigate("about")
                    })
                }
                .fillMaxWidth()
                .height(54.dp)
                .background(color = Color(0xFFF7F7F7), shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.lbl_setting_general_about),fontWeight = FontWeight.Medium, color = Color(0xFF171729), fontSize = 14.sp)
                Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "",tint = Color(0xFF171729), modifier = Modifier.rotate(rotate))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        navController.navigate("help")
                    })
                }
                .fillMaxWidth()
                .height(54.dp)
                .background(color = Color(0xFFF7F7F7), shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.btn_appMenu_support_help),fontWeight = FontWeight.Medium, color = Color(0xFF171729), fontSize = 14.sp)
                Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "",tint = Color(0xFF171729), modifier = Modifier.rotate(rotate))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        navController.navigate("privacy")
                    })
                }
                .fillMaxWidth()
                .height(54.dp)
                .background(color = Color(0xFFF7F7F7), shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.btn_appMenu_support_privacy),fontWeight = FontWeight.Medium, color = Color(0xFF171729), fontSize = 14.sp)
                Icon(painter = painterResource(id = R.drawable.ic_arrow_right), contentDescription = "",tint = Color(0xFF171729), modifier = Modifier.rotate(rotate))
            }
            Spacer(modifier = Modifier.height(12.dp))

//            Box(modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 20.dp)
//                .height(155.dp)
//                .border(
//                    border = BorderStroke(width = 1.dp, color = Color(0XFFE4E4E4)),
//                    shape = RoundedCornerShape(10.dp)
//                )){
//                Column(modifier = Modifier.fillMaxSize()) {
//                    Row(modifier = Modifier
//                        .fillMaxWidth()
//                        .pointerInput(Unit) {
//                            detectTapGestures(onTap = {
//                                navController.navigate("about")
////                                isAbout.value = true
//                            })
//                        }
//                        .padding(horizontal = 15.dp)
//                        .height(50.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                        Text(text = stringResource(R.string.lbl_setting_general_about), style = MaterialTheme.typography.body1)
//                        Icon(modifier = Modifier
//                            .size(20.dp)
//                            .rotate(rotate), tint = Color(0XFF848484),painter = painterResource(id = R.drawable.ic_arrow), contentDescription = "")
//
//                    }
//                    Divider(color = Color(0XFFE4E4E4), thickness = 1.dp)
//                    Row(modifier = Modifier
//                        .pointerInput(Unit) {
//                            detectTapGestures(onTap = {
//                                navController.navigate("help")
////                                isHelp.value = true
//                            })
//                        }
//                        .fillMaxWidth()
//                        .padding(horizontal = 15.dp)
//                        .height(50.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                        Text(text = stringResource(R.string.btn_appMenu_support_help), style = MaterialTheme.typography.body1)
//                        Icon(modifier = Modifier
//                            .size(20.dp)
//                            .rotate(rotate), tint = Color(0XFF848484),painter = painterResource(id = R.drawable.ic_arrow), contentDescription = "")
//
//                    }
//                    Divider(color = Color(0XFFE4E4E4), thickness = 1.dp)
//                    Row(modifier = Modifier
//                        .pointerInput(Unit) {
//                            detectTapGestures(onTap = {
////                                isPrivacy.value = true
//                                navController.navigate("privacy")
//                            })
//                        }
//                        .fillMaxWidth()
//                        .height(50.dp)
//                        .padding(horizontal = 15.dp)
//                        , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//                        Text(text =stringResource(R.string.btn_appMenu_support_privacy),style = MaterialTheme.typography.body1)
//                        Icon(modifier = Modifier
//                            .size(20.dp)
//                            .rotate(rotate), tint = Color(0XFF848484),painter = painterResource(id = R.drawable.ic_arrow), contentDescription = "")
//
//                    }
//                }
//            }
//            Column(modifier = Modifier
//                .fillMaxWidth()
//                .padding(20.dp)) {
//                Text(text = stringResource(R.string.lbl_appMenu_support_title), fontSize = 20.sp, fontWeight = FontWeight.Bold)
//                Spacer(modifier = Modifier.height(20.dp))
//
//                Row(modifier = Modifier.pointerInput(Unit){
//                    detectTapGestures (onTap = {
//                        isHelp.value = true
//                    })
//                }) {
//                    Icon(modifier = Modifier.size(25.dp), tint = Color(0XFFABAAAC),painter = painterResource(id = R.drawable.ic_help), contentDescription = "")
//                    Spacer(modifier = Modifier.width(10.dp))
//                    Text(text =  stringResource(R.string.lbl_FAQ_appBar_title))
//                }
//                Spacer(modifier = Modifier.height(15.dp))
//
//                Row(modifier = Modifier.pointerInput(Unit){
//                    detectTapGestures (onTap = {
//                        val request = manager.requestReviewFlow()
//                        request.addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                // We got the ReviewInfo object
//                                val reviewInfo = task.result
//                                val flow = manager.launchReviewFlow(activity, reviewInfo)
//                                flow.addOnCompleteListener { _ ->
//                                    // The flow has finished. The API does not indicate whether the user
//                                    // reviewed or not, or even whether the review dialog was shown. Thus, no
//                                    // matter the result, we continue our app flow.
//                                }
//                            } else {
//                                // There was some problem, log or handle the error code.
//                                Toast.makeText(context, task.exception?.message ?: "Error",Toast.LENGTH_LONG).show()
//                                //@ReviewErrorCode val reviewErrorCode = (task.getException() as TaskException).errorCode
//                            }
//                        }
//                    })
//                }) {
//                    Icon(modifier = Modifier.size(25.dp), tint = Color(0XFFABAAAC),painter = painterResource(id = R.drawable.ic_star), contentDescription = "")
//                    Spacer(modifier = Modifier.width(10.dp))
//                    Text(text = "Rate Us")
//                }
////                Spacer(modifier = Modifier.height(15.dp))
////                Row() {
////                    Icon(modifier = Modifier.size(25.dp),tint = Color(0XFFABAAAC),painter = painterResource(id = R.drawable.ic_support), contentDescription = "")
////                    Spacer(modifier = Modifier.width(10.dp))
////                    Text(text = "Customer Service")
////                }
//                Spacer(modifier = Modifier.height(15.dp))
//                Row(modifier = Modifier.pointerInput(Unit){
//                    detectTapGestures(onTap = {
//                        isPrivacy.value = true
//                    })
//                }) {
//                    Icon(modifier = Modifier.size(25.dp),tint = Color(0XFFABAAAC),painter = painterResource(id = R.drawable.ic_privacy), contentDescription = "")
//                    Spacer(modifier = Modifier.width(10.dp))
//                    Text(text = stringResource(R.string.lbl_privacy_appBar_title))
//                }
//                Spacer(modifier = Modifier.height(15.dp))
//                Row(modifier = Modifier.pointerInput(Unit){
//                    detectTapGestures(onTap = {
//                        openDialog.value = true
//                    })
//                }) {
//                    Icon(modifier = Modifier.size(25.dp),tint = Color(0XFFABAAAC),painter = painterResource(id = R.drawable.ic_sign_out), contentDescription = "")
//                    Spacer(modifier = Modifier.width(10.dp))
//                    Text(text = stringResource(R.string.btn_appMenu_support_sign_out), color = Color.Black)
//                }
//                Spacer(modifier = Modifier.height(50.dp))
//            }
//            Spacer(modifier = Modifier.height(10.dp))
//
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = { openDialog.value = true }) {
                    Text(text = stringResource(R.string.btn_appMenu_support_sign_out), color = Color(0xFFEA4E34))
                }
            }
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "VERSION:${Utils.currentVersion(context)} Build(${Utils.buildNumber(context)})", fontSize = 7.sp, fontWeight = FontWeight.W800, color = Color(0xFFC4C4C4))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Copyright © 2022 Cee. All right reserved", fontSize = 7.sp, fontWeight = FontWeight.W600, color = Color(0xFFC4C4C4))
            }
            Spacer(modifier = Modifier.height(55.dp))

        }
        if (openDialog.value){
            DynamicModal(
                icon = R.drawable.ic_cee_two,
                title = stringResource(R.string.lbl_appMenu_alert_title),
                positiveButtonText = stringResource(R.string.btn_home_alert_reportConfirmation_no),
                negativeButtonText = stringResource(R.string.btn_home_alert_reportConfirmation_yes),
                positiveButtonAction = {
                    openDialog.value = false
                },
                negativeButtonAction = {
                    coroutineScope.launch {
                        model.logout(context = context)
                    }
                },
                positiveButtonModifier = Modifier
                    .fillMaxWidth(0.49f),
                negativeButtonModifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(8.dp)
                    ),
            )
//            showCustomDialog(
//                onNegativeClick = {
//                    openDialog.value = false
//                },
//                onPositiveClick = {
//                    coroutineScope.launch {
//                        model.logout(context = context)
//                    }
//                },
//                title =stringResource(R.string.lbl_appMenu_alert_title),
//                buttonPositiveText = stringResource(R.string.btn_home_alert_reportConfirmation_yes),
//                buttonNegativeText = stringResource(R.string.btn_home_alert_reportConfirmation_no)
//            )
        }
    }



    }
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        visible = isShowSetting.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Setting(model = model){
//            isShowSetting.value = false
            navController.popBackStack()
        }
    }
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        visible = isGeneral.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        General(model = model){
//            isGeneral.value = false
            navController.popBackStack()
        }
    }
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        visible = isHelp.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        help(){
//            isHelp.value = false
            navController.popBackStack()
        }
    }
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        visible = isPrivacy.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        privacy(){
//            isPrivacy.value = false
            navController.popBackStack()
        }
    }
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        visible = isSound.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        sound(model){
//            isSound.value = false
            navController.popBackStack()
        }
    }
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        visible = isAbout.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        About(){
//            isAbout.value = false
            navController.popBackStack()
        }
    }
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        visible = isLanguage.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        language(model){
//            isLanguage.value = false
            navController.popBackStack()
        }
    }

}
fun initLocationComponent() {

}
//@Preview(showBackground = true)
//@Composable
//fun defaultPreview(){
//    //menu()
//
//}

fun valuesListt() = listOf(200f, 300f, 400f, 500f, 600f, 700f, 800f)

fun sunriseSliderColorsDefaultt() = SunriseSliderColors(
    thumbColor = Color(0xFF495CE8),
    thumbDisabledColor = Color(0xFF495CE8),
    inThumbColor = Color(0xFF495CE8),
    trackBrush = Brush.horizontalGradient(
        listOf(
            Color(0xFF495CE8), Color(0xFF495CE8)
        ),
        tileMode = TileMode.Clamp
    ),
    inactiveTrackColor = Color(0xFFD9D9D9),
    tickActiveColor = Color(0xFF495CE8),
    tickInactiveColor = Color(0xFFD9D9D9)
)