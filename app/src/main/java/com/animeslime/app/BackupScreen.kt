package com.animeslime.app
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.animeslime.app.data.BackupManager
import com.animeslime.app.ui.theme.*
import kotlinx.coroutines.launch
@Composable fun BackupScreen(back:()->Unit){val context=androidx.compose.ui.platform.LocalContext.current;val manager=remember{BackupManager(context)};val scope=rememberCoroutineScope();var message by remember{mutableStateOf<String?>(null)};val create=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")){uri:Uri?->if(uri!=null)scope.launch{manager.export(uri).onSuccess{message="تم إنشاء النسخة"}.onFailure{message=it.message}}};val restore=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri:Uri?->if(uri!=null)scope.launch{message=manager.read(uri).fold({"تم التحقق من النسخة: ${it.length} بايت"},{"فشل الاستعادة: ${it.message}"})}};Column(Modifier.fillMaxSize().padding(20.dp)){Text("النسخ الاحتياطي والاستعادة",style=MaterialTheme.typography.headlineSmall);Spacer(Modifier.height(20.dp));Button({create.launch("anime-slime-backup.zip")},Modifier.fillMaxWidth()){Text("إنشاء نسخة احتياطية")};Button({restore.launch(arrayOf("application/zip","application/json"))},Modifier.fillMaxWidth()){Text("استعادة نسخة")};message?.let{Text(it,color=Mint,modifier=Modifier.padding(top=20.dp))};TextButton(back){Text("رجوع")}}}
