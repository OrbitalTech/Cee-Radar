package com.orbital.cee.view.authentication.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.orbital.cee.core.countryList
import com.orbital.cee.core.localeToEmoji
import com.orbital.cee.core.searchCountryList
import com.orbital.cee.model.Country

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CountryPickerBottomSheet(
    title: @Composable () -> Unit,
    show: Boolean,
    onItemSelected: (country: Country) -> Unit,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val countries = remember { countryList(context) }
    var selectedCountry by remember { mutableStateOf(countries[0]) }
    var searchValue by remember { mutableStateOf("") }

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    LaunchedEffect(key1 = show) {
        if (show) modalBottomSheetState.show()
        else modalBottomSheetState.hide()
    }

    LaunchedEffect(key1 = modalBottomSheetState.currentValue) {
        if (modalBottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
            onDismissRequest()
        }
    }
    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetContent = {
            title()

            Column(modifier = Modifier.fillMaxHeight().background(color = MaterialTheme.colors.background)) {
                searchValue = countrySearchView(modalBottomSheetState)

                LazyColumn(
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(
                        if (searchValue.isEmpty()) {
                            countries
                        } else {
                            countries.searchCountryList(searchValue)
                        }
                    ) { country ->
                        Row(modifier = Modifier
                            .clickable {
                                selectedCountry = country
                                onItemSelected(selectedCountry)
                            }
                            .padding(12.dp)) {
                            Text(text = localeToEmoji(country.code))
                            Text(
                                text = country.name,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(2f)
                            )
                            Text(
                                text = country.dialCode,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                        }
                        Divider(
                            color = Color.LightGray, thickness = 0.5.dp
                        )
                    }
                }
            }

        }
    ) {
        content()
    }
}




















