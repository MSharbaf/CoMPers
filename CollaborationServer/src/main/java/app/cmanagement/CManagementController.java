package app.cmanagement;

import io.javalin.core.util.FileUtil;
import io.javalin.http.Handler;

import java.util.List;
import java.util.Map;

import app.util.Path;
import app.util.ViewUtil;

import static app.Main.*;
import static app.util.RequestUtil.*;

public class CManagementController {
    
    public static Handler handleSaveManageconflictPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("registerationFailed", true);
        String form = getQuerySubmit(ctx);
        
        if(form.equals("ResStrategy")) {
        	String res = getQueryResolutionStrategy(ctx);
        	cmanagementDao.updateResolutionStrategy(res);
        	
        }else if(form.equals("ClientPriority")) {
            List<String> cpList = getQueryCPriority(ctx);
            for(int i=0; i < cpList.size(); i++) {
            	int prio = Integer.parseInt(cpList.get(i));
            	String CID = cmanagementDao.cmanagement.clientsPriority.get(i).clientID ; 
            	cmanagementDao.updateOneClientPriority(CID, prio);
            }
            
        }else if(form.equals("DecStrategy")) {
        	String det = getQueryDetectionStrategy(ctx);
        	cmanagementDao.updateDetectionStrategy(det);
        	
        }else if(form.equals("ConfRuleDefinition")) {
        	String rName = getQueryRuleName(ctx);
        	String rFile = ctx.uploadedFile("ruleFile").getFilename() ; 
        	cmanagementDao.addNewConflicRules(rName, rFile, false);

            ctx.uploadedFiles("ruleFile").forEach(file -> {
                FileUtil.streamToFile(file.getContent(), "upload/rules/" + file.getFilename());
            });
        	
        }else if(form.equals("ConfRuleSelection")) {
        	cmanagementDao.unCheckedAllConflictRule();
            List<String> checkedRule = getQueryCDRule(ctx);
            for(String r : checkedRule) {
            	cmanagementDao.updateConflicRuleCheck(r,true); 
            }
            
        }else if(form.equals("ResRuleDefinition")) {
        	String rName = getQueryRuleName(ctx);
        	String rFile = ctx.uploadedFile("ruleFile").getFilename() ; 
        	cmanagementDao.addNewResolutionRules(rName, rFile, false);

            ctx.uploadedFiles("ruleFile").forEach(file -> {
                FileUtil.streamToFile(file.getContent(), "upload/rules/" + file.getFilename());
            });
        	
        }else if(form.equals("ResRuleSelection")) {
        	cmanagementDao.unCheckedAllResolutionRule();
            List<String> checkedRule = getQueryCDRule(ctx);
            for(String r : checkedRule) {
            	cmanagementDao.updateResolutionRuleCheck(r,true); 
            }
        }
        	
        model.put("registerationFailed", false);
        model.put("registerationSucceeded", true);
        model.put("CManagement", cmanagementDao.getCManagement());
        ctx.render(Path.Template.MANAGECONFLICT, model);
    };
    
}
