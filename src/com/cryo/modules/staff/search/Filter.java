package com.cryo.modules.staff.search;

import java.util.ArrayList;
import java.util.List;

import com.cryo.modules.account.support.punish.PunishDAO;

import lombok.*;

/**
 * @author Cody Thompson <eldo.imo.rs@hotmail.com>
 *
 * Created on: May 03, 2017 at 9:32:35 AM
 */
@RequiredArgsConstructor
public abstract class Filter {
	
	public final @Getter String name;
	protected @Getter Object value;
	
	public abstract String getFilter();
	
	public abstract boolean setValue(String value);
	
	public List<PunishDAO> filterList(List<PunishDAO> list) {
		return list;
	}
	
}
