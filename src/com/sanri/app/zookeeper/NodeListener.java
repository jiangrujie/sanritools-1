package com.sanri.app.zookeeper;

import java.util.Map;

public abstract interface NodeListener {
	public abstract void processEvent(String paramString1, String paramString2, Map<String, String> paramMap);
}