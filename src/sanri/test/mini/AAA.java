package sanri.test.mini;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class AAA {

	public static void main(String[] args) {
		List<Map> list = new ArrayList<Map>();

		Map map = new HashMap();
		map.put("1", "1");
		map.put("2", "2");
		map.put("3", "3");
		list.add(map);

		Map map1 = new HashMap();
		map1.put("1", "1");
		map1.put("2", "2");
		map1.put("3", "3");
		list.add(map1);

		Map map2 = new HashMap();
		map2.put("1", "3");
		map2.put("2", "3");
		map2.put("3", "3");
		list.add(map2);

		//找到哪些位置需要移除
		List<String> has = new ArrayList<String>();
		List<Integer> needRemove = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++) {
			Map item = list.get(i);
			Set itemSet = item.entrySet();
			List<Entry<String, String>> itemSetList = new ArrayList(itemSet);
			Collections.sort(itemSetList,new Comparator<Entry<String, String>>() {

				@Override
				public int compare(Entry<String, String> o1, Entry<String, String> o2) {
					String key = o1.getKey();
					String key2 = o2.getKey();
					return key.compareTo(key2);
				}
				
			});
			String join = StringUtils.join(itemSetList.toArray());
			if (has.contains(join)) {
				needRemove.add(i);
			}else{
				has.add(join);
			}
		}
		//移除指定位置
		List<Map> listMap = new ArrayList<Map>();
		for (Integer index : needRemove) {
			listMap.add(list.get(index.intValue()));
		}
		
		for (Map map3 : listMap) {
			list.remove(map3);
		}
		
		System.out.println(list);
	}
}
