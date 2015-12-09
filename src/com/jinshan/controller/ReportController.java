package com.jinshan.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.jinshan.model.CheckContent;
import com.jinshan.model.CheckItem;
import com.jinshan.model.Report;

public class ReportController extends Controller {

	public void index() {
		// redirect("/html/reportList.html");
		renderJson();
	}

	public void reportList() {

		setAttr("reportList", Report.dao.find("select * from report"));

		renderJson();
	}

	public void add() {
		setAttr("report_name", "");
		setAttr("report_creator", "");
		setAttr("report_create_time", "");
		setAttr("contents", Report.dao.query());

		renderJson();
	}

	public void query() {
		Integer report_id = getParaToInt("report_id");

		Report report = Report.dao.findById(report_id);
		if (report != null) {
			String report_name = report.getStr("report_name");
			String report_creator = report.getStr("report_creator");
			String report_create_time = report.getStr("report_create_time");

			setAttr("report_name", report_name);
			setAttr("report_creator", report_creator);
			setAttr("report_create_time", report_create_time);
			setAttr("contents", Report.dao.query(report_id));
		}
		renderJson();
	}

	public void updateByJsonStr() {
		String type = getPara("type");
		if (type.equals("add")) {
			if (addByJsonStr()) {
				setAttr("result", "add success");
			} else {
				setAttr("result", "add failed");
			}
		} else if (type.equals("delete")) {
			if (delete1()) {
				setAttr("result", "delete success");
			} else {
				setAttr("result", "delete failed");
			}
		} else if (type.equals("edit")) {
			if (editByJsonStr()) {
				setAttr("result", "edit success");
			} else {
				setAttr("result", "edit failed");
			}
		}

		renderJson();
	}

	private boolean addByJsonStr() {
		String jsonStr = null;
		try {
			jsonStr = getBody(getRequest());
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = new JSONObject(jsonStr);
		if (Report.dao.updateByJSONObject(jsonObject)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean delete1() {
		Integer report_id = getParaToInt("report_id");
		if (Report.dao.deleteById(report_id)) {
			setAttr("result", "delete success");
			return true;
		} else {
			setAttr("result", "delete failed");
			return false;
		}

	}

	public void delete() {
		Integer report_id = getParaToInt("report_id");
		if (Report.dao.deleteById(report_id)) {
			setAttr("result", "delete success");
		} else {
			setAttr("result", "delete failed");
		}
	}

	private boolean editByJsonStr() {
		String jsonStr = null;
		try {
			jsonStr = getBody(getRequest());
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = new JSONObject(jsonStr);
		if (Report.dao.updateByJSONObject(jsonObject)) {
			return true;
		} else {
			return false;
		}
	}

	public void update() {
		String saveDirectory = "upload" + File.separator
				+ new SimpleDateFormat("yyyyMMdd").format(new Date());
		List<UploadFile> list_uf = getFiles(saveDirectory);

		Map<String, Object> map = new HashMap<>();
		map.put("report_id", getPara("report_id"));
		map.put("report_name", getPara("report_name"));
		map.put("report_creator", getPara("report_creator"));
		map.put("list_uf", list_uf);

		ArrayList<Object> items = new ArrayList<>();

		List<CheckContent> list_content = CheckContent.dao
				.find("select * from check_content");
		for (int i = 0; i < list_content.size(); i++) {
			List<CheckItem> list_item = CheckItem.dao.find(
					"select * from check_item where check_content_id = ?",
					list_content.get(i).get("check_content_id"));
			for (int j = 0; j < list_item.size(); j++) {
				String passed = "0";
				String note = "";

				int check_content_id = i + 1;
				int table_sequence = j + 1;
				if (getPara("passed_" + String.valueOf((check_content_id))
						+ "_" + String.valueOf((table_sequence))) != null) {
					passed = "1";
				}
				if (getPara("note_" + String.valueOf((check_content_id)) + "_"
						+ String.valueOf((table_sequence))) != null) {
					note = getPara("note_" + String.valueOf((check_content_id))
							+ "_" + String.valueOf((table_sequence)));
				}
				CheckItem checkItem = CheckItem.dao
						.findFirst(
								"select * from check_item where check_content_id = ? and table_sequence = ?",
								check_content_id, table_sequence);
				int check_item_id = checkItem.getInt("check_item_id");

				Map<String, Object> map1 = new HashMap<>();
				map1.put("passed", passed);
				map1.put("note", note);
				map1.put("check_item_id", check_item_id);

				items.add(map1);
			}
		}
		map.put("items", items);

		if (map.get("report_id") != null) {
			if (editByMap(map)) {
				setAttr("result", "edit success");
			} else {
				setAttr("result", "edit failed");
			}

		} else {
			if (addByMap(map)) {
				setAttr("result", "edit success");
			} else {
				setAttr("result", "edit failed");
			}
		}

		renderJson();
	}

	private boolean editByMap(Map<String, Object> map) {

		return Report.dao.update(map);
	}

	private boolean addByMap(Map<String, Object> map) {

		return Report.dao.update(map);
	}

	private String getBody(HttpServletRequest request) throws ServletException,
			IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}
		String bodyStr = new String(sb);
		return bodyStr;
	}

}
