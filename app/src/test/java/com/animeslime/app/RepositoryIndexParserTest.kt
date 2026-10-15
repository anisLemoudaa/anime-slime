package com.animeslime.app
import com.animeslime.app.data.RepositoryIndexParser
import org.junit.Assert.*
import org.junit.Test
class RepositoryIndexParserTest{
 @Test fun parsesSupportedIndex(){val result=RepositoryIndexParser().parse("""{"name":"Test","extensions":[{"pkg":"x.test","name":"مصدر","lang":"ar","version":"1","adapter":"animelek","baseUrl":"https://animelek.cc/"}]}""");assertTrue(result.isSuccess);val item=result.getOrThrow().extensions.single();assertEquals("animelek",item.adapter);assertEquals("https://animelek.cc/",item.baseUrl)}
 @Test fun parsesRootArray(){val result=RepositoryIndexParser().parse("""[{"pkg":"x.test","name":"مصدر","adapter":"custom"}]""");assertTrue(result.isSuccess)}
 @Test fun rejectsEmptyIndex(){assertTrue(RepositoryIndexParser().parse("{\"name\":\"x\",\"extensions\":[]}").isFailure)}
}
