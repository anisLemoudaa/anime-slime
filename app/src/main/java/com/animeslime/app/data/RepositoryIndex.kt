package com.animeslime.app.data

import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

data class ExtensionManifest(val pkg:String,val name:String,val lang:String,val version:String,val nsfw:Boolean)
data class RepositoryIndex(val name:String,val extensions:List<ExtensionManifest>)
class RepositoryIndexParser {
 fun parse(body:String):Result<RepositoryIndex> = runCatching {
  val parsed=JSONTokener(body).nextValue()
  val root=parsed as? JSONObject
  val name=root?.optString("name").orEmpty().ifBlank{root?.optString("title").orEmpty()}.ifBlank{"مستودع إضافات"}
  val raw=when(parsed){is JSONArray->parsed;is JSONObject->root?.optJSONArray("extensions")?:root?.optJSONArray("sources")?:root?.optJSONArray("items")?:JSONArray();else->JSONArray()}
  val list=buildList { for(i in 0 until raw.length()){val o=raw.optJSONObject(i)?:continue;val pkg=o.optString("pkg").ifBlank{o.optString("package").ifBlank{o.optString("id")}};if(pkg.isBlank())continue;add(ExtensionManifest(pkg,o.optString("name",pkg),o.optString("lang",o.optString("language","en")),o.optString("version","unknown"),o.optBoolean("nsfw",false))) } }
  require(list.isNotEmpty()){"ملف الفهرس لا يحتوي إضافات صالحة"};RepositoryIndex(name,list)
 }
}
