package app.cmanagement;

import java.util.ArrayList;

public class CManagement {
	public String detectionStrategy;
	public String resolutionStrategy;
	public ArrayList<CDRule> conflicRules;
	public ArrayList<CDRule> resolutionRules;
	public ArrayList<CPriority> clientsPriority; 
	
	
    public CManagement(String defaultResolutionStrategy, String defaultDetectionStrategy) {
        this.detectionStrategy = defaultDetectionStrategy;
        this.resolutionStrategy = defaultResolutionStrategy;
        this.conflicRules = new ArrayList<CDRule>();
        this.resolutionRules = new ArrayList<CDRule>();
        this.clientsPriority = new ArrayList<CPriority>();
    }

    public String getDetectionStrategy() {
        return detectionStrategy;
    }

    public String getResolutionStrategy() {
        return resolutionStrategy;
    }

    public ArrayList<CDRule> getConflicRules() {
        return conflicRules;
    }
    
    public ArrayList<CDRule> getResolutionRules() {
        return resolutionRules;
    }
    
    public ArrayList<CPriority> getClientsPriority() {
        return clientsPriority;
    } 
    
	public void setDetectionStrategy(String detectionStrategy) {
		this.detectionStrategy = detectionStrategy;
	}

	public void setResolutionStrategy(String resolutionStrategy) {
		this.resolutionStrategy = resolutionStrategy;
	}
    
	public void setConflictRule(ArrayList<CDRule> conflicRules) {
		this.conflicRules = conflicRules;
	}
	
	public void setResolutionRule(ArrayList<CDRule> resolutionRules) {
		this.resolutionRules = resolutionRules;
	}
	
	public void setClientPriority(ArrayList<CPriority> clientsPriority) {
		this.clientsPriority = clientsPriority;
	}
	
	public void addConflictRule(String ruleName, String ruleFile, Boolean ruleChecked) {
		this.conflicRules.add(new CDRule(ruleName, ruleFile, ruleChecked));
	}
	
	public void addResolutionRule(String ruleName, String ruleFile, Boolean ruleChecked) {
		this.resolutionRules.add(new CDRule(ruleName, ruleFile, ruleChecked));
	}
   
	public void addClientPriority(String clientID, int priority) {
		this.clientsPriority.add(new CPriority(clientID, priority));
	}
	


}
