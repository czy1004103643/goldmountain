package com.jinshan.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Report extends Model<Report> {
	public static Report dao = new Report();

	@SuppressWarnings({ "unchecked" })
	public List<Object> query(int report_id) {
		ArrayList<Object> list = new ArrayList<>();

		List<CheckRecord> check_records = CheckRecord.dao.find(
				"select * from check_record where report_id = ?", report_id);

		if (check_records != null && check_records.size() > 0) {

			for (int i = 0; i < check_records.size(); i++) {
				CheckRecord check_record = check_records.get(i);
				Integer check_item_id = check_record.get("check_item_id");
				String passed = check_record.getStr("passed");
				String pic_url = check_record.getStr("pic_url");
				String note = check_record.getStr("note");

				CheckItem checkItem = CheckItem.dao.findById(check_item_id);
				String check_item = checkItem.get("check_item");
				int check_content_id = checkItem.getInt("check_content_id");
				int table_sequence = checkItem.getLong("table_sequence")
						.intValue();

				CheckContent checkContent = CheckContent.dao
						.findById(check_content_id);
				String check_content = checkContent.getStr("check_content");

				Map<String, Object> item = new HashMap<>();
				item.put("item", check_content_id + "." + table_sequence
						+ check_item);
				item.put("passed", passed);
				item.put("note", note);
				item.put("pic_url", pic_url);

				boolean hasThisNo = false;
				int NoIndex = -1;
				for (int j = 0; j < list.size(); j++) {
					Map<String, Object> map = (Map<String, Object>) list.get(j);
					if ((int) map.get("No") == check_content_id) {
						hasThisNo = true;
						NoIndex = j;
						break;

					} else {
						continue;
					}
				}
				if (hasThisNo) {
					Map<String, Object> map = (Map<String, Object>) list
							.get(NoIndex);
					ArrayList<Object> items = (ArrayList<Object>) map
							.get("items");
					items.add(item);

					map.put("items", items);
					list.set(NoIndex, map);

				} else {
					ArrayList<Object> items = new ArrayList<>();
					items.add(item);

					Map<String, Object> map = new HashMap<>();
					map.put("No", check_content_id);
					map.put("content", check_content);
					map.put("items", items);
					list.add(map);
				}
			}
			Collections.sort(list, new SortContentsByNo());
			for (int i = 0; i < list.size(); i++) {

				Map<String, Object> map = (Map<String, Object>) list.get(i);
				ArrayList<Object> items = (ArrayList<Object>) map.get("items");
				Collections.sort(items, new SortItemsByNo());
			}
			return list;
		} else {
			return null;
		}
	}

	public boolean updateByJSONObject(JSONObject jsonObject) {

		if (jsonObject.has("report_id")) {
			add(jsonObject);
		} else {
			edit(jsonObject);
		}

		return false;
	}

	private boolean add(JSONObject jsonObject) {
		final String report_create_time = jsonObject
				.getString("report_create_time");
		final String report_name = jsonObject.getString("report_name");
		final String report_creator = jsonObject.getString("report_creator");
		final Map<String, Object> map = new HashMap<>();
		map.put("report_create_time", report_create_time);
		map.put("report_name", report_name);
		map.put("report_creator", report_creator);

		final JSONArray contents = jsonObject.getJSONArray("contents");
		boolean succeed = Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				boolean addReport = new Report().setAttrs(map).save();
				int report_id = dao
						.findFirst(
								"select * from report where report_create_time = ? and report_name = ? and report_creator = ",
								report_create_time, report_name, report_creator)
						.getInt("report_id");

				boolean addCheckRecords = true;
				lableA: for (int i = 0; i < contents.length(); i++) {
					JSONObject content = contents.getJSONObject(i);
					JSONArray items = content.getJSONArray("items");
					for (int j = 0; j < items.length(); j++) {
						JSONObject item = items.getJSONObject(j);
						String check_item = item.getString("item").substring(2,
								item.getString("item").length() - 1);
						String passed = item.getString("passed");
						String pic_url = item.getString("pic_url");
						String note = item.getString("note");

						Map<String, Object> map = new HashMap<>();
						map.put("report_id", report_id);
						map.put("check_item", check_item);
						map.put("passed", passed);
						map.put("pic_url", pic_url);
						map.put("note", note);
						addCheckRecords = new CheckRecord().setAttrs(map)
								.save();
						if (!addCheckRecords) {
							break lableA;
						}
					}
				}

				return addReport && addCheckRecords;
			}
		});
		return succeed;
	}

	private boolean edit(JSONObject jsonObject) {
		final int report_id = jsonObject.getInt("report_id");

		String report_create_time = jsonObject.getString("report_create_time");
		String report_name = jsonObject.getString("report_name");
		String report_creator = jsonObject.getString("report_creator");
		final Map<String, Object> map = new HashMap<>();
		map.put("report_create_time", report_create_time);
		map.put("report_name", report_name);
		map.put("report_creator", report_creator);

		final JSONArray contents = jsonObject.getJSONArray("contents");
		boolean succeed = Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				boolean addReport = new Report().setAttrs(map).save();

				boolean addCheckRecords = true;
				lableA: for (int i = 0; i < contents.length(); i++) {
					JSONObject content = contents.getJSONObject(i);
					JSONArray items = content.getJSONArray("items");
					for (int j = 0; j < items.length(); j++) {
						JSONObject item = items.getJSONObject(j);
						String check_item = item.getString("item").substring(2,
								item.getString("item").length() - 1);
						String passed = item.getString("passed");
						String pic_url = item.getString("pic_url");
						String note = item.getString("note");

						Map<String, Object> map = new HashMap<>();
						map.put("report_id", report_id);
						map.put("check_item", check_item);
						map.put("passed", passed);
						map.put("pic_url", pic_url);
						map.put("note", note);
						addCheckRecords = new CheckRecord().setAttrs(map)
								.update();
						if (!addCheckRecords) {
							break lableA;
						}
					}
				}

				return addReport && addCheckRecords;
			}
		});
		return succeed;
	}
}

@SuppressWarnings("rawtypes")
class SortContentsByNo implements Comparator {

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Object o1, Object o2) {
		HashMap<String, Object> map1 = (HashMap<String, Object>) o1;
		HashMap<String, Object> map2 = (HashMap<String, Object>) o2;

		int no1 = (int) map1.get("No");
		int no2 = (int) map2.get("No");

		if (no1 > no2)
			return 1;
		else if (no1 < no2)
			return -1;
		return 0;
	}
}

@SuppressWarnings("rawtypes")
class SortItemsByNo implements Comparator {

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Object o1, Object o2) {
		HashMap<String, Object> map1 = (HashMap<String, Object>) o1;
		HashMap<String, Object> map2 = (HashMap<String, Object>) o2;

		String s1 = (String) map1.get("item");
		String s2 = (String) map2.get("item");
		char char1 = s1.charAt(3);
		char char2 = s2.charAt(3);

		int no1;
		int no2;
		if (char1 <= 57 && char1 >= 48) {
			no1 = Integer.valueOf(s1.substring(2, 4));
		} else {
			no1 = Integer.valueOf(s1.substring(2,3));
		}
		if (char2 <= 57 && char2 >= 48) {
			no2 = Integer.valueOf(s2.substring(2, 4));
		} else {
			no2 = Integer.valueOf(s2.substring(2,3));
		}

		if (no1 > no2)
			return 1;
		else if (no1 < no2)
			return -1;
		return 0;
	}
}
