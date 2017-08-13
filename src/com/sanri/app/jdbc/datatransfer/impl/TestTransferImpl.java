package com.sanri.app.jdbc.datatransfer.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.sanri.app.jdbc.Table;
import com.sanri.app.jdbc.datatransfer.DataTransfer;

public class TestTransferImpl implements DataTransfer{

	@Override
	public void handler(List<Map<String, String>> data,Table table) {
		System.out.println(data.get(0));
		System.out.println(JSONObject.toJSONString(table));
	}

}
