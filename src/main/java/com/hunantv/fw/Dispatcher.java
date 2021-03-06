package com.hunantv.fw;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.hunantv.fw.exceptions.HttpException;
import com.hunantv.fw.route.Routes;
import com.hunantv.fw.utils.FwLogger;
import com.hunantv.fw.utils.StringUtil;
import com.hunantv.fw.utils.WebUtil;
import com.hunantv.fw.view.View;

public class Dispatcher extends HttpServlet {

	public final static FwLogger logger = new FwLogger(Dispatcher.class);
	protected Routes routes = null;
	protected boolean debug = false;
	protected static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(
	        System.getProperty("java.io.tmpdir"));

	@Override
	public void init() {
		Application app = Application.getInstance();
		this.routes = app.getRoutes();
		this.debug = app.isDebug();
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Long btime = Calendar.getInstance().getTimeInMillis();
		logger.initSeqid();
		logger.debug(request.getRequestURL());
		String charset = "UTF-8";
		response.setCharacterEncoding(charset);
		response.setContentType("text/html;charset=" + charset);

		if (WebUtil.isMultipart(request)) {
			request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
		}

		try {
			View view = doIt(request, response);
			view.renderTo(response);
		} catch (HttpException ex) {
			this.httpErr(response, ex);
		} catch (Exception ex) {
			this.err500(response, ex);
		} finally {
			Long etime = Calendar.getInstance().getTimeInMillis();
			logger.delayInfo("cost", new Long(etime - btime).toString());
			logger.clearSeqid();
		}
	}

	public View doIt(HttpServletRequest request, HttpServletResponse response) throws HttpException {
		String uri = StringUtil.ensureEndedWith(request.getRequestURI(), "/");
		logger.delayInfo("uri", uri);
		ControllerAndAction controllerAndAction = routes.match(request.getMethod(), uri);
		return controllerAndAction.doAction(request, response);
	}

	public void httpErr(HttpServletResponse response, HttpException ex) throws IOException {
		int code = ex.getCode();
		if (HttpServletResponse.SC_INTERNAL_SERVER_ERROR == code) {
			err500(response, ex);
		} else {
			response.sendError(ex.getCode());
		}
	}

	public void err500(HttpServletResponse response, Exception ex) throws IOException {
		if (this.debug) {
			ex.printStackTrace(response.getWriter());
		}
		logger.error(ex, ex);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
}