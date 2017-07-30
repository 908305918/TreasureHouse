package com.zizoy.treasurehouse.util;

import java.io.Reader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

/**
 * @Description: Json数据转换
 *
 * @author falcon
 */
public class JsonUtil {

	private static Gson create() {
		return JsonUtil.GsonHolder.gson;
	}

	private static class GsonHolder {
		private static Gson gson = new Gson();
	}

	public static <T> T fromJson(String json, Class<T> type) throws JsonIOException, JsonSyntaxException {
		return create().fromJson(json, type);
	}

	public static <T> T fromJson(String json, Type type) {
		return create().fromJson(json, type);
	}

	public static <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
		return create().fromJson(reader, typeOfT);
	}

	public static <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
		return create().fromJson(json, classOfT);
	}

	public static <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
		return create().fromJson(json, typeOfT);
	}

	public static String toJson(Object src) {
		return create().toJson(src);
	}

	public static String toJson(Object src, Type typeOfSrc) {
		return create().toJson(src, typeOfSrc);
	}
}