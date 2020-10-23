package com.bah.msd.security.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.opentracing.Tracer;
import io.opentracing.Span;


@RestController
@RequestMapping("/")
public class ServiceAPI {
	
	@Autowired
	private Tracer tracer;

	public static long instanceId = new Random().nextInt();
	public static int count = 0;

	
	@GetMapping
	public String healthCheck() {
		Span span = tracer.buildSpan("health check").start();
		span.setTag("http.status_code", 201);
		count += 1;
		Date date = new Date();
		String dateformat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.FULL).format(date);
		span.finish();
		return "<h3>The Authentication service is up and running!</h3>" + "<br/>Instance: " + instanceId + ", " + "<br/>DateTime: " + dateformat  + "<br/>CallCount: "+count;
	}
	

}
