package com.cryo.modules.staff.recoveries;

import java.util.ArrayList;

import com.cryo.db.DatabaseConnection;
import com.cryo.db.impl.RecoveryConnection;
import com.cryo.utils.Utilities;

public class RecoveryUtils {
	
	public static RecoveryDAO getRecovery(String id) {
		Object[] data = RecoveryConnection.connection().handleRequest("get-recovery", id);
		if(data == null) return null;
		return (RecoveryDAO) data[0];
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<RecoveryDAO> getRecoveries(boolean archive, int page) {
		Object[] data = RecoveryConnection.connection().handleRequest("get-recoveries", archive, page);
		if(data == null)
			return new ArrayList<RecoveryDAO>();
		return (ArrayList<RecoveryDAO>) data[0];
	}
	
	public static int getTotalPages(boolean archive) {
		Object[] data = RecoveryConnection.connection().handleRequest("get-total-results", archive);
		if(data == null) return 0;
		int total = (int) data[0];
		total = (int) Utilities.roundUp(total, 10);
		return total;
	}

}
