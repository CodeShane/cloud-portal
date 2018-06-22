package de.papke.cloud.portal.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.papke.cloud.portal.constants.Constants;
import de.papke.cloud.portal.pojo.ProvisionLog;
import de.papke.cloud.portal.service.ProvisionLogService;
import de.papke.cloud.portal.service.SessionUserService;

@Controller
public class ProvisionLogController extends ApplicationController {
	
	private static final String MIME_TYPE_PRIVATE_KEY = "application/x-pem-file";
	private static final String MIME_TYPE_RESULT = "application/zip";
	private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	private static final String HEADER_FILENAME = "inline; filename=\"%s\"";
	private static final String PREFIX = "/provision-log";
	
	@Autowired
	private ProvisionLogService provisionLogService;
	
	@Autowired
	private SessionUserService sessionUserService;

	private String getPrivateKeyFileName(String id) {
		
		return new StringBuilder()
				.append(Constants.KEY_FILE_PREFIX)
				.append(Constants.CHAR_UNDERSCORE)
				.append(id)
				.append(Constants.KEY_FILE_PRIVATE_SUFFIX)
				.toString();
	}
	
	@RequestMapping(value = PREFIX + "/private-key/{id}", method = RequestMethod.GET)
	public void downloadPrivateKey(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
		
		ProvisionLog provisionLog = provisionLogService.get(id);
		
		if (provisionLog != null) {
			
			if(sessionUserService.isAllowed(provisionLog)) {
				
				byte[] privateKey = provisionLog.getPrivateKey();
				response.setContentType(MIME_TYPE_PRIVATE_KEY);
				response.setHeader(HEADER_CONTENT_DISPOSITION, String.format(HEADER_FILENAME, getPrivateKeyFileName(id)));
				response.setContentLength(privateKey.length);
				IOUtils.copy(new ByteArrayInputStream(privateKey), response.getOutputStream());
			}
			else {
				fail(String.format("You are not allowed to download the private key for the provision log with id '%s'", id), response);
			}
		}
		else {
			fail(String.format("No private key found for provision log with id '%s'.", id), response);
		}
	}
	
	private String getResultFileName(String id) {
		
		return new StringBuilder()
				.append(Constants.RESULT_FILE_PREFIX)
				.append(Constants.CHAR_UNDERSCORE)
				.append(id)
				.append(Constants.RESULT_FILE_SUFFIX)
				.toString();
	}	
	
    @RequestMapping(value = PREFIX + "/result/{id}", method = RequestMethod.GET)
    public void downloadResultFile(HttpServletResponse response, @PathVariable("id") String id) throws IOException {

    	ProvisionLog provisionLog = provisionLogService.get(id);
    	
    	if (provisionLog != null) {
    		
    		if(sessionUserService.isAllowed()) {
    			
    			byte[] resultFile = provisionLog.getResult();
    			response.setContentType(MIME_TYPE_RESULT);
    			response.setHeader(HEADER_CONTENT_DISPOSITION, String.format(HEADER_FILENAME, getResultFileName(id)));
    			response.setContentLength(resultFile.length);
    			IOUtils.copy(new ByteArrayInputStream(resultFile), response.getOutputStream());
    		}
    		else {
    			fail(String.format("You are not allowed to download the result file for the provision log with id '%s'", id), response);
    		}
    	}
    	else {
    		fail(String.format("No result file found for provision log with id '%s'.", id), response);
    	}
    }
}