package com.jinshan.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.jfinal.core.Controller;
import com.jinshan.model.Report;

public class ReportController extends Controller {

	public void index() {
		redirect("/html/reportList.html");
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

	public void update() {
		String type = getPara("type");
		if (type.equals("add")) {
			if (add()) {
				setAttr("result", "add success");
			} else {
				setAttr("result", "add failed");
			}
		} else if (type.equals("delete")) {
			if (delete()) {
				setAttr("result", "delete success");
			} else {
				setAttr("result", "delete failed");
			}
		} else if (type.equals("edit")) {
			if (edit()) {
				setAttr("result", "edit success");
			} else {
				setAttr("result", "edit failed");
			}
		}
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

	private String getBody(HttpServletRequest request) throws ServletException, IOException {
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
