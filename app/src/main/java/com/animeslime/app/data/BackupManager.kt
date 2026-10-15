package com.animeslime.app.data

import android.content.Context
import android.net.Uri
import androidx.room.Room
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupManager(private val context:Context){
 suspend fun export(uri:Uri):Result<Unit> = runCatching{val db=Room.databaseBuilder(context,AppDatabase::class.java,"anime_slime.db").build();val root=JSONObject().put("version",1).put("createdAt",System.currentTimeMillis());root.put("repositories",db.repositoryDao().all().first().map{JSONObject().put("name",it.name).put("url",it.url).put("enabled",it.enabled)});context.contentResolver.openOutputStream(uri)!!.use{out->ZipOutputStream(out).use{zip->zip.putNextEntry(ZipEntry("anime-slime.json"));zip.write(root.toString().toByteArray());zip.closeEntry()}}}
 fun read(uri:Uri):Result<String> = runCatching{context.contentResolver.openInputStream(uri)!!.use{input->ZipInputStream(input).use{zip->check(zip.nextEntry?.name=="anime-slime.json"){"ملف النسخة غير صالح"};zip.readBytes().toString(Charsets.UTF_8)}}}
}
