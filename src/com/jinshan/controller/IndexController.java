package com.jinshan.controller;

import com.jfinal.core.Controller;

public class IndexController extends Controller {
	
	public void index() {
		
		setAttr("status", "SUCCESS");
		renderJson();
	}
}