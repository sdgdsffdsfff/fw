package fw.test.unit;

import java.util.List;

import junit.framework.TestCase;

import com.hunantv.fw.exceptions.RouteDefineException;
import com.hunantv.fw.route2.Route;
import com.hunantv.fw.route2.Route.HttpMethod;
import com.hunantv.fw.utils.StringUtil;

public class RouteTest extends TestCase {

	private void p(String s) {
		System.out.println(String.format("*************** %s ***************", s));
	}

	public void testBaseConstructor() throws Exception {
		Route route = Route.get("/save", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals("/save/", route.getUriReg());
		assertEquals(ControllerForTestRoute.class, route.getController());
		assertEquals(ControllerForTestRoute.class.getMethod("save"), route.getAction());
		assertTrue(route.isStaticRule());
	}

	public void testBaseConstructor2() throws Exception {
		Route route = Route.get("/save/suffix", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals("/save/suffix/", route.getUriReg());
		assertEquals(ControllerForTestRoute.class, route.getController());
		assertEquals(ControllerForTestRoute.class.getMethod("save"), route.getAction());
		assertTrue(route.isStaticRule());
	}

	public void testIntRegConstructor() throws Exception {
		Route route = Route.get("/save/<int:age>", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals("/save/(\\d+)/", route.getUriReg());
		assertEquals(ControllerForTestRoute.class, route.getController());
		assertEquals(ControllerForTestRoute.class.getMethod("save", Integer.TYPE), route.getAction());
		assertFalse(route.isStaticRule());
	}

	public void testIntRegConstructor2() throws Exception {
		Route route = Route.get("/save/<int:age>/suffix", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals("/save/(\\d+)/suffix/", route.getUriReg());
		assertEquals(ControllerForTestRoute.class, route.getController());
		assertEquals(ControllerForTestRoute.class.getMethod("save", Integer.TYPE), route.getAction());
		assertFalse(route.isStaticRule());
	}

	public void testStrRegConstructor() throws Exception {
		Route route = Route.get("/save/<string:name>", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals("/save/([\\pP\\w\u4E00-\u9FA5]+)/", route.getUriReg());
		assertEquals(ControllerForTestRoute.class, route.getController());
		assertEquals(ControllerForTestRoute.class.getMethod("save", String.class), route.getAction());
		assertFalse(route.isStaticRule());
	}

	public void testStrRegConstructor2() throws Exception {
		Route route = Route.get("/save/<string:name>/suffix", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals("/save/([\\pP\\w\u4E00-\u9FA5]+)/suffix/", route.getUriReg());
		assertEquals(ControllerForTestRoute.class, route.getController());
		assertEquals(ControllerForTestRoute.class.getMethod("save", String.class), route.getAction());
		assertFalse(route.isStaticRule());
	}

	public void testStrRegConstructor3() throws Exception {
		Route route = Route.get("/save/<name>", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals("/save/([\\pP\\w\u4E00-\u9FA5]+)/", route.getUriReg());
		assertEquals(ControllerForTestRoute.class, route.getController());
		assertEquals(ControllerForTestRoute.class.getMethod("save", String.class), route.getAction());
		assertFalse(route.isStaticRule());
	}

	public void testStrRegConstructor4() throws Exception {
		Route route = Route.get("/save/<name>/suffix", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals("/save/([\\pP\\w\u4E00-\u9FA5]+)/suffix/", route.getUriReg());
		assertEquals(ControllerForTestRoute.class, route.getController());
		assertEquals(ControllerForTestRoute.class.getMethod("save", String.class), route.getAction());
		assertFalse(route.isStaticRule());
	}

	public void testListRegConstructor() throws Exception {
		Route route = Route.get("/save/<list:>", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals("/save/([\\w\u4E00-\u9FA5]+(?:,[\\w\u4E00-\u9FA5]+)*)/", route.getUriReg());
		assertEquals(ControllerForTestRoute.class, route.getController());
		assertEquals(ControllerForTestRoute.class.getMethod("save", List.class), route.getAction());
		assertFalse(route.isStaticRule());
	}

	public void testListRegConstructor2() throws Exception {
		Route route = Route.get("/save/<list:a>/suffix", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals("/save/([\\w\u4E00-\u9FA5]+(?:,[\\w\u4E00-\u9FA5]+)*)/suffix/", route.getUriReg());
		assertEquals(ControllerForTestRoute.class, route.getController());
		assertEquals(ControllerForTestRoute.class.getMethod("save", List.class), route.getAction());
		assertFalse(route.isStaticRule());
	}

	public void testComplexConstructor() throws Exception {
		Route route = Route.get("/save/<name>/<int:age>/<list:types>", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals("/save/([\\pP\\w\u4E00-\u9FA5]+)/(\\d+)/([\\w\u4E00-\u9FA5]+(?:,[\\w\u4E00-\u9FA5]+)*)/",
		        route.getUriReg());
		assertEquals(ControllerForTestRoute.class, route.getController());
		assertEquals(ControllerForTestRoute.class.getMethod("save", String.class, Integer.TYPE, List.class),
		        route.getAction());
		assertFalse(route.isStaticRule());
	}

	public void testComplexMatch() {
		Route route = Route.get("/save/<name>/<int:age>/<list:types>", "fw.test.unit.ControllerForTestRoute.save");
		Object[] matchRelts = route.match("/save/pengyi/29/a,b,c,d,e");
		assertEquals(3, matchRelts.length);
		assertEquals("pengyi", (String) matchRelts[0]);
		assertEquals(29, ((Integer) matchRelts[1]).intValue());
		String[] arg3 = (String[]) ((List) matchRelts[2]).toArray(new String[0]);
		assertEquals("a,b,c,d,e", StringUtil.join(arg3, ","));
	}

	public void testComplexMatch2() {
		Route route = Route.get("/save/<name>/<int:age>/<list:types>/suffix",
		        "fw.test.unit.ControllerForTestRoute.save");
		Object[] matchRelts = route.match("/save/测试帐号/29/a,b,c,d,e/suffix");
		assertEquals(3, matchRelts.length);
		assertEquals("测试帐号", (String) matchRelts[0]);
		assertEquals(29, ((Integer) matchRelts[1]).intValue());
		String[] arg3 = (String[]) ((List) matchRelts[2]).toArray(new String[0]);
		assertEquals("a,b,c,d,e", StringUtil.join(arg3, ","));
	}

	public void testComplexNotMatch() {
		Route route = Route.get("/save/<name>/<int:age>/<list:types>/", "fw.test.unit.ControllerForTestRoute.save");
		assertEquals(null, route.match("/save/pengyi/29"));
	}

	public void testRouteDefineException() throws Exception {
		try {
			Route route = Route.get("/save/<name>/<int:age>", ControllerForTestRoute.class, "save");
			throw new Exception("Can not run the line.");
		} catch (RouteDefineException ex) {
		}
	}

	public void testSoHunatvComMatch() throws Exception {
		Route route = Route.get("/so/k-<string:name>", "fw.test.unit.ControllerForTestRoute.search");
		Object[] matchRelts = route.match("/so/k-花儿与少年/");
		assertEquals(1, matchRelts.length);
		assertEquals("花儿与少年", (String) matchRelts[0]);
	}

	public void testListHunatvComMatch() throws Exception {
		Route route = Route.get("/<int:category>/<idStr>.html", "fw.test.unit.ControllerForTestRoute.list");
		Object[] matchRelts = route.match("/3/47-1----0-1----1---.html");
		assertEquals(2, matchRelts.length);
		assertEquals(3, ((Integer) matchRelts[0]).intValue());
		assertEquals("47-1----0-1----1---", (String) matchRelts[1]);
	}
}