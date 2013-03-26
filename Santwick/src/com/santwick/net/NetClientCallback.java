package com.santwick.net;

public interface NetClientCallback {
	
	public static final int NET_SUCCESS = 1;
	public static final int NET_FAILED = 2;
	public static final int NET_TIMEOUT = 3;
	public static final int NET_NOINIT = 4;
	
	public void execute(int status,String response); 
}
