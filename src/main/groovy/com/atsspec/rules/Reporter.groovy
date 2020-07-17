package com.atsspec.rules


public class Reporter {
	boolean colour
	public Reporter(boolean colour = false) {this.colour = colour}


String ANSI_RESET = "\u001B[0m";

String ANSI_BLACK = "\u001B[30m";
String ANSI_RED = "\u001B[31m";
String ANSI_GREEN = "\u001B[32m";
String ANSI_YELLOW = "\u001B[33m";
String ANSI_BLUE = "\u001B[34m";
String ANSI_PURPLE = "\u001B[35m";
String ANSI_CYAN = "\u001B[36m";
String ANSI_WHITE = "\u001B[37m";

String ANSI_BLACK_BACKGROUND = "\u001B[40m";
String ANSI_RED_BACKGROUND = "\u001B[41m";
String ANSI_GREEN_BACKGROUND = "\u001B[42m";
String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
String ANSI_BLUE_BACKGROUND = "\u001B[44m";
String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
String ANSI_CYAN_BACKGROUND = "\u001B[46m";
String ANSI_WHITE_BACKGROUND = "\u001B[47m";	


	
	def loge(cat) { return { msg->
		System.err.println (cat + msg) }}

	def logc(color) { return { msg->
		System.err.println (color + msg + ANSI_RESET) }}
	
//	def log =    logc(ANSI_GREEN)
//	def error =  logc(ANSI_RED)
//	def warn =   logc(ANSI_CYAN)
//	def notify = logc(ANSI_BLUE)

	def log =    loge('')
	def error =  loge('error: ')
	def warn =   loge('warning: ')
	def notify = loge('notice: ')

}
