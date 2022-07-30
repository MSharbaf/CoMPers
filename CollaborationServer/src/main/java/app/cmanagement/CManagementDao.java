package app.cmanagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class CManagementDao {
	
    private final String cmanagementFile = "src/main/resources/data/cmanagement.json";
    public CManagement cmanagement; 
    
    
    public CManagementDao() {
    	 cmanagement = new CManagement("", "");
    }
    
    public CManagement getCManagement() {
        return cmanagement;
    }
    
    public void updateCManagement(String resolutionStrategy, String detectionStrategy, ArrayList<CDRule> conflicRules, ArrayList<CDRule> resolutionRules, ArrayList<CPriority> clientsPriority) throws IOException {
    	this.cmanagement.detectionStrategy = detectionStrategy;
    	this.cmanagement.resolutionStrategy = resolutionStrategy;
    	this.cmanagement.conflicRules = conflicRules;
    	this.cmanagement.resolutionRules = resolutionRules;
    	this.cmanagement.clientsPriority = clientsPriority;
    	
    	writeCManagement();
    }
    
    public void updateResolutionStrategy(String resolutionStrategy) throws IOException {
    	this.cmanagement.setResolutionStrategy(resolutionStrategy);
    	writeCManagement();
    }
    
    public void updateDetectionStrategy(String detectionStrategy) throws IOException {
    	this.cmanagement.setDetectionStrategy(detectionStrategy);
    	
    	if(detectionStrategy.equals("Pattern Matching"))
    		updateResolutionStrategy("Pattern-Based Resolution");
    	
    	else if(detectionStrategy.equals("Change Overlapping"))
    		updateResolutionStrategy("Latest Operation Wins");

    	
    	writeCManagement();
    }
    
    public void updateClientPriority(ArrayList<CPriority> clientsPriority) throws IOException {
    	this.cmanagement.setClientPriority(clientsPriority);
    	writeCManagement();
    }
    
    public void updateOneClientPriority(String clientID, int priority) throws IOException {
    	for(CPriority cp : cmanagement.clientsPriority)
    		if(cp.clientID.equals(clientID))
    			cp.setPriority(priority);  
    	writeCManagement();
    }
    
    public void addNewClientPriority(String clientID, int priority) throws IOException {
    	this.cmanagement.addClientPriority(clientID, priority);
    	writeCManagement();
    }
    
    public int getClientPriority(String clientID) {
    	int priority = 0 ; 
    	for(CPriority cp : cmanagement.clientsPriority)
    		if(cp.clientID.equals(clientID)) {
    			priority = cp.getPriority();
    			break; 
    		}
    	return priority ; 
    }
    
    public void addNewConflicRules(String ruleName, String fileName, Boolean ruleChecked) throws IOException {
    	this.cmanagement.addConflictRule(ruleName, fileName, ruleChecked);
    	writeCManagement();
    }
    
    public void addNewResolutionRules(String ruleName, String fileName, Boolean ruleChecked) throws IOException {
    	this.cmanagement.addResolutionRule(ruleName, fileName, ruleChecked);
    	writeCManagement();
    }
    
    public void unCheckedAllConflictRule() throws IOException {
    	for(CDRule cdr : cmanagement.conflicRules)
    		cdr.setRuleChecked(false);
    	writeCManagement();
    }
    
    public void updateConflicRuleCheck(String ruleName, Boolean ruleChecked) throws IOException {
    	for(CDRule cdr : cmanagement.conflicRules)
    		if(cdr.ruleName.equals(ruleName)) {
    			cdr.setRuleChecked(ruleChecked);
    		}
    	writeCManagement();
    }
    
    public void unCheckedAllResolutionRule() throws IOException {
    	for(CDRule cdr : cmanagement.resolutionRules)
    		cdr.setRuleChecked(false);
    	writeCManagement();
    }
    
    public void updateResolutionRuleCheck(String ruleName, Boolean ruleChecked) throws IOException {
    	for(CDRule cdr : cmanagement.resolutionRules)
    		if(cdr.ruleName.equals(ruleName)) {
    			cdr.setRuleChecked(ruleChecked);
    		}
    	writeCManagement();
    }
    
    public void updateConflictRules(ArrayList<CDRule> conflictRules) throws IOException {
    	this.cmanagement.setConflictRule(conflictRules);
    	writeCManagement();
    }
    
    
    public void writeCManagement() throws IOException {
        
        try (FileOutputStream fos = new FileOutputStream(cmanagementFile);
                OutputStreamWriter isr = new OutputStreamWriter(fos, 
                        StandardCharsets.UTF_8)) {
            
            Gson gson = new Gson();
            gson.toJson(this.cmanagement, isr);
        }
        
    }
    
    
    public void readCManagement() throws IOException {
        Gson gson = new GsonBuilder().create();
        Path path = new File(cmanagementFile).toPath();
        
        try (Reader reader = Files.newBufferedReader(path, 
                StandardCharsets.UTF_8)) {
            
        	CManagement CM = gson.fromJson(reader, CManagement.class);
            
        	if(CM!=null) {
        		this.cmanagement = CM ;         		
        	}
        }
    }
    
    
    
}
