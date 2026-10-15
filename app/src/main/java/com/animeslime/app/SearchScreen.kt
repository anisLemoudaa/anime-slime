package com.animeslime.app
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.animeslime.app.ui.SearchViewModel
import com.animeslime.app.ui.theme.*
@Composable fun SearchScreen(back:()->Unit,open:(String)->Unit){val vm:SearchViewModel=hiltViewModel();val state by vm.state.collectAsState();var q by remember{mutableStateOf("")};Column(Modifier.fillMaxSize().background(Ink)){Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically){IconButton(back){Icon(Icons.Default.ArrowForward,"رجوع")};OutlinedTextField(q,{q=it},Modifier.weight(1f),singleLine=true,label={Text("ابحث في المصادر")});Spacer(Modifier.width(8.dp));Button({vm.search(q)},enabled=q.isNotBlank()){Text("بحث")}};if(state.loading)LinearProgressIndicator(Modifier.fillMaxWidth());state.error?.let{Text(it,color=MaterialTheme.colorScheme.error,modifier=Modifier.padding(20.dp))};LazyColumn{items(state.results){r->ListItem(headlineContent={Text(r.title)},supportingContent={Text(r.url,color=Muted)},modifier=Modifier.padding(horizontal=12.dp).fillMaxWidth(),leadingContent={Text("AN",color=Mint)},trailingContent={TextButton({open(r.url)}){Text("تفاصيل")}})}}}}
