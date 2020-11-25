/**
Copyright © 2020  Atos Spain SA. All rights reserved.
This file is part of SEAL eMRTD Identity Source (SEAL emrtd-is).
SEAL emrtd-is is free software: you can redistribute it and/or modify it under the terms of EUPL 1.2.
THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT ANY WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT, 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
DAMAGES OR OTHER LIABILITY, WHETHER IN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
See README file for the full disclaimer information and LICENSE file for full license information in the project root.

@author Atos Research and Innovation, Atos SPAIN SA
*/
package eu.seal.is.api.services;

import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.seal.is.model.AttributeSet;
import eu.seal.is.model.AttributeType;
import eu.seal.is.model.DataStore;
import eu.seal.is.model.EntityMetadata;
import eu.seal.is.sm_api.SessionManagerConnService;
import eu.seal.is.model.DataSet;

import java.security.*;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class IsLoadPostServiceImp implements IsLoadPostService{

	
	private static final Logger log = LoggerFactory.getLogger(IsLoadPostServiceImp.class);
	private final String senderId = System.getenv("SENDER_ID") == null ? "emrtdISms_001": System.getenv("SENDER_ID");
	
	private final String IssuingStateContent = "This is an issuing state";
	private final String DocumentNumberContent = "This is a doc number";
	
	@Override
	public void loadPost (String sessionId, /*String dataset,*/ SessionManagerConnService smConn) throws Exception {
    
		
		try {
			// MOCKING TODO TO REVIEW IT (2020.11.20)
			//mocking (sessionId, smConn);
			
			// dataset: #B64  $ref: '#/definitions/dataSet'
			String dataset = (String) smConn.readVariable(sessionId, "emrtdDataset");
			
			byte[] decodedBytes = Base64.getDecoder().decode(dataset);
			String datasetString = new String(decodedBytes);
			
			//log.info("Before decoding: " + dataset);
			log.info("Dataset to be loaded: " + datasetString );
			DataSet newDataSet = (new ObjectMapper()).readValue(datasetString,DataSet.class);
			
			// TODO: the eMRTD IS will receive the dataSet and check that the signature 
			// of it matches the signature received (using same signing algorithm as does on the app:
			// the eMRTD app will read the ePassport and load the data into a dataSet and will sign the whole dataSet  
			
			// Which signing algorithms are to be used? "SHA256withRSA" by the moment
			
			String theSignAlgorithm = null;
			String theSignature = null;
			PublicKey thePublicKey = null;
			Map<String, String> theProperties = newDataSet.getProperties();
			for (Map.Entry<String, String> entry : theProperties.entrySet()) {
			 
				if (entry.getKey().equals("sigAlgorithm"))
					theSignAlgorithm = entry.getValue();
				if (entry.getKey().equals("signature"))
					theSignature = entry.getValue();
				
// TODO: Retrieve from an ENVIRONMENT VARIABLE.
//				thePublicKey = ...
			}
			
// TEMPORARY: pending of implementing https://github.com/EC-SEAL/emrtd-is/issues/2 
			thePublicKey = generateKeyPair().getPublic();
			
			if (thePublicKey != null  &&
				theSignAlgorithm != null  && !theSignAlgorithm.isEmpty() &&
				theSignature != null  && !theSignature.isEmpty()) {
				
				log.info("thePublicKey: " + thePublicKey);
				log.info("theSignAlgorithm: " + theSignAlgorithm);
				log.info("theSignature: " + theSignature);
				
				// Check the SIGNED dataset received
				if (theSignAlgorithm.equals ("SHA256withRSA")) { 
					//if (!verify(dataset, theSignature, thePublicKey))
					// or datasetString?
					if (!signingAndValidatingMock (datasetString))  // SIGNATURE MOCKED
						throw new Exception("Invalid signature");
				} else
					throw new Exception("Sign algorithm not implemented.");
				
			}
			else
				throw new Exception("Missing signature information.");
			
						
			// Get sessionData from SM
			// Reading apRequest, apMetadata, dataStore
			
			Object objAprequest = smConn.readVariable(sessionId, "apRequest");
			Object objApmetadata = smConn.readVariable(sessionId, "apMetadata");			
			
			log.info("apRequest, apMetadata just read. "); // WHAT FOR??!! See next TODO
			
			if (objAprequest != null) {
			// TODO: Some checkings to do before updating: dataSet in the apRequest
								
				try
				{
					// Update dataStore in the session
					
//					ObjectMapper objMapper = new ObjectMapper();
//					smConn.updateVariable(sessionId,"dataStore",objMapper.writeValueAsString(dataStore));
					
					
					// *****
					// TEMPORARY
					// *****
					// Preparing the newDataSet to be store in the session dataStore
					newDataSet.setIssuerId("IssuingState");   // Pointing to XXX   TODO
					newDataSet.setSubjectId("DocumentNumber");  // Pointing to YYY        TODO
					
					
					List<AttributeType> attributes = new ArrayList<>();
					attributes.addAll(newDataSet.getAttributes());
					
					AttributeType issuerAttr = new AttributeType();
					issuerAttr.setName("IssuingState");
					issuerAttr.setFriendlyName("IssuingState");
					List<String> issuerValues = new ArrayList<String>();
					issuerValues.add (IssuingStateContent);
					issuerAttr.setValues(issuerValues);
					
					attributes.add(issuerAttr);
					
					AttributeType subjectAttr = new AttributeType();
					subjectAttr.setName("DocumentNumber");
					subjectAttr.setFriendlyName("DocumentNumber");
					List<String> issuerValues1 = new ArrayList<String>();
					issuerValues1.add (DocumentNumberContent);
					subjectAttr.setValues(issuerValues1);
					
					attributes.add(subjectAttr);
							
					newDataSet.setAttributes(attributes);
					
					String objectId = "urn:mace:project-seal.eu:id:dataset:"; 
					objectId = objectId + 
							URLEncoder.encode(senderId, StandardCharsets.UTF_8.toString()) + ":" + // TO ASK
							//"LLoA" + ":" +
							URLEncoder.encode(getSubjectIdLnk(newDataSet.getAttributes(), newDataSet.getSubjectId()), StandardCharsets.UTF_8.toString()) + ":" + 
							URLEncoder.encode(getIssuerIdLnk(newDataSet.getAttributes(), newDataSet.getIssuerId()), StandardCharsets.UTF_8.toString());
					
					smConn.updateDatastore(sessionId, objectId, newDataSet);
					
					log.info("dataStore just updated.");
				}
				catch (Exception ex)
				{
					log.error("Exception: ", ex);
					throw new Exception (ex);				
				}	
			
			}
			else {
			
				log.info("Invalid session: " + sessionId);
				throw new Exception ("Invalid session");
				
			}
			
		}
		catch (Exception e) {
			log.error("Exception: ", e);
			log.info("Exception: ", e);
			throw new Exception (e);
		}
	}
	
	
	// TODO
	// TO CHECK with the new SM, and dataStoreObject and dataStoreObjectList
	private void mocking (String sessionId, SessionManagerConnService smConn) throws Exception {
		try {
			
		
		AttributeSet myApRequest = new AttributeSet();
		myApRequest.setId( "AP_" + UUID.randomUUID().toString());
		myApRequest.setType(AttributeSet.TypeEnum.REQUEST);
		myApRequest.setIssuer( "ms001");
		myApRequest.setRecipient("ms002");
		
		List<AttributeType> myAttributes =  new ArrayList<AttributeType>();
		AttributeType anAttribute = new AttributeType ();
		//anAttribute.setEncoding("plain");
		anAttribute.setFriendlyName("FirstAttr");
		anAttribute.setName("FirstAttr");
		//anAttribute.setLanguage(null);
		//anAttribute.setIsMandatory(true);
		//anAttribute.addValuesItem("JOHN");
		myAttributes.add(0, anAttribute);
		
		AttributeType anAttribute2 = new AttributeType ();
		//anAttribute.setEncoding("plain");
		anAttribute2.setFriendlyName("SecondAttr");
		anAttribute2.setName("SecondAttr");
		//anAttribute.setLanguage(null);
		//anAttribute.setIsMandatory(true);
		//anAttribute.addValuesItem("ES/NO/1234ABCD");
		myAttributes.add(1, anAttribute2);
		
		myApRequest.setAttributes(myAttributes);
		
		ObjectMapper objMapper = new ObjectMapper();
		smConn.updateVariable(sessionId, "apRequest", objMapper.writeValueAsString(myApRequest));
		
		EntityMetadata apMetadata = new EntityMetadata();
		apMetadata.setEntityId("https://eMRTDtest/metadata");
		apMetadata.setDefaultDisplayName("eMRTDtest");
		
		ObjectMapper objMapper2 = new ObjectMapper();
		smConn.updateVariable(sessionId, "apMetadata", objMapper2.writeValueAsString(apMetadata));
		
		// Creating an empty datastore object
		DataStore datastore = new DataStore();
		datastore.setId("DS_" +UUID.randomUUID().toString());
		datastore.setEncryptedData(null);
		datastore.setEncryptionAlgorithm(null);
		datastore.setSignature(null);
		datastore.setSignatureAlgorithm(null);	
		
		
		DataSet dataSet = new DataSet();
        dataSet.setId("DATASET__" + UUID.randomUUID().toString());
        dataSet.setLoa("loa loa");
        dataSet.setIssued("issued issued");
        dataSet.setIssuerId("eIDAS issuer id");
        dataSet.setType("eIDAS type");
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM YYYY HH:mm:ss z", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String nowDate = formatter.format(date);
        dataSet.setIssued(nowDate);
        List<DataSet> dsList = new ArrayList<DataSet> ();
        dsList.add(dataSet);
        datastore.setClearData(dsList);
		
		ObjectMapper objDatastore3 = new ObjectMapper();
		smConn.updateVariable(sessionId, "dataStore",objDatastore3.writeValueAsString(datastore));
		
		log.info("apRequest, apMetadata, dataStore just MOCKED.");
		
		}
		catch (Exception e) {
			log.error("Exception: ", e);
			throw new Exception (e);
		}
		
	}
	
	
	private static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }
	
	//
	// USED FOR TESTING
	//
	private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048, new SecureRandom());
        KeyPair pair = generator.generateKeyPair();

        return pair;
    }
	
	private static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();

        return Base64.getEncoder().encodeToString(signature);
    }
	
	private boolean signingAndValidatingMock (String dataset) throws Exception {
		
		try {
			KeyPair pair = generateKeyPair();
			String  signature = sign(dataset, pair.getPrivate());
			
			log.info("SIGNATURE: " + signature);
		
			return (verify(dataset, signature, pair.getPublic()));
		}
		catch (Exception e) {
			log.error("Exception: ", e);
			throw new Exception (e);
		} 
		
	}
	
	  private String getSubjectIdLnk(List<AttributeType> attributes, String subjectId) {
		  String theSubjectId = null;
		  
		  for (AttributeType attr: attributes) {
			  if ((attr.getFriendlyName() != null) && 
				 (attr.getFriendlyName().contains (subjectId))){
				  
				  theSubjectId = attr.getValues().get(0);
				  break;
			  }
		  }
		  
		  return (theSubjectId != null ? theSubjectId : subjectId);
	  
	  }
	  
		private String getIssuerIdLnk(List<AttributeType> attributes, String issuerId) {
		  String theIssuerId = null;
		  
		  for (AttributeType attr: attributes) {
			  if ((attr.getFriendlyName() != null) && 
				 (attr.getFriendlyName().contains (issuerId))){
				  
				  theIssuerId = attr.getValues().get(0);
				  break;
			  }
		  }
		  
		  return (theIssuerId != null ? theIssuerId : issuerId);
		
		}
	
}

