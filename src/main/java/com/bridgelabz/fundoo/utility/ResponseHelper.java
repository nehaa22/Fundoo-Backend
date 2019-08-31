package com.bridgelabz.fundoo.utility;

import com.bridgelabz.fundoo.response.Response;

public class ResponseHelper {
	public static Response sendError(int code, String message) {
		Response response = new Response();
		response.setStatusMessage(message);
		response.setStatusCode(code);
		return response;
	}

}