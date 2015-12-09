package com.jinshan.model;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.upload.UploadFile;

@SuppressWarnings("serial")
public class Report extends Model<Report> {
	public static Report dao = new Report();

	public int tmp_report_id;

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

	public List<Object> query() {
		ArrayList<Object> list = new ArrayList<>();
		List<CheckContent> list_contents = CheckContent.dao
				.find("select * from check_content");
		int len_contents = list_contents.size();
		for (int i = 0; i < len_contents; i++) {
			CheckContent checkContent = list_contents.get(i);
			int check_content_id = checkContent.getInt("check_content_id");
			String check_content = checkContent.getStr("check_content");

			ArrayList<Object> items = new ArrayList<>();
			List<CheckItem> list_items = CheckItem.dao.find(
					"select * from check_item where check_content_id = ?",
					check_content_id);
			int len_items = list_items.size();
			for (int j = 0; j < len_items; j++) {
				CheckItem checkItem = list_items.get(j);
				String check_item = checkItem.getStr("check_item");
				int table_sequence = checkItem.getLong("table_sequence")
						.intValue();

				Map<String, Object> item = new HashMap<>();
				item.put("item", check_content_id + "." + table_sequence
						+ check_item);
				item.put("passed", "0");
				item.put("note", "");
				item.put("pic_url", "");
				items.add(item);
			}

			Map<String, Object> map = new HashMap<>();
			map.put("No", check_content_id);
			map.put("content", check_content);
			map.put("items", items);

			list.add(map);
		}

		return list;
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

	public boolean update(Map<String, Object> map) {
		if (map.get("report_id") != null) {
			return edit(map);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean edit(Map<String, Object> map) {

		final String report_id = (String) map.get("report_id");

		final String report_name = (String) map.get("report_name");
		final String report_creator = (String) map.get("report_creator");

		final List<UploadFile> list_uf = (List<UploadFile>) map.get("list_uf");

		final ArrayList<Object> items = (ArrayList<Object>) map.get("items");

		if (report_id.equals("add")) {
			String report_create_time = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss").format(new Date());

			if (report_name.equals("") || report_creator.equals("")) {
				return false;
			} else {
				if (new Report().set("report_name", report_name)
						.set("report_creator", report_creator)
						.set("report_create_time", report_create_time).save()) {
					Report report_add = dao
							.findFirst(
									"select * from report where report_name = ? and report_creator = ?",
									report_name, report_creator);
					int report_id_add = report_add.getInt("report_id");

					List<CheckItem> list_checkItems = CheckItem.dao
							.find("select * from check_item");
					for (int i = 0; i < list_checkItems.size(); i++) {
						CheckItem checkItem = list_checkItems.get(i);

						int check_item_id = checkItem.getInt("check_item_id");

						if (!new CheckRecord().set("report_id", report_id_add)
								.set("check_item_id", check_item_id).save()) {
							break;
						}
					}
					tmp_report_id = report_id_add;
				}
			}
		}

		boolean succeed = Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				int _report_id;
				if (report_id.equals("add")) {
					_report_id = tmp_report_id;
				} else {
					_report_id = Integer.parseInt(report_id);
				}

				boolean editCheckReports = true;
				editCheckReports = dao.findById(_report_id)
						.set("report_name", report_name)
						.set("report_creator", report_creator).update();

				boolean editCheckRecords = true;
				for (int i = 0; i < items.size(); i++) {
					Map<String, Object> map1 = (Map<String, Object>) items
							.get(i);
					int check_item_id = (int) map1.get("check_item_id");

					CheckRecord checkRecord = CheckRecord.dao
							.findFirst(
									"select * from check_record where report_id = ? and check_item_id = ?",
									_report_id, check_item_id);
					editCheckRecords = checkRecord.setAttrs(map1).update();
					if (!editCheckRecords) {
						break;
					}

				}

				boolean editCheckRecordsPic_url = true;
				for (int i = 0; i < list_uf.size(); i++) {
					UploadFile uf = list_uf.get(i);

					String pic_url_name = uf.getParameterName();
					String[] para = pic_url_name.split("_");

					int check_content_id = Integer.parseInt(para[2]);
					int table_sequence = Integer.parseInt(para[3]);

					String saveDirectory = "/upload"
							+ File.separator
							+ new SimpleDateFormat("yyyyMMdd")
									.format(new Date());
					String pic_url = saveDirectory + File.separator
							+ uf.getFileName();

					CheckItem checkItem = CheckItem.dao
							.findFirst(
									"select * from check_item where check_content_id = ? and table_sequence = ?",
									check_content_id, table_sequence);
					int check_item_id = checkItem.getInt("check_item_id");

					CheckRecord checkRecord = CheckRecord.dao
							.findFirst(
									"select * from check_record where report_id = ? and check_item_id = ?",
									report_id, check_item_id);
					editCheckRecordsPic_url = checkRecord.set("pic_url",
							pic_url).update();
					if (!editCheckRecordsPic_url) {
						break;
					}

				}

				return editCheckRecords && editCheckReports
						&& editCheckRecordsPic_url;
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
			no1 = Integer.valueOf(s1.substring(2, 3));
		}
		if (char2 <= 57 && char2 >= 48) {
			no2 = Integer.valueOf(s2.substring(2, 4));
		} else {
			no2 = Integer.valueOf(s2.substring(2, 3));
		}

		if (no1 > no2)
			return 1;
		else if (no1 < no2)
			return -1;
		return 0;
	}
}
