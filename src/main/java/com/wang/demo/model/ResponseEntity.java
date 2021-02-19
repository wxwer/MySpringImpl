package com.wang.demo.model;

public class ResponseEntity {
	public Integer errorCode=200;
	public String msg="";
	public Object data=null;
	
	public ResponseEntity(Integer errorCode,String msg,Object data) {
		// TODO Auto-generated constructor stub
		this.errorCode=errorCode;
		this.msg = msg;
		this.data=data;
	}
	
	public static  ResponseEntity success(Object data) {
		ResponseEntity entity = new ResponseEntity(200,null,data);
		return entity;
	}
	public static  ResponseEntity success(Object data,String msg) {
		ResponseEntity entity = new ResponseEntity(200,msg,data);
		return entity;
	}
	public static ResponseEntity fail(String msg) {
		ResponseEntity entity = new ResponseEntity(500,msg,null);
		return entity;
	}
}
