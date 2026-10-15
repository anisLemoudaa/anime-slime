package com.animeslime.app
import com.animeslime.app.data.RepositoryIndexParser
import org.junit.Assert.*
import org.junit.Test
class RepositoryIndexParserTest{
 @Test fun parsesSupportedIndex(){val result=RepositoryIndexParser().parse("""{"name":"Test","extensions":[{"pkg":"x.test","name":"مصدر","lang":"ar","version":"1"}]}""");assertTrue(result.isSuccess);assertEquals(1,result.getOrThrow().extensions.size)}
 @Test fun rejectsEmptyIndex(){assertTrue(RepositoryIndexParser().parse("{\"name\":\"x\",\"extensions\":[]}").isFailure)}
}
