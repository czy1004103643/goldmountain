package com.jinshan.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
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

	@SuppressWarnings("unused")
	public void update() {
		// String type = getPara("type");
		// if (type.equals("add")) {
		// if (add()) {
		// setAttr("result", "add success");
		// } else {
		// setAttr("result", "add failed");
		// }
		// } else if (type.equals("delete")) {
		// if (delete()) {
		// setAttr("result", "delete success");
		// } else {
		// setAttr("result", "delete failed");
		// }
		// } else if (type.equals("edit")) {
		// if (edit()) {
		// setAttr("result", "edit success");
		// } else {
		// setAttr("result", "edit failed");
		// }
		// }

		List<UploadFile> list_uf = getFiles("upload");

		Map<String, Object> map = new HashMap<>();
		map.put("report_id", getPara("report_id"));
		map.put("report_name", getPara("report_name"));
		map.put("report_creator", getPara("report_creator"));

		ArrayList<Object> items = new ArrayList<>();

		List<CheckContent> list_content = CheckContent.dao
				.find("select * from check_content");
		for (int i = 0; i < list_content.size(); i++) {
			List<CheckItem> list_item = CheckItem.dao.find(
					"select * from check_item where check_content_id = ?",
					list_content.get(i).get("check_content_id"));
			for (int j = 0; j < list_item.size(); j++) {
				String pic_url = "";
				String passed = "0";
				String note = "";

				if (getPara("pic_url_" + String.valueOf((i + 1)) + "_"
						+ String.valueOf((i + 1))) != null) {
					pic_url = getPara("pic_url_" + String.valueOf((i + 1))
							+ "_" + String.valueOf((j + 1)));
				}
				if (getPara("passed_" + String.valueOf((i + 1)) + "_"
						+ String.valueOf((j + 1))) != null) {
					passed = "1";
				}
				if (getPara("note_" + String.valueOf((i + 1)) + "_"
						+ String.valueOf((i + 1))) != null) {
					note = getPara("note_" + String.valueOf((i + 1)) + "_"
							+ String.valueOf((j + 1)));
				}
				Map<String, Object> map1 = new HashMap<>();
				map1.put("pic_url", pic_url);
				map1.put("passed", passed);
				map1.put("note", note);
				
				
				@!!!!!!!!!!
				
				
				
			}
		}

		// Map<String, String[]> map = getParaMap();

		renderJson();
	}

	private boolean add() {
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

	private boolean delete() {
		Integer report_id = getParaToInt("report_id");
		if (Report.dao.deleteById(report_id)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean edit() {
		// String jsonStr = null;
		// try {
		// jsonStr = getBody(getRequest());
		// } catch (ServletException | IOException e) {
		// e.printStackTrace();
		// }
		// JSONObject jsonObject = new JSONObject(jsonStr);
		// if (Report.dao.updateByJSONObject(jsonObject)) {
		// return true;
		// } else {
		// return false;
		// }

		// getFiles("/upload");
		// if () {
		// return true;
		// } else {
		return true;
		// }
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
