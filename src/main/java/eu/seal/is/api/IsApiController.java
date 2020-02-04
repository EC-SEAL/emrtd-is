package eu.seal.is.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import eu.seal.is.api.services.IsLoadPostService;
import eu.seal.is.model.DataSet;
import eu.seal.is.sm_api.SessionManagerConnService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-01-20T11:14:25.775Z")

@Controller
public class IsApiController implements IsApi {
	
	@Autowired
	private SessionManagerConnService smConn;

    private static final Logger log = LoggerFactory.getLogger(IsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public IsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Autowired
	private IsLoadPostService isLoadPostService;
    
    public ResponseEntity<Void> isLoadPost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken,@ApiParam(value = "The data set to add", required=true) @RequestParam(value="dataset", required=true)  String dataset) {
        String accept = request.getHeader("Accept");
        
        if (accept != null) {
        	
        	try {
        		String sessionId ="";
        		
        		sessionId = smConn.validateToken( msToken);
        		if (sessionId != "") {
    	        // msToken validated
        		
        			// TESTING
        			DataSet myDataset = new DataSet();
        			myDataset.setId("DATASET__" + UUID.randomUUID().toString());     			
        			myDataset.setLoa("loa TEST");
                    myDataset.setIssued("issued TEST");
                    myDataset.setIssuerId("eMRTD issuerId TEST");
                    myDataset.setType("eMRTD type TEST");
                    
                    // TODO
                    Map <String, String> myProperties = new HashMap<>();
                    myProperties.put ("sigAlgorithm", "THIS_IS_THE_ALGORITHM_USED");
                    myProperties.put ("publicKey", "THIS_IS_THE_PUBLIC_KEY");
                    myDataset.setProperties(myProperties);
                    // TODO: sign myDataset
                    	
        			String myDatasetStr = myDataset.toString();
        			log.info("Before encoding: myDatasetStr"+ myDatasetStr);     			
        			
        			Gson gson = new Gson();
        			String jsonMyDataset = gson.toJson(myDataset);
        			log.info("Before encoding jsonMyDataset: "+ jsonMyDataset);     			
        			String encodedDataset = Base64.getEncoder().encodeToString(jsonMyDataset.getBytes());
        			
       			
        			isLoadPostService.loadPost (sessionId, encodedDataset, smConn);
        			// END TESTING
        			
	        		//isLoadPostService.loadPost (sessionId, dataset, smConn);
	        		
	                return new ResponseEntity<Void>(HttpStatus.OK);
        		}
	        	else  {
	        		log.error("msToken not validated");
	        		log.info("msToken not validated");
	        		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	        	}
	        	
            }
            catch (Exception e) {
	        	return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
            }
            
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> isQueryPost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    @ApiOperation(value = "FOR TESTING: Returns a token from SM", nickname = "isToken", notes = "FOR TESTING: Returns a token from SM", tags={ "IdentitySource", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Request admitted"),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 401, message = "Not authorised") })
    @RequestMapping(value = "/is/token",
        produces = { "application/x-www-form-urlencoded" },
        method = RequestMethod.GET)
    public ResponseEntity<String> isToken() {
    	
    	// Start Session: POST /sm/startSession
    	String sessionId;
    	String msToken = null;
		try {
			sessionId = smConn.startSession();
			msToken = smConn.generateToken(sessionId, "emrtdISms_001");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return new ResponseEntity<String>(msToken, HttpStatus.OK);
    }
    

}
