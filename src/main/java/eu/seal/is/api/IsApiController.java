package eu.seal.is.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-01-20T11:14:25.775Z")

@Controller
public class IsApiController implements IsApi {

    private static final Logger log = LoggerFactory.getLogger(IsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public IsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> isLoadPost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken,@ApiParam(value = "The data set to add", required=true) @RequestParam(value="dataset", required=true)  String dataset) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> isQueryPost(@ApiParam(value = "The security token for ms to ms calls", required=true) @RequestParam(value="msToken", required=true)  String msToken) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }
    
    
    
//    private String load(String msToken)
//    {
//    	// Revisar token recibido
//		//				 
//		if (msToken.endsWith("="))
//		{
//			msToken = msToken.replace("=", "");
//		}
//		if (msToken.startsWith("msToken="))
//		{
//			msToken = msToken.replace("msToken=", "");
//		}
//		log.info("***after:<"+msToken+">");
//		
//		// Antes de empezar compruebo que tengo los datos del CM rellenos si no es así lo relleno
//		//fillCMData();
//		
//		
//		//           Llamar al servicio de SessionManager(SM) /validateToken con el msToken
//		// 		     Obtener de SessionManagerResponse el sessionId -> se hace en SessionManagerConnService
//		String sessionId="";
//		try
//		{
//			//sessionId = smConnService.validateToken( msToken);
//		}
//		catch (Exception ex)
//		{
//			String errorMsg= "Exception calling SM (validateToken) with token:"+msToken+"\n";
//			errorMsg += "Exception message:"+ex.getMessage()+"\n";
//			//model.addAttribute("ErrorMessage",errorMsg);
//	        System.out.println("Devuelvo error "+errorMsg);
//	        
//	        return "acmError";
//		}
//    	return "";
//    }

}
