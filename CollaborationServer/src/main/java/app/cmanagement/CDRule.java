package app.cmanagement;

public class CDRule{
	public String ruleName;
	public String ruleFile;
	public Boolean ruleChecked; 
	
	public CDRule(String ruleName, String ruleFile, Boolean ruleChecked) {
		this.ruleName = ruleName;
		this.ruleFile = ruleFile;
		this.ruleChecked = ruleChecked;
	}
	
    public String getRuleName() {
        return ruleName;
    }
    
    public String getRuleFile() {
        return ruleFile;
    }
    
    public Boolean getRuleChecked() {
        return ruleChecked;
    }
    
    public void setRuleFile(String ruleFile) {
    	this.ruleFile = ruleFile ; 
    }
    
    public void setRuleChecked(Boolean ruleChecked) {
    	this.ruleChecked = ruleChecked ; 
    }
}
